package io.github.divinerealms.commands.player;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetupPlayerCommand implements CommandExecutor {
  @Getter private final Helper helper;
  @Getter private final Logger logger;

  public SetupPlayerCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.setup")) {
      getLogger().sendMessage(sender, "insufficient-permission");
    } else {
      if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLongMessage(sender, "user.help");
      } else if (args.length == 3) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final String name = args[2], nameUppercase = name.toUpperCase();

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().sendMessage(sender, "user.not-found");
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          // is player in database?
          if (getHelper().groupExists(name)) {
            if (!getHelper().playerInGroup(target.getUniqueId(), name)) {
              getHelper().playerAddGroup(target.getUniqueId(), name);
              getLogger().sendMessage(sender, target.getName(), "user.added-to-team", nameUppercase);
            } else getLogger().sendMessage(sender, target.getName(), "user.already-in-that-team", nameUppercase);
          } else getLogger().sendMessage(sender, "team.not-found", nameUppercase);
        } else getLogger().sendLongMessage(sender, "user.usage.set");
      } else getLogger().sendLongMessage(sender, "unknown-command");
    }
    return true;
  }
}
