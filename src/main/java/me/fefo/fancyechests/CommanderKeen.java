package me.fefo.fancyechests;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.RootCommandNode;
import me.fefo.facilites.ColorFormat;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.fefo.facilites.BrigadierHelper.literal;
import static me.fefo.facilites.BrigadierHelper.pLiteral;

public final class CommanderKeen implements TabExecutor {
  private static final RootCommandNode<Player> ROOT_NODE = new RootCommandNode<>();
  private static final CommandDispatcher<Player> DISPATCHER = new CommandDispatcher<>(ROOT_NODE);
  private static final RootCommandNode<CommandSender> CONSOLE_ROOT_NODE = new RootCommandNode<>();
  private static final CommandDispatcher<CommandSender> CONSOLE_DISPATCHER = new CommandDispatcher<>(CONSOLE_ROOT_NODE);

  private static final Map<String, ParseResults<Player>> RESULTS_CACHE = new HashMap<>();
  private static final Map<String, ParseResults<CommandSender>> CONSOLE_RESULTS_CACHE = new HashMap<>();

  private static FancyEChests plugin;

  static {
    ROOT_NODE.addChild(pLiteral("fec").executes(CommanderKeen::printVersion)
                                      .then(pLiteral("help").executes(CommanderKeen::printUsage))
                                      .then(pLiteral("nearest").executes(CommanderKeen::teleportNearest))
                                      .then(pLiteral("reload").executes(CommanderKeen::reload))
                                      .then(pLiteral("remove").executes(CommanderKeen::remove)
                                                              .then(pLiteral("nearest").executes(CommanderKeen::removeNearest)))
                                      .then(pLiteral("set").executes(CommanderKeen::set))
                                      .then(pLiteral("setpersistent").executes(CommanderKeen::setPersistent))
                                      .build());

    CONSOLE_ROOT_NODE.addChild(literal("fec").executes(CommanderKeen::printVersion)
                                             .then(literal("help").executes(CommanderKeen::printUsage))
                                             .then(literal("reload").executes(CommanderKeen::reload))
                                             .build());
  }

