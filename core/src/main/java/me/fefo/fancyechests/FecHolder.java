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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class FecHolder {

  private static final Pattern VALID_SLOT_NODE = Pattern.compile("^fancyechests\\.slots\\.(\\d+)$");
  private static final int PERM_PREFIX_LENGTH = "fancyechests.slots.".length();

  private final FancyEChests plugin;
  private Inventory fancyEnderChest;
  private final File playerFile;
  private final YamlConfiguration yamlFile = new YamlConfiguration();
  private int lastHash = 0;

  public FecHolder(final FancyEChests plugin, final UUID uuid) {
    this.plugin = plugin;

    playerFile = new File(plugin.playerDataFolder, uuid + ".yml");

    if (playerFile.exists()) {
      try {
        yamlFile.load(playerFile);
      } catch (IOException | InvalidConfigurationException e) {
        e.printStackTrace();
      }
    }
  }

  public void migrationCompleted() {
    try {
      playerFile.createNewFile();
      save(plugin.getScheduler()::async);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public CompletableFuture<Void> save() {
    return save(Runnable::run);
  }

  public CompletableFuture<Void> save(final Executor executor) {
    final int hash = Arrays.hashCode(fancyEnderChest.getStorageContents());
    if (hash == lastHash) {
      return CompletableFuture.completedFuture(null);
    }

    final List<ItemStack> items = new ArrayList<>(Arrays.asList(fancyEnderChest.getStorageContents()));
    items.removeIf(Objects::isNull);

    return CompletableFuture.runAsync(() -> {
      if (!items.isEmpty()) {
        try {
          yamlFile.set("root", items);
          yamlFile.save(playerFile);
          lastHash = hash;
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        playerFile.delete();
      }
    }, executor);
  }

  public Inventory getInventory() {
    return fancyEnderChest;
  }

  @SuppressWarnings("unchecked")
  public void createInventory(final Player player) {
    fancyEnderChest = Bukkit.createInventory(player, getMaxSlots(player).orElse(27), "Ender Chest");

    final List<ItemStack> items = (List<ItemStack>) yamlFile.getList("root", new ArrayList<>());
    final Map<Integer, ItemStack> remaining = fancyEnderChest.addItem(items.toArray(new ItemStack[0]));
    if (!remaining.isEmpty()) {
      plugin.getLogger().warning("Could not add " + remaining.size() + " items to " + player.getName() + "'s FEC");
    }
  }

  private static OptionalInt getMaxSlots(final Permissible permissible) {
    return permissible.getEffectivePermissions().stream()
                      .map(PermissionAttachmentInfo::getPermission)
                      .map(String::toLowerCase)
                      .filter(FecHolder::isValidSlotsPerm)
                      .flatMapToInt(FecHolder::getSlotsFromPerm).max();
  }

  private static boolean isValidSlotsPerm(final String perm) {
    final Matcher matcher = VALID_SLOT_NODE.matcher(perm.toLowerCase(Locale.ROOT));
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
