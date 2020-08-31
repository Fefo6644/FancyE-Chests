package me.fefo.fancyechests;

import org.bukkit.SoundCategory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ChestInteractListener implements Listener {
  private final Main main;

  public ChestInteractListener(Main main) { this.main = main; }

  @EventHandler
  public void chestInteract(@NotNull PlayerInteractAtEntityEvent e) {
    if (main.spinnyChests.size() > 0) {
      final Entity entity = e.getRightClicked();
      final UUID uuid = entity.getUniqueId();

      if (entity instanceof ArmorStand) {
        if (main.spinnyChests.containsKey(uuid)) {
          e.setCancelled(true);
          if (main.playersRemovingChest.contains(e.getPlayer().getUniqueId())) {
            return;
          }

          final SpinnyChest sc = main.spinnyChests.get(uuid);
          if (sc.getHiddenUntil() == 0L
              && !sc.isBeingUsed()) {

            sc.updateUsage();

            final Player player = e.getPlayer();
            player.openInventory(player.getEnderChest());
            main.playersUsingChest.put(player.getUniqueId(), uuid);

            e.getPlayer()
             .getWorld()
             .playSound(e.getPlayer().getLocation(),
                        main.sound,
                        SoundCategory.BLOCKS,
                        1.0f, 1.0f);
          }
        }
      }
    }
  }
}
