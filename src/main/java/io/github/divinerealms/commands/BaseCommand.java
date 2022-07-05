package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.commands.player.BanPlayerCommand;
import io.github.divinerealms.commands.player.SetTeamCommand;
import io.github.divinerealms.commands.player.UnbanPlayerCommand;
import io.github.divinerealms.commands.player.UnsetTeamCommand;
import io.github.divinerealms.commands.team.CreateTeamCommand;
import io.github.divinerealms.commands.team.DeleteTeamCommand;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BaseCommand implements CommandExecutor {
  @Getter private final LeagueManager plugin;
  @Getter private final UtilManager utilManager;
  @Getter private final Logger logger;

  public BaseCommand(final LeagueManager plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("leaguemanager.admin")) {
      getLogger().sendMessage(sender, "insufficient-permission");
    } else {
      if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
        final HelpCommand helpCommand = new HelpCommand(getUtilManager());
        helpCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("reload")) {
        final ReloadCommand reloadCommand = new ReloadCommand(getPlugin(), getUtilManager());
        reloadCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("toggle")) {
        final ToggleCommand toggleCommand = new ToggleCommand(getUtilManager());
        toggleCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("ban")) {
        final BanPlayerCommand banPlayerCommand = new BanPlayerCommand(getUtilManager());
        banPlayerCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("unban")) {
        final UnbanPlayerCommand unbanPlayerCommand = new UnbanPlayerCommand(getUtilManager());
        unbanPlayerCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("setTeam") || args[0].equalsIgnoreCase("st")) {
        final SetTeamCommand setTeamCommand = new SetTeamCommand(getUtilManager());
        setTeamCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("unsetTeam") || args[0].equalsIgnoreCase("ut")) {
        final UnsetTeamCommand unsetTeamCommand = new UnsetTeamCommand(getUtilManager());
        unsetTeamCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("createTeam") || args[0].equalsIgnoreCase("ct")) {
        final CreateTeamCommand createTeamCommand = new CreateTeamCommand(getPlugin(), getUtilManager());
        createTeamCommand.onCommand(sender, command, label, args);
      } else if (args[0].equalsIgnoreCase("deleteTeam") || args[0].equalsIgnoreCase("dt")) {
        final DeleteTeamCommand deleteTeamCommand = new DeleteTeamCommand(getPlugin(), getUtilManager());
        deleteTeamCommand.onCommand(sender, command, label, args);
      } else getLogger().sendMessage(sender, "unknown-command");
    }
    return true;
  }
}