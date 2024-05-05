package io.github.divinerealms.commands.timers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.*;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

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
  private String home, away, prefix;
  private int home_result, away_result, extraTimeNew, timerId;
  private static String HOME_NAME, AWAY_NAME;
  private boolean secondHalf = false, league = false;
  private static YamlConfiguration config;
  private DiscordWebhook webhook = null;

  public ResultCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
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
      getLogger().send("fcfa", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(getTimerId())}));
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("stop")
  @CommandPermission("leaguemanager.command.result.stop")
  public void onStop(CommandSender sender) {
    if (getUtilManager().isTaskQueued(getTimerId())) {
      getLogger().send("owner", UtilManager.formatTime(Timer.getSecondsParsed()));
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_ENDED.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME, UtilManager.formatTime(Timer.getSecondsParsed())}));
        try {
          webhook.execute();
        } catch (IOException e) {
          getLogger().send("hoster", e.getMessage());
        }
      }
      secondHalf().getAfterTimer().run();
      Bukkit.getScheduler().cancelTasks(getPlugin());
      reset();
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("pause|p")
  @CommandPermission("leaguemanager.command.result.pause")
  public void onPause(CommandSender sender) {
    if (getUtilManager().isTaskQueued(getTimerId())) {
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
  @CommandPermission("leaguemanager.command.result.extend")
  public void onExtend(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(getTimerId()) && args.length == 1) {
      try {
        extraTime = Time.parseString(args[0]);
      } catch (Time.TimeParseException timeParseException) {
        getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[0]}));
      }
      Timer.seconds = (int) (Timer.getSeconds() + extraTime.toSeconds());
      if (isSecondHalf()) extraTimeNew = (int) (Timer.seconds - time.toSeconds()) - 60;
      else extraTimeNew = (int) (Timer.seconds - time.toSeconds());
      getLogger().send("fcfa", Lang.TIMER_TIME_SET.getConfigValue(new String[]{UtilManager.formatTime(extraTimeNew)}));
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("add")
  @CommandCompletion("home|away|@players")
  @CommandPermission("leaguemanager.command.result.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(getTimerId()) && (args.length == 2 || args.length == 3)) {
      if (args[0].equalsIgnoreCase("home")) {
        home_result++;
        if (args.length == 2)
          getLogger().send("default", Lang.RESULT_ADD.getConfigValue(new String[]{args[1], home}));
        else getLogger().send("default", Lang.RESULT_ADD_ASSIST.getConfigValue(new String[]{args[1], home, args[2]}));
        if (webhook != null && isLeague()) {
          webhook.setContent(Lang.WEBHOOK_MATCH_SCORE.getConfigValue(new String[]{args[1], HOME_NAME, UtilManager.formatTime(Timer.getSecondsParsed())}));
          if (args.length == 3)
            webhook.setContent(Lang.WEBHOOK_MATCH_ASSIST.getConfigValue(new String[]{args[1], HOME_NAME, UtilManager.formatTime(Timer.getSecondsParsed()), args[2]}));
          try {
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
          webhook.setContent(Lang.WEBHOOK_MATCH_SCORE.getConfigValue(new String[]{args[1], AWAY_NAME, UtilManager.formatTime(Timer.getSecondsParsed())}));
          if (args.length == 3)
            webhook.setContent(Lang.WEBHOOK_MATCH_ASSIST.getConfigValue(new String[]{args[1], AWAY_NAME, UtilManager.formatTime(Timer.getSecondsParsed()), args[2]}));
          try {
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
      if (getHelper().groupExists(args[0].toLowerCase())) {
        if (getHelper().groupHasMeta(args[0].toLowerCase(), "team")) {
          home = getHelper().getGroupMeta(args[0].toLowerCase(), "team");
          league = true;
        } else home = getHelper().getGroupMeta(args[0].toLowerCase(), "b");
      } else home = args[0];
      if (getHelper().groupExists(args[1].toLowerCase())) {
        if (getHelper().groupHasMeta(args[1].toLowerCase(), "team")) {
          away = getHelper().getGroupMeta(args[1].toLowerCase(), "team");
          league = true;
        } else away = getHelper().getGroupMeta(args[1].toLowerCase(), "b");
      } else away = args[1];
      getLogger().send("fcfa", Lang.TIMER_TEAMS_SET.getConfigValue(new String[]{home, away}));
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_TEAMS_SET.getConfigValue(new String[]{HOME_NAME, AWAY_NAME}));
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
    getLogger().send("fcfa", Lang.TIMER_PREFIX_SET.getConfigValue(new String[]{getPrefix()}));
  }

  private Timer firstHalf() {
    return new Timer(getPlugin(), (int) time.toSeconds(), () -> {
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_START.getConfigValue(new String[]{HOME_NAME, AWAY_NAME}));
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
      getLogger().send("default", Lang.RESULT_HALFTIME.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away}));
      if (webhook != null && isLeague()) {
        webhook.setContent(Lang.WEBHOOK_MATCH_HALFTIME.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME}));
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
    return new Timer(getPlugin(), (int) (time.toSeconds() + 60), () -> getLogger().send("default", Lang.RESULT_SECONDHALF.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away})), () -> {
      getLogger().send("default", Lang.RESULT_OVER.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away}));
      getLogger().broadcastBar(Lang.RESULT_END.getConfigValue(new String[]{getPrefix(), home, "" + home_result, "" + away_result, away}));
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