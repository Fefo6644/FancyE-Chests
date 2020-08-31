package me.fefo.fancyechests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public final class SpinnyChest {
  private static FancyEChests plugin;
  private static final @NotNull ItemStack enderChest = new ItemStack(Material.ENDER_CHEST);

  private final @NotNull UUID uuid;
  private final ArmorStand as;
  private boolean isBeingUsed = false;
  private final boolean shouldDisappear;
  private long hiddenUntil = 0L;

  static void setPlugin(final FancyEChests plugin) {
    SpinnyChest.plugin = plugin;
  }

  public static boolean isPlaceOccupied(@NotNull final Location loc) {
    Location _loc = loc.clone();
    _loc.setX(_loc.getBlockX() + .5);
    _loc.setY(_loc.getBlockY() - 1.0);
    _loc.setZ(_loc.getBlockZ() + .5);
    final Collection<Entity> nearbyEntities = _loc.getWorld().getNearbyEntities(_loc, .0625, .0625, .0625);

    for (final Entity e : nearbyEntities) {
      if (e.getType() == EntityType.ARMOR_STAND) {
        if (plugin.spinnyChests.containsKey(e.getUniqueId())) {
          return true;
        }
      }
    }
    return false;
  }

  public SpinnyChest(@NotNull final Location loc,
                     final boolean shouldDisappear) {
    loc.setX(loc.getBlockX() + 0.5d);
    loc.setY(loc.getBlockY() - 1.0d);
    loc.setZ(loc.getBlockZ() + 0.5d);
    loc.setYaw(0.0f);
    loc.setPitch(0.0f);
    as = ((ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND));
    as.setHeadPose(EulerAngle.ZERO);
    as.setBodyPose(EulerAngle.ZERO);
    as.setLeftArmPose(EulerAngle.ZERO);
    as.setRightArmPose(EulerAngle.ZERO);
    as.setLeftLegPose(EulerAngle.ZERO);
    as.setRightLegPose(EulerAngle.ZERO);
    as.setGravity(false);
    as.setVisible(false);
    as.setBasePlate(false);
    as.setSmall(false);
    as.getEquipment().setHelmet(enderChest);
    uuid = as.getUniqueId();

    this.shouldDisappear = shouldDisappear;
  }

  public SpinnyChest(@NotNull final UUID uuid,
                     final long hiddenUntil,
                     final boolean shouldDisappear) {
    as = ((ArmorStand) plugin.getServer().getEntity(uuid));
    this.uuid = uuid;
    this.hiddenUntil = hiddenUntil;
    this.shouldDisappear = shouldDisappear;
  }

  public Location getLocation() { return as.getEyeLocation(); }

  public long getHiddenUntil() { return hiddenUntil; }
  public void setHiddenUntil(long hiddenUntil) {
    this.hiddenUntil = hiddenUntil;
    if (hiddenUntil == 0L) {
      isBeingUsed = false;
      if (as == null) {
        return;
      }
      as.getEquipment().setHelmet(enderChest);
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
  public final UUID getUUID() { return uuid; }
  public void kill() { as.remove(); }
  public boolean rotate(final double rad) {
    if (as == null) {
      return false;
    }
    as.setHeadPose(as.getHeadPose()
                     .add(.0,
                          rad / 1200.0,
                          .0));
    return true;
  }

  public void updateUsage() { isBeingUsed = shouldDisappear; }
  public boolean isBeingUsed() { return isBeingUsed; }

  public boolean shouldDisappear() { return shouldDisappear; }

  @Override
  public boolean equals(@Nullable Object o) {
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
  public int hashCode() { return Objects.hash(as.getUniqueId()); }
}
