package io.github.divinerealms.commands.team;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.WeightNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@Getter
public class CreateTeamCommand implements CommandExecutor {
  private final LuckPerms luckPermsAPI;
  private final Helper helper;
  private final Logger logger;

  public CreateTeamCommand(final LeagueManager plugin, final UtilManager utilManager) {
    this.luckPermsAPI = plugin.getLuckPermsAPI();
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.createteam")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.TEAM_HELP.getConfigValue(null));
      } else if (args.length == 3) {
        final String name = args[1], tag = args[2];
        final GroupManager groupManager = getLuckPermsAPI().getGroupManager();
        String nameA;

        if (name.length() == 4) nameA = name.substring(0, 3);
        else nameA = name;

        if (!groupManager.isLoaded(nameA.toLowerCase())) {
          groupManager.createAndLoadGroup(nameA).thenApplyAsync(group -> {
            if (name.endsWith("b")) {
              group.data().add(WeightNode.builder(99).withContext("server", "football").build());
              group.data().add(MetaNode.builder("b", tag).withContext("server", "football").build());
            } else {
              group.data().add(WeightNode.builder(100).withContext("server", "football").build());
              group.data().add(MetaNode.builder("team", tag).withContext("server", "football").build());
            }
            for (String permission : getHelper().getPermissions()) {
              permission = permission.replace("%team%", nameA.toLowerCase());
              group.data().add(PermissionNode.builder(permission).withContext("server", "football").build());
            }

            getLogger().send("fcfa", Lang.TEAM_CREATED.getConfigValue(new String[]{nameA.toUpperCase()}));
            return group;
          }).thenCompose(groupManager::saveGroup);
        } else getLogger().send(sender, Lang.TEAM_ALREADY_DEFINED.getConfigValue(new String[]{nameA.toUpperCase()}));
      } else getLogger().send(sender, Lang.TEAM_USAGE_CREATE.getConfigValue(null));
    }
    return true;
  }
}
