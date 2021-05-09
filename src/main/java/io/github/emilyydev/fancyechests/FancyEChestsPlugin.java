//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021 emilyy-dev
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package io.github.emilyydev.fancyechests;

import io.github.emilyydev.fancyechests.config.ConfigAdapter;
import io.github.emilyydev.fancyechests.config.ConfigKeys;
import io.github.emilyydev.fancyechests.config.adapters.YamlConfigAdapter;
import io.github.emilyydev.fancyechests.listeners.ChunksListener;
import io.github.emilyydev.fancyechests.listeners.chest.ChestCloseListener;
import io.github.emilyydev.fancyechests.listeners.chest.ChestInteractListener;
import io.github.emilyydev.fancyechests.listeners.chest.ChestRemoveListener;
import io.github.emilyydev.fancyechests.listeners.player.PlayerLoginListener;
import io.github.emilyydev.fancyechests.listeners.player.PlayerQuitListener;
import io.github.emilyydev.fancyechests.message.SubjectFactory;
import io.github.emilyydev.fancyechests.model.chest.ChestMap;
import io.github.emilyydev.fancyechests.model.chest.SpinningChest;
import io.github.emilyydev.fancyechests.model.holder.HolderManager;
import io.github.emilyydev.fancyechests.util.TaskScheduler;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class FancyEChestsPlugin extends JavaPlugin implements Listener {

  static {
    try {
      // ensure class loading, run static init block (clinit)
      final ClassLoader classLoader = FancyEChestsPlugin.class.getClassLoader();
      classLoader.loadClass("io.github.emilyydev.fancyechests.util.CommandMapHelper");
    } catch (final ClassNotFoundException exception) {
      // ??? shouldn't throw but if it does we're up to no bueno
      throw new RuntimeException(exception);
    }
  }

  private final Path dataFolder = getDataFolder().toPath();
  private final ConfigAdapter configAdapter = new YamlConfigAdapter(this, this.dataFolder);
  private final ChestMap chestMap = new ChestMap(this);
  private final HolderManager holderManager = new HolderManager(this, this.dataFolder);
  private final TaskScheduler taskScheduler = new TaskScheduler(this);
  private final ChunksListener chunksListener = new ChunksListener(this); // why is this here again?

  private SubjectFactory subjectFactory = null;

  public TaskScheduler getTaskScheduler() {
    return this.taskScheduler;
  }

  public ConfigAdapter getConfigAdapter() {
    return this.configAdapter;
  }

  public SubjectFactory getSubjectFactory() {
    return this.subjectFactory;
  }

  public Path getDataFolderPath() {
    return this.dataFolder;
  }

  public ChestMap getChestMap() {
    return this.chestMap;
  }

  public HolderManager getHolderManager() {
    return this.holderManager;
  }

  @Override
  public void onLoad() {
    try {
      Files.createDirectories(this.dataFolder);
      this.configAdapter.load();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  public boolean reload() {
    try {
      this.configAdapter.reload();
      this.chestMap.reload();
      this.holderManager.unloadAllHolders();
      Bukkit.getOnlinePlayers().stream()
            .peek(this.holderManager::createHolder)
            .forEach(this.holderManager::createInventory);
      return true;
    } catch (final IOException exception) {
      getLogger().severe("There was an error while reloading the config/data");
      exception.printStackTrace();
      return false;
    }
  }

  @Override
  public void onEnable() {
    try {
      this.chestMap.load();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }

    this.subjectFactory = new SubjectFactory(this);

    if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
      this.holderManager.setVaultMeta(Bukkit.getServicesManager().load(Chat.class));
    }
    // ye darn /reload
    Bukkit.getOnlinePlayers().stream()
          .peek(this.holderManager::createHolder)
          .forEach(this.holderManager::createInventory);

    new FecCommand(this);

    new ChestInteractListener(this);
    new ChestCloseListener(this);
    new ChestRemoveListener(this);
    this.chunksListener.register(this);
    new PlayerLoginListener(this);
    new PlayerQuitListener(this);

    // move async? contains file IO on main thread;
    // will probably do when moving holder IO async w/ locks
    // process in parallel for now
    this.taskScheduler.sync(this.holderManager::saveAllHolders, 20L * 60L, 20L * 60L);

    this.taskScheduler.async(() -> {
      final long now = System.currentTimeMillis();
      this.chestMap.values().stream()
                   .filter(chest -> chest.getHiddenUntil() <= now)
                   .forEach(chest -> chest.setHiddenUntil(Long.MIN_VALUE));
    }, 500L, 500L);

    this.taskScheduler.async(() -> {
      this.chestMap.values().stream()
                   .filter(chest -> {
                     return chest.rotate(2.0 * Math.PI * this.configAdapter.get(ConfigKeys.RPM));
                   })
                   .map(SpinningChest::getLocation)
                   .forEach(location -> {
                     location.getWorld().spawnParticle(Particle.PORTAL, location,
                                                       1, 0.0, 0.0, 0.0, 0.7);
                   });
    }, 25L, 25L);
  }

  @Override
  public void onDisable() {
    this.holderManager.saveAllHolders();
    this.holderManager.unloadAllHolders();
    this.chestMap.clear();
    this.taskScheduler.shutdown();
  }

  public <T extends Event> void registerListener(final Class<T> eventType, final Consumer<T> handler) {
    registerListener(eventType, handler, EventPriority.NORMAL, true);
  }

  public <T extends Event> void registerListener(final Class<T> eventType, final Consumer<T> handler, final EventPriority priority) {
    registerListener(eventType, handler, priority, true);
  }

  public <T extends Event> void registerListener(final Class<T> eventType, final Consumer<T> handler, final boolean callIfCancelled) {
    registerListener(eventType, handler, EventPriority.NORMAL, callIfCancelled);
  }

  public <T extends Event> void registerListener(final Class<T> eventType, final Consumer<T> handler, final EventPriority priority, final boolean callIfCancelled) {
    Bukkit.getPluginManager().registerEvent(eventType, this, priority,
                                            (l, e) -> {
                                              if (eventType.isInstance(e)) {
                                                handler.accept(eventType.cast(e));
                                              }
                                            }, this, !callIfCancelled);
  }
}
