package io.github.divinerealms.commands;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class TimerCommand implements CommandExecutor {
  private final Plugin plugin;
  private final Logger logger;
  private static String team_1_name;
  private static String team_2_name;
  private static int team_1_result = 0;
  private static int team_2_result = 0;
  private Time time = null;
  private String finalPrefix = null;
  private int taskId;

  public TimerCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.timer")) {
      getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      if (args.length <= 1 || args[1].equalsIgnoreCase("help")) {
        getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
      } else if (args.length >= 3) {
        if (args[1].equalsIgnoreCase("stop")) {
          final int id = Integer.parseInt(args[2]);
          getPlugin().getServer().getScheduler().cancelTask(id);
          getLogger().send(sender, Lang.TIMER_OVER.getConfigValue(new String[] { String.valueOf(id) }));
        } else if (args[1].equalsIgnoreCase("result")) {
          if (args.length < 4)
            getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
          if (args[2].equalsIgnoreCase("setteams")) {
            if (args.length != 5) getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
            else {
              team_1_name = args[3];
              team_2_name = args[4];
              getLogger().send(sender, Lang.TIMER_RESULT_TEAMS_SET.getConfigValue(new String[] { team_1_name, team_2_name }));
            }
          } else if (args[2].equalsIgnoreCase("add")) {
            if (team_1_name != null) {
              if (args[3].equalsIgnoreCase("1")) {
                team_1_result++;
                getLogger().send(sender, Lang.TIMER_RESULT_ADD.getConfigValue(new String[] { team_1_name }));
              } else if (args[3].equalsIgnoreCase("2")) {
                team_2_result++;
                getLogger().send(sender, Lang.TIMER_RESULT_ADD.getConfigValue(new String[] { team_2_name }));
              } else getLogger().send(sender, Lang.TIMER_RESULT_ADD_USAGE.getConfigValue(null));
            } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
          } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
        } else if (args[1].equalsIgnoreCase("create")) {
          try {
            time = Time.parseString(args[2]);
          } catch (Time.TimeParseException | NullPointerException e) {
            getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
            return true;
          }

          String prefix = StringUtils.join(args, ' ', 3, args.length);
          prefix = ChatColor.translateAlternateColorCodes('&', prefix);
          finalPrefix = prefix;

          matchResult();
          getLogger().send(sender, Lang.TIMER_CREATE.getConfigValue(new String[] { String.valueOf(getTaskId()) }));
        } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
    }
    return true;
  }

  private void matchResult() {
    taskId = getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(),
        new Timer(getPlugin(), (int) time.toSeconds(), () -> {
          getLogger().log(Lang.TIMER_STARTING.getConfigValue(new String[] { finalPrefix }), "default");
          getLogger().broadcastBar(Lang.TIMER_RESULT_STARTING.getConfigValue(new String[] { finalPrefix }));
          }, () -> {
          getLogger().log(Lang.TIMER_END.getConfigValue(new String[] { finalPrefix, team_1_name, String.valueOf(team_1_result), String.valueOf(team_2_result), team_2_name }), "default");
          getLogger().broadcastBar(Lang.TIMER_RESULT_END.getConfigValue(new String[] { finalPrefix, team_1_name, String.valueOf(team_1_result), String.valueOf(team_2_result), team_2_name }));
          resetTeams();
          }, (t) -> {
          String secondsParsed = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSecondsParsed())).format(DateTimeFormatter.ofPattern("mm:ss"));
          String seconds = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSeconds())).format(DateTimeFormatter.ofPattern("mm:ss"));
          if (team_1_name == null)
            getLogger().broadcastBar(Lang.TIMER_CURRENT_TIME.getConfigValue(new String[] { finalPrefix, secondsParsed, seconds }));
          else
            getLogger().broadcastBar(Lang.TIMER_RESULT.getConfigValue(new String[] { finalPrefix, team_1_name, String.valueOf(team_1_result), String.valueOf(team_2_result), team_2_name, secondsParsed, seconds }));
        }), 20L, 20L);
  }

  private void resetTeams() {
    team_1_name = null;
    team_2_name = null;
    team_1_result = 0;
    team_2_result = 0;
  }
}