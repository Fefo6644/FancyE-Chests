package me.fefo.fancyechests;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public final class ChestCloseListener implements Listener {
  private final Main main;

  public ChestCloseListener(Main main) { this.main = main; }

  @EventHandler
  public void chestClose(@NotNull InventoryCloseEvent e) {
    final UUID uuid = e.getPlayer().getUniqueId();
    if (main.playersUsingChest.containsKey(uuid)) {
      if (main.spinnyChests.get(main.playersUsingChest.get(uuid))
                           .shouldDisappear()) {
        final long hiddenUntil = Instant.now().toEpochMilli() + main.delayMillis;

        main.spinnyChests.get(main.playersUsingChest.get(uuid))
                         .setHiddenUntil(hiddenUntil);
        final ConfigurationSection cs
            = main.chestsYaml.getConfigurationSection(main.playersUsingChest.get(uuid).toString());
        cs.set(Main.YAML_HIDDEN_UNTIL, hiddenUntil);
        cs.set(Main.YAML_SHOULD_DISAPPEAR, true);
        try {
          main.chestsYaml.save(main.chestsFile);
        } catch (IOException ex) {
          main.getLogger().severe("Could not save data file!");
          ex.printStackTrace();
        }
      }

      main.playersUsingChest.remove(uuid);

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
