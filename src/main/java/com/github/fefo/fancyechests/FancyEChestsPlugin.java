//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
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

package com.github.fefo.fancyechests;

import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.config.ConfigKeys;
import com.github.fefo.fancyechests.config.adapters.YamlConfigAdapter;
import com.github.fefo.fancyechests.listeners.ChunksListener;
import com.github.fefo.fancyechests.listeners.chest.ChestCloseListener;
import com.github.fefo.fancyechests.listeners.chest.ChestInteractListener;
import com.github.fefo.fancyechests.listeners.chest.ChestRemoveListener;
import com.github.fefo.fancyechests.listeners.player.PlayerLoginListener;
import com.github.fefo.fancyechests.listeners.player.PlayerQuitListener;
import com.github.fefo.fancyechests.message.SubjectFactory;
import com.github.fefo.fancyechests.model.chest.ChestMap;
import com.github.fefo.fancyechests.model.chest.SpinningChest;
import com.github.fefo.fancyechests.model.holder.HolderManager;
import com.github.fefo.fancyechests.util.TaskScheduler;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FancyEChestsPlugin extends JavaPlugin {

  public static final Logger LOGGER = LoggerFactory.getLogger(FancyEChestsPlugin.class);

  private final Path dataFolder = getDataFolder().toPath();
  private final ConfigAdapter configAdapter = new YamlConfigAdapter(this, this.dataFolder);
  private final ChestMap chestMap = new ChestMap(this);
  private final HolderManager holderManager = new HolderManager(this.dataFolder);
  private final TaskScheduler taskScheduler = new TaskScheduler(this);
  private final ChunksListener chunksListener = new ChunksListener(this);

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
      LOGGER.error("There was an error while reloading the config/data", exception);
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
      this.holderManager.setMeta(Bukkit.getServicesManager().load(Chat.class));
    }
    // ye darn /reload
    Bukkit.getOnlinePlayers().stream()
          .peek(this.holderManager::createHolder)
          .forEach(this.holderManager::createInventory);

    new FecCommand(this);

    Bukkit.getPluginManager().registerEvents(new ChestInteractListener(this), this);
    Bukkit.getPluginManager().registerEvents(new ChestCloseListener(this), this);
    Bukkit.getPluginManager().registerEvents(new ChestRemoveListener(this), this);
    Bukkit.getPluginManager().registerEvents(this.chunksListener, this);
    Bukkit.getPluginManager().registerEvents(new PlayerLoginListener(this), this);
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);

    // move async? contains file IO on main thread;
    // will probably do when moving holder IO async w/ locks
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

  @Override
  public Logger getSLF4JLogger() {
    return LOGGER;
  }
}
