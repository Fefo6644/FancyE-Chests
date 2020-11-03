package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import me.fefo.facilites.TaskUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener extends SelfRegisteringListener {

  private final TaskUtil scheduler;
  private final FancyEChests plugin;

  public PlayerQuitListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
    this.scheduler = plugin.getScheduler();
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final UUID uuid = event.getPlayer().getUniqueId();
    final FecHolder holder = plugin.playerInventories.remove(uuid);
    holder.save(scheduler::async)
          .thenRunAsync(() -> plugin.playerInventories.remove(uuid), scheduler::sync);
  }
}
