//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
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

package com.github.fefo.fancyechests.message;

import com.google.common.base.Joiner;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

public interface Message {

  Joiner JOINER = Joiner.on(' ');

  Component PREFIX =
      text()
          .append(text('[', GRAY),
                  text('F', DARK_AQUA, BOLD),
                  text("EC", AQUA, BOLD),
                  text(']', GRAY))
          .build();

  Args1<Plugin> PLUGIN_INFO = plugin ->
      prefixed()
          .color(AQUA)
          .append(text("Fancy", DARK_AQUA),
                  text("E-Chests"),
                  text(" - ", GRAY),
                  text('v'),
                  text(plugin.getDescription().getVersion()));

  Args1<String> NO_PERMISSION = action ->
      prefixed()
          .color(RED)
          .append(text("You are not allowed to"),
                  space(),
                  text(action));

  Args1<String> COMMAND_EXCEPTION = exception ->
      prefixed()
          .append(text(exception, RED));

  Args0 USAGE_TITLE = () ->
      prefixed()
          .append(text("Usages:"));

  Args1<String> USAGE_COMMAND = command ->
      prefixed()
          .color(GRAY)
          .append(text('/'),
                  text(command))
          .hoverEvent(text()
                          .color(GRAY)
                          .append(text("Click to run:", WHITE),
                                  space(),
                                  text('/'),
                                  text(command))
                          .build().asHoverEvent())
          .clickEvent(suggestCommand('/' + command));

  Args0 PLAYERS_ONLY = () ->
      prefixed()
          .append(text("Only players can run this command", RED));

  Args0 FILES_RELOADED = () ->
      prefixed()
          .append(text("Files reloaded successfully!", AQUA));

  Args0 RELOADING_ERROR = () ->
      text()
          .append(join(newline(),
                       prefixed()
                           .append(text("There was an error while reloading files", RED)),
                       prefixed()
                           .append(text("Please check the console for any errors", RED))));

  Args0 ACTION_CANCELLED = () ->
      prefixed()
          .append(text("Action cancelled", AQUA));

  Args0 CHEST_REMOVED = () ->
      prefixed()
          .append(text("Ender chest removed", AQUA));

  Args2<String, Location> CHEST_REMOVED_AT = (target, location) ->
      prefixed()
          .color(GRAY)
          .append(join(space(),
                       text("Ender chest removed at", AQUA),
                       text("x:"), text(location.getBlockX()),
                       text("y:"), text(location.getBlockY()),
                       text("z:"), text(location.getBlockZ()))
                      .hoverEvent(text()
                                      .color(GRAY)
                                      .append(join(space(),
                                                   text("Click to teleport to", WHITE),
                                                   text(location.getBlockX()),
                                                   text(location.getBlockY()),
                                                   text(location.getBlockZ())))
                                      .build().asHoverEvent())
                      .clickEvent(suggestCommand(JOINER.join("/teleport", target,
                                                             location.getBlockX(),
                                                             location.getBlockY(),
                                                             location.getBlockZ()))));

  Args0 HIT_TO_REMOVE = () ->
      prefixed()
          .append(text("Hit an ender chest to remove it", AQUA));

  Args0 RUN_TO_CANCEL = () ->
      prefixed()
          .append(text("Run the command again to cancel", AQUA));

  Args0 NO_LOADED_CHESTS = () ->
      prefixed()
          .append(text("There are no loaded ender chests to remove", RED));

  Args0 COULDNT_FIND_NEAREST = () ->
      prefixed()
          .append(text("There are no ender chests in loaded chunks in this world", RED));

  Args0 ALREADY_OCCUPIED = () ->
      prefixed()
          .append(text("This place is already occupied by another ender chest!", RED));

  Args0 SELECT_ANOTHER_LOCATION = () ->
      prefixed()
          .append(text("Please, select another location", RED));

  Args0 CHEST_PLACED = () ->
      prefixed()
          .append(text("Ender chest placed successfully!", AQUA));

  static TextComponent.Builder prefixed() {
    return TextComponent.ofChildren(PREFIX, space()).toBuilder().resetStyle();
  }

  @FunctionalInterface
  interface Args0 {

    default void sendMessage(final Audience audience) {
      audience.sendMessage(build());
    }

    default String legacy() {
      return legacySection().serialize(build().asComponent());
    }

    ComponentLike build();
  }

  @FunctionalInterface
  interface Args1<T> {

    default void sendMessage(final Audience audience, final T t) {
      audience.sendMessage(build(t));
    }

    default String legacy(final T t) {
      return legacySection().serialize(build(t).asComponent());
    }

    ComponentLike build(final T t);
  }

  @FunctionalInterface
  interface Args2<T, S> {

    default void sendMessage(final Audience audience, final T t, final S s) {
      audience.sendMessage(build(t, s));
    }

    default String legacy(final T t, final S s) {
      return legacySection().serialize(build(t, s).asComponent());
    }

    ComponentLike build(final T t, final S s);
  }

  @FunctionalInterface
  interface Args3<T, S, R> {

    default void sendMessage(final Audience audience, final T t, final S s, final R r) {
      audience.sendMessage(build(t, s, r));
    }

    default String legacy(final T t, final S s, final R r) {
      return legacySection().serialize(build(t, s, r).asComponent());
    }

    ComponentLike build(final T t, final S s, final R r);
  }

  @FunctionalInterface
  interface Args4<T, S, R, Q> {

    default void sendMessage(final Audience audience, final T t, final S s, final R r, final Q q) {
      audience.sendMessage(build(t, s, r, q));
    }

    default String legacy(final T t, final S s, final R r, final Q q) {
      return legacySection().serialize(build(t, s, r, q).asComponent());
    }

    ComponentLike build(final T t, final S s, final R r, final Q q);
  }

  @FunctionalInterface
  interface Args5<T, S, R, Q, P> {

    default void sendMessage(
        final Audience audience, final T t, final S s, final R r, final Q q, final P p) {
      audience.sendMessage(build(t, s, r, q, p));
    }

    default String legacy(final T t, final S s, final R r, final Q q, final P p) {
      return legacySection().serialize(build(t, s, r, q, p).asComponent());
    }

    ComponentLike build(final T t, final S s, final R r, final Q q, final P p);
  }
}
