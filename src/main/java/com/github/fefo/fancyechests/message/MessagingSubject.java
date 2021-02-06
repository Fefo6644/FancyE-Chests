package com.github.fefo.fancyechests.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class MessagingSubject implements ForwardingAudience.Single {

  protected final Audience audience;
  protected final String name;
  protected final WeakReference<Permissible> permissible;

  MessagingSubject(final @NotNull Audience audience,
                   final @NotNull String name,
                   final @NotNull Permissible permissible) {
    this.audience = audience;
    this.name = name;
    this.permissible = new WeakReference<>(permissible);
  }

  @Override
  public @NotNull Audience audience() {
    return this.audience;
  }

  public @NotNull String getName() {
    return this.name;
  }

  public boolean isPlayer() {
    return false;
  }

  public @Nullable PlayerMessagingSubject asPlayerSubject() {
    if (isPlayer()) {
      return (PlayerMessagingSubject) this;
    }
    return null;
  }

  public boolean hasPermission(final @NotNull String name) {
    final Permissible handle = this.permissible.get();
    if (handle != null) {
      return handle.hasPermission(name);
    }
    return false;
  }
}
