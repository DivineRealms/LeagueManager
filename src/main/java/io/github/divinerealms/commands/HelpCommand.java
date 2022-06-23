package io.github.divinerealms.commands;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
  @Getter private final Logger logger;

  public HelpCommand(final UtilManager utilManager) {
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    getLogger().sendLongMessage(sender, "help");
    return true;
  }
}