package io.github.divinerealms.commands.timers;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class TwoXFourCommand implements CommandExecutor {
  private final Plugin plugin;
  private final Logger logger;
  private static Map<String, String> teams = createMap();
  private static int taskId;
  private Time time = null;
  private String finalPrefix = null;
  private static String blue, red, green, yellow;

  public TwoXFourCommand(final Plugin plugin, final UtilManager utilManager) {
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
      getLogger().send(sender, Lang.TWO_TIMES_FOUR_HELP.getConfigValue(null));
      return true;
    } else if (args.length == 1) {
      if (args[0].equalsIgnoreCase("tp")) {
        Bukkit.dispatchCommand(sender, "warp 2x4");
      } else if (args[0].equalsIgnoreCase("create")) {
        taskId = startResult().startTask();
        getLogger().log(Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(taskId)}), "hoster");
      } else if (args[0].equalsIgnoreCase("reset")) {
        createMap();
        getLogger().send(sender, Lang.RESULT_RESET.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("stop")) {
        if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
          getLogger().send(sender, Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(taskId)}));
          startResult().getAfterTimer().run();
          startResult().cancelTask(taskId);
        } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
      } else getLogger().send(sender, Lang.TWO_TIMES_FOUR_HELP.getConfigValue(null));
    } else if (getPlugin().getServer().getScheduler().isQueued(taskId)) {
      if (args[0].equalsIgnoreCase("add")) {
        if (teams.containsKey(args[1])) {
          if (!teams.get(args[1]).equals("4")) {
            final Integer add = Integer.parseInt(teams.get(args[1])) + 1;
            teams.put(args[1], String.valueOf(add));
            getLogger().log(Lang.RESULT_ADD.getConfigValue(new String[]{args[1]}), "hoster");
          } else getLogger().send(sender, Lang.RESULT_FULL_LIVES.getConfigValue(new String[]{args[1]}));
        } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
      } else if (args[0].equalsIgnoreCase("remove")) {
        if (teams.containsKey(args[1])) {
          if (!teams.get(args[1]).equals("0")) {
            final Integer remove = Integer.parseInt(teams.get(args[1])) - 1;
            teams.put(args[1], String.valueOf(remove));
            getLogger().log(Lang.RESULT_REMOVE.getConfigValue(new String[]{args[1]}), "hoster");
          } else getLogger().send(sender, Lang.RESULT_ELIMINATED.getConfigValue(new String[]{args[1]}));
        } else getLogger().send(sender, Lang.RESULT_USAGE.getConfigValue(null));
      } else getLogger().send(sender, Lang.TWO_TIMES_FOUR_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
    return true;
  }

  private Timer startResult() {
    String prefix = color("&b&lEvent 2x4");
    Time time = Time.parseString("30min");
    return new Timer(getPlugin(), (int) time.toSeconds(), () -> {
      getLogger().log(Lang.TIMER_STARTING.getConfigValue(new String[]{prefix}), "default");
      getLogger().broadcastBar(Lang.RESULT_STARTING.getConfigValue(new String[]{prefix}));
    }, () -> {
      getLogger().log(Lang.TWO_TIMES_FOUR_RESULT_OVER.getConfigValue(new String[]{prefix, blue, red, green, yellow}), "default");
      getLogger().broadcastBar(Lang.TWO_TIMES_FOUR_RESULT_END.getConfigValue(new String[]{prefix, blue, red, green, yellow}));
    }, (t) -> {
      blue = color("&b" + teams.get("blue"));
      red = color("&c" + teams.get("red"));
      green = color("&a" + teams.get("green"));
      yellow = color("&e" + teams.get("yellow"));
      String secondsParsed = LocalTime.MIDNIGHT.plus(Duration.ofSeconds(Timer.getSecondsParsed())).format(DateTimeFormatter.ofPattern("mm:ss"));
      teams.forEach((team, life) -> {
        if (life.equals("0")) teams.put(team, "✕");
      });
      getLogger().broadcastBar(Lang.TWO_TIMES_FOUR_ACTIONBAR.getConfigValue(new String[]{prefix, blue, red, green, yellow, secondsParsed}));
    });
  }

  private static Map<String, String> createMap() {
    Map<String, String> myMap = new HashMap<>();
    myMap.put("blue", "4");
    myMap.put("red", "4");
    myMap.put("green", "4");
    myMap.put("yellow", "4");
    return myMap;
  }

  private String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
}
