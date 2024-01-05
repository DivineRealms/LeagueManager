package io.github.divinerealms.commands;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import lombok.Setter;
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
  private static String blue;
  private static String red;
  private static int blueRes = 0;
  private static int redRes = 0;
  private static int taskId;
  private Time time = null;
  private String finalPrefix = null;

  public ResultCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.logger = utilManager.getLogger();
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
    } else if (args[0].equalsIgnoreCase("create")) {
      if (args.length == 5) {
        try {
          setTime(Time.parseString(args[1]));
        } catch (Time.TimeParseException | NullPointerException e) {
          getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
          return true;
        }
        resetTeams();
        finalPrefix = ChatColor.translateAlternateColorCodes('&', args[2]);
        blue = args[3];
        red = args[4];
        taskId = startResult().startTask();
        getLogger().log(Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(taskId)}), "fcfa");
      } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("stop")) {
        final int id = Integer.parseInt(args[1]);
        if (getPlugin().getServer().getScheduler().isQueued(id)) {
          resetTeams();
          startResult().cancelTask(id);
          getLogger().send(sender, Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(id)}));
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
        if (args[0].equalsIgnoreCase("add")) {
          if (args[1].equalsIgnoreCase("1")) {
            blueRes++;
            getLogger().log(Lang.RESULT_ADD.getConfigValue(new String[]{blue}), "fcfa");
          } else if (args[1].equalsIgnoreCase("2")) {
            redRes++;
            getLogger().log(Lang.RESULT_ADD.getConfigValue(new String[]{red}), "fcfa");
          } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
        } else if (args[0].equalsIgnoreCase("remove")) {
          if (args[1].equalsIgnoreCase("1")) {
            blueRes--;
            getLogger().log(Lang.RESULT_REMOVE.getConfigValue(new String[]{blue}), "fcfa");
          } else if (args[1].equalsIgnoreCase("2")) {
            redRes--;
            getLogger().log(Lang.RESULT_REMOVE.getConfigValue(new String[]{red}), "fcfa");
          } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
        } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
    } else getLogger().send(sender, Lang.RESULT_HELP.getConfigValue(null));
    return true;
  }

  private Timer startResult() {
    return new Timer(getPlugin(), (int) getTime().toSeconds(), () -> {
      getLogger().log(Lang.TIMER_STARTING.getConfigValue(new String[]{getFinalPrefix()}), "default");
      getLogger().broadcastBar(Lang.RESULT_STARTING.getConfigValue(new String[]{getFinalPrefix()}));
      }, () -> {
      getLogger().log(Lang.RESULT_OVER.getConfigValue(new String[]{getFinalPrefix(), blue, "" + blueRes, "" + redRes, red}), "default");
      getLogger().broadcastBar(Lang.RESULT_END.getConfigValue(new String[]{getFinalPrefix(), blue, "" + blueRes, "" + redRes, red}));
      }, (t) -> {
      String secondsParsed = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSecondsParsed())).format(DateTimeFormatter.ofPattern("mm:ss"));
      String seconds = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSeconds())).format(DateTimeFormatter.ofPattern("mm:ss"));
      getLogger().broadcastBar(Lang.RESULT_ACTIONBAR.getConfigValue(new String[]{getFinalPrefix(), blue, "" + blueRes, "" + redRes, red, secondsParsed, seconds}));
    });
  }

  private void resetTeams() {
    blue = null;
    red = null;
    blueRes = 0;
    redRes = 0;
  }
}
