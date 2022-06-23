package io.github.divinerealms.commands;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreateTeamCommand implements CommandExecutor {
  @Getter private final Helper helper;
  @Getter private final Logger logger;

  public CreateTeamCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
      getLogger().sendLongMessage(sender, "team.help");
    } else if (args.length == 3) {
      final String name = args[1], tag = args[2], nameUppercase = name.toUpperCase();
      final boolean isBranch = name.endsWith("b");

      if (!getHelper().isGroupLoaded(name)) {
        getHelper().createGroup(name);
        getHelper().setGroupMeta(name, "team", tag);
        if (isBranch) getHelper().setGroupMeta(name, "team-b", "&a B");
        getHelper().setGroupPermissions(name);
        getLogger().sendMessage(sender, "team.created", nameUppercase);
      } else getLogger().sendMessage(sender, "team.already-defined", nameUppercase);
    } else getLogger().sendLongMessage(sender, "team.usage.create");
    return true;
  }
}
