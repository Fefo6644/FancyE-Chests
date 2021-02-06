package com.github.fefo.fancyechests.message;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerMessagingSubject extends MessagingSubject {

  PlayerMessagingSubject(final @NotNull Audience audience, final @NotNull Player player) {
    super(audience, player.getName(), player);
  }

  @Override
  public boolean isPlayer() {
    return true;
  }

  @Override
  public @NotNull PlayerMessagingSubject asPlayerSubject() {
    return this;
  }

  public @NotNull Player getPlayer() {
    final Player player = (Player) this.permissible.get();
    return player != null ? player : DummyPlayer.INSTANCE;
  }
}
