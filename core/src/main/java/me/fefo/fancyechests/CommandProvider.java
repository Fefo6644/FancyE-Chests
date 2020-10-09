package me.fefo.fancyechests;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public final class CommandProvider<S> {

  public static <S> CommandProvider<S> of(final Command<S> version, final Command<S> reload,
                                          final Command<S> remove, final Command<S> removeNearest,
                                          final Command<S> set, final Command<S> setPersistent,
                                          final Command<S> teleportNearest, final String alias) {
    return new CommandProvider<>(version, reload, remove, removeNearest, set, setPersistent, teleportNearest, alias);
  }

  private LiteralArgumentBuilder<S> literal(final String name) {
    return LiteralArgumentBuilder.literal(name);
  }

  private final LiteralArgumentBuilder<S> builder;

  private CommandProvider(final Command<S> version, final Command<S> reload,
                          final Command<S> remove, final Command<S> removeNearest,
                          final Command<S> set, final Command<S> setPersistent,
                          final Command<S> teleportNearest, final String alias) {
    builder = literal(alias);
    builder.executes(version)
           .then(literal("nearest").executes(teleportNearest))
           .then(literal("reload").executes(reload))
           .then(literal("remove").executes(remove)
                                  .then(literal("nearest").executes(removeNearest)))
           .then(literal("set").executes(set))
           .then(literal("setpersistent").executes(setPersistent));
  }

  public LiteralArgumentBuilder<S> getBuilder() {
    return builder;
  }
}
