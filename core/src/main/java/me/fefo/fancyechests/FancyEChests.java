package me.fefo.fancyechests;

import me.fefo.facilites.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FancyEChests extends JavaPlugin {

  public static final String YAML_HIDDEN_UNTIL = "hiddenUntil";
  public static final String YAML_SHOULD_DISAPPEAR = "shouldDisappear";
  public static final Sound SOUND;

  static {
    Sound temp;
    try {
      temp = Sound.valueOf("BLOCK_ENDERCHEST_OPEN");
    } catch (IllegalArgumentException ignored) {
      temp = Sound.valueOf("BLOCK_ENDER_CHEST_OPEN");
    }
    SOUND = temp;
  }

  public final Map<UUID, FecHolder> playerInventories = Collections.synchronizedMap(new Hashtable<>());
  public final Map<UUID, SpinnyChest> spinnyChests = Collections.synchronizedMap(new Hashtable<>());
  public final Map<UUID, UUID> playersUsingChest = Collections.synchronizedMap(new Hashtable<>());
  public final Set<UUID> playersRemovingChest = Collections.synchronizedSet(new HashSet<>());
  public final File chestsFile = new File(getDataFolder(), "enderchests.yml");
  public final File playerDataFolder = new File(getDataFolder(), "playerdata");

  public YamlConfiguration chestsYaml;
  public long delayMillis = 60000L;
  public Particle particle = Particle.END_ROD;
  public int particleCount = 100;
  public double particleSpeed = 0.1;
  private double rpm = 45.0;
  private ChunksListener chunksListener;

  private final TaskUtil scheduler = new TaskUtil(this);

  public FancyEChests() {
    super();

    getDataFolder().mkdirs();
    playerDataFolder.mkdirs();
  }

  public TaskUtil getScheduler() {
    return scheduler;
  }

  @Override
  public void onEnable() {
    try {
      saveDefaultConfig();
      chestsFile.createNewFile();
    } catch (IOException exception) {
      throw new RuntimeException("Could not create data file!");
    }

    Bukkit.getOnlinePlayers().stream()
          .map(Player::getUniqueId)
          .forEach(uuid -> playerInventories.put(uuid, new FecHolder(this, uuid)));
    Bukkit.getOnlinePlayers().forEach(player -> {
      final FecHolder holder = playerInventories.get(player.getUniqueId());
      holder.createInventory(player);
    });

    final PluginCommand command = getCommand("fancyechests");
    if (command == null) {
      throw new RuntimeException();
    }

    try {
      Class.forName("com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource");
      new CommandRegisteredListener<>(this);
    } catch (ClassNotFoundException e) {
      // shrug
    }

    final CommanderKeen<Player> ck = new CommanderKeen<>(this, Player.class::cast);
    command.setExecutor(ck);
    command.setTabCompleter(ck);

    new ChestInteractListener(this);
    new ChestCloseListener(this);
    new ChestRemoveListener(this);
    chunksListener = new ChunksListener(this);
    new PlayerLoginListener(this);
    new PlayerQuitListener(this);

    scheduler.sync(() -> playerInventories.values().forEach(holder -> holder.save(scheduler::async)), 20L * 60L, 20L * 60L);

    scheduler.sync(() -> {
      for (final SpinnyChest sc : spinnyChests.values()) {
        if (sc.rotate(2 * Math.PI * rpm)) {
          sc.getLocation().getWorld().spawnParticle(Particle.PORTAL, sc.getLocation(),
                                                    1, 0.0, 0.0, 0.0, 0.7);
        }
      }
    }, 1L, 1L);

    scheduler.sync(() -> {
      final int yamlHash = chestsYaml.getValues(true).hashCode();
      final long now = Instant.now().toEpochMilli();

      for (final SpinnyChest sc : spinnyChests.values()) {
        if (sc.getHiddenUntil() != 0L && sc.getHiddenUntil() <= now) {
          sc.setHiddenUntil(0L);
          chestsYaml.set(sc.getUUID() + "." + YAML_HIDDEN_UNTIL, 0L);
        }
      }

      if (yamlHash != chestsYaml.getValues(true).hashCode()) {
        try {
          chestsYaml.save(chestsFile);
        } catch (IOException e) {
          getLogger().severe("Could not save data file!");
          e.printStackTrace();
        }
      }
    }, 20L, 20L);

    reloadConfig();
  }

  @Override
  public void onDisable() {
    playerInventories.values().forEach(FecHolder::save);
    playerInventories.clear();
    spinnyChests.clear();
  }

  @Override
  public void reloadConfig() {
    super.reloadConfig();
    rpm = getConfig().getDouble("rpm", 45.0);
    delayMillis = getConfig().getLong("secondsUntilReappearance", 60L) * 1000L;
    particle = Particle.valueOf(getConfig().getString("particleType", "END_ROD"));
    particleCount = getConfig().getInt("particleCount", 100);
    particleSpeed = getConfig().getDouble("particleSpeed", 0.1);

    spinnyChests.clear();
    chestsYaml = YamlConfiguration.loadConfiguration(chestsFile);
    for (final World world : Bukkit.getWorlds()) {
      for (final Chunk chunk : world.getLoadedChunks()) {
        chunksListener.onChunkLoad(new ChunkLoadEvent(chunk, false));
      }
    }
  }
}
