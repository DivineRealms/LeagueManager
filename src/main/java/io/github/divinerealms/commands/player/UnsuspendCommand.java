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

@SuppressWarnings("deprecation")
@Getter
public class UnsuspendCommand implements CommandExecutor {
  private final Helper helper;
  private final Logger logger;

  public UnsuspendCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.unsuspend")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.USER_HELP.getConfigValue(null));
      } else if (args.length == 2) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          final InheritanceNode node = InheritanceNode.builder("suspend").withContext("server", "football").build();

          if (node.hasExpiry()) {
            getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
              final DataMutateResult result = user.data().remove(node);

              if (result.wasSuccessful()) {
                getLogger().send(sender, Lang.USER_UNSUSPEND.getConfigValue(new String[]{target.getName()}));
                if (target.isOnline())
                  getLogger().send(target.getPlayer(), Lang.USER_UNSUSPENDED.getConfigValue(null));
              } else
                getLogger().send(sender, Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
            });
          } else
            getLogger().send(target.getPlayer(), Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.USER_USAGE_UNSUSPEND.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
