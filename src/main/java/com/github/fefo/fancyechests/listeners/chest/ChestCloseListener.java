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

package com.github.fefo.fancyechests.listeners.chest;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.config.ConfigKeys;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.IOException;
import java.util.UUID;

public final class ChestCloseListener implements Listener {

  private final FancyEChestsPlugin plugin;
  private final ConfigAdapter config;

  public ChestCloseListener(final FancyEChestsPlugin plugin) {
    this.plugin = plugin;
    this.config = plugin.getConfigAdapter();
  }

  @EventHandler
  public void chestClose(final InventoryCloseEvent event) {
    final HumanEntity player = event.getPlayer();
    final UUID uuid = player.getUniqueId();
    final UUID chestUuid = this.plugin.getHolderManager().usedChestCompleted(uuid);
    if (chestUuid == null) {
      return;
    }

    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENDERCHEST_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);

    if (!this.plugin.getChestMap().get(chestUuid).shouldDisappear()) {
      return;
    }

    final long hiddenUntil = System.currentTimeMillis() + this.config.get(ConfigKeys.SECONDS_HIDDEN) * 1000L;
    this.plugin.getChestMap().get(chestUuid).setHiddenUntil(hiddenUntil);

    try {
      this.plugin.getChestMap().save(); // TODO move async, use locks
    } catch (final IOException exception) {
      this.plugin.getSLF4JLogger().error("Could not save data file!", exception);
      exception.printStackTrace();
    }
  }
}
