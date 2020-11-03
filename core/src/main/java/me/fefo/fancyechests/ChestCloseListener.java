package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public final class ChestCloseListener extends SelfRegisteringListener {

  private final FancyEChests plugin;

  public ChestCloseListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler
  public void chestClose(@NotNull final InventoryCloseEvent e) {
    final UUID uuid = e.getPlayer().getUniqueId();
    if (plugin.playersUsingChest.containsKey(uuid)) {
      if (plugin.spinnyChests.get(plugin.playersUsingChest.get(uuid))
                             .shouldDisappear()) {
        final long hiddenUntil = Instant.now().toEpochMilli() + plugin.delayMillis;

        plugin.spinnyChests.get(plugin.playersUsingChest.get(uuid))
                           .setHiddenUntil(hiddenUntil);
        final ConfigurationSection cs
            = plugin.chestsYaml.getConfigurationSection(plugin.playersUsingChest.get(uuid).toString());
        cs.set(FancyEChests.YAML_HIDDEN_UNTIL, hiddenUntil);
        cs.set(FancyEChests.YAML_SHOULD_DISAPPEAR, true);
        try {
          plugin.chestsYaml.save(plugin.chestsFile);
        } catch (IOException ex) {
          plugin.getLogger().severe("Could not save data file!");
          ex.printStackTrace();
        }
      }

      plugin.playersUsingChest.remove(uuid);

      Sound sound;
      try {
        sound = Sound.valueOf("BLOCK_ENDERCHEST_CLOSE");
      } catch (IllegalArgumentException ignored) {
        sound = Sound.valueOf("BLOCK_ENDER_CHEST_CLOSE");
      }

      e.getPlayer()
       .getWorld()
       .playSound(e.getPlayer().getLocation(),
                  sound,
                  SoundCategory.BLOCKS,
                  1.0f, 1.0f);
    }
  }
}
