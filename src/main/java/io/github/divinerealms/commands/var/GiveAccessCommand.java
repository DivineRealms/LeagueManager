package io.github.divinerealms.commands.var;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
@Getter
public class GiveAccessCommand implements CommandExecutor {
  private final Helper helper;
  private final Logger logger;

  public GiveAccessCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.var.give")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length <= 1 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.VAR_HELP.getConfigValue(null));
      } else if (args.length == 3) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          final User user = getHelper().getPlayer(target.getUniqueId());
          final boolean hasPermission = user.getCachedData().getPermissionData().checkPermission("vulcan.bypass.client-brand.whitelist").asBoolean();
          if (!hasPermission) {
            Time time;

            try {
              time = Time.parseString(args[2]);
            } catch (Time.TimeParseException | NullPointerException e) {
              getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
              return true;
            }

            final PermissionNode tempPermission = PermissionNode.builder("vulcan.bypass.client-brand.whitelist")
                .expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS).build();
            getHelper().getUserManager().modifyUser(target.getUniqueId(), u -> {
              final DataMutateResult result = u.data().add(tempPermission);

              if (result.wasSuccessful())
                getLogger().send("fcfa", Lang.VAR_GIVEN_ACCESS_1.getConfigValue(new String[]{target.getName(), time.toString()}));
              else
                getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
            });
          } else getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.VAR_USAGE_ADD.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
