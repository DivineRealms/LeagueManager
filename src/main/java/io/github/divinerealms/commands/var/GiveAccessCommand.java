package io.github.divinerealms.commands.var;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GiveAccessCommand implements CommandExecutor {
  @Getter
  private final Helper helper;
  @Getter
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
          if (!getHelper().playerInGroup(target.getUniqueId(), "var")) {
            if (args.length == 2) {
              getHelper().playerAddGroup(target.getUniqueId(), "var", "global");
              getLogger().send(sender, Lang.VAR_GIVEN_ACCESS.getConfigValue(new String[]{target.getName()}));
            } else {
              final Time time = Time.parseString(args[2]);
              getHelper().playerAddTempGroup(target.getUniqueId(), "var", time);
              getLogger().send(sender, Lang.VAR_GIVEN_ACCESS.getConfigValue(new String[]{target.getName(), time.toString()}));
            }
          } else
            getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.VAR_USAGE_ADD.getConfigValue(null));
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
    }
    return true;
  }
}
