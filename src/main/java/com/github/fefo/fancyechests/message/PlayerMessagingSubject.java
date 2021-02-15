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
