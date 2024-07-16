package io.github.divinerealms.leaguemanager.commands.timers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.leaguemanager.configs.Config;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.*;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.io.IOException;

@Getter
@CommandAlias("result|rs")
@CommandPermission("leaguemanager.command.result")
public class ResultCommand extends BaseCommand {
  private final Plugin plugin;
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private Time time, extraTime;
  private String home, away, prefix, cleanPrefix;
  private int home_result, away_result, extraTimeNew, timerId;
  private static String HOME_NAME, AWAY_NAME;
  private boolean secondHalf = false, league = false;
  private static YamlConfiguration config;
  private DiscordWebhook webhook = null;
  private String matchTime = null;
  private final DataManager dataManager;

  public ResultCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(plugin);
    config = Config.getConfig("config.yml");

    if (config.getStringList("discordWebhookURL") != null)
      webhook = new DiscordWebhook(config.getStringList("discordWebhookURL"));

    reset();
  }

  @Default
  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("leaguemanager.command.result.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
  }

  @Subcommand("start|s")
  @CommandPermission("leaguemanager.command.result.start")
  public void onStart(CommandSender sender) {
    if (!getUtilManager().isTaskQueued(getTimerId()) && isSetup() && !Timer.isRunning()) {
      timerId = firstHalf().startTask();
      Timer.isRunning = true;
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      getLogger().send("fcfa", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(getTimerId())}));
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("stop")
  @CommandPermission("leaguemanager.command.result.stop")
  public void onStop(CommandSender sender) {
    if (getUtilManager().isTaskQueued(getTimerId())) {
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      getLogger().send("default", Lang.RESULT_OVER.getConfigValue(new String[]{matchTime, getPrefix(), home, "" + home_result, "" + away_result, away}));
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_ENDED.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME, matchTime}));
        try {
          webhook.execute();
        } catch (IOException e) {
          getLogger().send("hoster", e.getMessage());
        }
      }
      secondHalf().getAfterTimer().run();
      Bukkit.getScheduler().cancelTasks(getPlugin());
      reset();
      Location spawn = new Location(sender.getServer().getWorld("world"), -8374.500, 9.00, -6040.500);
      Player player = (Player) sender;
      player.teleport(spawn);
      Bukkit.dispatchCommand(player, "setspawn");
      getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> Bukkit.dispatchCommand(player, "back"), 10L);
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("pause|p")
  @CommandPermission("leaguemanager.command.result.pause")
  public void onPause(CommandSender sender) {
    if (getUtilManager().isTaskQueued(getTimerId())) {
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      getLogger().send("fcfa", Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(getTimerId())}));
      firstHalf().cancelTask(getTimerId());
      secondHalf = true;
      timerId = halfTime().startTask();
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("resume|r")
  @CommandPermission("leaguemanager.command.result.resume")
  public void onResume(CommandSender sender) {
    if (getUtilManager().isTaskQueued(getTimerId())) {
      matchTime = UtilManager.formatTime(Timer.getSeconds() + 1);
      halfTime().cancelTask(getTimerId());
      timerId = secondHalf().startTask();
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_SECONDHALF.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME}));
        try {
          webhook.execute();
        } catch (IOException e) {
          getLogger().send("hoster", e.getMessage());
        }
      }
      getLogger().send("hoster", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(getTimerId())}));
      getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> Timer.secondsParsed = (Timer.getSeconds() - 60) / 2, 20L);
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("extend")
  @CommandCompletion("add|remove")
  @CommandPermission("leaguemanager.command.result.extend")
  public void onExtend(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(getTimerId())) {
      if (args.length < 1) {
        getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
        return;
      }
      try {
        extraTime = Time.parseString(args[1]);
      } catch (Time.TimeParseException timeParseException) {
        getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[0]}));
        return;
      }
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      switch (args[0]) {
        case "add":
          Timer.seconds = (int) (Timer.getSeconds() + extraTime.toSeconds());
          if (isSecondHalf()) extraTimeNew = (int) (Timer.seconds - time.toSeconds()) - 60;
          else extraTimeNew = (int) (Timer.seconds - time.toSeconds());
          break;
        case "remove":
          Timer.seconds = (int) (Timer.getSeconds() - extraTime.toSeconds());
          if (isSecondHalf()) extraTimeNew = (int) (Timer.seconds - time.toSeconds()) - 60;
          else extraTimeNew = (int) (Timer.seconds - time.toSeconds());
          break;
        default:
          break;
      }
      getLogger().send("fcfa", Lang.TIMER_TIME_SET.getConfigValue(new String[]{UtilManager.formatTime(extraTimeNew)}));
    }
  }

  @Subcommand("add")
  @CommandCompletion("home|away|@players")
  @CommandPermission("leaguemanager.command.result.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(getTimerId()) && (args.length == 2 || args.length == 3)) {
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      if (args[0].equalsIgnoreCase("home")) {
        home_result++;
        if (args.length == 2)
          getLogger().send("default", Lang.RESULT_ADD.getConfigValue(new String[]{args[1], home}));
        else getLogger().send("default", Lang.RESULT_ADD_ASSIST.getConfigValue(new String[]{args[1], home, args[2]}));
        if (webhook != null && isLeague()) {
          webhook.setContent("");
          if (args.length == 2) {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.decode(Lang.WEBHOOK_MATCH_SCORE_COLOR.getConfigValue(null)))
                .setAuthor(Lang.WEBHOOK_MATCH_SCORE_AUTHOR_NAME.getConfigValue(new String[]{HOME_NAME}), null, Lang.WEBHOOK_MATCH_SCORE_AUTHOR_ICON.getConfigValue(null))
                .setDescription(Lang.WEBHOOK_MATCH_SCORE_DESC.getConfigValue(new String[]{args[1], HOME_NAME, matchTime, HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME})));
          } else {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.decode(Lang.WEBHOOK_MATCH_SCORE_COLOR.getConfigValue(null)))
                .setAuthor(Lang.WEBHOOK_MATCH_SCORE_AUTHOR_NAME.getConfigValue(new String[]{HOME_NAME}), null, Lang.WEBHOOK_MATCH_SCORE_AUTHOR_ICON.getConfigValue(null))
                .setDescription(Lang.WEBHOOK_MATCH_ASSIST.getConfigValue(new String[]{args[1], HOME_NAME, matchTime, args[2], HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME})));
          } try {
            webhook.execute();
          } catch (IOException e) {
            getLogger().send(sender, e.getMessage());
          }
        } else getLogger().send(sender, Lang.WEBHOOK_NOT_SETUP.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("away")) {
        away_result++;
        if (args.length == 2)
          getLogger().send("default", Lang.RESULT_ADD.getConfigValue(new String[]{args[1], away}));
        else getLogger().send("default", Lang.RESULT_ADD_ASSIST.getConfigValue(new String[]{args[1], away, args[2]}));
        if (webhook != null && isLeague()) {
          webhook.setContent("");
          if (args.length == 2) {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.decode(Lang.WEBHOOK_MATCH_SCORE_COLOR.getConfigValue(null)))
                .setAuthor(Lang.WEBHOOK_MATCH_SCORE_AUTHOR_NAME.getConfigValue(new String[]{AWAY_NAME}), null, Lang.WEBHOOK_MATCH_SCORE_AUTHOR_ICON.getConfigValue(null))
                .setDescription(Lang.WEBHOOK_MATCH_SCORE_DESC.getConfigValue(new String[]{args[1], AWAY_NAME, matchTime, HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME})));
          } else {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.decode(Lang.WEBHOOK_MATCH_SCORE_COLOR.getConfigValue(null)))
                .setAuthor(Lang.WEBHOOK_MATCH_SCORE_AUTHOR_NAME.getConfigValue(new String[]{AWAY_NAME}), null, Lang.WEBHOOK_MATCH_SCORE_AUTHOR_ICON.getConfigValue(null))
                .setDescription(Lang.WEBHOOK_MATCH_ASSIST.getConfigValue(new String[]{args[1], AWAY_NAME, matchTime, args[2], HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME})));
          } try {
            webhook.execute();
          } catch (IOException e) {
            getLogger().send(sender, e.getMessage());
          }
        } else getLogger().send(sender, Lang.WEBHOOK_NOT_SETUP.getConfigValue(null));
      } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("remove")
  @CommandCompletion("home|away")
  @CommandPermission("leaguemanager.command.result.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(getTimerId()) && args.length == 1) {
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      if (args[0].equalsIgnoreCase("home")) {
        if (home_result != 0) {
          home_result--;
          getLogger().send("fcfa", Lang.RESULT_REMOVE.getConfigValue(new String[]{home}));
        } else getLogger().send(sender, Lang.RESULT_ELIMINATED.getConfigValue(new String[]{home}));
      } else if (args[0].equalsIgnoreCase("away")) {
        if (away_result != 0) {
          away_result--;
          getLogger().send("fcfa", Lang.RESULT_REMOVE.getConfigValue(new String[]{away}));
        } else getLogger().send(sender, Lang.RESULT_ELIMINATED.getConfigValue(new String[]{away}));
      } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("time")
  @CommandPermission("leaguemanager.command.result.time")
  public void onTime(CommandSender sender, String[] args) {
    if (!getUtilManager().isTaskQueued(getTimerId())) {
      try {
        time = Time.parseString(args[0]);
        if (time.toSeconds() < Time.parseString("10min").toSeconds())
          time = Time.parseString("10min");
        getLogger().send("fcfa", Lang.TIMER_TIME_SET.getConfigValue(new String[]{UtilManager.formatTime((int) time.toSeconds())}));
      } catch (Time.TimeParseException timeParseException) {
        getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[1]}));
      }
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("teams")
  @CommandPermission("leaguemanager.command.result.teams")
  public void onTeams(CommandSender sender, String[] args) {
    if (args.length == 2) {
      HOME_NAME = args[0].toUpperCase();
      AWAY_NAME = args[1].toUpperCase();
      league = false;
      if (sender.hasPermission("group.fcfa")) {
        if (!getHelper().groupExists(args[0]) && !getHelper().groupExists(args[1])) {
          league = false;
          home = args[0];
          away = args[1];
        } else {
          if (getPrefix().equals("&bEvent")) {
            getLogger().send(sender, Lang.WEBHOOK_PREFIX_NOT_SETUP.getConfigValue(null));
            return;
          }
          league = true;
          home = getHelper().groupHasMeta(args[0], "team") ?
              getHelper().getGroupMeta(args[0], "team") :
              getHelper().groupHasMeta(args[0], "b") ?
                  getHelper().getGroupMeta(args[0], "b") : HOME_NAME;
          away = getHelper().groupHasMeta(args[1], "team") ?
              getHelper().getGroupMeta(args[1], "team") :
              getHelper().groupHasMeta(args[1], "b") ?
                  getHelper().getGroupMeta(args[1], "b") : AWAY_NAME;
          Bukkit.dispatchCommand(sender, "warp " + args[0] + "top");
          Bukkit.dispatchCommand(sender, "setspawn");
          for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () ->
                Bukkit.dispatchCommand(player, "spawn"), 20L);
          }
        }
      } else {
        league = false;
        home = args[0];
        away = args[1];
      }
      getLogger().send(sender, Lang.TIMER_TEAMS_SET.getConfigValue(new String[]{home, away}));
      if (!isLeague()) return;
      if (webhook != null) {
        webhook.setContent(Lang.WEBHOOK_TEAMS_SET.getConfigValue(new String[]{getCleanPrefix(), HOME_NAME, AWAY_NAME}));
        try {
          webhook.execute();
        } catch (IOException e) {
          getLogger().send(sender, e.getMessage());
        }
      } else getLogger().send(sender, Lang.WEBHOOK_NOT_SETUP.getConfigValue(null));
    } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
  }

  @Subcommand("prefix")
  @CommandPermission("leaguemanager.command.result.prefix")
  public void onPrefix(CommandSender sender, String[] args) {
    prefix = getUtilManager().color(StringUtils.join(args, " ", 0, args.length));
    cleanPrefix = ChatColor.stripColor(prefix);
    getLogger().send("fcfa", Lang.TIMER_PREFIX_SET.getConfigValue(new String[]{getPrefix()}));
  }

  private Timer firstHalf() {
    return new Timer(getPlugin(), (int) time.toSeconds(), () -> {
      matchTime = UtilManager.formatTime(Timer.getSecondsParsed());
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_START.getConfigValue(new String[]{matchTime, HOME_NAME, AWAY_NAME}));
        try {
          webhook.execute();
        } catch (IOException e) {
          getLogger().send("hoster", e.getMessage());
        }
      }
      getLogger().send("default", Lang.TIMER_STARTING.getConfigValue(new String[]{getPrefix()}));
      getLogger().broadcastBar(Lang.RESULT_STARTING.getConfigValue(new String[]{getPrefix()}));
    }, () -> Timer.isRunning = false, (t) -> {
      String secondsParsed = UtilManager.formatTime(Timer.getSecondsParsed());
      String seconds = UtilManager.formatTime(Timer.seconds);
      String extraTimeString = UtilManager.formatTime(extraTimeNew);
      String formatted, color;

      if (Timer.getSeconds() != (int) time.toSeconds())
        formatted = getUtilManager().color("&7 ┃ &c(+" + extraTimeString + " ET)");
      else formatted = "";

      if (Timer.getSecondsParsed() > (time.toSeconds() / 2)) color = getUtilManager().color("&c");
      else color = getUtilManager().color("&a");

      getLogger().broadcastBar(Lang.RESULT_ACTIONBAR.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away, color, secondsParsed, seconds, formatted}));
    });
  }

  private Timer halfTime() {
    return new Timer(getPlugin(), 600, () -> {
      matchTime = UtilManager.formatTime(Timer.getSeconds());
      getLogger().send("default", Lang.RESULT_HALFTIME.getConfigValue(new String[]{matchTime, getPrefix(), home, "" + home_result, "" + away_result, away}));
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_HALFTIME.getConfigValue(new String[]{matchTime, HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME}));
        try {
          webhook.execute();
        } catch (IOException e) {
          getLogger().send("hoster", e.getMessage());
        }
      }
    }, () -> Timer.isRunning = false, (t ->
        getLogger().broadcastBar(Lang.RESULT_ACTIONBAR_HT.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away})))
    );
  }

  private Timer secondHalf() {
    return new Timer(getPlugin(), (int) (time.toSeconds() + 60), () -> getLogger().send("default", Lang.RESULT_SECONDHALF.getConfigValue(new String[]{matchTime, getPrefix(), home, "" + home_result, "" + away_result, away})), () -> {
      if (isLeague()) {
        if (webhook != null) {
          if (home_result != away_result) {
            getDataManager().setConfig("teamdata", "main");
            webhook.setContent(getDataManager().getConfig("main").getString(home_result > away_result ?
                HOME_NAME + ".win-video" : away_result > home_result ?
                AWAY_NAME + ".win-video" : null));
            try {
              webhook.execute();
            } catch (IOException e) {
              getLogger().send("hoster", e.getMessage());
            }
          }
        }
      }
      getLogger().broadcastBar(Lang.RESULT_END.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away}));
      reset();
    }, (t) -> {
      String secondsParsed = UtilManager.formatTime(Timer.getSecondsParsed());
      String seconds = UtilManager.formatTime(Timer.getSeconds() - 60);
      String extraTimeString = UtilManager.formatTime(extraTimeNew);
      String formatted = "", color = "&a";

      // if there's et
      if ((Timer.getSeconds() - 60) != (int) time.toSeconds()) {
        if (extraTimeNew != 0) formatted = getUtilManager().color("&7 ┃ &e2HT &c(+" + extraTimeString + " ET)");
        else color = getUtilManager().color("&a");
      } else formatted = getUtilManager().color("&7 ┃ &e2HT");

      // format last attack
      if (Timer.getSecondsParsed() > ((Timer.getSeconds() - 60) - 5))
        color = getUtilManager().color("&c");

      getLogger().broadcastBar(Lang.RESULT_ACTIONBAR.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away, color, secondsParsed, seconds, formatted}));
    });
  }

  private boolean isSetup() {
    return getTime() != null && getHome() != null && getAway() != null;
  }

  private void reset() {
    time = Time.parseString("20min");
    home = null;
    away = null;
    HOME_NAME = null;
    AWAY_NAME = null;
    prefix = "&bEvent";
    home_result = 0;
    away_result = 0;
    Timer.isRunning = false;
    league = false;
  }
}