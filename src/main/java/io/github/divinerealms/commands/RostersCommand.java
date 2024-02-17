package io.github.divinerealms.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.gui.impl.RostersGUI;
import io.github.divinerealms.managers.GUIManager;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@CommandAlias("rosters|rt")
@CommandPermission("leaguemanager.command.rosters")
public class RostersCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  private final Logger logger;

  public RostersCommand(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.guiManager = guiManager;
  }

  @Default
  @Subcommand("gui")
  public void onOpen(Player player) {
    getGuiManager().openGUI(new RostersGUI(getUtilManager()), player);
  }

  @CatchUnknown
  @Subcommand("help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
  }
}
