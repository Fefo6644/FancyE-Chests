//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021 emilyy-dev
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package io.github.emilyydev.fancyechests.model.chest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.emilyydev.fancyechests.FancyEChestsPlugin;
import io.github.emilyydev.fancyechests.config.ConfigAdapter;
import io.github.emilyydev.fancyechests.util.adapter.LocationAdapter;
import io.github.emilyydev.fancyechests.util.adapter.SpinningChestAdapter;
import io.github.emilyydev.fancyechests.util.adapter.WorldAdapter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
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

  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeHierarchyAdapter(World.class, WorldAdapter.ADAPTER)
          .registerTypeAdapter(Location.class, LocationAdapter.ADAPTER)
          .registerTypeAdapter(SpinningChest.class, SpinningChestAdapter.ADAPTER)
          .setPrettyPrinting()
          .create();
  private static final Type CHESTS_TYPE = new TypeToken<Set<SpinningChest>>() { }.getType();

  private final JavaPlugin plugin;
  private final ConfigAdapter configAdapter;
  private final Path chestsFile;
  private final Map<UUID, SpinningChest> chests = new HashMap<>();

  public ChestMap(final FancyEChestsPlugin plugin) {
    this.plugin = plugin;
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
      if (loaded != null) {
        this.chests.putAll(loaded.stream()
                                 .peek(chest -> chest.setConfigAdapter(this.configAdapter))
                                 .peek(SpinningChest::summon)
                                 .collect(Collectors.toMap(SpinningChest::getUuid,
                                                           Function.identity())));
      } else {
        this.plugin.getLogger().warning("There was an error while reading " + this.chestsFile);
      }
    }
  }

  public void save() throws IOException {
    try (final BufferedWriter writer = Files.newBufferedWriter(this.chestsFile)) {
      GSON.toJson(this.chests.values(), CHESTS_TYPE, writer);
    }
  }

  public boolean isPlaceOccupied(Location location) {
    location = location.getBlock().getLocation().clone();
    location.setX(location.getX() + 0.5);
    location.setY(location.getY() - 1.0);
    location.setZ(location.getZ() + 0.5);
    return location.getWorld()
                   .getNearbyEntities(location, 0.0625, 0.0625, 0.0625)
                   .stream()
                   .filter(ArmorStand.class::isInstance)
                   .map(Entity::getUniqueId)
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
