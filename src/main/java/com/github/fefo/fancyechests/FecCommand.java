package com.github.fefo.fancyechests;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.github.fefo.fancyechests.message.Message;
import com.github.fefo.fancyechests.message.MessagingSubject;
import com.github.fefo.fancyechests.message.PlayerMessagingSubject;
import com.github.fefo.fancyechests.message.SubjectFactory;
import com.github.fefo.fancyechests.model.chest.SpinningChest;
import com.github.fefo.fancyechests.util.CommandMapHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FecCommand extends Command implements Listener {

  private static final Joiner OR_JOINER = Joiner.on('|');
  private static final UUID ZERO = new UUID(0L, 0L);

  private final FancyEChestsPlugin plugin;
  private final SubjectFactory subjectFactory;
  private final CommandDispatcher<PlayerMessagingSubject> dispatcher = new CommandDispatcher<>();
  private final RootCommandNode<PlayerMessagingSubject> rootNode = this.dispatcher.getRoot();
  private final Predicate<? super String> commandPredicate;

  public FecCommand(final FancyEChestsPlugin plugin) {
    super("fancyechests", "", "/fancyechests help", ImmutableList.of("fec"));
    this.plugin = plugin;
    this.subjectFactory = plugin.getSubjectFactory();

    setPermission("fancyechests.use");
    setPermissionMessage(Message.NO_PERMISSION.legacy("run this command"));
    CommandMapHelper.getCommandMap().register("fancyechests", this);

    this.commandPredicate
        = Pattern.compile("^/?(?:fancyechests:)?"
                          + "(?:" + getName() + '|' + OR_JOINER.join(getAliases()) + ") ")
                 .asPredicate();

    try {
      Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
      Bukkit.getPluginManager().registerEvents(this, plugin);
    } catch (final ClassNotFoundException exception) {
      // oh well
    }

    final LiteralArgumentBuilder<PlayerMessagingSubject> builder = literal(getName());
    builder
        .requires(subject -> subject.hasPermission(getPermission()))
        .then(literal("help")
                  .executes(this::help))
        .then(literal("nearest")
                  .executes(this::teleportNearest))
        .then(literal("reload")
                  .executes(this::reload))
        .then(literal("remove")
                  .executes(this::remove)
                  .then(literal("nearest")
                            .executes(this::removeNearest)))
        .then(literal("set")
                  .executes(this::set))
        .then(literal("setpersistent")
                  .executes(this::setPersistent));

    this.dispatcher.register(builder);
  }

  private void usage(final MessagingSubject subject) {
    Message.PLUGIN_INFO.sendMessage(subject, this.plugin);
  }

  private int help(final CommandContext<PlayerMessagingSubject> context) {
    final PlayerMessagingSubject subject = context.getSource();
    usage(subject);

    Message.USAGE_TITLE.sendMessage(subject);
    for (final String usage : this.dispatcher.getAllUsage(this.rootNode, subject, true)) {
      Message.USAGE_COMMAND.sendMessage(subject, usage);
    }

    return 1;
  }

  private int teleportNearest(final CommandContext<PlayerMessagingSubject> context) {
    final PlayerMessagingSubject subject = context.getSource();
    final UUID nearestChest = getNearestChest(subject.getPlayer());

    if (nearestChest == ZERO) {
      Message.COULDNT_FIND_NEAREST.sendMessage(subject);
    } else {
      subject.getPlayer().teleport(this.plugin.getChestMap().get(nearestChest).getLocation());
    }

    return 1;
  }

  private int reload(final CommandContext<PlayerMessagingSubject> context) {
    if (this.plugin.reload()) {
      Message.FILES_RELOADED.sendMessage(context.getSource());
    } else {
      Message.RELOADING_ERROR.sendMessage(context.getSource());
    }
    return 1;
  }

  private int remove(final CommandContext<PlayerMessagingSubject> context) {
    final PlayerMessagingSubject subject = context.getSource();

    if (this.plugin.getHolderManager().removedChestCompleted(subject.getPlayer().getUniqueId())) {
      Message.ACTION_CANCELLED.sendMessage(subject);
      return 1;
    }

    if (this.plugin.getChestMap().isEmpty()) {
      Message.NO_LOADED_CHESTS.sendMessage(subject);
      return 1;
    }

    this.plugin.getHolderManager().startedRemovingChest(subject.getPlayer().getUniqueId());
    Message.HIT_TO_REMOVE.sendMessage(subject);
    Message.RUN_TO_CANCEL.sendMessage(subject);
    return 1;
  }

  private int removeNearest(final CommandContext<PlayerMessagingSubject> context) {
    final PlayerMessagingSubject subject = context.getSource();
    final UUID nearestChest = getNearestChest(subject.getPlayer());

    if (nearestChest == ZERO) {
      Message.COULDNT_FIND_NEAREST.sendMessage(subject);
      return 1;
    }

    final Location chestLocation = this.plugin.getChestMap().remove(nearestChest).getLocation();

    try {
      this.plugin.getChestMap().save();
    } catch (final IOException exception) {
      this.plugin.getSLF4JLogger().error("Could not save data file!", exception);
      exception.printStackTrace();
    }

    Message.CHEST_REMOVED_AT.sendMessage(subject, subject.getName(), chestLocation);
    return 1;
  }

  private int set(final CommandContext<PlayerMessagingSubject> context) {
    final PlayerMessagingSubject subject = context.getSource();
    createEnderChest(subject, true);
    return 1;
  }

  private int setPersistent(final CommandContext<PlayerMessagingSubject> context) {
    final PlayerMessagingSubject subject = context.getSource();
    createEnderChest(subject, false);
    return 1;
  }

  private void createEnderChest(final PlayerMessagingSubject subject,
                                final boolean shouldDisappear) {
    final Player player = subject.getPlayer();
    final Location location = player.getLocation().clone();

    if (this.plugin.getChestMap().isPlaceOccupied(location)) {
      Message.ALREADY_OCCUPIED.sendMessage(subject);
      Message.SELECT_ANOTHER_LOCATION.sendMessage(subject);
      return;
    }

    final SpinningChest chest = new SpinningChest(this.plugin.getConfigAdapter(),
                                                  location, shouldDisappear);
    this.plugin.getChestMap().put(chest.getUuid(), chest);
    Message.CHEST_PLACED.sendMessage(subject);

    try {
      this.plugin.getChestMap().save();
    } catch (final IOException exception) {
      this.plugin.getSLF4JLogger().error("Could not save data file!", exception);
      exception.printStackTrace();
    }
  }

  private UUID getNearestChest(final Player player) {
    return player.getWorld().getEntitiesByClass(ArmorStand.class).stream()
                 .map(Entity::getUniqueId)
                 .filter(this.plugin.getChestMap()::containsKey)
                 .map(this.plugin.getChestMap()::get)
                 .min(ChestsSorter.from(player.getLocation()))
                 .map(SpinningChest::getUuid)
                 .orElse(ZERO);
  }

  @Override
  public boolean execute(final @NotNull CommandSender sender,
                         final @NotNull String alias,
                         final @NotNull String @NotNull [] args) {
    final MessagingSubject subject = this.subjectFactory.sender(sender);
    final String cmd = getName() + ' ' + String.join(" ", args);

    if (!subject.isPlayer()) {
      Message.PLAYERS_ONLY.sendMessage(subject);
      return true;
    }

    final ParseResults<PlayerMessagingSubject> result =
        this.dispatcher.parse(cmd.trim(), subject.asPlayerSubject());

    if (result.getContext().getNodes().isEmpty()) {
      Message.NO_PERMISSION.sendMessage(subject, "run this command");
      return true;
    }

    if (!result.getExceptions().isEmpty()) {
      result.getExceptions().values().forEach(exception -> {
        Message.COMMAND_EXCEPTION.sendMessage(subject, exception.getMessage());
      });
      return true;
    }

    try {
      this.dispatcher.execute(result);
    } catch (final CommandSyntaxException exception) {
      usage(subject);
    }

    return true;
  }

  private List<String> tabComplete(final MessagingSubject subject, final String input) {
    if (!subject.isPlayer()) {
      return ImmutableList.of();
    }

    final ParseResults<PlayerMessagingSubject> result
        = this.dispatcher.parse(input, subject.asPlayerSubject());
    return this.dispatcher.getCompletionSuggestions(result).join()
                          .getList().stream()
                          .map(Suggestion::getText)
                          .collect(Collectors.toList());
  }

  @Override
  public @NotNull List<String> tabComplete(final @NotNull CommandSender sender,
                                           final @NotNull String alias,
                                           final @NotNull String @NotNull [] args) {
    final MessagingSubject subject = this.subjectFactory.sender(sender);
    final String input = getName() + ' ' + String.join(" ", args);
    return tabComplete(subject, input);
  }

  @EventHandler(ignoreCancelled = true)
  private void onAsyncTabComplete(final AsyncTabCompleteEvent event) {
    if (event.isHandled() || !event.isCommand()) {
      return;
    }

    final String buffer = event.getBuffer();
    if (!this.commandPredicate.test(buffer)) {
      return;
    }

    final MessagingSubject subject = this.subjectFactory.sender(event.getSender());
    final String input = getName() + buffer.substring(buffer.indexOf(' '));
    event.setCompletions(tabComplete(subject, input));
    event.setHandled(true);
  }

  private LiteralArgumentBuilder<PlayerMessagingSubject> literal(final String name) {
    return LiteralArgumentBuilder.literal(name);
  }

  private static final class ChestsSorter implements Comparator<SpinningChest> {

    public static ChestsSorter from(final Location origin) {
      return new ChestsSorter(origin);
    }

    private final Location origin;

    private ChestsSorter(final Location origin) {
      this.origin = origin;
    }

    @Override
    public int compare(final SpinningChest first, final SpinningChest second) {
      return Double.compare(this.origin.distanceSquared(first.getLocation()),
                            this.origin.distanceSquared(second.getLocation()));
    }
  }
}
