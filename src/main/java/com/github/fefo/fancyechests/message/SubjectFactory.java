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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public final class SubjectFactory {

  private final LoadingCache<CommandSender, MessagingSubject> subjectsCache =
      CacheBuilder.newBuilder()
                  .weakKeys()
                  .expireAfterWrite(5L, TimeUnit.MINUTES)
                  .expireAfterAccess(5L, TimeUnit.MINUTES)
                  .build(CacheLoader.from(sender -> {
                    if (sender instanceof Player) {
                      return new PlayerMessagingSubject(audienceFrom(sender), (Player) sender);
                    }

                    return new MessagingSubject(audienceFrom(sender), nameFrom(sender), sender);
                  }));

  private final BukkitAudiences audiences;

  public SubjectFactory(final Plugin plugin) {
    this.audiences = BukkitAudiences.create(plugin);
  }

  public void cleanup() {
    this.subjectsCache.invalidateAll();
  }

  public @NotNull MessagingSubject sender(final @NotNull CommandSender sender) {
    return this.subjectsCache.getUnchecked(sender);
  }

  @SuppressWarnings("ConstantConditions")
  public @NotNull PlayerMessagingSubject player(final @NotNull Player player) {
    return this.subjectsCache.getUnchecked(player).asPlayerSubject();
  }

  public @NotNull Audience audienceFrom(final @Nullable CommandSender sender) {
    if (sender == null) {
      return this.audiences.console();
    }
    return this.audiences.sender(sender);
  }

  private String nameFrom(final CommandSender sender) {
    if (sender == null) {
      return "INVALID";
    }
    return sender.getName();
  }
}
