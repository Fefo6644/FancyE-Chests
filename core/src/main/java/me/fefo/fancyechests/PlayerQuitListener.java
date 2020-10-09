package me.fefo.fancyechests;

import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener extends SelfRegisteringListener {

  private final FancyEChests plugin;

  public PlayerQuitListener(final FancyEChests plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final FecHolder holder = plugin.playerInventories.remove(event.getPlayer().getUniqueId());
    if (!holder.requiresMigration()) {
      holder.save(true);
    } else {
      holder.deleteData();
    }
  }
}
