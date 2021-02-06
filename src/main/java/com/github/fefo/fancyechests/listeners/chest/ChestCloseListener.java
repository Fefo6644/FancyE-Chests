package com.github.fefo.fancyechests.listeners.chest;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.config.ConfigKeys;
import com.github.fefo.fancyechests.model.chest.ChestMap;
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

    player.getWorld().playSound(player.getLocation(), ChestMap.ENDERCHEST_CLOSE_SOUND,
                                SoundCategory.BLOCKS, 1.0f, 1.0f);

    if (!this.plugin.getChestMap().get(chestUuid).shouldDisappear()) {
      return;
    }

    final long hiddenUntil = System.currentTimeMillis()
                             + this.config.get(ConfigKeys.SECONDS_HIDDEN) * 1000L;
    this.plugin.getChestMap().get(chestUuid).setHiddenUntil(hiddenUntil);

    try {
      this.plugin.getChestMap().save(); // TODO move async, use locks
    } catch (final IOException exception) {
      this.plugin.getSLF4JLogger().error("Could not save data file!", exception);
      exception.printStackTrace();
    }
  }
}
