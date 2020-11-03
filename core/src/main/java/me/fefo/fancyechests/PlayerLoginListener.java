package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.entity.Player;
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

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
    if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
      plugin.playerInventories.put(event.getUniqueId(), new FecHolder(plugin, event.getUniqueId()));
    }
  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    final FecHolder holder = plugin.playerInventories.get(player.getUniqueId());
    holder.createInventory(player);
  }
}
