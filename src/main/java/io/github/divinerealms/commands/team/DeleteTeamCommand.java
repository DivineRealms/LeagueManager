package io.github.divinerealms.commands.team;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@Getter
public class DeleteTeamCommand implements CommandExecutor {
  private final LuckPerms luckPermsAPI;
  private final Helper helper;
  private final Logger logger;

  public DeleteTeamCommand(final LeagueManager plugin, final UtilManager utilManager) {
    this.luckPermsAPI = plugin.getLuckPermsAPI();
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.deleteteam")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.TEAM_HELP.getConfigValue(null));
      } else if (args.length == 2) {
        final String name = args[1], nameUppercase = name.toUpperCase();
        final GroupManager groupManager = getLuckPermsAPI().getGroupManager();

        if (groupManager.isLoaded(name.toLowerCase())) {
          final Group group = getHelper().getGroup(name.toLowerCase());
          groupManager.deleteGroup(group);
          getLogger().log(Lang.TEAM_DELETED.getConfigValue(new String[]{nameUppercase}), "fcfa");
        } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{nameUppercase}));
      } else getLogger().send(sender, Lang.TEAM_USAGE_DELETE.getConfigValue(null));
    }
    return true;
  }
}
