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

      Message.CHEST_REMOVED.sendMessage(this.subjectFactory.player((Player) damager));
    }
  }
}
