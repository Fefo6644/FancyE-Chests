package me.fefo.fancyechests;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public final class ChunkUnloadListener implements Listener {
  private final Main main;

  public ChunkUnloadListener(Main main) { this.main = main; }

  @EventHandler
  public void onChunkUnload(ChunkUnloadEvent event) {
    if (main.spinnyChests.size() == 0) {
      return;
    }

    for (Entity entity : event.getChunk().getEntities()) {
      if (!(entity instanceof ArmorStand)) {
        continue;
      }

      main.spinnyChests.remove(entity.getUniqueId());
    }
  }
}
