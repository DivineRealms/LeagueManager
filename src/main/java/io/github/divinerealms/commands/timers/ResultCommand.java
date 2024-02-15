package io.github.divinerealms.commands.timers;

import io.github.divinerealms.configs.Config;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class ResultCommand implements CommandExecutor, Listener {
  private final Plugin plugin;
  private final Logger logger;
  private final Helper helper;
  private static String home = "home";
  private static String away = "away";
  private static String HOME_NAME, CAPTAIN_HOME, AWAY_NAME, CAPTAIN_AWAY;
  private static int home_result = 0;
  private static int away_result = 0;
  private static Time time = Time.parseString("20min");
  private static Time extraTime = null;
  private static double extraTimeNew;
  private String finalPrefix = "&bEvent";
  private boolean secondHalf = false;
  private static YamlConfiguration config = Config.getConfig("config.yml");
  private DiscordWebhook webhook = null;

  public ResultCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();

    if (!config.getString("discordWebhookURL").isEmpty())
      webhook = new DiscordWebhook(config.getString("discordWebhookURL"));
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.timer")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
      return true;
    }

    if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
      getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      return true;
    } else if (args[0].equalsIgnoreCase("prefix")) {
      final String prefix = StringUtils.join(args, " ", 1, args.length);
      setFinalPrefix(color(prefix));
      getLogger().send("hoster", Lang.TIMER_PREFIX_SET.getConfigValue(new String[]{getFinalPrefix()}));
    } else if (args.length == 1) {
      if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")) {
        if (!isTaskQueued(Timer.assignedTaskId) && time != null && home != null && away != null) {
          Timer.assignedTaskId = firstHalf().startTask();
          getLogger().send("hoster", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(Timer.assignedTaskId)}));
        } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("stop")) {
        if (isTaskQueued(Timer.assignedTaskId)) {
          secondHalf().getAfterTimer().run();
          Bukkit.getScheduler().cancelTasks(getPlugin());
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("pause") || args[0].equalsIgnoreCase("p")) {
        if (isTaskQueued(Timer.assignedTaskId)) {
          getLogger().send("hoster", Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(Timer.assignedTaskId)}));
          firstHalf().cancelTask(Timer.assignedTaskId);
          secondHalf = true;
          Timer.assignedTaskId = halfTime().startTask();
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("resume") || args[0].equalsIgnoreCase("r")) {
        if (isTaskQueued(Timer.assignedTaskId)) {
          halfTime().cancelTask(Timer.assignedTaskId);
          Timer.assignedTaskId = secondHalf().startTask();
          webhook.setContent(Lang.WEBHOOK_MATCH_SECONDHALF.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME}));
          try {
            webhook.execute();
          } catch (IOException e) {
            getLogger().send("hoster", e.getMessage());
          }
          getLogger().send("hoster", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(Timer.assignedTaskId)}));
          getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> Timer.secondsParsed = (Timer.getSeconds() - 60) / 2, 20L);
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("time")) {
        if (!isTaskQueued(Timer.assignedTaskId)) {
          try {
            time = Time.parseString(args[1]);
            if (time.toSeconds() < Time.parseString("10min").toSeconds())
              time = Time.parseString("10min");
          } catch (Time.TimeParseException timeParseException) {
            getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[1]}));
            return true;
          }
          getLogger().send("hoster", Lang.TIMER_TIME_SET.getConfigValue(new String[]{time.toString()}));
        } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("extend")) {
        if (isTaskQueued(Timer.assignedTaskId)) {
          try {
            extraTime = Time.parseString(args[1]);
          } catch (Time.TimeParseException timeParseException) {
            getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[1]}));
            return true;
          }
          Timer.seconds = (int) (Timer.getSeconds() + extraTime.toSeconds());
          if (isSecondHalf()) extraTimeNew = Timer.seconds - time.toSeconds() - 60;
          else extraTimeNew = Timer.seconds - time.toSeconds();
          getLogger().send("hoster", Lang.TIMER_ADDED_EXTRA_TIME.getConfigValue(new String[]{String.valueOf(extraTime)}));
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (isTaskQueued(Timer.assignedTaskId)) {
        if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rem")) {
          if (args[1].equalsIgnoreCase("home")) {
            if (home_result != 0) {
              home_result--;
              getLogger().send("hoster", Lang.RESULT_REMOVE.getConfigValue(new String[]{home}));
            } else getLogger().send(sender, Lang.RESULT_ELIMINATED.getConfigValue(new String[]{home}));
          } else if (args[1].equalsIgnoreCase("away")) {
            if (away_result != 0) {
              away_result--;
              getLogger().send("hoster", Lang.RESULT_REMOVE.getConfigValue(new String[]{away}));
            } else getLogger().send(sender, Lang.RESULT_ELIMINATED.getConfigValue(new String[]{away}));
          } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
    } else if (args.length == 3 || args.length == 4) {
      if (args[0].equalsIgnoreCase("teams")) {
        if (getHelper().groupExists(args[1])) {
          if (getHelper().groupHasMeta(args[1], "team")) {
            home = getHelper().getGroupMeta(args[1], "team");
            HOME_NAME = args[1].toUpperCase();
          } else home = getHelper().getGroupMeta(args[1], "b");
        } else home = args[1];
        if (getHelper().groupExists(args[2])) {
          if (getHelper().groupHasMeta(args[2], "team")) {
            away = getHelper().getGroupMeta(args[2], "team");
            AWAY_NAME = args[2].toUpperCase();
          } else away = getHelper().getGroupMeta(args[2], "b");
        } else away = args[2];
        getLogger().send("hoster", Lang.TIMER_TEAMS_SET.getConfigValue(new String[]{home,away}));
        if (webhook != null) {
          webhook.setContent(Lang.WEBHOOK_TEAMS_SET.getConfigValue(new String[]{HOME_NAME,AWAY_NAME}));
          try {
            webhook.execute();
          } catch (IOException e) {
            getLogger().send(sender, e.getMessage());
            return true;
          }
        } else getLogger().send(sender, Lang.WEBHOOK_NOT_SETUP.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("cap") || args[0].equalsIgnoreCase("captain")) {
        if (args.length == 3) {
          CAPTAIN_HOME = args[1];
          CAPTAIN_AWAY = args[2];
          getLogger().send(sender, Lang.RESULT_CAPTAINS_SET.getConfigValue(new String[]{CAPTAIN_HOME,CAPTAIN_AWAY}));
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else if (isTaskQueued(Timer.assignedTaskId)) {
        if (args[0].equalsIgnoreCase("add")) {
          if (args[1].equalsIgnoreCase("home")) {
            home_result++;
            if (args.length == 3) getLogger().send("default", Lang.RESULT_ADD.getConfigValue(new String[]{args[2], home}));
            else getLogger().send("default", Lang.RESULT_ADD_ASSIST.getConfigValue(new String[]{args[2], home, args[3]}));
            if (webhook != null) {
              webhook.setContent(Lang.WEBHOOK_MATCH_SCORE.getConfigValue(new String[]{args[2], HOME_NAME, formatTime(Timer.getSecondsParsed())}));
              if (args.length == 4)
                webhook.setContent(Lang.WEBHOOK_MATCH_ASSIST.getConfigValue(new String[]{args[2], HOME_NAME, formatTime(Timer.getSecondsParsed()), args[3]}));
              try {
                webhook.execute();
              } catch (IOException e) {
                getLogger().send(sender, e.getMessage());
                return true;
              }
            } else getLogger().send(sender, Lang.WEBHOOK_NOT_SETUP.getConfigValue(null));
          } else if (args[1].equalsIgnoreCase("away")) {
            away_result++;
            if (args.length == 3) getLogger().send("default", Lang.RESULT_ADD.getConfigValue(new String[]{args[2], away}));
            else getLogger().send("default", Lang.RESULT_ADD_ASSIST.getConfigValue(new String[]{args[2], away, args[3]}));
            if (webhook != null) {
              webhook.setContent(Lang.WEBHOOK_MATCH_SCORE.getConfigValue(new String[]{args[2], AWAY_NAME, formatTime(Timer.getSecondsParsed())}));
              if (args.length == 4)
                webhook.setContent(Lang.WEBHOOK_MATCH_ASSIST.getConfigValue(new String[]{args[2], AWAY_NAME, formatTime(Timer.getSecondsParsed()), args[3]}));
              try {
                webhook.execute();
              } catch (IOException e) {
                getLogger().send(sender, e.getMessage());
                return true;
              }
            } else getLogger().send(sender, Lang.WEBHOOK_NOT_SETUP.getConfigValue(null));
          } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
    } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    return true;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onChat(final AsyncPlayerChatEvent event) {
    final Player player = event.getPlayer();
    final String message = event.getMessage();

    if (event.isCancelled()) return;
    if (home == null) return;

    if (player.getName().equals(CAPTAIN_HOME) || player.getName().equals(CAPTAIN_AWAY) ||
        player.hasPermission("tab.group." + HOME_NAME + "-director") ||
        player.hasPermission("tab.group." + AWAY_NAME + "-director")) {
      String team = getHelper().playerGetMeta(player.getUniqueId(), "team");
      String teamClean = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', team));

      if (message.startsWith("postava")) {
        event.setCancelled(true);
        String[] lines = message.split("\\s", 0);
        if (lines.length != 6) getLogger().send(player, Lang.RESULT_INVALID_LINEUP.getConfigValue(null));
        else {
          if (webhook != null) {
            webhook.setContent(Lang.WEBHOOK_TEAM_LINEUP.getConfigValue(new String[]{teamClean,player.getName(),lines[1],lines[2],lines[3],lines[4],lines[5]}));
            try {
              webhook.execute();
            } catch (IOException e) {
              getLogger().send(player, e.getMessage());
            }
          }
          getLogger().send("default", Lang.RESULT_LINEUP.getConfigValue(new String[]{team,player.getName(),lines[1],lines[2],lines[3],lines[4],lines[5]}));
        }
      }

      if (isTaskQueued(Timer.assignedTaskId)) {
        event.setMessage(Lang.RESULT_MANAGER_FORMAT.getConfigValue(new String[]{message}));
        if (message.startsWith("izmena")) {
          event.setCancelled(true);
          String[] lines = message.split("\\s", 0);
          if (lines.length != 3) getLogger().send(player, Lang.RESULT_INVALID_SUB.getConfigValue(null));
          else {
            if (webhook != null) {
              webhook.setContent(Lang.WEBHOOK_TEAM_SUB.getConfigValue(new String[]{teamClean,lines[1],lines[2]}));
              try {
                webhook.execute();
              } catch (IOException e) {
                getLogger().send(player, e.getMessage());
              }
            }
            getLogger().send("default", Lang.RESULT_SUB.getConfigValue(new String[]{team,player.getName(),lines[1],lines[2]}));
          }
        }
      }
    }
  }

  private Timer secondHalf() {
    return new Timer(getPlugin(), (int) (time.toSeconds() + 60),
        () -> getLogger().send("default", Lang.RESULT_SECONDHALF.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away})),
        () -> {
      webhook.setContent(Lang.WEBHOOK_MATCH_ENDED.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME, formatTime(Timer.getSecondsParsed())}));
      try {
        webhook.execute();
      } catch (IOException e) {
        getLogger().send("hoster", e.getMessage());
      }
      getLogger().send("default", Lang.RESULT_OVER.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}));
      getLogger().broadcastBar(Lang.RESULT_END.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}));
      reset();},
        (t) -> {
      String secondsParsed = formatTime(Timer.getSecondsParsed());
      String seconds = formatTime(Timer.getSeconds() - 60);
      String extraTimeString = formatTime((int) extraTimeNew);
      String formatted = "", color = "&a";

      // if there's et
      if ((Timer.getSeconds() - 60) != (int) time.toSeconds()) {
        if (extraTimeNew != 0) formatted = color("&7 ┃ &e2HT &c(+" + extraTimeString + " ET)");
        else color = color("&a");
      } else formatted = color("&7 ┃ &e2HT");

      // format last attack
      if (Timer.getSecondsParsed() > ((Timer.getSeconds() - 60) - 5))
        color = color("&c");

      getLogger().broadcastBar(Lang.RESULT_ACTIONBAR.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away, color, secondsParsed, seconds, formatted}));
    });
  }

  private Timer halfTime() {
    return new Timer(getPlugin(), 600,
        () -> {
          getLogger().send("default", Lang.RESULT_HALFTIME.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}));
          webhook.setContent(Lang.WEBHOOK_MATCH_HALFTIME.getConfigValue(new String[]{HOME_NAME, String.valueOf(home_result), String.valueOf(away_result), AWAY_NAME}));
          try {
            webhook.execute();
          } catch (IOException e) {
            getLogger().send("hoster", e.getMessage());
          }
        },
        () -> {},
        (t -> getLogger().broadcastBar(Lang.RESULT_ACTIONBAR_HT.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}))));
  }

  private Timer firstHalf() {
    return new Timer(getPlugin(), (int) time.toSeconds(), () -> {
      webhook.setContent(Lang.WEBHOOK_MATCH_START.getConfigValue(new String[]{HOME_NAME, AWAY_NAME}));
      try {
        webhook.execute();
      } catch (IOException e) {
        getLogger().send("hoster", e.getMessage());
      }
      getLogger().send("default", Lang.TIMER_STARTING.getConfigValue(new String[]{getFinalPrefix()}));
      getLogger().broadcastBar(Lang.RESULT_STARTING.getConfigValue(new String[]{getFinalPrefix()}));
      }, () -> {}, (t) -> {
      String secondsParsed = formatTime(Timer.getSecondsParsed());
      String seconds = formatTime(Timer.seconds);
      String extraTimeString = formatTime((int) extraTimeNew);
      String formatted, color;

      if (Timer.getSeconds() != (int) time.toSeconds())
        formatted = color("&7 ┃ &c(+" + extraTimeString + " ET)");
      else formatted = "";

      if (Timer.getSecondsParsed() > (time.toSeconds() / 2)) color = color("&c");
      else color = color("&a");

      getLogger().broadcastBar(Lang.RESULT_ACTIONBAR.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away, color, secondsParsed, seconds, formatted}));
    });
  }

  private static String formatTime(int time) {
    return LocalTime.MIDNIGHT.plus(Duration.ofSeconds(time)).format(DateTimeFormatter.ofPattern("mm:ss"));
  }

  private void reset() {
    home = null;
    away = null;
    home_result = 0;
    away_result = 0;
    time = Time.parseString("20min");
  }

  private String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  private boolean isTaskQueued(final Integer taskId) {
    if (taskId != null) return getPlugin().getServer().getScheduler().isQueued(taskId);
    else return false;
  }
}
