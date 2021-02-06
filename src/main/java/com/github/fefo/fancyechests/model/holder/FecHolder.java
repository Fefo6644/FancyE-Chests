package com.github.fefo.fancyechests.model.holder;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FecHolder {

  private final UUID uuid;
  private Inventory chest = null;
  private int lastHash = Integer.MIN_VALUE;

  public FecHolder(final UUID uuid) {
    this.uuid = uuid;
  }

  public @Nullable Inventory getInventory() {
    return this.chest;
  }

  protected void setInventory(final Inventory chest) {
    this.chest = chest;
    this.lastHash = Lists.newArrayList(chest.getStorageContents()).stream()
                         .filter(Objects::nonNull)
                         .filter(item -> item.getType() != Material.AIR)
                         .collect(Collectors.toList())
                         .hashCode();
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public int getLastHash() {
    return this.lastHash;
  }

  protected void setLastHash(final int hash) {
    this.lastHash = hash;
  }
}
