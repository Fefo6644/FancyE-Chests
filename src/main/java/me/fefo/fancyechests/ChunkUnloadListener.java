package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

public final class ChunkUnloadListener extends SelfRegisteringListener {
  private final FancyEChests plugin;

  public ChunkUnloadListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler
  public void onChunkUnload(final ChunkUnloadEvent event) {
    if (plugin.spinnyChests.size() == 0) {
      return;
    }

    for (final Entity entity : event.getChunk().getEntities()) {
      if (entity.getType() != EntityType.ARMOR_STAND) {
        continue;
      }

      plugin.spinnyChests.remove(entity.getUniqueId());
    }
  }
}
