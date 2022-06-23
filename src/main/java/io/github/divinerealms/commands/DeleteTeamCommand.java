package io.github.divinerealms.commands;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteTeamCommand implements CommandExecutor {
  @Getter private final Helper helper;
  @Getter private final Logger logger;

  public DeleteTeamCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
      getLogger().sendLongMessage(sender, "team.help");
    } else if (args.length == 2) {
      final String name = args[1], nameUppercase = name.toUpperCase();

      if (getHelper().isGroupLoaded(name)) {
        getHelper().deleteGroup(name);
        getLogger().sendMessage(sender, "team.deleted", nameUppercase);
      } else getLogger().sendMessage(sender, "team.not-found", nameUppercase);
    } else getLogger().sendLongMessage(sender, "team.usage.delete");
    return true;
  }
}
