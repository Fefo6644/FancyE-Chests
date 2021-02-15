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

package com.github.fefo.fancyechests.model.chest;

import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.config.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpinningChest {

  private static final ItemStack ENDER_CHEST_ITEM = new ItemStack(Material.ENDER_CHEST);

  private final UUID uuid;
  private transient final Location location;
  private final boolean shouldDisappear;
  private long hiddenUntil = Long.MIN_VALUE;
  private transient ConfigAdapter configAdapter;
  private transient ArmorStand stand;
  private transient boolean isBeingUsed = false;

  public SpinningChest(final ConfigAdapter configAdapter, Location location,
                       final boolean shouldDisappear) {
    this.configAdapter = configAdapter;
    location = location.toBlockLocation();

    location.setX(location.getX() + 0.5);
    location.setY(location.getY() - 1.0);
    location.setZ(location.getZ() + 0.5);
    location.setYaw(0.0f);
    location.setPitch(0.0f);

    this.stand = location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
      armorStand.setHeadPose(EulerAngle.ZERO);
      armorStand.setBodyPose(EulerAngle.ZERO);
      armorStand.setLeftArmPose(EulerAngle.ZERO);
      armorStand.setRightArmPose(EulerAngle.ZERO);
      armorStand.setLeftLegPose(EulerAngle.ZERO);
      armorStand.setRightLegPose(EulerAngle.ZERO);
      armorStand.setGravity(false);
      armorStand.setVisible(false);
      armorStand.setBasePlate(false);
      armorStand.setSmall(false);
      armorStand.getEquipment().setHelmet(ENDER_CHEST_ITEM);
    });
    this.uuid = this.stand.getUniqueId();
    this.location = this.stand.getEyeLocation();

    this.shouldDisappear = shouldDisappear;
  }

  public SpinningChest(final UUID uuid, final Location location,
                       final long hiddenUntil, final boolean shouldDisappear) {
    this.uuid = uuid;
    this.location = location;
    this.hiddenUntil = hiddenUntil;
    this.shouldDisappear = shouldDisappear;
  }

  public void setConfigAdapter(final ConfigAdapter configAdapter) {
    this.configAdapter = configAdapter;
  }

  public void summon() {
    this.stand = (ArmorStand) Bukkit.getEntity(this.uuid);
  }

  public Location getLocation() {
    return this.location.clone();
  }

  public long getHiddenUntil() {
    return this.hiddenUntil;
  }

  public void setHiddenUntil(final long hiddenUntil) {
    this.hiddenUntil = hiddenUntil;
    if (hiddenUntil <= System.currentTimeMillis()) {
      this.isBeingUsed = false;
      if (this.stand != null && this.stand.getEquipment().getHelmet() == null) {
        this.stand.getEquipment().setHelmet(ENDER_CHEST_ITEM);
      }
      return;
    }

    if (this.stand != null) {
      this.stand.getEquipment().setHelmet(null);
      this.stand.getWorld()
                .spawnParticle(this.configAdapter.get(ConfigKeys.PARTICLE_TYPE),
                               this.location,
                               this.configAdapter.get(ConfigKeys.PARTICLE_COUNT).intValue(),
                               0.0, 0.0, 0.0,
                               this.configAdapter.get(ConfigKeys.PARTICLE_SPEED).doubleValue());
    }
  }

  public @NotNull UUID getUuid() {
    return this.uuid;
  }

  public void kill() {
    this.stand.remove();
    unload();
  }

  public void unload() {
    this.stand = null;
    this.isBeingUsed = false;
  }

  public boolean rotate(final double radians) {
    if (this.stand == null) {
      return false;
    }
    this.stand.setHeadPose(this.stand.getHeadPose().add(0.0, radians / 2400.0, 0.0));
    return true;
  }

  public void updateUsage() {
    this.isBeingUsed = this.shouldDisappear;
  }

  public boolean isBeingUsed() {
    return this.isBeingUsed;
  }

  public boolean shouldDisappear() {
    return this.shouldDisappear;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SpinningChest)) {
      return false;
    }

    final SpinningChest that = (SpinningChest) other;
    return this.uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return this.uuid.hashCode();
  }
}
