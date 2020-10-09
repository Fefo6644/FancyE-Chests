package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import me.fefo.facilites.VariousUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public final class ChestRemoveListener extends SelfRegisteringListener {
  private final FancyEChests plugin;

  public ChestRemoveListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler
  public void chestRemove(@NotNull final EntityDamageByEntityEvent e) {
    final Entity damaged = e.getEntity();
    final UUID uuid = damaged.getUniqueId();

    if (plugin.playersRemovingChest.size() == 0 ||
        plugin.spinnyChests.size() == 0 ||
        !plugin.spinnyChests.containsKey(uuid)) {
      return;
    }

    e.setCancelled(true);

    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
      final Entity damager = e.getDamager();

      if (damager.getType() == EntityType.PLAYER &&
          plugin.playersRemovingChest.remove(damager.getUniqueId())) {
        plugin.spinnyChests.remove(uuid).kill();
        plugin.chestsYaml.set(uuid.toString(), null);
        try {
          plugin.chestsYaml.save(plugin.chestsFile);
        } catch (IOException ex) {
          plugin.getLogger().severe("Could not save data file!");
          ex.printStackTrace();
        }

        VariousUtils.sendMessage(damager, CommanderKeen.CHEST_REMOVED);
      }
    }
  }
}
