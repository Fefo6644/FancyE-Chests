//
// FancyE-Chests - Provide your players with isolated, fancy spinning ender chests.
// Copyright (C) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

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
