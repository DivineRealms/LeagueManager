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
    if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
      getLogger().sendLong(sender, "team.help");
    } else if (args[1].equalsIgnoreCase("info")) {
      if (args.length == 3) {
        if (!getUtilManager().getConfig().getConfig().contains(args[2])) {
          getLogger().sendLong(sender, "team.info", args[2]);
        } else getLogger().send(sender, "team.not-found", args[2].toUpperCase());
      } else getLogger().sendLong(sender, "team.help");
    } else if (args[1].equalsIgnoreCase("list")) {
      if (getUtilManager().getConfig().getConfig().getKeys(true).size() != 0) {
        getLogger().send(sender, "team.list");
      } else getLogger().send(sender, "team.list-empty");
    } else if (args[1].equalsIgnoreCase("create")) {
      if (!getUtilManager().getConfig().getConfig().contains(args[2])) {
        if (args.length == 4) {
          getUtilManager().getConfig().set(args[2] + ".main", args[3]);
          getLogger().send(sender, "team.created", args[2].toUpperCase());
        } else if (args.length == 5) {
          if (args[4].equalsIgnoreCase("branch")) {
            getUtilManager().getConfig().set(args[2] + ".main", args[3]);
            getUtilManager().getConfig().set(args[2] + ".juniors", "&a B");
            getLogger().send(sender, "team.created", args[2].toUpperCase());
          } else if (args[4].equalsIgnoreCase("main-single") || args[4].equalsIgnoreCase("ms")) {
            getUtilManager().getConfig().set(args[2] + ".main", args[3]);
            getLogger().send(sender, "team.created", args[2].toUpperCase());
          } else if (args[4].equalsIgnoreCase("juniors-single") || args[4].equalsIgnoreCase("js")) {
            getUtilManager().getConfig().set(args[2] + ".juniors", args[3]);
            getLogger().send(sender, "team.created", args[2].toUpperCase());
          } else getLogger().sendLong(sender, "team.usage.create");
        } else if (args.length == 6) {
          if (args[4].equalsIgnoreCase("branch")) {
            getUtilManager().getConfig().set(args[2] + ".main", args[3]);
            getUtilManager().getConfig().set(args[2] + ".juniors", args[5]);
            getLogger().send(sender, "team.created", args[2].toUpperCase());
          } else getLogger().sendLong(sender, "team.usage.create");
        } else getLogger().sendLong(sender, "team.usage.create");
      } else getLogger().send(sender, "team.already-defined", args[2].toUpperCase());
    } else if (args[1].equalsIgnoreCase("delete")) {
      if (args.length == 3) {
        if (getUtilManager().getConfig().getConfig().contains(args[2])) {
          getUtilManager().getConfig().set(args[2], null);
          getLogger().send(sender, "team.deleted", args[2].toUpperCase());
        } else getLogger().send(sender, "team.not-found", args[2].toUpperCase());
      } else getLogger().sendLong(sender, "team.usage.delete");
    } else getLogger().send(sender, "unknown-command");
    return true;
  }
}
