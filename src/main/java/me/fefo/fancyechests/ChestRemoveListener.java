package me.fefo.fancyechests;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public final class ChestRemoveListener implements Listener {
  private final Main main;

  public ChestRemoveListener(Main main) { this.main = main; }

  @EventHandler
  public void chestRemove(@NotNull EntityDamageByEntityEvent e) {
    final Entity damaged = e.getEntity();
    final UUID uuid = damaged.getUniqueId();

    if (main.playersRemovingChest.size() == 0 ||
        main.spinnyChests.size() == 0 ||
        !main.spinnyChests.containsKey(uuid)) {
      return;
    }

    e.setCancelled(true);

    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
      final Entity damager = e.getDamager();

      if (damager instanceof Player &&
          main.playersRemovingChest.remove(damager.getUniqueId())) {
        main.spinnyChests.get(uuid).kill();
        main.spinnyChests.remove(uuid);
        main.chestsYaml.set(uuid.toString(), null);
        try {
          main.chestsYaml.save(main.chestsFile);
        } catch (IOException ex) {
          main.getLogger().severe("Could not save data file!");
          ex.printStackTrace();
        }
        damager.sendMessage(ChatColor.AQUA + "Ender chest removed");
      }
    }
  }
}
