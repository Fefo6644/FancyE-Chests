package me.fefo.fancyechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public final class SpinnyChest {

  private static final ItemStack ITEM_STACK = new ItemStack(Material.ENDER_CHEST);

  public static boolean isPlaceOccupied(final FancyEChests plugin, @NotNull final Location location) {
    final Location loc = location.clone();
    loc.setX(loc.getBlockX() + .5);
    loc.setY(loc.getBlockY() - 1.0);
    loc.setZ(loc.getBlockZ() + .5);
    final Collection<ArmorStand> nearbyEntities = loc.getWorld().getNearbyEntitiesByType(ArmorStand.class, loc, 0.0625, 0.0625, 0.0625);

    for (final ArmorStand armorStand : nearbyEntities) {
      if (plugin.spinnyChests.containsKey(armorStand.getUniqueId())) {
        return true;
      }
    }
    return false;
  }

  private final FancyEChests plugin;
  private final UUID uuid;
  private final ArmorStand as;
  private final boolean shouldDisappear;
  private boolean isBeingUsed = false;
  private long hiddenUntil = 0L;

  public SpinnyChest(final FancyEChests plugin, @NotNull final Location loc, final boolean shouldDisappear) {
    this.plugin = plugin;

    loc.setX(loc.getBlockX() + 0.5);
    loc.setY(loc.getBlockY() - 1.0);
    loc.setZ(loc.getBlockZ() + 0.5);
    loc.setYaw(0.0f);
    loc.setPitch(0.0f);

    as = loc.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
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
      armorStand.getEquipment().setHelmet(ITEM_STACK);
    });
    uuid = as.getUniqueId();

    this.shouldDisappear = shouldDisappear;
  }

  public SpinnyChest(final FancyEChests plugin, @NotNull final UUID uuid, final long hiddenUntil, final boolean shouldDisappear) {
    this.plugin = plugin;
    as = (ArmorStand) Bukkit.getEntity(uuid);
    this.uuid = uuid;
    this.hiddenUntil = hiddenUntil;
    this.shouldDisappear = shouldDisappear;
  }

  public Location getLocation() {
    return as.getEyeLocation();
  }

  public long getHiddenUntil() {
    return hiddenUntil;
  }

  public void setHiddenUntil(long hiddenUntil) {
    this.hiddenUntil = hiddenUntil;
    if (hiddenUntil == 0L) {
      isBeingUsed = false;
      if (as == null) {
        return;
      }
      as.getEquipment().setHelmet(ITEM_STACK);
    } else {
      if (as == null) {
        return;
      }
      as.getEquipment().setHelmet(null);
      as.getWorld().spawnParticle(plugin.particle,
                                  as.getEyeLocation(),
                                  plugin.particleCount,
                                  .0, .0, .0,
                                  plugin.particleSpeed);
    }
  }

  @NotNull
  public final UUID getUUID() {
    return uuid;
  }

  public void kill() {
    as.remove();
  }

  public boolean rotate(final double rad) {
    if (as == null) {
      return false;
    }
    as.setHeadPose(as.getHeadPose().add(0.0, rad / 1200.0, 0.0));
    return true;
  }

  public void updateUsage() {
    isBeingUsed = shouldDisappear;
  }

  public boolean isBeingUsed() {
    return isBeingUsed;
  }

  public boolean shouldDisappear() {
    return shouldDisappear;
  }

  @Override
  public boolean equals(@Nullable final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpinnyChest that = (SpinnyChest) o;
    return as.getUniqueId().equals(that.as.getUniqueId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(as.getUniqueId());
  }
}
