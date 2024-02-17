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
  @CommandPermission("leaguemanager.command.rosters")
  public void onOpen(Player player) {
    getGuiManager().openGUI(new RostersGUI(getUtilManager()), player);
  }

  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("leaguemanager.command.rosters.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("create")
  @CommandPermission("leaguemanager.command.rosters.create")
  public void onCreate(CommandSender sender, String[] args) {
    // todo
  }

  @Subcommand("delete")
  @CommandPermission("leaguemanager.command.rosters.delete")
  public void onDelete(CommandSender sender, String[] args) {
    // todo
  }

  @Subcommand("add")
  @CommandPermission("leaguemanager.command.rosters.add")
  public void onAdd(CommandSender sender, String[] args) {
    // todo
  }

  @Subcommand("remove")
  @CommandPermission("leaguemanager.command.rosters.remove")
  public void onRemove(CommandSender sender, String[] args) {
    // todo
  }
}
