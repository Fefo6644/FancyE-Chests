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
