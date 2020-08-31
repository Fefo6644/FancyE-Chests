package me.fefo.fancyechests;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;

public final class Main extends JavaPlugin {
  public static final String YAML_HIDDEN_UNTIL = "hiddenUntil";
  public static final String YAML_SHOULD_DISAPPEAR = "shouldDisappear";

  Sound sound;

  final Hashtable<UUID, SpinnyChest> spinnyChests = new Hashtable<>();
  final Hashtable<UUID, UUID> playersUsingChest = new Hashtable<>();
  final Set<UUID> playersRemovingChest = Collections.synchronizedSet(new HashSet<>());
  File chestsFile;
  YamlConfiguration chestsYaml;
  private double rpm = 45.0;
  long delayMillis = 60000L;
  Particle particle = Particle.END_ROD;
  int particleCount = 100;
  double particleSpeed = 0.1;

  @Override
  public void onEnable() {
    SpinnyChest.setMain(this);

    try {
      saveDefaultConfig();
      chestsFile = new File(getDataFolder(), "enderchests.yml");
      getDataFolder().mkdirs();
      chestsFile.createNewFile();
      reload();
    } catch (IOException e) {
      getLogger().severe("Could not create data file!");
      e.printStackTrace();
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    try {
      sound = Sound.valueOf("BLOCK_ENDERCHEST_OPEN");
    } catch (IllegalArgumentException ignored) {
      sound = Sound.valueOf("BLOCK_ENDER_CHEST_OPEN");
    }

    final CommanderKeen ck = new CommanderKeen(this);
    getCommand("fancyechests").setExecutor(ck);
    getCommand("fancyechests").setTabCompleter(ck);
    getServer().getPluginManager().registerEvents(new ChestInteractListener(this), this);
    getServer().getPluginManager().registerEvents(new ChestCloseListener(this), this);
    getServer().getPluginManager().registerEvents(new ChestRemoveListener(this), this);
    getServer().getPluginManager().registerEvents(new ChunkLoadListener(this), this);
    getServer().getPluginManager().registerEvents(new ChunkUnloadListener(this), this);

    getServer().getScheduler().runTaskTimer(this, () -> {
      for (SpinnyChest sc : spinnyChests.values()) {
        if (sc.rotate(2 * Math.PI * rpm)) {
          sc.getLocation().getWorld().spawnParticle(Particle.PORTAL,
                                                    sc.getLocation(),
                                                    1,
                                                    0.0, 0.0, 0.0,
                                                    0.7);
        } /*else {
          spinnyChests.put(sc.getUUID(),
                           new SpinnyChest(sc.getUUID(),
                                           sc.getHiddenUntil(),
                                           sc.shouldDisappear()));
        }*/
      }
    }, 0L, 1L);

    getServer().getScheduler().runTaskTimer(this, () -> {
      final int yamlHash = chestsYaml.getValues(true).hashCode();
      final long now = Instant.now().toEpochMilli();

      for (SpinnyChest sc : spinnyChests.values()) {
        if (sc.getHiddenUntil() != 0L &&
            sc.getHiddenUntil() <= now) {
          sc.setHiddenUntil(0L);
          chestsYaml.set(sc.getUUID() +
                         String.valueOf(chestsYaml.options().pathSeparator()) +
                         YAML_HIDDEN_UNTIL, 0L);
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
    }, 0L, 20L);
  }

  void reload() {
    reloadConfig();
    rpm = getConfig().getDouble("rpm", 45.0);
    delayMillis = getConfig().getLong("secondsUntilReappearance", 60L) * 1000L;
    particle = Particle.valueOf(getConfig().getString("particleType", "END_ROD"));
    particleCount = getConfig().getInt("particleCount", 100);
    particleSpeed = getConfig().getDouble("particleSpeed", 0.1);

    spinnyChests.clear();
    chestsYaml = YamlConfiguration.loadConfiguration(chestsFile);
    for (String k : chestsYaml.getKeys(false)) {
      if (chestsYaml.isConfigurationSection(k)) {
        final ConfigurationSection cs = chestsYaml.getConfigurationSection(k);
        spinnyChests.put(UUID.fromString(k),
                         new SpinnyChest(UUID.fromString(k),
                                         cs.getLong(YAML_HIDDEN_UNTIL, 0L),
                                         cs.getBoolean(YAML_SHOULD_DISAPPEAR, true)));
      } else {
        final long hiddenUntil = chestsYaml.getLong(k, 0L);
        chestsYaml.set(k, null);
        final ConfigurationSection cs = chestsYaml.createSection(k);
        cs.set(YAML_HIDDEN_UNTIL, hiddenUntil);
        cs.set(YAML_SHOULD_DISAPPEAR, true);
        try {
          chestsYaml.save(chestsFile);
        } catch (IOException ignored) {
        }

        spinnyChests.put(UUID.fromString(k),
                         new SpinnyChest(UUID.fromString(k),
                                         hiddenUntil,
                                         true));
      }
    }
  }
}
