//
// FancyE-Chests - Provide your players with isolated, fancy spinning ender chests.
// Copyright (C) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
