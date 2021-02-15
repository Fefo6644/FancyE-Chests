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

package com.github.fefo.fancyechests.model.holder;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class HolderManager {

  private final Map<UUID, YamlConfiguration> loadedYamls = new HashMap<>();
  private final Map<UUID, FecHolder> holders = new HashMap<>();
  private final Set<UUID> playersRemovingChests = new HashSet<>();
  private final Map<UUID, UUID> playersUsingChests = new HashMap<>();
  private final Path playerdata;
  private Chat meta = null;

  public HolderManager(final Path dataFolder) {
    this.playerdata = dataFolder.resolve("playerdata");
    try {
      Files.createDirectories(this.playerdata);
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  public void setMeta(final Chat meta) {
    Preconditions.checkState(this.meta == null, "Vault chat is already loaded");
    this.meta = meta;
  }

  public FecHolder getHolder(final UUID uuid) {
    return this.holders.get(uuid);
  }

  public FecHolder createHolder(final Player player) {
    return createHolder(player.getUniqueId());
  }

  public FecHolder createHolder(final UUID uuid) {
    final FecHolder holder = new FecHolder(uuid);
    this.holders.put(uuid, holder);

    final YamlConfiguration configuration = new YamlConfiguration();
    this.loadedYamls.put(uuid, configuration);
    if (Files.exists(this.playerdata.resolve(uuid + ".yml"))) {
      try (final BufferedReader reader =
               Files.newBufferedReader(this.playerdata.resolve(uuid + ".yml"))) {
        configuration.load(reader);
      } catch (final InvalidConfigurationException | IOException exception) {
        exception.printStackTrace();
      }
    }

    return holder;
  }

  public void createInventory(final Player player) {
    final FecHolder holder = this.holders.get(player.getUniqueId());
    Preconditions.checkState(holder != null, "holder not created");
    final YamlConfiguration configuration = this.loadedYamls.remove(player.getUniqueId());
    final Inventory chest = Bukkit.createInventory(player, getMaxSlots(player), "Ender Chest");
    holder.setInventory(chest);

    if (configuration == null) {
      return;
    }

    final List<ItemStack> items =
        (List<ItemStack>) configuration.getList("items", new ArrayList<>());
    final Map<Integer, ItemStack> remaining = chest.addItem(items.toArray(new ItemStack[0]));
    if (!remaining.isEmpty()) {
      FancyEChestsPlugin.LOGGER.warn(String.format("Could not add %d items to %s's FEC",
                                                   remaining.size(), player.getName()));
    }
  }

  public CompletableFuture<Void> saveHolder(final UUID uuid) {
    return saveHolder(uuid, Runnable::run);
  }

  public CompletableFuture<Void> saveHolder(final UUID uuid, final Executor executor) {
    final FecHolder holder = this.holders.get(uuid);
    if (holder == null) {
      return CompletableFuture.completedFuture(null);
    }

    final List<ItemStack> items = Lists.newArrayList(holder.getInventory().getStorageContents());
    items.removeIf(Objects::isNull);
    items.removeIf(item -> item.getType() == Material.AIR);
    final int hash = items.hashCode();
    if (hash == holder.getLastHash()) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.runAsync(() -> {
      if (items.isEmpty()) {
        try {
          Files.delete(this.playerdata.resolve(holder.getUuid() + ".yml"));
        } catch (final IOException exception) {
          exception.printStackTrace();
        }
        return;
      }

      final YamlConfiguration configuration = new YamlConfiguration();
      configuration.set("items", items);
      try {
        configuration.save(this.playerdata.resolve(holder.getUuid() + ".yml").toFile());
        holder.setLastHash(hash);
      } catch (final IOException exception) {
        exception.printStackTrace();
      }
    }, executor);
  }

  public void unloadHolder(final UUID uuid) {
    this.holders.remove(uuid);
  }

  public CompletableFuture<Void> saveAllHolders() {
    return CompletableFuture.allOf(this.holders.keySet().stream()
                                               .map(this::saveHolder)
                                               .toArray(CompletableFuture[]::new));
  }

  public void unloadAllHolders() {
    this.holders.clear();
    this.loadedYamls.clear();
  }

  public void startedRemovingChest(final UUID uuid) {
    this.playersRemovingChests.add(uuid);
  }

  public boolean isRemovingChest(final UUID uuid) {
    return this.playersRemovingChests.contains(uuid);
  }

  public boolean isAnyoneRemovingChest() {
    return !this.playersRemovingChests.isEmpty();
  }

  public boolean removedChestCompleted(final UUID uuid) {
    return this.playersRemovingChests.remove(uuid);
  }

  public void startedUsingChest(final UUID uuid, final UUID chestId) {
    this.playersUsingChests.put(uuid, chestId);
  }

  public boolean isUsingChest(final UUID uuid) {
    return this.playersUsingChests.containsKey(uuid);
  }

  public boolean isAnyoneUsingChest() {
    return !this.playersUsingChests.isEmpty();
  }

  public UUID usedChestCompleted(final UUID uuid) {
    return this.playersUsingChests.remove(uuid);
  }

  private int getMaxSlots(final Player player) {
    if (this.meta == null) {
      // fallback if Vault isn't installed
      return 27;
    }

    int slots = this.meta.getPlayerInfoInteger(player, "fancyechests.slots", 27);
    slots = Math.max(9, Math.min(slots, 54)); // clamp
    slots = (int) (Math.ceil((double) slots / 9.0) * 9.0);  // ceil to nearest multiple of 9

    return slots;
  }
}
