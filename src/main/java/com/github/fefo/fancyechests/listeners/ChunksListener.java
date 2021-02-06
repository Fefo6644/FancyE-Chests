package com.github.fefo.fancyechests.listeners;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.model.chest.ChestMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public final class ChunksListener implements Listener {

  private final ChestMap chestMap;

  public ChunksListener(final FancyEChestsPlugin plugin) {
    this.chestMap = plugin.getChestMap();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onChunkLoad(final ChunkLoadEvent event) {
    if (this.chestMap.isEmpty()) {
      return;
    }

    for (final Entity entity : event.getChunk().getEntities()) {
      if (entity.getType() != EntityType.ARMOR_STAND) {
        continue;
      }

      if (this.chestMap.containsKey(entity.getUniqueId())) {
        this.chestMap.summon(entity.getUniqueId());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onChunkUnload(final ChunkUnloadEvent event) {
    if (this.chestMap.isEmpty()) {
      return;
    }

    for (final Entity entity : event.getChunk().getEntities()) {
      if (entity.getType() == EntityType.ARMOR_STAND) {
        this.chestMap.unload(entity.getUniqueId());
      }
    }
  }
}
