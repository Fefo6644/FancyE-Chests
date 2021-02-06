package com.github.fefo.fancyechests.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class TaskScheduler {

  private final Plugin plugin;
  private final BukkitScheduler syncScheduler = Bukkit.getScheduler();
  private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(
      16, new ThreadFactoryBuilder()
          .setDaemon(false)
          .setPriority(Thread.NORM_PRIORITY)
          .setNameFormat("fancyechests-scheduler-thread-%d")
          .build());

  public TaskScheduler(final Plugin plugin) {
    this.plugin = plugin;
  }

  public void shutdown() {
    try {
      this.asyncScheduler.shutdown();
      this.asyncScheduler.awaitTermination(15L, TimeUnit.SECONDS);
    } catch (final InterruptedException exception) {
      exception.printStackTrace();
    }
  }

  public BukkitTask sync(final @NotNull Runnable task) {
    return this.syncScheduler.runTask(this.plugin, task);
  }

  public <T> Future<T> sync(final @NotNull Callable<T> task) {
    return this.syncScheduler.callSyncMethod(this.plugin, task);
  }

  public BukkitTask sync(final @NotNull Runnable task, final long delay) {
    return this.syncScheduler.runTaskLater(this.plugin, task, delay);
  }

  public BukkitTask sync(final @NotNull Runnable task, final long delay, final long period) {
    return this.syncScheduler.runTaskTimer(this.plugin, task, delay, period);
  }

  public void async(final @NotNull Runnable task) {
    this.asyncScheduler.submit(task);
  }

  public <T> Future<T> async(final @NotNull Callable<T> task) {
    return this.asyncScheduler.submit(task);
  }

  public ScheduledFuture<?> async(final @NotNull Runnable task, final long delay) {
    return this.asyncScheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
  }

  public <T> ScheduledFuture<T> async(final @NotNull Callable<T> task, final long delay) {
    return this.asyncScheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
  }

  public ScheduledFuture<?> async(final @NotNull Runnable task,
                                  final long delay, final long period) {
    return this.asyncScheduler.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
  }
}
