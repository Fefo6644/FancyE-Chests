package me.fefo.fancyechests;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CommanderKeen<S extends Player> implements TabExecutor {

  private final FancyEChests plugin;
  private final Function<CommandSender, S> transformer;
  private final CommandDispatcher<S> dispatcher = new CommandDispatcher<>();

  public CommanderKeen(final FancyEChests plugin, final Function<CommandSender, S> transformer) {
    this.plugin = plugin;
    this.transformer = transformer;
    final CommandProvider<S> provider = CommandProvider.of(this::version, this::usage, this::reload,
                                                           this::remove, this::removeNearest,
                                                           this::set, this::setPersistent,
                                                           this::teleportNearest, "fec");
    dispatcher.register(provider.getBuilder());
  }

  @Override
  public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
    final String cmd = "fec " + String.join(" ", args);

    if (!(sender instanceof Player)) {
      Message.PLAYERS_ONLY.send(sender);
      return true;
    }

    final ParseResults<S> result = dispatcher.parse(cmd.trim(), transformer.apply(sender));

    try {
      dispatcher.execute(result);
    } catch (CommandSyntaxException exception) {
      usage(result.getContext().build(cmd.trim()));
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
    if (!(sender instanceof Player)) {
      return Collections.emptyList();
    }

    final String cmd = "fec " + String.join(" ", args);
    final ParseResults<S> result = dispatcher.parse(cmd, transformer.apply(sender));

    return dispatcher.getCompletionSuggestions(result).join()
                     .getList().stream()
                     .map(Suggestion::getText)
                     .collect(Collectors.toList());
  }

  private int version(final CommandContext<S> context) {
    final S source = context.getSource();
    Message.VERSION.send(source, plugin.getDescription().getVersion());
    return 1;
  }

  private int usage(final CommandContext<S> context) {
    version(context);
    final S source = context.getSource();

    Message.USAGE_TITLE.send(source);
    for (final String usage : dispatcher.getAllUsage(dispatcher.getRoot(), source, true)) {
      Message.USAGE_COMMAND.send(source, usage);
    }

    return 1;
  }

  private int teleportNearest(final CommandContext<S> context) {
    final S source = context.getSource();
    final UUID nearestChest = getNearestChest(source);

    if (nearestChest != null) {
      source.teleport(plugin.spinnyChests.get(nearestChest).getLocation());
    } else {
      Message.COULDNT_FIND_NEAREST.send(source);
    }

    return 1;
  }

  private int reload(final CommandContext<S> context) {
    plugin.reloadConfig();
    Message.FILES_RELOADED.send(context.getSource());
    return 1;
  }

  private int remove(final CommandContext<S> context) {
    final S source = context.getSource();

    if (plugin.playersRemovingChest.remove(source.getUniqueId())) {
      Message.ACTION_CANCELLED.send(source);
    } else {
      if (plugin.spinnyChests.size() > 0) {
        plugin.playersRemovingChest.add(source.getUniqueId());
        Message.HIT_TO_REMOVE.send(source);
        Message.RUN_TO_CANCEL.send(source);
      } else {
        Message.NO_LOADED_CHESTS.send(source);
      }
    }
    return 1;
  }

  private int removeNearest(final CommandContext<S> context) {
    final S source = context.getSource();
    final UUID nearestChest = getNearestChest(source);

    if (nearestChest != null) {
      final Location chestLocation = plugin.spinnyChests.get(nearestChest).getLocation();
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

      Message.CHEST_REMOVED_AT.send(source, String.valueOf(x), String.valueOf(y), String.valueOf(z));
    } else {
      Message.COULDNT_FIND_NEAREST.send(source);
    }

    return 1;
  }

  private int set(final CommandContext<S> context) {
    final S source = context.getSource();
    createEnderChest(source, true);

    return 1;
  }

  private int setPersistent(final CommandContext<S> context) {
    final S source = context.getSource();
    createEnderChest(source, false);

    return 1;
  }

  private void createEnderChest(final S source, final boolean shouldDisappear) {
    final Location loc = source.getLocation().clone();

    if (SpinnyChest.isPlaceOccupied(plugin, loc)) {
      Message.ALREADY_OCCUPIED.send(source);
      Message.SELECT_ANOTHER_LOCATION.send(source);

    } else {
      final SpinnyChest sc = new SpinnyChest(plugin, loc, shouldDisappear);
      plugin.spinnyChests.put(sc.getUUID(), sc);
      Message.CHEST_PLACED.send(source);

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

  private UUID getNearestChest(final S source) {
    final ChestsSorter sorter = new ChestsSorter(source);

    return source.getWorld()
                 .getEntitiesByClass(ArmorStand.class).stream()
                 .map(Entity::getUniqueId)
                 .filter(plugin.spinnyChests::containsKey)
                 .map(plugin.spinnyChests::get)
                 .min(sorter)
                 .map(SpinnyChest::getUUID).orElse(null);
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
