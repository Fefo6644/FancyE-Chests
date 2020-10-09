package me.fefo.fancyechests;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import me.fefo.facilites.SelfRegisteringListener;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

public final class CommandRegisteredListener<S extends BukkitBrigadierCommandSource> extends SelfRegisteringListener implements Command<S> {

  public CommandRegisteredListener(final Plugin plugin) {
    super(plugin);
  }

  @EventHandler
  public void onCommandRegistered(final CommandRegisteredEvent<S> event) {
    if (!event.getCommand().getLabel().equalsIgnoreCase("fancyechests")) {
      return;
    }

    if (event.getCommandLabel().contains(":")) {
      event.setCancelled(true);
      return;
    }

    event.setLiteral(CommandProvider.of(this, this,
                                        this, this,
                                        this, this,
                                        this, event.getCommandLabel())
                                    .getBuilder().build());
  }

  @Override
  public int run(CommandContext<S> context) {
    return SINGLE_SUCCESS;
  }
}
