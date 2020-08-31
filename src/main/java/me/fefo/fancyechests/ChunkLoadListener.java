package me.fefo.fancyechests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public final class ChunkLoadListener implements Listener {
  private final Main main;

  public ChunkLoadListener(Main main) { this.main = main; }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent event) {
    if (main.chestsYaml.getKeys(false).size() == 0) {
      return;
    }

    for (Entity entity : event.getChunk().getEntities()) {
      if (!(entity instanceof ArmorStand)) {
        continue;
      }

      if (main.chestsYaml.getKeys(false).contains(entity.getUniqueId().toString())) {
        final ConfigurationSection cs = main.chestsYaml.getConfigurationSection(entity.getUniqueId()
                                                                                      .toString());
        main.spinnyChests.put(entity.getUniqueId(),
                              new SpinnyChest(entity.getUniqueId(),
                                              cs.getLong(Main.YAML_HIDDEN_UNTIL),
                                              cs.getBoolean(Main.YAML_SHOULD_DISAPPEAR)));
      }
    }
  }
}
