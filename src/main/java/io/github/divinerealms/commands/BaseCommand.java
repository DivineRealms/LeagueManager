package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BaseCommand implements CommandExecutor {
  @Getter private final LeagueManager leagueManager;
  @Getter private final UtilManager utilManager;
  @Getter private final Logger logger;

  public BaseCommand(final LeagueManager leagueManager, final UtilManager utilManager) {
    this.leagueManager = leagueManager;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
      final HelpCommand helpCommand = new HelpCommand(getUtilManager());
      helpCommand.onCommand(sender, command, label, args);
    } else if (args[0].equalsIgnoreCase("reload")) {
      final ReloadCommand reloadCommand = new ReloadCommand(getLeagueManager(), getUtilManager());
      reloadCommand.onCommand(sender, command, label, args);
    } else if (args[0].equalsIgnoreCase("team") || args[0].equalsIgnoreCase("t")) {
      final TeamCommand teamCommand = new TeamCommand(getUtilManager());
      teamCommand.onCommand(sender, command, label, args);
    } else if (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("u")) {
      final UserCommand userCommand = new UserCommand(getUtilManager());
      userCommand.onCommand(sender, command, label, args);
    } else getLogger().send(sender, "unknown-command");
    return true;
  }
}