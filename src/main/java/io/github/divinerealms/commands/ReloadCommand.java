package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
  @Getter private final LeagueManager plugin;
  @Getter private final Logger logger;

  public ReloadCommand(final LeagueManager leagueManager, final UtilManager utilManager) {
    this.plugin = leagueManager;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    getPlugin().reload();
    getLogger().sendMessage(sender, "reload");
    return true;
  }
}