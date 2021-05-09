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
