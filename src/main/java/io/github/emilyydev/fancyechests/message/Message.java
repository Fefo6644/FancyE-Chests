//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021 emilyy-dev
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package io.github.emilyydev.fancyechests.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.toComponent;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public interface Message {

  Component PREFIX =
      text()
          .append(text('[', GRAY),
                  text('F', DARK_AQUA, BOLD),
                  text("EC", AQUA, BOLD),
                  text(']', GRAY))
          .build();

  Args1<Plugin> PLUGIN_INFO = plugin -> prefixed(
      text()
          .color(AQUA)
          .append(text(plugin.getName(), DARK_AQUA),
                  space(),
                  text("by"),
                  space())
          .apply(builder -> {
            final List<String> authors = plugin.getDescription().getAuthors();
            builder.append(text(authors.get(0), DARK_AQUA));
            if (authors.size() > 1) {
              final Component contributors = authors.stream().skip(1).map(Component::text).collect(toComponent(text(", ")));

              builder.append(space(),
                             text("and", DARK_AQUA),
                             space(),
                             text()
                                 .content("contributors")
                                 .color(DARK_AQUA)
                                 .hoverEvent(contributors));
            }
          })
          .append(text(" - ", GRAY),
                  text('v'),
                  text(plugin.getDescription().getVersion()))
          .hoverEvent(text(plugin.getDescription().getWebsite(), GRAY))
          .clickEvent(openUrl(plugin.getDescription().getWebsite())));

  Args1<String> NO_PERMISSION = action -> prefixed(
      text("You are not allowed to", RED),
      space(),
      text(action, RED));

  Args1<String> COMMAND_EXCEPTION = exception -> prefixed(text(exception, RED));

  Args0 USAGE_TITLE = () -> prefixed(text("Usages:"));

  Args1<String> USAGE_COMMAND = command -> prefixed(
      text()
          .color(GRAY)
          .append(text('/'),
                  text(command))
          .hoverEvent(text()
                          .color(GRAY)
                          .append(text("Click to run:", WHITE),
                                  space(),
                                  text('/'),
                                  text(command))
                          .build())
          .clickEvent(suggestCommand('/' + command)));

  Args0 PLAYERS_ONLY = () -> prefixed(text("Only players can run this command", RED));

  Args0 FILES_RELOADED = () -> prefixed(text("Files reloaded successfully!", AQUA));

  Args0 RELOADING_ERROR = () -> prefixed(text("There was an error while reloading files", RED));

  Args0 ACTION_CANCELLED = () -> prefixed(text("Action cancelled", AQUA));

  Args0 CHEST_REMOVED = () -> prefixed(text("Ender chest removed", AQUA));

  Args2<String, Location> CHEST_REMOVED_AT = (target, location) -> prefixed(
      text()
          .color(GRAY)
          .append(text("Ender chest removed at", AQUA),
                  space(),
                  text()
                      .apply(builder -> {
                        final String blockX = "x:" + location.getBlockX();
                        final String blockY = "y:" + location.getBlockY();
                        final String blockZ = "z:" + location.getBlockZ();

                        builder
                            .append(join(space(),
                                         text(blockX),
                                         text(blockY),
                                         text(blockZ)))
                            .hoverEvent(text("Click to teleport", WHITE));
                      }))
          .apply(builder -> {
            final String x = String.format(Locale.ROOT, "%.2f", location.getX());
            final String y = String.format(Locale.ROOT, "%.2f", location.getY());
            final String z = String.format(Locale.ROOT, "%.2f", location.getZ());
            final String command = String.format("/teleport %s %s %s %s", target, x, y, z);

            builder.clickEvent(suggestCommand(command));
          }));

  Args0 HIT_TO_REMOVE = () -> prefixed(text("Hit an ender chest to remove it", AQUA));

  Args0 RUN_TO_CANCEL = () -> prefixed(text("Run the command again to cancel", AQUA));

  Args0 NO_LOADED_CHESTS = () -> prefixed(text("There are no loaded ender chests to remove", RED));

  Args0 COULDNT_FIND_NEAREST = () -> prefixed(text("There are no ender chests in loaded chunks in this world", RED));

  Args0 ALREADY_OCCUPIED = () -> prefixed(text("Please, select another location", RED));

  Args0 SELECT_ANOTHER_LOCATION = () -> prefixed(text("Please, select another location", RED));

  Args0 CHEST_PLACED = () -> prefixed(text("Ender chest placed successfully!", AQUA));

  Args0 CHECK_CONSOLE_FOR_ERRORS = () -> prefixed(text("Please check the console for any errors", RED));

  static TextComponent prefixed(final ComponentLike first, final ComponentLike... rest) {
    return text()
        .append(PREFIX)
        .append(space())
        .append(first)
        .append(rest)
        .build();
  }

  @FunctionalInterface
  interface Args0 {

    ComponentLike build();

    default void send(final Audience audience) {
      audience.sendMessage(build());
    }
  }

  @FunctionalInterface
  interface Args1<T> {

    ComponentLike build(final T t);

    default void send(final Audience audience, final T t) {
      audience.sendMessage(build(t));
    }
  }

  @FunctionalInterface
  interface Args2<T, S> {

    ComponentLike build(final T t, final S s);

    default void send(final Audience audience, final T t, final S s) {
      audience.sendMessage(build(t, s));
    }
  }

  @FunctionalInterface
  interface Args3<T, S, R> {

    ComponentLike build(final T t, final S s, final R r);

    default void send(final Audience audience, final T t, final S s, final R r) {
      audience.sendMessage(build(t, s, r));
    }
  }
}
