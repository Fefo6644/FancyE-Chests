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
