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
import com.github.fefo.fancyechests.message.Message;
import com.github.fefo.fancyechests.message.SubjectFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.IOException;
import java.util.UUID;

public final class ChestRemoveListener implements Listener {

  private final FancyEChestsPlugin plugin;
  private final SubjectFactory subjectFactory;

  public ChestRemoveListener(final FancyEChestsPlugin plugin) {
    this.plugin = plugin;
    this.subjectFactory = plugin.getSubjectFactory();
  }

  @EventHandler
  public void chestRemove(final EntityDamageByEntityEvent event) {
    final Entity damaged = event.getEntity();
    final UUID uuid = damaged.getUniqueId();

    if (!this.plugin.getHolderManager().isAnyoneRemovingChest()
        || this.plugin.getChestMap().isEmpty()
        || !this.plugin.getChestMap().containsKey(uuid)) {
      return;
    }

    event.setCancelled(true);
    if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }

    final Entity damager = event.getDamager();
    if (damager.getType() == EntityType.PLAYER
        && this.plugin.getHolderManager().removedChestCompleted(damager.getUniqueId())) {
      this.plugin.getChestMap().remove(uuid);
      try {
        this.plugin.getChestMap().save();
      } catch (final IOException exception) {
        this.plugin.getSLF4JLogger().error("Could not save data file!", exception);
        exception.printStackTrace();
      }

      Message.CHEST_REMOVED.send(this.subjectFactory.player((Player) damager));
    }
  }
}
