package io.github.divinerealms.commands.team;

import io.github.divinerealms.LeagueManager;
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

public class CreateTeamCommand implements CommandExecutor {
  @Getter
  private final LuckPerms luckPermsAPI;
  @Getter
  private final Helper helper;
  @Getter
  private final Logger logger;

  public CreateTeamCommand(final LeagueManager plugin, final UtilManager utilManager) {
    this.luckPermsAPI = plugin.getLuckPermsAPI();
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.createteam")) {
      getLogger().sendMessage(sender, "insufficient-permission");
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLongMessage(sender, "team.help");
      } else if (args.length == 3) {
        final String name = args[1], tag = args[2], nameUppercase = name.toUpperCase();
        final boolean isBranch = name.endsWith("b");
        final GroupManager groupManager = getLuckPermsAPI().getGroupManager();

        if (!groupManager.isLoaded(name)) {
          groupManager.createAndLoadGroup(name).thenApplyAsync(group -> {
            group.data().add(WeightNode.builder(100).build());
            group.data().add(MetaNode.builder("team", tag).build());
            if (isBranch) group.data().add(MetaNode.builder("team-b", "&a B").build());

            for (final String permission : getHelper().getPermissions()) {
              final String branch = isBranch ? name.replaceAll("b$", "") : name, formattedPermission = permission.replace("%team%", branch);
              group.data().add(PermissionNode.builder(formattedPermission).build());
            }

            getLogger().sendMessage(sender, "team.created", nameUppercase);
            return group;
          }).thenCompose(groupManager::saveGroup);
        } else getLogger().sendMessage(sender, "team.already-defined", nameUppercase);
      } else getLogger().sendLongMessage(sender, "team.usage.create");
    }
    return true;
  }
}
