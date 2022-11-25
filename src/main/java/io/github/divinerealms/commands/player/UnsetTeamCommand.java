package io.github.divinerealms.commands.player;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnsetTeamCommand implements CommandExecutor {
  @Getter
  private final Helper helper;
  @Getter
  private final Logger logger;

  public UnsetTeamCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.unsetteam")) {
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
          if (getHelper().groupExists(name)) {
            getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
              final DataMutateResult result = user.data().remove(InheritanceNode.builder(name).withContext("server", "football").build());
              if (result.wasSuccessful())
                getLogger().log(Lang.USER_REMOVED_FROM_A_TEAM.getConfigValue(new String[]{target.getName(), nameUppercase}));
              else
                getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), nameUppercase}));
            });
          } else
            getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{nameUppercase}));
        } else getLogger().send(sender, Lang.USER_USAGE_UNSET.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
