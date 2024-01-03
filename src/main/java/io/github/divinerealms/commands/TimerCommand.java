package io.github.divinerealms.commands;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
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

@Getter
public class TimerCommand implements CommandExecutor {
  private final Plugin plugin;
  private final Logger logger;
  @Setter private String team_1_name;
  @Setter private String team_2_name;
  @Setter private int team_1_result = 0;
  @Setter private int team_2_result = 0;

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
        if (args[1].equalsIgnoreCase("cancel")) {
          final int id = Integer.parseInt(args[2]);
          getPlugin().getServer().getScheduler().cancelTask(id);
          getLogger().log(Lang.TIMER_OVER.getConfigValue(new String[] { String.valueOf(id) }), "default");
          return true;
        } else if (args[1].equalsIgnoreCase("result")) {
          if (args.length < 4)
            getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
          if (args[2].equalsIgnoreCase("setteams")) {
            if (args.length != 5) getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
            else {
              setTeam_1_name(args[3]);
              setTeam_2_name(args[4]);
              getLogger().send(sender, Lang.TIMER_RESULT_TEAMS_SET.getConfigValue(new String[] { getTeam_1_name(), getTeam_2_name() }));
            }
          } else if (args[2].equalsIgnoreCase("add")) {
            if (getTeam_1_name() != null) {
              if (args[3].equalsIgnoreCase("1")) {
                team_1_result++;
                getLogger().send(sender, Lang.TIMER_RESULT_ADD.getConfigValue(new String[] { getTeam_1_name() }));
              } else if (args[3].equalsIgnoreCase("2")) {
                team_2_result++;
                getLogger().send(sender, Lang.TIMER_RESULT_ADD.getConfigValue(new String[] { getTeam_2_name() }));
              } else getLogger().send(sender, Lang.TIMER_RESULT_ADD_USAGE.getConfigValue(null));
            } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
          } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
        } else if (args[1].equalsIgnoreCase("add")) {
          Time time;

          try {
            time = Time.parseString(args[2]);
          } catch (Time.TimeParseException | NullPointerException e) {
            getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
            return true;
          }

          String prefix = StringUtils.join(args, ' ', 3, args.length);
          prefix = ChatColor.translateAlternateColorCodes('&', prefix);
          String finalPrefix = prefix;

          Timer timer = new Timer(getPlugin(), (int) time.toSeconds(), () -> {
            getLogger().log(Lang.TIMER_STARTING.getConfigValue(new String[] { finalPrefix }), "default");
            getLogger().broadcastBar(Lang.TIMER_STARTING.getConfigValue(new String[] { finalPrefix }));
            }, () -> {
            getLogger().log(Lang.TIMER_END.getConfigValue(new String[] { finalPrefix, getTeam_1_name(), String.valueOf(getTeam_1_result()), getTeam_2_name(), String.valueOf(getTeam_2_result())}), "default");
            getLogger().broadcastBar(Lang.TIMER_END.getConfigValue(new String[] { finalPrefix, getTeam_1_name(), String.valueOf(getTeam_1_result()), getTeam_2_name(), String.valueOf(getTeam_2_result())}));
            resetTeams();
            }, (t) -> {
            String secondsParsed = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSecondsParsed())).format(DateTimeFormatter.ofPattern("mm:ss"));
            String seconds = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSeconds())).format(DateTimeFormatter.ofPattern("mm:ss"));
            if (getTeam_1_name() != null)
              getLogger().broadcastBar(Lang.TIMER_RESULT.getConfigValue(new String[] { finalPrefix, getTeam_1_name(), String.valueOf(getTeam_1_result()), getTeam_2_name(), String.valueOf(getTeam_2_result()), secondsParsed, seconds }));
            else
              getLogger().broadcastBar(Lang.TIMER_CURRENT_TIME.getConfigValue(new String[] { finalPrefix, secondsParsed, seconds }));
          });
          timer.scheduleTimer();
          getLogger().send(sender, Lang.TIMER_ADD.getConfigValue(new String[] { String.valueOf(timer.getAssignedTaskId()) }));
        } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
    }
    return true;
  }

  private void resetTeams() {
    setTeam_1_name(null);
    setTeam_2_name(null);
    setTeam_1_result(0);
    setTeam_2_result(0);
  }
}