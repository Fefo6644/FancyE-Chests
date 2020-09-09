package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLoginListener extends SelfRegisteringListener {
  private final FancyEChests plugin;

  public PlayerLoginListener(@NotNull final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
    plugin.playerInventories.put(event.getUniqueId(), new FecHolder(event.getUniqueId()));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    plugin.playerInventories.get(event.getPlayer().getUniqueId()).createInventory(event.getPlayer());
  }
}
