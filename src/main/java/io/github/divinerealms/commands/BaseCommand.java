package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.commands.player.*;
import io.github.divinerealms.commands.team.CreateTeamCommand;
import io.github.divinerealms.commands.team.DeleteTeamCommand;
import io.github.divinerealms.commands.var.GiveAccessCommand;
import io.github.divinerealms.commands.var.RemoveAccessCommand;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BaseCommand implements CommandExecutor {
  private final LeagueManager instance;
  private final UtilManager utilManager;
  private final Logger logger;
  private final List<String> banner = new ArrayList<>();

  public BaseCommand(final LeagueManager instance, final UtilManager utilManager) {
    this.instance = instance;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, String label, String[] args) {
    if (args.length < 1) {
      sendBanner(sender);
      return true;
    } else if (args[0].equalsIgnoreCase("help")) {
      final HelpCommand helpCommand = new HelpCommand(getUtilManager());
      helpCommand.onCommand(sender, cmd, label, args);
      return true;
    } else {
      switch (args[0].toLowerCase()) {
        case "reload":
        case "rl":
          final ReloadCommand reloadCommand = new ReloadCommand(getInstance(), getUtilManager());
          reloadCommand.onCommand(sender, cmd, label, args);
          break;
        case "toggle":
          final ToggleCommand toggleCommand = new ToggleCommand(getInstance(), getUtilManager());
          toggleCommand.onCommand(sender, cmd, label, args);
          break;
        case "ban":
          final BanPlayerCommand banPlayerCommand = new BanPlayerCommand(getUtilManager());
          banPlayerCommand.onCommand(sender, cmd, label, args);
          break;
        case "unban":
          final UnbanPlayerCommand unbanPlayerCommand = new UnbanPlayerCommand(getUtilManager());
          unbanPlayerCommand.onCommand(sender, cmd, label, args);
          break;
        case "suspend":
          final SuspendCommand suspendCommand = new SuspendCommand(getUtilManager());
          suspendCommand.onCommand(sender, cmd, label, args);
          break;
        case "unsuspend":
          final UnsuspendCommand unsuspendCommand = new UnsuspendCommand(getUtilManager());
          unsuspendCommand.onCommand(sender, cmd, label, args);
          break;
        case "setteam":
        case "st":
          final SetTeamCommand setTeamCommand = new SetTeamCommand(getUtilManager());
          setTeamCommand.onCommand(sender, cmd, label, args);
          break;
        case "unsetteam":
        case "ut":
          final UnsetTeamCommand unsetTeamCommand = new UnsetTeamCommand(getUtilManager());
          unsetTeamCommand.onCommand(sender, cmd, label, args);
          break;
        case "setmanager":
        case "sm":
          final SetManagerCommand setManagerCommand = new SetManagerCommand(getUtilManager());
          setManagerCommand.onCommand(sender, cmd, label, args);
          break;
        case "unsetmanager":
        case "um":
          final UnsetManagerCommand unsetManagerCommand = new UnsetManagerCommand(getUtilManager());
          unsetManagerCommand.onCommand(sender, cmd, label, args);
          break;
        case "createteam":
        case "ct":
          final CreateTeamCommand createTeamCommand = new CreateTeamCommand(getInstance(), getUtilManager());
          createTeamCommand.onCommand(sender, cmd, label, args);
          break;
        case "deleteteam":
        case "dt":
          final DeleteTeamCommand deleteTeamCommand = new DeleteTeamCommand(getInstance(), getUtilManager());
          deleteTeamCommand.onCommand(sender, cmd, label, args);
          break;
        case "varadd":
        case "va":
          final GiveAccessCommand giveAccessCommand = new GiveAccessCommand(getUtilManager());
          giveAccessCommand.onCommand(sender, cmd, label, args);
          break;
        case "varremove":
        case "vr":
          final RemoveAccessCommand removeAccessCommand = new RemoveAccessCommand(getUtilManager());
          removeAccessCommand.onCommand(sender, cmd, label, args);
          break;
        default:
          getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
          break;
      }
    }
    return true;
  }

  private void sendBanner(final CommandSender sender) {
    if (sender instanceof Player) for (final String message : startupBanner())
      getLogger().send(sender, ChatColor.translateAlternateColorCodes('&', message));
    else getLogger().sendBanner();
  }

  private String[] startupBanner() {
    return new String[]{"&8▎ &r","&8▎ &d  88       &e8b      d8","&8▎ &d  88       &e88b   d88   &a" + getLogger().getPluginName(),"&8▎ &d  88    .o &e88YbdP88   &3Authors: &b" + getLogger().getAuthors(),"&8▎ &d  88ood8 &e88 Y||Y 88","&8▎ &r"};
  }
}