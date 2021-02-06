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
