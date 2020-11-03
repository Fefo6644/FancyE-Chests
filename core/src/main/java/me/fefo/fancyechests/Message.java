package me.fefo.fancyechests;

import me.fefo.facilites.VariousUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.DARK_AQUA;
import static net.md_5.bungee.api.ChatColor.GRAY;
import static net.md_5.bungee.api.ChatColor.RED;

public enum Message {
  PREFIX(builder("[").color(GRAY)
                     .append("F").bold(true).color(DARK_AQUA)
                     .append("EC").color(AQUA)
                     .append("] ", ComponentBuilder.FormatRetention.NONE).color(GRAY)),
  VERSION(builder("Fancy").color(DARK_AQUA)
                          .append("E-Chests").color(AQUA)
                          .append(" - ").color(GRAY)
                          .append("v{0}").color(AQUA)),
  USAGE_TITLE(prefixed("Usages:").color(RED)),
  USAGE_COMMAND(builder("/{0}").color(RED)
                               .event(hover(builder("/{0}").color(RED)))
                               .event(suggest("/{0}"))),
  PLAYERS_ONLY(prefixed("Only players can run this command!").color(RED)),
  FILES_RELOADED(prefixed("Files reloaded successfully!").color(AQUA)),
  ACTION_CANCELLED(prefixed("Action cancelled").color(AQUA)),
  CHEST_REMOVED(prefixed("Ender chest removed").color(AQUA)),
  CHEST_REMOVED_AT(prefixed("Ender chest removed at ").color(AQUA)
                                                      .append("x:{0} y:{1} z:{2}").color(GRAY)
                                                      .event(hover(builder("Click to teleport").color(GRAY)))
                                                      .event(suggest("/teleport {-1} {0} {1} {2}"))),
  HIT_TO_REMOVE(prefixed("Hit an ender chest to remove it").color(AQUA)),
  RUN_TO_CANCEL(prefixed("Run the command again to cancel").color(AQUA)),
  NO_LOADED_CHESTS(prefixed("There are no loaded ender chests to remove").color(RED)),
  COULDNT_FIND_NEAREST(prefixed("Couldn't find the nearest ender chest within loaded chunks in this world").color(RED)),
  ALREADY_OCCUPIED(prefixed("This place is already occupied by another ender chest!").color(RED)),
  SELECT_ANOTHER_LOCATION(prefixed("Please, select another location").color(RED)),
  CHEST_PLACED(prefixed("Ender chest placed successfully!").color(AQUA));

  private static ComponentBuilder prefixed(final String text) {
    return new ComponentBuilder(new TextComponent(PREFIX.components)).append(text, ComponentBuilder.FormatRetention.NONE);
  }

  private static ComponentBuilder builder(final String text) {
    return new ComponentBuilder(text);
  }

  private static HoverEvent hover(final ComponentBuilder builder) {
    return new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create());
  }

  private static HoverEvent hover(final BaseComponent[] components) {
    return new HoverEvent(HoverEvent.Action.SHOW_TEXT, components);
  }

  private static ClickEvent suggest(final String text) {
    return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, text);
  }

  private static ClickEvent run(final String text) {
    return new ClickEvent(ClickEvent.Action.RUN_COMMAND, text);
  }

  private static ClickEvent openUrl(final String text) {
    return new ClickEvent(ClickEvent.Action.OPEN_URL, text);
  }

  private final BaseComponent[] components;

  Message(final ComponentBuilder builder) {
    components = builder.create();
  }

  public void send(final CommandSender target, final String... replacements) {
    VariousUtils.sendMessage(target, replace(components, target.getName(), replacements));
  }

  private BaseComponent[] replace(final BaseComponent[] components, final String target, final String... replacements) {
    final BaseComponent[] copies = new BaseComponent[components.length];

    for (int i = 0, componentsLength = components.length; i < componentsLength; ++i) {
      final BaseComponent component = components[i];
      final BaseComponent copy = component.duplicate();
      copies[i] = copy;

      for (int j = -1; j < replacements.length; ++j) {
        final String replacement = j == -1 ? target : replacements[j];

        if (copy instanceof TextComponent) {
          ((TextComponent) copy).setText(((TextComponent) copy).getText().replace("{" + j + "}", replacement));
        }

        if (copy.getClickEvent() != null) {
          copy.setClickEvent(suggest(copy.getClickEvent().getValue().replace("{" + j + "}", replacement)));
        }
      }

      if (copy.getHoverEvent() != null) {
        copy.setHoverEvent(hover(replace(copy.getHoverEvent().getValue(), target, replacements)));
      }
    }

    return copies;
  }
}
