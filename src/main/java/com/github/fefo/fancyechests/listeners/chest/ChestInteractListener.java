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
import com.github.fefo.fancyechests.model.chest.SpinningChest;
import com.github.fefo.fancyechests.model.holder.FecHolder;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public final class ChestInteractListener implements Listener {

  private final FancyEChestsPlugin plugin;

  public ChestInteractListener(final FancyEChestsPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void chestInteract(final PlayerInteractAtEntityEvent event) {
    if (this.plugin.getChestMap().isEmpty()) {
      return;
    }

    final Entity entity = event.getRightClicked();
    final UUID uuid = entity.getUniqueId();

    if (entity.getType() != EntityType.ARMOR_STAND
        || !this.plugin.getChestMap().containsKey(uuid)) {
      return;
    }

    event.setCancelled(true);
    if (this.plugin.getHolderManager().isRemovingChest(event.getPlayer().getUniqueId())) {
      return;
    }

    final SpinningChest chest = this.plugin.getChestMap().get(uuid);
    if (chest.getHiddenUntil() > System.currentTimeMillis() || chest.isBeingUsed()) {
      return;
    }
    chest.updateUsage();

    final Player player = event.getPlayer();
    final FecHolder holder = this.plugin.getHolderManager().getHolder(player.getUniqueId());
    player.openInventory(holder.getInventory());
    this.plugin.getHolderManager().startedUsingChest(player.getUniqueId(), uuid);

    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENDERCHEST_OPEN,
                                SoundCategory.BLOCKS, 1.0f, 1.0f);
  }
}
