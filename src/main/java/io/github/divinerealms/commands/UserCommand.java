package io.github.divinerealms.commands;

import io.github.divinerealms.configs.Config;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UserCommand implements CommandExecutor {
  @Getter private final Config config;
  @Getter private final Logger logger;
  @Getter private final Helper helper;

  public UserCommand(final UtilManager utilManager) {
    this.config = utilManager.getConfig();
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leagueManager.admin")) {
      getLogger().send(sender, "insufficient-permission");
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLong(sender, "user.help");
      } else {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target.hasPlayedBefore()) {
          if (args[1].equalsIgnoreCase(target.getName())) {
            if (args[2].equalsIgnoreCase("set")) {
              if (args.length == 5) {
                getHelper().addToTeam(target.getUniqueId(), args[3], args[4]);
                getLogger().send(sender, args[1], "user.added-to-team", getConfig().getString(args[3]));
              } else getLogger().sendLong(sender, "user.help");
            } else if (args[2].equalsIgnoreCase("unset")) {
              if (args.length == 4) {
                getHelper().removeFromATeam(target.getUniqueId(), args[3]);
                getLogger().send(sender, args[1], "user.removed-from-a-team", getConfig().getString(args[3]));
              } else getLogger().sendLong(sender, "user.help");
            } else getLogger().send(sender, "unknown-command");
          } else getLogger().send(sender, "unknown-command");
        } else getLogger().send(sender, "user.not-found");
      }
    }
    return true;
  }
}
