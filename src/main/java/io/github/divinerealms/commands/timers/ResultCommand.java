package io.github.divinerealms.commands.timers;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public class ResultCommand implements CommandExecutor {
  private final Plugin plugin;
  private final Logger logger;
  private final Helper helper;
  private static String home;
  private static String away;
  private static int home_result = 0;
  private static int away_result = 0;
  private static int taskId;
  private static Time time = null;
  private static Time extraTime = null;
  private static double extraTimeNew;
  private String finalPrefix = null;

  public ResultCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
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
      setFinalPrefix(color(StringUtils.join(args, " ", 1, args.length)));
      getLogger().send(sender, Lang.TIMER_PREFIX_SET.getConfigValue(new String[]{getFinalPrefix()}));
    } else if (args.length == 1) {
      if (args[0].equalsIgnoreCase("start")) {
        if (isSetup() && !home.contains("Invalid") && !away.contains("Invalid")) {
          taskId = firstHalf().startTask();
          getLogger().log(Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(taskId)}), "fcfa");
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("stop")) {
        if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
          getLogger().send(sender, Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(taskId)}));
          firstHalf().cancelTask(taskId);
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("reset")) {
        reset();
        getLogger().send(sender, Lang.TIMER_RESET.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("ht")) {
        if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
          getLogger().send(sender, Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(taskId)}));
          firstHalf().cancelTask(taskId);
          taskId = halfTime().startTask();
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("continue")) {
        if (isSetup() && getPlugin().getServer().getScheduler().isQueued(taskId)) {
          halfTime().cancelTask(taskId);
          taskId = secondHalf().startTask();
          Timer.secondsParsed = Timer.getSeconds() / 2;
          getLogger().log(Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(taskId)}), "fcfa");
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("time")) {
        if (!getPlugin().getServer().getScheduler().isQueued(taskId)) {
          try {
            time = Time.parseString(args[1]);
          } catch (Time.TimeParseException timeParseException) {
            getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[1]}));
            return true;
          }
          getLogger().send(sender, Lang.TIMER_TIME_SET.getConfigValue(new String[]{args[1]}));
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("extend")) {
        if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
          try {
            extraTime = Time.parseString(args[1]);
          } catch (Time.TimeParseException timeParseException) {
            getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(new String[]{args[1]}));
            return true;
          }
          Timer.seconds = (int) (Timer.getSeconds() + extraTime.toSeconds());
          extraTimeNew = Timer.seconds - time.toSeconds();
          getLogger().send(sender, Lang.TIMER_ADDED_EXTRA_TIME.getConfigValue(new String[]{String.valueOf(extraTime)}));
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
        if (args[0].equalsIgnoreCase("add")) {
          if (args[1].equalsIgnoreCase("home")) {
            home_result++;
            getLogger().log(Lang.RESULT_ADD.getConfigValue(new String[]{home}), "fcfa");
          } else if (args[1].equalsIgnoreCase("away")) {
            away_result++;
            getLogger().log(Lang.RESULT_ADD.getConfigValue(new String[]{away}), "fcfa");
          } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
        } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rem")) {
          if (args[1].equalsIgnoreCase("home")) {
            home_result--;
            getLogger().log(Lang.RESULT_REMOVE.getConfigValue(new String[]{home}), "fcfa");
          } else if (args[1].equalsIgnoreCase("away")) {
            away_result--;
            getLogger().log(Lang.RESULT_REMOVE.getConfigValue(new String[]{away}), "fcfa");
          } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
    } else if (args.length == 3) {
      if (args[0].equalsIgnoreCase("teams")) {
        home = getHelper().getGroupMeta(args[1], "team");
        away = getHelper().getGroupMeta(args[2], "team");
        getLogger().send(sender, Lang.TIMER_TEAMS_SET.getConfigValue(new String[]{home, away}));
      } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    return true;
  }

  private Timer secondHalf() {
    return new Timer(getPlugin(), (int) time.toSeconds(),
        () -> getLogger().log(Lang.RESULT_SECONDHALF.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}), "default"),
        () -> {
      getLogger().log(Lang.RESULT_OVER.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}), "default");
      getLogger().broadcastBar(Lang.RESULT_END.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}));},
        (t) -> {
      String secondsParsed = formatTime(Timer.getSecondsParsed());
      String seconds = formatTime(Timer.seconds);
      String extraTimeString = formatTime((int) extraTimeNew);
      String formatted;

      // if there's et
      if (Timer.getSeconds() != (int) time.toSeconds())
        formatted = color("&e 2HT &c(+" + extraTimeString + ")");
      else formatted = "&e 2HT";

      getLogger().broadcastBar(Lang.RESULT_ACTIONBAR.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away, "" + ChatColor.GREEN, secondsParsed, seconds, formatted}));
    });
  }

  private Timer halfTime() {
    return new Timer(getPlugin(), 600,
        () -> getLogger().log(Lang.RESULT_HALFTIME.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}), "default"),
        () -> {},
        (t -> getLogger().broadcastBar(Lang.RESULT_ACTIONBAR_HT.getConfigValue(new String[]{getFinalPrefix(), home, "" + home_result, "" + away_result, away}))));
  }

  private Timer firstHalf() {
    return new Timer(getPlugin(), (int) time.toSeconds(), () -> {
      getLogger().log(Lang.TIMER_STARTING.getConfigValue(new String[]{getFinalPrefix()}), "default");
      getLogger().broadcastBar(Lang.RESULT_STARTING.getConfigValue(new String[]{getFinalPrefix()}));
      }, () -> {}, (t) -> {
      String secondsParsed = formatTime(Timer.getSecondsParsed());
      String seconds = formatTime(Timer.seconds);
      String extraTimeString = formatTime((int) extraTimeNew);
      String formatted, color;

      if (Timer.getSeconds() != (int) time.toSeconds())
        formatted = color("&c (+" + extraTimeString + ")");
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
    time = null;
  }

  private boolean isSetup() {
    return home != null && away != null && time != null && finalPrefix != null;
  }

  private String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
}
