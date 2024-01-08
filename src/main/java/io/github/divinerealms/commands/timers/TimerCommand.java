package io.github.divinerealms.commands.timers;

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
public class TimerCommand implements CommandExecutor {
  private final Plugin plugin;
  private final Logger logger;
  private static int taskId;
  private Time time = null;
  private String finalPrefix = null;

  public TimerCommand(final Plugin plugin, final UtilManager utilManager) {
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
      getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
      return true;
    } else if (args[0].equalsIgnoreCase("create")) {
      if (args.length == 3) {
        try {
          setTime(Time.parseString(args[1]));
        } catch (Time.TimeParseException | NullPointerException e) {
          getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
          return true;
        }
        finalPrefix = color(args[2].replace("_", " "));
        taskId = startTimer().startTask();
        getLogger().log(Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(taskId)}), "fcfa");
      } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
    } else if (args[0].equalsIgnoreCase("stop")) {
      if (args.length == 1) {
        if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
          getLogger().send(sender, Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(taskId)}));
          startTimer().getAfterTimer().run();
          startTimer().cancelTask(taskId);
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
    return true;
  }

  private Timer startTimer() {
    return new Timer(getPlugin(), (int) getTime().toSeconds(), () ->
        getLogger().log(Lang.TIMER_STARTING.getConfigValue(new String[]{getFinalPrefix()}), "default"), () -> {
      getLogger().log(Lang.TIMER_OVER.getConfigValue(new String[]{String.valueOf(taskId)}), "default");
      getLogger().broadcastBar(Lang.TIMER_END.getConfigValue(new String[]{getFinalPrefix()}));
      }, (t) -> {
      String secondsParsed = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(Timer.getSecondsParsed())).format(DateTimeFormatter.ofPattern("mm:ss"));
      String seconds = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(Timer.getSeconds())).format(DateTimeFormatter.ofPattern("mm:ss"));
      getLogger().broadcastBar(Lang.TIMER_CURRENT_TIME.getConfigValue(new String[]{getFinalPrefix(), secondsParsed, seconds}));
    });
  }

  private String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
}