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

package io.github.emilyydev.fancyechests.model.holder;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
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
