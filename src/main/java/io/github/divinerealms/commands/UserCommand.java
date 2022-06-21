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
    if (!sender.hasPermission("leaguemanager.admin")) {
      getLogger().send(sender, "insufficient-permission");
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLong(sender, "user.help");
      } else {
        //noinspection deprecation
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target.hasPlayedBefore()) {
          if (args[1].equalsIgnoreCase(target.getName())) {
            if (args[2].equalsIgnoreCase("set")) {
              if (args.length >= 4) {
                final String teamName = args[3].toLowerCase(),
                    teamTag = getConfig().getString(args[3] + ".main"),
                    branchTag = getConfig().getString(args[3] + ".juniors");
                if (getConfig().getConfig().contains(args[3])) {
                  if (!getHelper().hasPermission(target.getUniqueId(), "chatcontrol.channel." + teamName)) {
                    for (final String permission : getHelper().getPermissions())
                      getHelper().addPermission(target.getUniqueId(), permission.replace("%team%", teamName));
                    if (args.length == 4) {
                      getHelper().setMeta(target.getUniqueId(), "team", teamTag);
                      getLogger().send(sender, args[1], "user.added-to-team", teamTag);
                    } else if (args.length == 5 && args[4].equalsIgnoreCase("true")) {
                      if (branchTag.equals(" &aB")) getHelper().setMeta(target.getUniqueId(), "team-b", branchTag);
                      else getHelper().setMeta(target.getUniqueId(), "team", branchTag);
                      getLogger().send(sender, args[1], "user.added-to-team", branchTag);
                    } else getLogger().sendLong(sender, "user.usage.set");
                  } else getLogger().send(sender, args[1], "user.already-in-that-team", args[3].toUpperCase());
                } else getLogger().send(sender, "team.not-found", args[3].toUpperCase());
              } else getLogger().sendLong(sender, "user.usage.set");
            } else if (args[2].equalsIgnoreCase("unset")) {
              if (args.length == 4) {
                final String teamName = args[3].toLowerCase();
                if (getConfig().getConfig().contains(args[3])) {
                  if (getHelper().hasPermission(target.getUniqueId(), "chatcontrol.channel." + teamName)) {
                    for (final String permission : getHelper().getPermissions())
                      getHelper().unsetPermission(target.getUniqueId(), permission
                          .replace("%team%", teamName));
                    getHelper().unsetMeta(target.getUniqueId(), "team");
                    getHelper().unsetMeta(target.getUniqueId(), "team-b");
                    getLogger().send(sender, args[1], "user.removed-from-a-team", args[3].toUpperCase());
                  } else getLogger().send(sender, args[1], "user.not-in-that-team", args[3].toUpperCase());
                } else getLogger().send(sender, "team.not-found", args[3].toUpperCase());
              } else getLogger().sendLong(sender, "user.usage.unset");
            } else getLogger().send(sender, "unknown-command");
          } else getLogger().sendLong(sender, "user.help");
        } else getLogger().send(sender, "user.not-found");
      }
    }
    return true;
  }
}
