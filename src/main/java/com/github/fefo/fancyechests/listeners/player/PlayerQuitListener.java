package com.github.fefo.fancyechests.listeners.player;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.model.holder.HolderManager;
import com.github.fefo.fancyechests.util.TaskScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

  private final TaskScheduler scheduler;
  private final HolderManager holderManager;

  public PlayerQuitListener(final FancyEChestsPlugin plugin) {
    this.scheduler = plugin.getTaskScheduler();
    this.holderManager = plugin.getHolderManager();
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final UUID uuid = event.getPlayer().getUniqueId();
    this.holderManager.saveHolder(uuid, this.scheduler::async)
                      .thenRun(() -> this.holderManager.unloadHolder(uuid));
  }
}
