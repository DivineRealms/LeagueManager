package io.github.divinerealms.commands;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleCommand implements CommandExecutor {
  @Getter private final Logger logger;
  @Getter private final Helper helper;

  public ToggleCommand(final UtilManager utilManager) {
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.admin")) {
      getLogger().send(sender, "insufficient-permission");
    } else {
      final String groupName = "default", permission = "commandwhitelist.bypass.fc", path = "toggle";
      if (getHelper().groupHasPermission(groupName, permission)) {
        getHelper().groupRemovePermission(groupName, permission);
        getLogger().announceState(path, "off");
      } else {
        getHelper().groupAddPermission(groupName, permission);
        getLogger().announceState(path, "on");
      }
    }
    return true;
  }
}