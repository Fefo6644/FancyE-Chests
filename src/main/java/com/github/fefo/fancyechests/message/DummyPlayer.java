package com.github.fefo.fancyechests.message;

import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Achievement;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.Sign;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class DummyPlayer implements Player {

  public static final DummyPlayer INSTANCE = new DummyPlayer();

  private DummyPlayer() { }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public void setDisplayName(final String name) {

  }

  @Override
  public String getPlayerListName() {
    return null;
  }

  @Override
  public void setPlayerListName(final String name) {

  }

  @Override
  public void setCompassTarget(final Location loc) {

  }

  @Override
  public Location getCompassTarget() {
    return null;
  }

  @Override
  public InetSocketAddress getAddress() {
    return null;
  }

  @Override
  public int getProtocolVersion() {
    return 0;
  }

  @Nullable
  @Override
  public InetSocketAddress getVirtualHost() {
    return null;
  }

  @Override
  public boolean isConversing() {
    return false;
  }

  @Override
  public void acceptConversationInput(final String input) {

  }

  @Override
  public boolean beginConversation(final Conversation conversation) {
    return false;
  }

  @Override
  public void abandonConversation(final Conversation conversation) {

  }

  @Override
  public void abandonConversation(final Conversation conversation,
                                  final ConversationAbandonedEvent details) {

  }

  @Override
  public void sendRawMessage(final String message) {

  }

  @Override
  public void kickPlayer(final String message) {

  }

  @Override
  public void chat(final String msg) {

  }

  @Override
  public boolean performCommand(final String command) {
    return false;
  }

  @Override
  public boolean isSneaking() {
    return false;
  }

  @Override
  public void setSneaking(final boolean sneak) {

  }

  @Override
  public boolean isSprinting() {
    return false;
  }

  @Override
  public void setSprinting(final boolean sprinting) {

  }

  @Override
  public void saveData() {

  }

  @Override
  public void loadData() {

  }

  @Override
  public void setSleepingIgnored(final boolean isSleeping) {

  }

  @Override
  public boolean isSleepingIgnored() {
    return false;
  }

  @Override
  public void playNote(final Location loc, final byte instrument, final byte note) {

  }

  @Override
  public void playNote(final Location loc, final Instrument instrument, final Note note) {

  }

  @Override
  public void playSound(final Location location, final Sound sound, final float volume,
                        final float pitch) {

  }

  @Override
  public void playSound(final Location location, final String sound, final float volume,
                        final float pitch) {

  }

  @Override
  public void playSound(final Location location, final Sound sound, final SoundCategory category,
                        final float volume,
                        final float pitch) {

  }

  @Override
  public void playSound(final Location location, final String sound, final SoundCategory category,
                        final float volume,
                        final float pitch) {

  }

  @Override
  public void stopSound(final Sound sound) {

  }

  @Override
  public void stopSound(final String sound) {

  }

  @Override
  public void stopSound(final Sound sound, final SoundCategory category) {

  }

  @Override
  public void stopSound(final String sound, final SoundCategory category) {

  }

  @Override
  public void playEffect(final Location loc, final Effect effect, final int data) {

  }

  @Override
  public <T> void playEffect(final Location loc, final Effect effect, final T data) {

  }

  @Override
  public void sendBlockChange(final Location loc, final Material material, final byte data) {

  }

  @Override
  public boolean sendChunkChange(final Location loc, final int sx, final int sy, final int sz,
                                 final byte[] data) {
    return false;
  }

  @Override
  public void sendBlockChange(final Location loc, final int material, final byte data) {

  }

  @Override
  public void sendSignChange(final Location loc, final String[] lines) throws
                                                                       IllegalArgumentException {

  }

  @Override
  public void sendMap(final MapView map) {

  }

  @Override
  public void sendActionBar(final String message) {

  }

  @Override
  public void sendActionBar(final char alternateChar, final String message) {

  }

  @Override
  public void setPlayerListHeaderFooter(final BaseComponent[] header,
                                        final BaseComponent[] footer) {

  }

  @Override
  public void setPlayerListHeaderFooter(final BaseComponent header, final BaseComponent footer) {

  }

  @Override
  public void setTitleTimes(final int fadeInTicks, final int stayTicks, final int fadeOutTicks) {

  }

  @Override
  public void setSubtitle(final BaseComponent[] subtitle) {

  }

  @Override
  public void setSubtitle(final BaseComponent subtitle) {

  }

  @Override
  public void showTitle(final BaseComponent[] title) {

  }

  @Override
  public void showTitle(final BaseComponent title) {

  }

  @Override
  public void showTitle(final BaseComponent[] title, final BaseComponent[] subtitle,
                        final int fadeInTicks,
                        final int stayTicks, final int fadeOutTicks) {

  }

  @Override
  public void showTitle(final BaseComponent title, final BaseComponent subtitle,
                        final int fadeInTicks, final int stayTicks,
                        final int fadeOutTicks) {

  }

  @Override
  public void sendTitle(final Title title) {

  }

  @Override
  public void updateTitle(final Title title) {

  }

  @Override
  public void hideTitle() {

  }

  @Override
  public void updateInventory() {

  }

  @Override
  public void awardAchievement(final Achievement achievement) {

  }

  @Override
  public void removeAchievement(final Achievement achievement) {

  }

  @Override
  public boolean hasAchievement(final Achievement achievement) {
    return false;
  }

  @Override
  public void incrementStatistic(final Statistic statistic) throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(final Statistic statistic) throws IllegalArgumentException {

  }

  @Override
  public void incrementStatistic(final Statistic statistic, final int amount) throws
                                                                              IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(final Statistic statistic, final int amount) throws
                                                                              IllegalArgumentException {

  }

  @Override
  public void setStatistic(final Statistic statistic, final int newValue) throws
                                                                          IllegalArgumentException {

  }

  @Override
  public int getStatistic(final Statistic statistic) throws IllegalArgumentException {
    return 0;
  }

  @Override
  public void incrementStatistic(final Statistic statistic, final Material material) throws
                                                                                     IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(final Statistic statistic, final Material material) throws
                                                                                     IllegalArgumentException {

  }

  @Override
  public int getStatistic(final Statistic statistic, final Material material) throws
                                                                              IllegalArgumentException {
    return 0;
  }

  @Override
  public void incrementStatistic(final Statistic statistic, final Material material,
                                 final int amount) throws
                                                   IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(final Statistic statistic, final Material material,
                                 final int amount) throws
                                                   IllegalArgumentException {

  }

  @Override
  public void setStatistic(final Statistic statistic, final Material material,
                           final int newValue) throws
                                               IllegalArgumentException {

  }

  @Override
  public void incrementStatistic(final Statistic statistic, final EntityType entityType) throws
                                                                                         IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(final Statistic statistic, final EntityType entityType) throws
                                                                                         IllegalArgumentException {

  }

  @Override
  public int getStatistic(final Statistic statistic, final EntityType entityType) throws
                                                                                  IllegalArgumentException {
    return 0;
  }

  @Override
  public void incrementStatistic(final Statistic statistic, final EntityType entityType,
                                 final int amount) throws
                                                   IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(final Statistic statistic, final EntityType entityType,
                                 final int amount) {

  }

  @Override
  public void setStatistic(final Statistic statistic, final EntityType entityType,
                           final int newValue) {

  }

  @Override
  public void setPlayerTime(final long time, final boolean relative) {

  }

  @Override
  public long getPlayerTime() {
    return 0;
  }

  @Override
  public long getPlayerTimeOffset() {
    return 0;
  }

  @Override
  public boolean isPlayerTimeRelative() {
    return false;
  }

  @Override
  public void resetPlayerTime() {

  }

  @Override
  public void setPlayerWeather(final WeatherType type) {

  }

  @Override
  public WeatherType getPlayerWeather() {
    return null;
  }

  @Override
  public void resetPlayerWeather() {

  }

  @Override
  public void giveExp(final int amount, final boolean applyMending) {

  }

  @Override
  public int applyMending(final int amount) {
    return 0;
  }

  @Override
  public void giveExpLevels(final int amount) {

  }

  @Override
  public float getExp() {
    return 0;
  }

  @Override
  public void setExp(final float exp) {

  }

  @Override
  public int getLevel() {
    return 0;
  }

  @Override
  public void setLevel(final int level) {

  }

  @Override
  public int getTotalExperience() {
    return 0;
  }

  @Override
  public void setTotalExperience(final int exp) {

  }

  @Override
  public float getExhaustion() {
    return 0;
  }

  @Override
  public void setExhaustion(final float value) {

  }

  @Override
  public float getSaturation() {
    return 0;
  }

  @Override
  public void setSaturation(final float value) {

  }

  @Override
  public int getFoodLevel() {
    return 0;
  }

  @Override
  public void setFoodLevel(final int value) {

  }

  @Override
  public boolean isOnline() {
    return false;
  }

  @Override
  public boolean isBanned() {
    return false;
  }

  @Override
  public boolean isWhitelisted() {
    return false;
  }

  @Override
  public void setWhitelisted(final boolean value) {

  }

  @Override
  public Player getPlayer() {
    return null;
  }

  @Override
  public long getFirstPlayed() {
    return 0;
  }

  @Override
  public long getLastPlayed() {
    return 0;
  }

  @Override
  public boolean hasPlayedBefore() {
    return false;
  }

  @Override
  public Location getBedSpawnLocation() {
    return null;
  }

  @Override
  public void setBedSpawnLocation(final Location location) {

  }

  @Override
  public void setBedSpawnLocation(final Location location, final boolean force) {

  }

  @Override
  public boolean getAllowFlight() {
    return false;
  }

  @Override
  public void setAllowFlight(final boolean flight) {

  }

  @Override
  public void hidePlayer(final Player player) {

  }

  @Override
  public void hidePlayer(final Plugin plugin, final Player player) {

  }

  @Override
  public void showPlayer(final Player player) {

  }

  @Override
  public void showPlayer(final Plugin plugin, final Player player) {

  }

  @Override
  public boolean canSee(final Player player) {
    return false;
  }

  @Override
  public boolean isFlying() {
    return false;
  }

  @Override
  public void setFlying(final boolean value) {

  }

  @Override
  public void setFlySpeed(final float value) throws IllegalArgumentException {

  }

  @Override
  public void setWalkSpeed(final float value) throws IllegalArgumentException {

  }

  @Override
  public float getFlySpeed() {
    return 0;
  }

  @Override
  public float getWalkSpeed() {
    return 0;
  }

  @Override
  public void setTexturePack(final String url) {

  }

  @Override
  public void setResourcePack(final String url) {

  }

  @Override
  public void setResourcePack(final String url, final byte[] hash) {

  }

  @Override
  public Scoreboard getScoreboard() {
    return null;
  }

  @Override
  public void setScoreboard(final Scoreboard scoreboard) throws
                                                         IllegalArgumentException,
                                                         IllegalStateException {

  }

  @Override
  public boolean isHealthScaled() {
    return false;
  }

  @Override
  public void setHealthScaled(final boolean scale) {

  }

  @Override
  public void setHealthScale(final double scale) throws IllegalArgumentException {

  }

  @Override
  public double getHealthScale() {
    return 0;
  }

  @Override
  public Entity getSpectatorTarget() {
    return null;
  }

  @Override
  public void setSpectatorTarget(final Entity entity) {

  }

  @Override
  public void sendTitle(final String title, final String subtitle) {

  }

  @Override
  public void sendTitle(final String title, final String subtitle, final int fadeIn, final int stay,
                        final int fadeOut) {

  }

  @Override
  public void resetTitle() {

  }

  @Override
  public void spawnParticle(final Particle particle, final Location location, final int count) {

  }

  @Override
  public void spawnParticle(final Particle particle, final double x, final double y, final double z,
                            final int count) {

  }

  @Override
  public <T> void spawnParticle(final Particle particle, final Location location, final int count,
                                final T data) {

  }

  @Override
  public <T> void spawnParticle(final Particle particle, final double x, final double y,
                                final double z, final int count,
                                final T data) {

  }

  @Override
  public void spawnParticle(final Particle particle, final Location location, final int count,
                            final double offsetX,
                            final double offsetY, final double offsetZ) {

  }

  @Override
  public void spawnParticle(final Particle particle, final double x, final double y, final double z,
                            final int count,
                            final double offsetX, final double offsetY, final double offsetZ) {

  }

  @Override
  public <T> void spawnParticle(final Particle particle, final Location location, final int count,
                                final double offsetX,
                                final double offsetY, final double offsetZ, final T data) {

  }

  @Override
  public <T> void spawnParticle(final Particle particle, final double x, final double y,
                                final double z, final int count,
                                final double offsetX, final double offsetY, final double offsetZ,
                                final T data) {

  }

  @Override
  public void spawnParticle(final Particle particle, final Location location, final int count,
                            final double offsetX,
                            final double offsetY, final double offsetZ, final double extra) {

  }

  @Override
  public void spawnParticle(final Particle particle, final double x, final double y, final double z,
                            final int count,
                            final double offsetX, final double offsetY, final double offsetZ,
                            final double extra) {

  }

  @Override
  public <T> void spawnParticle(final Particle particle, final Location location, final int count,
                                final double offsetX,
                                final double offsetY, final double offsetZ, final double extra,
                                final T data) {

  }

  @Override
  public <T> void spawnParticle(final Particle particle, final double x, final double y,
                                final double z, final int count,
                                final double offsetX, final double offsetY, final double offsetZ,
                                final double extra,
                                final T data) {

  }

  @Override
  public AdvancementProgress getAdvancementProgress(final Advancement advancement) {
    return null;
  }

  @Override
  public String getLocale() {
    return null;
  }

  @Override
  public boolean getAffectsSpawning() {
    return false;
  }

  @Override
  public void setAffectsSpawning(final boolean affects) {

  }

  @Override
  public int getViewDistance() {
    return 0;
  }

  @Override
  public void setViewDistance(final int viewDistance) {

  }

  @Override
  public void setResourcePack(final String url, final String hash) {

  }

  @Override
  public PlayerResourcePackStatusEvent.Status getResourcePackStatus() {
    return null;
  }

  @Override
  public String getResourcePackHash() {
    return null;
  }

  @Override
  public boolean hasResourcePack() {
    return false;
  }

  @Override
  public PlayerProfile getPlayerProfile() {
    return null;
  }

  @Override
  public void setPlayerProfile(final PlayerProfile profile) {

  }

  @Override
  public Location getLocation() {
    return new Location(null, 0.0, 0.0, 0.0);
  }

  @Override
  public Location getLocation(final Location loc) {
    return null;
  }

  @Override
  public void setVelocity(final Vector velocity) {

  }

  @Override
  public Vector getVelocity() {
    return null;
  }

  @Override
  public double getHeight() {
    return 0;
  }

  @Override
  public double getWidth() {
    return 0;
  }

  @Override
  public boolean isOnGround() {
    return false;
  }

  @Override
  public World getWorld() {
    return null;
  }

  @Override
  public boolean teleport(final Location location) {
    return false;
  }

  @Override
  public boolean teleport(final Location location, final PlayerTeleportEvent.TeleportCause cause) {
    return false;
  }

  @Override
  public boolean teleport(final Entity destination) {
    return false;
  }

  @Override
  public boolean teleport(final Entity destination, final PlayerTeleportEvent.TeleportCause cause) {
    return false;
  }

  @Override
  public List<Entity> getNearbyEntities(final double x, final double y, final double z) {
    return null;
  }

  @Override
  public int getEntityId() {
    return 0;
  }

  @Override
  public int getFireTicks() {
    return 0;
  }

  @Override
  public int getMaxFireTicks() {
    return 0;
  }

  @Override
  public void setFireTicks(final int ticks) {

  }

  @Override
  public void remove() {

  }

  @Override
  public boolean isDead() {
    return false;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public void sendMessage(final String message) {

  }

  @Override
  public void sendMessage(final String[] messages) {

  }

  @Override
  public Server getServer() {
    return null;
  }

  @Override
  public Entity getPassenger() {
    return null;
  }

  @Override
  public boolean setPassenger(final Entity passenger) {
    return false;
  }

  @Override
  public List<Entity> getPassengers() {
    return null;
  }

  @Override
  public boolean addPassenger(final Entity passenger) {
    return false;
  }

  @Override
  public boolean removePassenger(final Entity passenger) {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean eject() {
    return false;
  }

  @Override
  public float getFallDistance() {
    return 0;
  }

  @Override
  public void setFallDistance(final float distance) {

  }

  @Override
  public void setLastDamageCause(final EntityDamageEvent event) {

  }

  @Override
  public EntityDamageEvent getLastDamageCause() {
    return null;
  }

  @Override
  public UUID getUniqueId() {
    return null;
  }

  @Override
  public int getTicksLived() {
    return 0;
  }

  @Override
  public void setTicksLived(final int value) {

  }

  @Override
  public void playEffect(final EntityEffect type) {

  }

  @Override
  public EntityType getType() {
    return null;
  }

  @Override
  public boolean isInsideVehicle() {
    return false;
  }

  @Override
  public boolean leaveVehicle() {
    return false;
  }

  @Override
  public Entity getVehicle() {
    return null;
  }

  @Override
  public void setCustomNameVisible(final boolean flag) {

  }

  @Override
  public boolean isCustomNameVisible() {
    return false;
  }

  @Override
  public void setGlowing(final boolean flag) {

  }

  @Override
  public boolean isGlowing() {
    return false;
  }

  @Override
  public void setInvulnerable(final boolean flag) {

  }

  @Override
  public boolean isInvulnerable() {
    return false;
  }

  @Override
  public boolean isSilent() {
    return false;
  }

  @Override
  public void setSilent(final boolean flag) {

  }

  @Override
  public boolean hasGravity() {
    return false;
  }

  @Override
  public void setGravity(final boolean gravity) {

  }

  @Override
  public int getPortalCooldown() {
    return 0;
  }

  @Override
  public void setPortalCooldown(final int cooldown) {

  }

  @Override
  public Set<String> getScoreboardTags() {
    return null;
  }

  @Override
  public boolean addScoreboardTag(final String tag) {
    return false;
  }

  @Override
  public boolean removeScoreboardTag(final String tag) {
    return false;
  }

  @Override
  public PistonMoveReaction getPistonMoveReaction() {
    return null;
  }

  @Override
  public Spigot spigot() {
    return null;
  }

  @Override
  public Location getOrigin() {
    return null;
  }

  @Override
  public boolean fromMobSpawner() {
    return false;
  }

  @Override
  public Chunk getChunk() {
    return null;
  }

  @Override
  public Map<String, Object> serialize() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public PlayerInventory getInventory() {
    return null;
  }

  @Override
  public Inventory getEnderChest() {
    return null;
  }

  @Override
  public MainHand getMainHand() {
    return null;
  }

  @Override
  public boolean setWindowProperty(final InventoryView.Property prop, final int value) {
    return false;
  }

  @Override
  public InventoryView getOpenInventory() {
    return null;
  }

  @Override
  public InventoryView openInventory(final Inventory inventory) {
    return null;
  }

  @Override
  public InventoryView openWorkbench(final Location location, final boolean force) {
    return null;
  }

  @Override
  public InventoryView openEnchanting(final Location location, final boolean force) {
    return null;
  }

  @Override
  public void openInventory(final InventoryView inventory) {

  }

  @Override
  public InventoryView openMerchant(final Villager trader, final boolean force) {
    return null;
  }

  @Override
  public InventoryView openMerchant(final Merchant merchant, final boolean force) {
    return null;
  }

  @Override
  public void closeInventory() {

  }

  @Override
  public void closeInventory(final InventoryCloseEvent.Reason reason) {

  }

  @Override
  public ItemStack getItemInHand() {
    return null;
  }

  @Override
  public void setItemInHand(final ItemStack item) {

  }

  @Override
  public ItemStack getItemOnCursor() {
    return null;
  }

  @Override
  public void setItemOnCursor(final ItemStack item) {

  }

  @Override
  public boolean hasCooldown(final Material material) {
    return false;
  }

  @Override
  public int getCooldown(final Material material) {
    return 0;
  }

  @Override
  public void setCooldown(final Material material, final int ticks) {

  }

  @Override
  public boolean isSleeping() {
    return false;
  }

  @Override
  public int getSleepTicks() {
    return 0;
  }

  @Override
  public GameMode getGameMode() {
    return null;
  }

  @Override
  public void setGameMode(final GameMode mode) {

  }

  @Override
  public boolean isBlocking() {
    return false;
  }

  @Override
  public double getEyeHeight() {
    return 0;
  }

  @Override
  public double getEyeHeight(final boolean ignorePose) {
    return 0;
  }

  @Override
  public Location getEyeLocation() {
    return null;
  }

  @Override
  public List<Block> getLineOfSight(final Set<Material> transparent, final int maxDistance) {
    return null;
  }

  @Override
  public Block getTargetBlock(final Set<Material> transparent, final int maxDistance) {
    return null;
  }

  @Override
  public List<Block> getLastTwoTargetBlocks(final Set<Material> transparent,
                                            final int maxDistance) {
    return null;
  }

  @Override
  public int getRemainingAir() {
    return 0;
  }

  @Override
  public void setRemainingAir(final int ticks) {

  }

  @Override
  public int getMaximumAir() {
    return 0;
  }

  @Override
  public void setMaximumAir(final int ticks) {

  }

  @Override
  public int getMaximumNoDamageTicks() {
    return 0;
  }

  @Override
  public void setMaximumNoDamageTicks(final int ticks) {

  }

  @Override
  public double getLastDamage() {
    return 0;
  }

  @Override
  public void setLastDamage(final double damage) {

  }

  @Override
  public int getNoDamageTicks() {
    return 0;
  }

  @Override
  public void setNoDamageTicks(final int ticks) {

  }

  @Override
  public Player getKiller() {
    return null;
  }

  @Override
  public void setKiller(@Nullable final Player killer) {

  }

  @Override
  public boolean addPotionEffect(final PotionEffect effect) {
    return false;
  }

  @Override
  public boolean addPotionEffect(final PotionEffect effect, final boolean force) {
    return false;
  }

  @Override
  public boolean addPotionEffects(final Collection<PotionEffect> effects) {
    return false;
  }

  @Override
  public boolean hasPotionEffect(final PotionEffectType type) {
    return false;
  }

  @Override
  public PotionEffect getPotionEffect(final PotionEffectType type) {
    return null;
  }

  @Override
  public void removePotionEffect(final PotionEffectType type) {

  }

  @Override
  public Collection<PotionEffect> getActivePotionEffects() {
    return null;
  }

  @Override
  public boolean hasLineOfSight(final Entity other) {
    return false;
  }

  @Override
  public boolean getRemoveWhenFarAway() {
    return false;
  }

  @Override
  public void setRemoveWhenFarAway(final boolean remove) {

  }

  @Override
  public EntityEquipment getEquipment() {
    return null;
  }

  @Override
  public void setCanPickupItems(final boolean pickup) {

  }

  @Override
  public boolean getCanPickupItems() {
    return false;
  }

  @Override
  public boolean isLeashed() {
    return false;
  }

  @Override
  public Entity getLeashHolder() throws IllegalStateException {
    return null;
  }

  @Override
  public boolean setLeashHolder(final Entity holder) {
    return false;
  }

  @Override
  public boolean isGliding() {
    return false;
  }

  @Override
  public void setGliding(final boolean gliding) {

  }

  @Override
  public void setAI(final boolean ai) {

  }

  @Override
  public boolean hasAI() {
    return false;
  }

  @Override
  public void setCollidable(final boolean collidable) {

  }

  @Override
  public boolean isCollidable() {
    return false;
  }

  @Override
  public int getArrowsStuck() {
    return 0;
  }

  @Override
  public void setArrowsStuck(final int arrows) {

  }

  @Override
  public int getShieldBlockingDelay() {
    return 0;
  }

  @Override
  public void setShieldBlockingDelay(final int delay) {

  }

  @Override
  public ItemStack getActiveItem() {
    return null;
  }

  @Override
  public int getItemUseRemainingTime() {
    return 0;
  }

  @Override
  public int getHandRaisedTime() {
    return 0;
  }

  @Override
  public boolean isHandRaised() {
    return false;
  }

  @Override
  public int getExpToLevel() {
    return 0;
  }

  @Override
  public Entity releaseLeftShoulderEntity() {
    return null;
  }

  @Override
  public Entity releaseRightShoulderEntity() {
    return null;
  }

  @Override
  public Entity getShoulderEntityLeft() {
    return null;
  }

  @Override
  public void setShoulderEntityLeft(final Entity entity) {

  }

  @Override
  public Entity getShoulderEntityRight() {
    return null;
  }

  @Override
  public void setShoulderEntityRight(final Entity entity) {

  }

  @Override
  public void openSign(final Sign sign) {

  }

  @Override
  public AttributeInstance getAttribute(final Attribute attribute) {
    return null;
  }

  @Override
  public void damage(final double amount) {

  }

  @Override
  public void damage(final double amount, final Entity source) {

  }

  @Override
  public double getHealth() {
    return 0;
  }

  @Override
  public void setHealth(final double health) {

  }

  @Override
  public double getMaxHealth() {
    return 0;
  }

  @Override
  public void setMaxHealth(final double health) {

  }

  @Override
  public void resetMaxHealth() {

  }

  @Override
  public String getCustomName() {
    return null;
  }

  @Override
  public void setCustomName(final String name) {

  }

  @Override
  public void setMetadata(final String metadataKey, final MetadataValue newMetadataValue) {

  }

  @Override
  public List<MetadataValue> getMetadata(final String metadataKey) {
    return null;
  }

  @Override
  public boolean hasMetadata(final String metadataKey) {
    return false;
  }

  @Override
  public void removeMetadata(final String metadataKey, final Plugin owningPlugin) {

  }

  @Override
  public boolean isPermissionSet(final String name) {
    return false;
  }

  @Override
  public boolean isPermissionSet(final Permission perm) {
    return false;
  }

  @Override
  public boolean hasPermission(final String name) {
    return false;
  }

  @Override
  public boolean hasPermission(final Permission perm) {
    return false;
  }

  @Override
  public PermissionAttachment addAttachment(final Plugin plugin, final String name,
                                            final boolean value) {
    return null;
  }

  @Override
  public PermissionAttachment addAttachment(final Plugin plugin) {
    return null;
  }

  @Override
  public PermissionAttachment addAttachment(final Plugin plugin, final String name,
                                            final boolean value, final int ticks) {
    return null;
  }

  @Override
  public PermissionAttachment addAttachment(final Plugin plugin, final int ticks) {
    return null;
  }

  @Override
  public void removeAttachment(final PermissionAttachment attachment) {

  }

  @Override
  public void recalculatePermissions() {

  }

  @Override
  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    return null;
  }

  @Override
  public boolean isOp() {
    return false;
  }

  @Override
  public void setOp(final boolean value) {

  }

  @Override
  public void sendPluginMessage(final Plugin source, final String channel, final byte[] message) {

  }

  @Override
  public Set<String> getListeningPluginChannels() {
    return null;
  }

  @Override
  public <T extends Projectile> T launchProjectile(final Class<? extends T> projectile) {
    return null;
  }

  @Override
  public <T extends Projectile> T launchProjectile(final Class<? extends T> projectile,
                                                   final Vector velocity) {
    return null;
  }
}
