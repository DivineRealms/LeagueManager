package io.github.divinerealms.commands.var;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

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
      } else if (args.length <= 3) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          if (!getHelper().playerInGroup(target.getUniqueId(), "group._var")) {
            if (args.length == 2) {
              final Node node = Node.builder("group._var").build();

              getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
                final DataMutateResult result = user.data().add(node);

                if (result.wasSuccessful())
                  getLogger().log(Lang.VAR_GIVEN_ACCESS.getConfigValue(new String[]{target.getName()}), "fcfa");
                else
                  getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
              });
            } else {
              Time time;

              try {
                time = Time.parseString(args[2]);
              } catch (Time.TimeParseException | NullPointerException e) {
                getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
                return true;
              }

              final Node node = Node.builder("group._var").expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS).build();

              getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
                final DataMutateResult result = user.data().add(node);

                if (result.wasSuccessful())
                  getLogger().log(Lang.VAR_GIVEN_ACCESS_1.getConfigValue(new String[]{target.getName(), time.toString()}), "fcfa");
                else
                  getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
              });
            }
          }
        } else getLogger().send(sender, Lang.VAR_USAGE_ADD.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
