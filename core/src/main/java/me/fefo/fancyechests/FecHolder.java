package me.fefo.fancyechests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class FecHolder {

  private static final Pattern VALID_SLOT_NODE = Pattern.compile("(?i)^fancyechests\\.slots\\.(\\d+)$");
  private static final int PERM_PREFIX_LENGTH = "fancyechests.slots.".length();

  private static FancyEChests plugin;
  private final UUID uuid;
  private Inventory fancyEnderChest;
  private final File playerFile;
  private final YamlConfiguration yamlFile = new YamlConfiguration();
  private boolean requiresMigration;
  private int lastHash = 0;

  public static void setPlugin(final FancyEChests plugin) {
    FecHolder.plugin = plugin;
  }

  public FecHolder(final UUID uuid) {
    this.uuid = uuid;

    playerFile = new File(plugin.playerDataFolder, uuid + ".yml");
    requiresMigration = !playerFile.exists();
    if (requiresMigration) {
      try {
        playerFile.createNewFile();
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }

    try {
      yamlFile.load(playerFile);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  public void migrationCompleted() {
    requiresMigration = false;
    save(true);
  }

  public boolean requiresMigration() {
    return requiresMigration;
  }

  public void save() {
    save(false);
  }

  public void save(final boolean runAsync) {
    final int hash = Arrays.hashCode(fancyEnderChest.getStorageContents());
    if (hash == lastHash) {
      return;
    }

    final List<Map<String, Object>> serializedItems = new ArrayList<>();
    for (final ItemStack item : fancyEnderChest.getStorageContents()) {
      if (item != null) {
        serializedItems.add(item.serialize());
      }
    }
    yamlFile.set("root", serializedItems);

    final Runnable task = () -> {
      try {
        yamlFile.save(playerFile);
        lastHash = hash;
      } catch (IOException e) {
        e.printStackTrace();
      }
    };

    if (runAsync) {
      plugin.getScheduler().async(task);
    } else {
      task.run();
    }
  }

  public void deleteData() {
    deleteData(false);
  }

  public void deleteData(final boolean runAsync) {
    if (runAsync) {
      plugin.getScheduler().async(playerFile::delete);
    } else {
      playerFile.delete();
    }
  }

  public UUID getUniqueId() {
    return uuid;
  }

  public Inventory getInventory() {
    return fancyEnderChest;
  }

  public void createInventory(final Player player) {
    fancyEnderChest = Bukkit.createInventory(player, getMaxSlots(player).orElse(27), "Ender Chest");

    for (final Map<?, ?> serialized : yamlFile.getMapList("root")) {
      //noinspection unchecked
      final ItemStack itemStack = ItemStack.deserialize((Map<String, Object>) serialized);
      final Map<Integer, ItemStack> remaining = fancyEnderChest.addItem(itemStack);
      if (!remaining.isEmpty()) {
        plugin.getLogger().warning("Could not add " + remaining.get(0) +
                                   " to " + player.getName() + "'s FEC");
      }
    }
  }

  private static OptionalInt getMaxSlots(final Permissible permissible) {
    return permissible.getEffectivePermissions().stream()
                      .map(PermissionAttachmentInfo::getPermission)
                      .map(String::toLowerCase)
                      .filter(FecHolder::isValidSlotsPerm)
                      .flatMapToInt(FecHolder::getSlotsFromPerm)
                      .max();
  }

  private static boolean isValidSlotsPerm(final String perm) {
    final Matcher matcher = VALID_SLOT_NODE.matcher(perm);
    if (!matcher.find()) {
      return false;
    }

    try {
      final int slots = Integer.parseInt(matcher.group(1));
      return slots % 9 == 0 && slots > 0;
    } catch (NumberFormatException exception) {
      return false;
    }
  }

  private static IntStream getSlotsFromPerm(final String perm) {
    try {
      final int ret = Integer.parseInt(perm.substring(PERM_PREFIX_LENGTH));
      return IntStream.of(Math.min(ret, 54));
    } catch (NumberFormatException exception) {
      return IntStream.empty();
    }
  }
}
