//
// FancyE-Chests - Provide your players with isolated, fancy spinning ender chests.
// Copyright (C) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

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
