package me.fefo.fancyechests;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import me.fefo.facilites.VariousUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

  public static final BaseComponent[] PLAYERS_ONLY = TextComponent.fromLegacyText("§cOnly players can run this command!");
  public static final BaseComponent[] USAGE = TextComponent.fromLegacyText("§cUsage:");
  public static final BaseComponent[] FILES_RELOADED = TextComponent.fromLegacyText("§bFiles reloaded successfully!");
  public static final BaseComponent[] ACTION_CANCELLED = TextComponent.fromLegacyText("§bAction cancelled");
  public static final BaseComponent[] CHEST_REMOVED = TextComponent.fromLegacyText("§bEnder chest removed");
  public static final BaseComponent[] HIT_TO_REMOVE = TextComponent.fromLegacyText("§bHit an ender chest to remove it");
  public static final BaseComponent[] RUN_TO_CANCEL = TextComponent.fromLegacyText("§bRun the command again to cancel");
  public static final BaseComponent[] NO_LOADED_CHESTS = TextComponent.fromLegacyText("§cThere are no loaded ender chests to remove");
  public static final BaseComponent[] COULDNT_FIND_NEAREST = TextComponent.fromLegacyText("§cCouldn't find the nearest ender chest within loaded chunks in this world");
  public static final BaseComponent[] ALREADY_OCCUPIED = TextComponent.fromLegacyText("§cThis place is already occupied by another ender chest!");
  public static final BaseComponent[] SELECT_ANOTHER_LOCATION = TextComponent.fromLegacyText("§cPlease, select another location");
  public static final BaseComponent[] CHEST_PLACED = TextComponent.fromLegacyText("§bEnder chest placed successfully!");

  private final FancyEChests plugin;
  private final Function<CommandSender, S> transformer;
  private final CommandDispatcher<S> dispatcher = new CommandDispatcher<>();

  public CommanderKeen(final FancyEChests plugin, final Function<CommandSender, S> transformer) {
    this.plugin = plugin;
    this.transformer = transformer;
    final CommandProvider<S> provider = CommandProvider.of(this::version, this::reload,
                                                           this::remove, this::removeNearest,
                                                           this::set, this::setPersistent,
                                                           this::teleportNearest, "fec");
    dispatcher.register(provider.getBuilder());
  }

  @Override
  public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
    final String cmd = "fec " + String.join(" ", args);

    if (!(sender instanceof Player)) {
      VariousUtils.sendMessage(sender, PLAYERS_ONLY);
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
    final BaseComponent[] version = TextComponent.fromLegacyText("§3FancyE-Chests §7- §bv" + plugin.getDescription().getVersion());
    VariousUtils.sendMessage(source, version);
    return 1;
  }

  private void usage(final CommandContext<S> context) {
    version(context);

    final S source = context.getSource();

    VariousUtils.sendMessage(source, USAGE);
    for (final String usage : dispatcher.getAllUsage(dispatcher.getRoot(), source, true)) {
      final TextComponent usageComponent = new TextComponent("/" + usage);
      usageComponent.setColor(ChatColor.RED);
      final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent(usageComponent) });
      usageComponent.setHoverEvent(hoverEvent);
      final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + usage);
      usageComponent.setClickEvent(clickEvent);
      VariousUtils.sendMessage(source, usageComponent);
    }
  }

  private int teleportNearest(final CommandContext<S> context) {
    final S source = context.getSource();
    final UUID nearestChest = getNearestChest(source);

    if (nearestChest != null) {
      source.teleport(plugin.spinnyChests.get(nearestChest).getLocation());
    } else {
      VariousUtils.sendMessage(source, COULDNT_FIND_NEAREST);
    }

    return 1;
  }

  private int reload(final CommandContext<S> context) {
    plugin.reloadConfig();
    VariousUtils.sendMessage(context.getSource(), FILES_RELOADED);
    return 1;
  }

  private int remove(final CommandContext<S> context) {
    final S source = context.getSource();

    if (plugin.playersRemovingChest.remove(source.getUniqueId())) {
      VariousUtils.sendMessage(source, ACTION_CANCELLED);
    } else {
      if (plugin.spinnyChests.size() > 0) {
        plugin.playersRemovingChest.add(source.getUniqueId());
        VariousUtils.sendMessage(source, HIT_TO_REMOVE);
        VariousUtils.sendMessage(source, RUN_TO_CANCEL);
      } else {
        VariousUtils.sendMessage(source, NO_LOADED_CHESTS);
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

      final TextComponent chestRemovedAt = new TextComponent("Ender chest removed at ");
      chestRemovedAt.setColor(ChatColor.AQUA);
      final TextComponent chestCoords = new TextComponent("x:" + x + " y:" + y + " z:" + z);
      chestCoords.setColor(ChatColor.GRAY);
      VariousUtils.sendMessage(source, chestRemovedAt, chestCoords);

    } else {
      VariousUtils.sendMessage(source, COULDNT_FIND_NEAREST);
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

    if (SpinnyChest.isPlaceOccupied(loc)) {
      VariousUtils.sendMessage(source, ALREADY_OCCUPIED);
      VariousUtils.sendMessage(source, SELECT_ANOTHER_LOCATION);

    } else {
      final SpinnyChest sc = new SpinnyChest(loc, shouldDisappear);
      plugin.spinnyChests.put(sc.getUUID(), sc);
      VariousUtils.sendMessage(source, CHEST_PLACED);

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
