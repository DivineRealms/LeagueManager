package io.github.divinerealms.commands.player;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.types.PermissionNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class BanPlayerCommand implements CommandExecutor {
  @Getter
  private final Helper helper;
  @Getter
  private final Logger logger;

  public BanPlayerCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.ban")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length <= 1 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.USER_HELP.getConfigValue(null));
      } else if (args.length >= 3) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final Time time = Time.parseString(args[2]);
        final String permission = "footcube.banned";

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          final PermissionNode node = PermissionNode.builder(permission).value(true).expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS).withContext("server", "football").build();

          getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
            final DataMutateResult result = user.data().add(node);

            if (result.wasSuccessful()) {
              if (args.length == 3) {
                getLogger().send(sender, Lang.USER_BAN.getConfigValue(new String[] { target.getName(), time.toString(), "Rule Breaking" }));
                if (target.isOnline())
                  getLogger().send(target.getPlayer(), Lang.USER_BANNED.getConfigValue(new String[] { time.toString(), "Rule Breaking" }));
              } else {
                final String reason = StringUtils.join(args, ' ', 3, args.length);
                getLogger().send(sender, Lang.USER_BAN.getConfigValue(new String[] { target.getName(), time.toString(), reason }));
                if (target.isOnline())
                  getLogger().send(target.getPlayer(), Lang.USER_BANNED.getConfigValue(new String[] { time.toString(), reason }));
              }
            } else
              getLogger().send(sender, Lang.USER_ALREADY_BANNED.getConfigValue(new String[] { target.getName() }));
          });
        } else getLogger().send(sender, Lang.USER_USAGE_BAN.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
