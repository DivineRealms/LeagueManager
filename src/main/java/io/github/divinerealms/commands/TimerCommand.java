package io.github.divinerealms.commands;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class TimerCommand implements CommandExecutor {
  private final LeagueManager plugin;
  private final Logger logger;
  static String team_1_name;
  static String team_2_name;
  static int team_1_result =0;
  static int team_2_result = 0;

  public TimerCommand(final LeagueManager plugin, final UtilManager utilManager) {
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
          Bukkit.getScheduler().cancelTask(id);
          getLogger().send(sender, Lang.TIMER_OVER.getConfigValue(new String[]{String.valueOf(id)}));
          return true;
        } else if(args[1].equalsIgnoreCase("result")) {
          if(args.length<4) {
            getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
          }
          if (args[2].equalsIgnoreCase("add")) {
            if (args[3].equalsIgnoreCase("1")) {
              sender.sendMessage("Tim 1 je dao gol.");
              team_1_result++;
            } else if (args[3].equalsIgnoreCase("2")) {
              team_2_result++;
              sender.sendMessage("TIm 2 je dao gol.");
            } else {
              sender.sendMessage("Napisi koj tim je dao gol.");
            }
          } else if (args[2].equalsIgnoreCase("setteams")) {
            if (!(args.length == 5)) {
              getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
            } else {
              team_1_name = args[3];
              team_2_name = args[4];
              sender.sendMessage("Timovi postavljeni. " + ChatColor.translateAlternateColorCodes('&', team_1_name) + ChatColor.WHITE + " vs " + ChatColor.translateAlternateColorCodes('&', team_2_name));
            }
          }
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

          Timer timer = new Timer(plugin, (int) time.toSeconds(),
                  () -> {
                    getLogger().log(finalPrefix + "Host pocinje!", "default");
                    Bukkit.dispatchCommand(sender, "chc announce actionbar Pocinje host! " + "server:footbal");
                  },
                  () -> {
                    getLogger().log(finalPrefix + "Utakmica zavrsena!", "default");
                    Bukkit.dispatchCommand(sender, "chc announce actionbar Utakmica zavrsena! " + "server:footbal");
                    team_1_name = null;
                    team_2_name = null;
                    team_1_result = 0;
                    team_2_result = 0;
                  },
                  (t) -> {
                    String secondsParsed = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSecondsParsed())).format(DateTimeFormatter.ofPattern("mm:ss"));
                    String seconds = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(t.getSeconds())).format(DateTimeFormatter.ofPattern("mm:ss"));
                    if(team_1_name == null) {
                      Bukkit.dispatchCommand(sender, "chc announce actionbar Trenutno Vreme: " + ChatColor.YELLOW + secondsParsed + "/" + seconds + " server:footbal");
                    } else {
                      Bukkit.dispatchCommand(sender, "chc announce actionbar Trenutno Vreme: " + ChatColor.YELLOW + secondsParsed + "/" + seconds + " " + ChatColor.translateAlternateColorCodes('&', team_1_name + " " + ChatColor.YELLOW + team_1_result + ChatColor.WHITE + " - " + ChatColor.YELLOW + team_2_result + " " + team_2_name + " server:footbal"));
                    }
                  });
          timer.scheduleTimer();
          getLogger().send(sender, Lang.TIMER_ADD.getConfigValue(new String[]{String.valueOf(timer.getAssignedTaskId())}));
        } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
    }
    return true;
  }
}