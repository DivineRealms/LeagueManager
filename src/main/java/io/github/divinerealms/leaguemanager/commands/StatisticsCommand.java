package io.github.divinerealms.leaguemanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
@Getter
@CommandAlias("statistics|stats")
public class StatisticsCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final DataManager dataManager;
  private final String playerData = "playerdata";

  public StatisticsCommand(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
  }

  @Default
  @CommandCompletion("@players")
  public void onDefault(CommandSender sender, String[] args) {
    if (args.length == 0) {
      if (!(sender instanceof Player)) {
        getLogger().send(sender, Lang.INGAME_ONLY.getConfigValue(null));
        return;
      }

      Player player = (Player) sender;
      UUID playerUUID = player.getUniqueId();

      getDataManager().setConfig(getPlayerData(), playerUUID.toString());
      getLogger().send(sender, Lang.STATISTICS.getConfigValue(new String[]{
          Lang.STATISTICS_SELF_TITLE.getConfigValue(null),
          String.valueOf(getDataManager().getConfig(playerUUID.toString()).getInt("goals", 0)),
          String.valueOf(getDataManager().getConfig(playerUUID.toString()).getInt("assists", 0)),
          String.valueOf(getDataManager().getConfig(playerUUID.toString()).getInt("yellow-cards", 0)),
          String.valueOf(getDataManager().getConfig(playerUUID.toString()).getInt("red-cards", 0)),
          String.valueOf(getDataManager().getConfig(playerUUID.toString()).getInt("clean-sheets", 0))
      }));
    } else if (args.length == 1) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (!getDataManager().configExists(getPlayerData(), target.getUniqueId().toString())) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      UUID targetUUID = target.getUniqueId();

      getDataManager().setConfig(getPlayerData(), targetUUID.toString());
      getLogger().send(sender, Lang.STATISTICS.getConfigValue(new String[]{
          Lang.STATISTICS_OTHER_TITLE.getConfigValue(new String[]{target.getName()}),
          String.valueOf(getDataManager().getConfig(targetUUID.toString()).getInt("goals", 0)),
          String.valueOf(getDataManager().getConfig(targetUUID.toString()).getInt("assists", 0)),
          String.valueOf(getDataManager().getConfig(targetUUID.toString()).getInt("yellow-cards", 0)),
          String.valueOf(getDataManager().getConfig(targetUUID.toString()).getInt("red-cards", 0)),
          String.valueOf(getDataManager().getConfig(targetUUID.toString()).getInt("clean-sheets", 0))
      }));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("leaderboard|lb|top")
  @CommandCompletion("goals|assists|yellow-cards|red-cards|clean-sheets")
  public void onLeaderBoard(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.LEADERBOARD_DEFAULT.getConfigValue(null));
    } else {
      switch (args[0]) {
        case "goals":
          getLogger().send(sender, Lang.LEADERBOARD_GOALS.getConfigValue(new String[]{

          }));
      }
    }
  }

  @Subcommand("add")
  @CommandPermission("leaguemanager.stats.add")
  @CommandCompletion("@players|goals|assists|yellow-cards|red-cards|clean-sheets")
  public void onAdd(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.STATISTICS_USAGE_ADD.getConfigValue(null));
    } else if (args.length == 3) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target == null || !target.hasPlayedBefore()) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      UUID targetUUID = target.getUniqueId();

      if (!getDataManager().configExists(getPlayerData(), targetUUID.toString())) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      getDataManager().setConfig(getPlayerData(), targetUUID.toString());
      String type = args[1];
      int value;

      try {
        value = Integer.parseInt(args[2]);
      } catch (NumberFormatException exception) {
        getLogger().send(sender, Lang.INVALID_VALUE.getConfigValue(null));
        return;
      }

      int existingValue = getDataManager().getConfig(targetUUID.toString()).getInt(type);
      getDataManager().getConfig(targetUUID.toString()).set(type, existingValue + value);
      getDataManager().saveConfig(targetUUID.toString());

      getLogger().send(sender, Lang.STATISTICS_ADDED.getConfigValue(new String[]{String.valueOf(value), type, target.getName()}));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("remove")
  @CommandPermission("leaguemanager.stats.remove")
  @CommandCompletion("@players|goals|assists|yellow-cards|red-cards|clean-sheets")
  public void onRemove(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.STATISTICS_USAGE_REMOVE.getConfigValue(null));
    } else if (args.length == 3) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target == null || !target.hasPlayedBefore()) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      if (!getDataManager().configExists(getPlayerData(), target.getName())) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      UUID targetUUID = target.getUniqueId();

      getDataManager().setConfig(getPlayerData(), targetUUID.toString());
      String type = args[1];
      int value;

      try {
        value = Integer.parseInt(args[2]);
      } catch (NumberFormatException exception) {
        getLogger().send(sender, Lang.INVALID_VALUE.getConfigValue(null));
        return;
      }

      int existingValue = getDataManager().getConfig(targetUUID.toString()).getInt(type);
      getDataManager().getConfig(targetUUID.toString()).set(type, existingValue - value);
      getDataManager().saveConfig(targetUUID.toString());

      getLogger().send(sender, Lang.STATISTICS_REMOVED.getConfigValue(new String[]{String.valueOf(value), type, target.getName()}));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("set")
  @CommandPermission("leaguemanager.stats.set")
  @CommandCompletion("@players|goals|assists|yellow-cards|red-cards|clean-sheets")
  public void onSet(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.STATISTICS_USAGE_SET.getConfigValue(null));
    } else if (args.length == 3) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target == null || !target.hasPlayedBefore()) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      UUID targetUUID = target.getUniqueId();

      if (!getDataManager().configExists(getPlayerData(), targetUUID.toString())) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      getDataManager().setConfig(getPlayerData(), targetUUID.toString());
      String type = args[1];
      int value;

      try {
        value = Integer.parseInt(args[2]);
      } catch (NumberFormatException exception) {
        getLogger().send(sender, Lang.INVALID_VALUE.getConfigValue(null));
        return;
      }

      getDataManager().getConfig(targetUUID.toString()).set(type, value);
      getDataManager().saveConfig(targetUUID.toString());

      getLogger().send(sender, Lang.STATISTICS_SET.getConfigValue(new String[]{String.valueOf(value), type, target.getName()}));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }
}
