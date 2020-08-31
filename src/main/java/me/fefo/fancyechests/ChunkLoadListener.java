package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;

public final class ChunkLoadListener extends SelfRegisteringListener {
  private final FancyEChests plugin;

  public ChunkLoadListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler
  public void onChunkLoad(final ChunkLoadEvent event) {
    if (plugin.chestsYaml.getKeys(false).size() == 0) {
      return;
    }

    for (final Entity entity : event.getChunk().getEntities()) {
      if (entity.getType() != EntityType.ARMOR_STAND) {
        continue;
      }

      if (plugin.chestsYaml.getKeys(false).contains(entity.getUniqueId().toString())) {
        final ConfigurationSection cs = plugin.chestsYaml.getConfigurationSection(entity.getUniqueId()
                                                                                        .toString());
        plugin.spinnyChests.put(entity.getUniqueId(),
                                new SpinnyChest(entity.getUniqueId(),
                                                cs.getLong(FancyEChests.YAML_HIDDEN_UNTIL),
                                                cs.getBoolean(FancyEChests.YAML_SHOULD_DISAPPEAR)));
      }
    }
  }
}
