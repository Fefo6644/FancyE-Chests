package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ChestInteractListener extends SelfRegisteringListener {

  private final FancyEChests plugin;

  public ChestInteractListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler
  public void chestInteract(@NotNull final PlayerInteractAtEntityEvent e) {
    if (plugin.spinnyChests.size() > 0) {
      final Entity entity = e.getRightClicked();
      final UUID uuid = entity.getUniqueId();

      if (entity.getType() == EntityType.ARMOR_STAND) {
        if (plugin.spinnyChests.containsKey(uuid)) {
          e.setCancelled(true);
          if (plugin.playersRemovingChest.contains(e.getPlayer().getUniqueId())) {
            return;
          }

          final SpinnyChest sc = plugin.spinnyChests.get(uuid);
          if (sc.getHiddenUntil() == 0L && !sc.isBeingUsed()) {
            sc.updateUsage();

            final Player player = e.getPlayer();
            final FecHolder holder = plugin.playerInventories.get(player.getUniqueId());

            player.openInventory(holder.getInventory());
            plugin.playersUsingChest.put(player.getUniqueId(), uuid);

            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(),
                                               FancyEChests.SOUND, SoundCategory.BLOCKS,
                                               1.0f, 1.0f);
          }
        }
      }
    }
  }
}
