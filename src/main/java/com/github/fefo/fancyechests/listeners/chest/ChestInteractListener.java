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

package com.github.fefo.fancyechests.listeners.chest;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.model.chest.SpinningChest;
import com.github.fefo.fancyechests.model.holder.FecHolder;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public final class ChestInteractListener {

  private final FancyEChestsPlugin plugin;

  public ChestInteractListener(final FancyEChestsPlugin plugin) {
    this.plugin = plugin;
    plugin.registerListener(PlayerInteractAtEntityEvent.class, this::chestInteract);
  }

  private void chestInteract(final PlayerInteractAtEntityEvent event) {
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
