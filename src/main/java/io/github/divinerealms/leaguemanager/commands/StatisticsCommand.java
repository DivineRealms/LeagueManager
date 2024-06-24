package io.github.divinerealms.leaguemanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.gui.impl.statistics.YellowCardsGUI;
import io.github.divinerealms.leaguemanager.gui.impl.statistics.StatisticsGUI;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.GUIManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@CommandAlias("leaderboard|lb|statistics|stats")
public class StatisticsCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  private final Logger logger;
  private final Helper helper;
  private final DataManager dataManager;

  public StatisticsCommand(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.guiManager = guiManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
  }

  @Default
  @Subcommand("gui")
  public void onOpen(Player player, String[] args) {
    if (args.length == 0) {
      getGuiManager().openGUI(new StatisticsGUI(getUtilManager(), getGuiManager()), player);
    } else if (args.length == 1) {
      Player target = Bukkit.getPlayer(args[0]);
      getDataManager().setFolderName("playerdata");
      getDataManager().setConfig(target.getName());
      if (!getDataManager().configExists(target.getName())) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }
      getGuiManager().setPlayerName(target.getName());
      getGuiManager().openGUI(new YellowCardsGUI(getUtilManager(), getGuiManager()), player);
    } else getLogger().send(player, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("add")
  public void onAdd(CommandSender sender, String[] args) {

  }

  @Subcommand("remove")
  public void onRemove(CommandSender sender, String[] args) {

  }
}
