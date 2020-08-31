package me.fefo.fancyechests;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CommanderKeen implements CommandExecutor, TabCompleter {
  private final Main main;

  public CommanderKeen(@NotNull Main main) { this.main = main; }

  @Override
  public boolean onCommand(@NotNull CommandSender sender,
                           @NotNull Command cmd,
                           @NotNull String alias,
                           @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Only players can use this command");
      return true;
    }

    if (args.length > 1) {
      return false;
    } else if (args.length == 0) {
      sender.sendMessage(ChatColor.DARK_AQUA + "FancyE-Chests " +
                         ChatColor.GRAY + "- " +
                         ChatColor.AQUA + "v" + main.getDescription().getVersion());
      return true;
    } else {
      boolean shouldDisappear = true;
      switch (args[0]) {
        case "setpersistent":
          shouldDisappear = false;
        case "set": {
          final Location loc = ((Player)sender).getLocation().clone();

          if (SpinnyChest.isPlaceOccupied(loc)) {
            final String[] messages = new String[] {
                ChatColor.RED + "This place is already occupied by an ender chest!",
                ChatColor.RED + "Please, select another location"
            };
            sender.sendMessage(messages);
          } else {
            final SpinnyChest sc = new SpinnyChest(loc, shouldDisappear);
            main.spinnyChests.put(sc.getUUID(), sc);
            sender.sendMessage(ChatColor.AQUA + "Ender chest placed successfully!");

            final ConfigurationSection cs = main.chestsYaml.createSection(sc.getUUID().toString());
            cs.set(Main.YAML_HIDDEN_UNTIL, 0L);
            cs.set(Main.YAML_SHOULD_DISAPPEAR, shouldDisappear);
            try {
              main.chestsYaml.save(main.chestsFile);
            } catch (IOException e) {
              main.getLogger().severe("Could not save data file!");
              e.printStackTrace();
            }
          }

          return true;
        }

        case "remove": {
          if (main.spinnyChests.size() > 0) {
            if (main.playersRemovingChest.remove(((Player)sender).getUniqueId())) {
              sender.sendMessage(ChatColor.AQUA + "Action cancelled");
            } else {
              main.playersRemovingChest.add(((Player)sender).getUniqueId());
              final String[] messages = new String[] {
                  ChatColor.AQUA + "Hit a spinning ender chest to remove it",
                  ChatColor.AQUA + "Run the command again to cancel"
              };
              sender.sendMessage(messages);
            }
          } else {
            sender.sendMessage(ChatColor.RED + "There are no ender chests to remove");
          }

          return true;
        }

        case "reload":
          main.reload();
          sender.sendMessage(ChatColor.AQUA + "Files reloaded successfully!");
          return true;

        default:
          return false;
      }
    }
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender,
                                    @NotNull Command cmd,
                                    @NotNull String alias,
                                    @NotNull String[] args) {
    final ArrayList<String> ret = new ArrayList<>();

    if (args.length == 1) {
      if ("setpersistent".startsWith(args[0])) {
        ret.add("setpersistent");
      }

      if ("set".startsWith(args[0])) {
        ret.add("set");
      }

      if ("remove".startsWith(args[0])) {
        ret.add("remove");
      }

      if ("reload".startsWith(args[0])) {
        ret.add("reload");
      }
    }

    return ret;
  }
}
