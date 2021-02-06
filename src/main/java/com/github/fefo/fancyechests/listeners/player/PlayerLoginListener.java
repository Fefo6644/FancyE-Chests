package com.github.fefo.fancyechests.listeners.player;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.model.holder.FecHolder;
import com.github.fefo.fancyechests.model.holder.HolderManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoginListener implements Listener {

  private final HolderManager holderManager;

  public PlayerLoginListener(final FancyEChestsPlugin plugin) {
    this.holderManager = plugin.getHolderManager();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
    if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
      this.holderManager.createHolder(event.getUniqueId());
    }
  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    this.holderManager.createInventory(player);
  }
}
