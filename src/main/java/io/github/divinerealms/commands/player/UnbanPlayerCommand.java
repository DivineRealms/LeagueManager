package io.github.divinerealms.commands.player;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanPlayerCommand implements CommandExecutor {
  @Getter
  private final Helper helper;
  @Getter
  private final Logger logger;

  public UnbanPlayerCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.unban")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.USER_HELP.getConfigValue(null));
      } else if (args.length == 2) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final String permission = "footcube.banned";

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          final User user = getHelper().getPlayer(target.getUniqueId());
          final Node node = user.getCachedData().getPermissionData().queryPermission(permission).node();

          if (node != null && node.hasExpiry()) {
            getHelper().getUserManager().modifyUser(target.getUniqueId(), user1 -> {
              final DataMutateResult result = user1.data().remove(node);

              if (result.wasSuccessful()) {
                getLogger().send(sender, Lang.USER_UNBAN.getConfigValue(new String[]{target.getName()}));
                if (target.isOnline())
                  getLogger().send(target.getPlayer(), Lang.USER_UNBANNED.getConfigValue(null));
              } else
                getLogger().send(sender, Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
            });
          } else
            getLogger().send(target.getPlayer(), Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.USER_USAGE_UNBAN.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
