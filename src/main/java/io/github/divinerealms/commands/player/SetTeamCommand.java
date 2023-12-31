package io.github.divinerealms.commands.player;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@Getter
public class SetTeamCommand implements CommandExecutor {
  private final Helper helper;
  private final Logger logger;

  public SetTeamCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.setteam")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.USER_HELP.getConfigValue(null));
      } else if (args.length == 3) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final String name = args[2], nameUppercase = name.toUpperCase();

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          if (getHelper().groupExists(name.toLowerCase())) {
            if (!getHelper().playerInGroup(target.getUniqueId(), name.toLowerCase())) {
              getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
                for (final Group group : user.getInheritedGroups(user.getQueryOptions())) {
                  final int weight = 100, groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
                  if (groupWeight == weight) user.data().remove(InheritanceNode.builder(group.getName()).withContext("server", "football").build());
                }

                user.data().add(InheritanceNode.builder(name.toLowerCase()).withContext("server", "football").build());
              }).whenComplete((v, th) -> getLogger().log(Lang.USER_ADDED_TO_TEAM.getConfigValue(new String[]{target.getName(), nameUppercase}), "fcfa"));
            } else
              getLogger().send(sender, Lang.USER_ALREADY_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), nameUppercase}));
          } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{nameUppercase}));
        } else getLogger().send(sender, Lang.USER_USAGE_SET.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
