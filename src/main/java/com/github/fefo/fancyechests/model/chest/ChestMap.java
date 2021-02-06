package com.github.fefo.fancyechests.model.chest;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.util.adapter.LocationAdapter;
import com.github.fefo.fancyechests.util.adapter.SpinningChestAdapter;
import com.github.fefo.fancyechests.util.adapter.WorldAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ChestMap implements Map<UUID, SpinningChest> {

  public static final Sound ENDERCHEST_OPEN_SOUND;
  public static final Sound ENDERCHEST_CLOSE_SOUND;
  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(World.class, WorldAdapter.ADAPTER)
          .registerTypeAdapter(Location.class, LocationAdapter.ADAPTER)
          .registerTypeAdapter(SpinningChest.class, SpinningChestAdapter.ADAPTER)
          .setPrettyPrinting()
          .create();
  private static final Type CHESTS_TYPE = new TypeToken<Set<SpinningChest>>() { }.getType();

  static {
    Sound open, close;

    try {
      open = Sound.valueOf("BLOCK_ENDERCHEST_OPEN");
      close = Sound.valueOf("BLOCK_ENDERCHEST_CLOSE");
    } catch (final IllegalArgumentException exception) {
      open = Sound.valueOf("BLOCK_ENDER_CHEST_OPEN");
      close = Sound.valueOf("BLOCK_ENDER_CHEST_CLOSE");
    }

    ENDERCHEST_OPEN_SOUND = open;
    ENDERCHEST_CLOSE_SOUND = close;
  }

  private final ConfigAdapter configAdapter;
  private final Path chestsFile;
  private final Map<UUID, SpinningChest> chests = new HashMap<>();

  public ChestMap(final FancyEChestsPlugin plugin) {
    this.configAdapter = plugin.getConfigAdapter();
    this.chestsFile = plugin.getDataFolderPath().resolve("enderchests.json");
  }

  public void load() throws IOException {
    if (Files.notExists(this.chestsFile)) {
      Files.createFile(this.chestsFile);
      try (final BufferedWriter writer = Files.newBufferedWriter(this.chestsFile)) {
        writer.write("[]");
        writer.newLine();
      }
    }
    reload();
  }

  public void reload() throws IOException {
    this.chests.clear();
    try (final BufferedReader reader = Files.newBufferedReader(this.chestsFile)) {
      final Set<SpinningChest> loaded = GSON.fromJson(reader, CHESTS_TYPE);
      this.chests.putAll(loaded.stream()
                               .peek(chest -> chest.setConfigAdapter(this.configAdapter))
                               .peek(SpinningChest::summon)
                               .collect(Collectors.toMap(SpinningChest::getUuid,
                                                         Function.identity())));
    }
  }

  public void save() throws IOException {
    try (final BufferedWriter writer = Files.newBufferedWriter(this.chestsFile)) {
      GSON.toJson(this.chests.values(), CHESTS_TYPE, writer);
    }
  }

  public boolean isPlaceOccupied(Location location) {
    location = location.toBlockLocation();
    location.setX(location.getX() + 0.5);
    location.setY(location.getY() - 1.0);
    location.setZ(location.getZ() + 0.5);
    return location.getWorld()
                   .getNearbyEntitiesByType(ArmorStand.class, location, 0.0625, 0.0625, 0.0625)
                   .stream()
                   .map(ArmorStand::getUniqueId)
                   .anyMatch(this.chests::containsKey);
  }

  public void summon(final UUID uuid) {
    this.chests.get(uuid).summon();
  }

  @Override
  public void clear() {
    this.chests.values().forEach(SpinningChest::unload);
    this.chests.clear();
  }

  @Override
  public int size() {
    return this.chests.size();
  }

  @Override
  public boolean isEmpty() {
    return this.chests.isEmpty();
  }

  @Override
  public boolean containsKey(final Object key) {
    return this.chests.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return this.chests.containsValue(value);
  }

  @Override
  public SpinningChest get(final Object key) {
    return this.chests.get(key);
  }

  @Override
  public @Nullable SpinningChest put(final UUID key, final SpinningChest value) {
    value.setConfigAdapter(this.configAdapter);
    return this.chests.put(key, value);
  }

  @Override
  public SpinningChest remove(final Object key) {
    final SpinningChest chest = this.chests.remove(key);
    chest.kill();
    return chest;
  }

  public SpinningChest unload(final UUID key) {
    final SpinningChest chest = this.chests.get(key);
    chest.unload();
    return chest;
  }

  @Override
  public void putAll(final @NotNull Map<? extends UUID, ? extends SpinningChest> map) {
    map.values().forEach(chest -> chest.setConfigAdapter(this.configAdapter));
    this.chests.putAll(map);
  }

  @Override
  public @NotNull Set<UUID> keySet() {
    return this.chests.keySet();
  }

  @Override
  public @NotNull Collection<SpinningChest> values() {
    return this.chests.values();
  }

  @Override
  public @NotNull Set<Entry<UUID, SpinningChest>> entrySet() {
    return this.chests.entrySet();
  }
}
