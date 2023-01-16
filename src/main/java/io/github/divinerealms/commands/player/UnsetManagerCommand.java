package io.github.divinerealms.commands.player;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnsetManagerCommand implements CommandExecutor {
  @Getter
  private final Helper helper;
  @Getter
  private final Logger logger;

  public UnsetManagerCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.unsetmanager")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.USER_HELP.getConfigValue(null));
      } else if (args.length == 3) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final String name = args[2], nameUppercase = name.toUpperCase(),
            permission = "tab.group." + name.toLowerCase() + "-director";

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          if (getHelper().groupExists("director")) {
            if (getHelper().playerInGroup(target.getUniqueId(), "director")) {
              getHelper().playerRemovePermission(target.getUniqueId(), permission);
              getHelper().playerRemoveGroup(target.getUniqueId(), "director");
              getLogger().log(Lang.USER_UNSET_MANAGER.getConfigValue(new String[]{target.getName(), nameUppercase}));
            } else
              getLogger().send(sender, Lang.USER_NOT_MANAGER.getConfigValue(new String[]{target.getName(), nameUppercase}));
          } else
            getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{nameUppercase}));
        } else getLogger().send(sender, Lang.USER_USAGE_UNSET_MANAGER.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
