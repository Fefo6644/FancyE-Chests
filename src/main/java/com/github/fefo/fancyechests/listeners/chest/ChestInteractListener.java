package com.github.fefo.fancyechests.listeners.chest;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.model.chest.ChestMap;
import com.github.fefo.fancyechests.model.chest.SpinningChest;
import com.github.fefo.fancyechests.model.holder.FecHolder;
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

    player.getWorld().playSound(player.getLocation(), ChestMap.ENDERCHEST_OPEN_SOUND,
                                SoundCategory.BLOCKS, 1.0f, 1.0f);
  }
}
