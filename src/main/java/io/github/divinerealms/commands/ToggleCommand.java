package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleCommand implements CommandExecutor {
  @Getter
  private final LeagueManager plugin;
  @Getter
  private final Logger logger;
  @Getter
  private final Helper helper;

  public ToggleCommand(final LeagueManager plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.toggle")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      String groupName = "default", permission = "commandwhitelist.bypass.fc", state;
      if (getHelper().groupHasPermission(groupName, permission)) {
        state = "off";
        getHelper().groupRemovePermission(groupName, permission);
      } else {
        state = "on";
        getHelper().groupAddPermission(groupName, permission);
      }
      getPlugin().getServer().broadcastMessage(Lang.TOGGLE.getConfigValue(new String[]{state}));
    }
    return true;
  }
}