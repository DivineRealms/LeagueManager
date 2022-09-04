package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.commands.var.GiveAccessCommand;
import io.github.divinerealms.commands.var.RemoveAccessCommand;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VARCommand implements CommandExecutor {
  @Getter
  private final LeagueManager plugin;
  @Getter
  private final UtilManager utilManager;
  @Getter
  private final Logger logger;

  public VARCommand(final LeagueManager plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
      getLogger().send(sender, Lang.VAR_HELP.getConfigValue(null));
    } else {
      switch (args[0].toLowerCase()) {
        case "add":
        case "set":
          final GiveAccessCommand giveAccessCommand = new GiveAccessCommand(getUtilManager());
          giveAccessCommand.onCommand(sender, command, label, args);
          break;
        case "remove":
        case "unset":
          final RemoveAccessCommand removeAccessCommand = new RemoveAccessCommand(getUtilManager());
          removeAccessCommand.onCommand(sender, command, label, args);
          break;
        default:
          getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      }
    }
    return true;
  }
}