  public CommanderKeen(@NotNull final FancyEChests plugin) {
    CommanderKeen.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull final CommandSender sender,
                           @NotNull final Command command,
                           @NotNull final String alias,
                           @NotNull final String[] args) {
    final String cmd = ("fec " + String.join(" ", args)).trim();

    if (!(sender instanceof Player)) {
      final ParseResults<CommandSender> result = CONSOLE_RESULTS_CACHE.getOrDefault(cmd, CONSOLE_DISPATCHER.parse(cmd, sender));

      try {
        CONSOLE_DISPATCHER.execute(result);
        CONSOLE_RESULTS_CACHE.putIfAbsent(cmd, result);
      } catch (CommandSyntaxException exception) {
        printUsage(result.getContext().build(cmd));
      }

      return true;
    }

    final ParseResults<Player> result = RESULTS_CACHE.getOrDefault(cmd, DISPATCHER.parse(cmd, ((Player) sender)));

    try {
      DISPATCHER.execute(result);
      RESULTS_CACHE.putIfAbsent(cmd, result);
    } catch (CommandSyntaxException exception) {
      printUsage(result.getContext().build(cmd));
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull final CommandSender sender,
                                    @NotNull final Command command,
                                    @NotNull final String alias,
                                    @NotNull final String[] args) {
    final String cmd = "fec " + String.join(" ", args);

    if (sender instanceof Player) {
      final ParseResults<Player> result = RESULTS_CACHE.getOrDefault(cmd, DISPATCHER.parse(cmd, ((Player) sender)));

      return DISPATCHER.getCompletionSuggestions(result)
                       .join()
                       .getList()
                       .stream()
                       .map(Suggestion::getText)
                       .collect(Collectors.toList());
    } else {
      final ParseResults<CommandSender> result = CONSOLE_RESULTS_CACHE.getOrDefault(cmd, CONSOLE_DISPATCHER.parse(cmd, sender));

      return CONSOLE_DISPATCHER.getCompletionSuggestions(result)
                               .join()
                               .getList()
                               .stream()
                               .map(Suggestion::getText)
                               .collect(Collectors.toList());
    }
  }

  private static <S extends CommandSender> int printVersion(final CommandContext<S> context) {
    context.getSource().sendMessage(ColorFormat.format("&3FancyE-Chests &7- &bv"
                                                       + plugin.getDescription().getVersion()));
    return 0;
  }

  private static <S extends CommandSender> int printUsage(final CommandContext<S> context) {
    printVersion(context);

    final S source = context.getSource();

    if (source instanceof Player) {
      source.sendMessage(ColorFormat.format("&cUsages:"));
      source.sendMessage(ColorFormat.format("  &c/fancyechests [help|nearest|reload]"));
      source.sendMessage(ColorFormat.format("  &c/fancyechests remove [nearest]"));
      source.sendMessage(ColorFormat.format("  &c/fancyechests (set|setpersistent)"));
    } else {
      source.sendMessage(ColorFormat.format("&cUsages:"));
      source.sendMessage(ColorFormat.format("  &c/fancyechests [help|reload]"));
    }

    return 0;
  }

  private static int teleportNearest(final CommandContext<Player> context) {
    final Player player = context.getSource();
    final UUID nearestChest = getNearestChest(player);

    if (nearestChest != null) {
      player.teleport(plugin.spinnyChests.get(nearestChest).getLocation());
    } else {
      player.sendMessage(ColorFormat.format("&cCouldn't find the nearest ender chest within loaded chunks in this world"));
    }

    return 0;
  }

  private static <S extends CommandSender> int reload(final CommandContext<S> context) {
    plugin.reloadConfig();
    context.getSource().sendMessage(ColorFormat.format("&bFiles reloaded successfully!"));
    return 0;
  }

  private static int remove(final CommandContext<Player> context) {
    final Player player = context.getSource();

    if (plugin.spinnyChests.size() > 0) {
      if (plugin.playersRemovingChest.remove(player.getUniqueId())) {
        player.sendMessage(ColorFormat.format("&bAction cancelled"));
      } else {
        plugin.playersRemovingChest.add(player.getUniqueId());
        player.sendMessage(ColorFormat.format("&bHit an ender chest to remove it"));
        player.sendMessage(ColorFormat.format("&bRun the command again to cancel"));
      }
    } else {
      player.sendMessage(ColorFormat.format("&cThere are no ender chests to remove"));
    }
    return 0;
  }

  private static int removeNearest(final CommandContext<Player> context) {
    final Player player = context.getSource();
    final UUID nearestChest = getNearestChest(player);

    if (nearestChest != null) {
      final Location chestLocation = plugin.spinnyChests.get(nearestChest).getLocation();
      assert chestLocation != null;
      final int x = chestLocation.getBlockX();
      final int y = chestLocation.getBlockY();
      final int z = chestLocation.getBlockZ();

      plugin.spinnyChests.remove(nearestChest).kill();
      plugin.chestsYaml.set(nearestChest.toString(), null);

      try {
        plugin.chestsYaml.save(plugin.chestsFile);
      } catch (IOException ex) {
        plugin.getLogger().severe("Could not save data file!");
        ex.printStackTrace();
      }

      player.sendMessage(ColorFormat.format("&bEnder chest removed at " +
                                            "&7x:" + x + " y:" + y + " z:" + z));

    } else {
      player.sendMessage(ColorFormat.format("&cCouldn't find the nearest ender chest within loaded chunks in this world"));
    }

    return 0;
  }

  private static int set(final CommandContext<Player> context) {
    final Player player = context.getSource();
    createEnderChest(player, true);

    return 0;
  }

  private static int setPersistent(final CommandContext<Player> context) {
    final Player player = context.getSource();
    createEnderChest(player, false);

    return 0;
  }

  private static void createEnderChest(final Player player, final boolean shouldDisappear) {
    final Location loc = player.getLocation().clone();

    if (SpinnyChest.isPlaceOccupied(loc)) {
      player.sendMessage(ColorFormat.format("&cThis place is already occupied by another ender chest!"));
      player.sendMessage(ColorFormat.format("&cPlease, select another location"));

    } else {
      final SpinnyChest sc = new SpinnyChest(loc, shouldDisappear);
      plugin.spinnyChests.put(sc.getUUID(), sc);
      player.sendMessage(ColorFormat.format("&bEnder chest placed successfully!"));

      final ConfigurationSection cs = plugin.chestsYaml.createSection(sc.getUUID().toString());
      cs.set(FancyEChests.YAML_HIDDEN_UNTIL, 0L);
      cs.set(FancyEChests.YAML_SHOULD_DISAPPEAR, shouldDisappear);

      try {
        plugin.chestsYaml.save(plugin.chestsFile);
      } catch (IOException e) {
        plugin.getLogger().severe("Could not save data file!");
        e.printStackTrace();
      }
    }
  }

  private static UUID getNearestChest(final Player player) {
    return player.getWorld()
                 .getEntitiesByClass(ArmorStand.class)
                 .stream()
                 .map(Entity::getUniqueId)
                 .filter(plugin.spinnyChests::containsKey)
                 .map(plugin.spinnyChests::get)
                 .min(new ChestsSorter(player))
                 .map(SpinnyChest::getUUID)
                 .orElse(null);
  }

  public static void clearCaches() {
    RESULTS_CACHE.clear();
    CONSOLE_RESULTS_CACHE.clear();
  }

  private static final class ChestsSorter implements Comparator<SpinnyChest> {

    private final Entity source;

    public ChestsSorter(final Entity source) {
      this.source = source;
    }

    @Override
    public int compare(final SpinnyChest first, final SpinnyChest second) {
      return Double.compare(source.getLocation().distanceSquared(first.getLocation()),
                            source.getLocation().distanceSquared(second.getLocation()));
    }
  }
}
