package io.github.divinerealms.commands;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeamCommand implements CommandExecutor {
  @Getter private final UtilManager utilManager;
  @Getter private final Logger logger;

  public TeamCommand(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leagueManager.admin")) {
      getLogger().send(sender, "insufficient-permission");
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLong(sender, "team.help");
      } else if (args[1].equalsIgnoreCase("create")) {
        if (args.length == 4) {
          if (!getUtilManager().getConfig().getConfig().contains(args[2])) {
            getUtilManager().getConfig().set(args[2], args[3]);
            getLogger().send(sender, "team.created", args[3]);
          } else getLogger().send(sender, "team.already-defined", args[2].toUpperCase());
        } else getLogger().send(sender, "unknown-command");
      } else if (args[1].equalsIgnoreCase("delete")) {
        if (args.length == 3) {
          if (getUtilManager().getConfig().getConfig().contains(args[2])) {
            getUtilManager().getConfig().set(args[2], null);
            getLogger().send(sender, "team.deleted", args[2].toUpperCase());
          } else getLogger().send(sender, "team.not-found", args[2].toUpperCase());
        } else getLogger().send(sender, "unknown-command");
      } else getLogger().send(sender, "unknown-command");
    }
    return true;
  }
}
