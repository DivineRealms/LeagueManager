package io.github.divinerealms.commands.timers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.*;

@Getter
@CommandAlias("1x8")
@CommandPermission("leaguemanager.command.1x8")
public class OXECommand extends BaseCommand {
  private final Plugin plugin;
  private final UtilManager utilManager;
  private final Logger logger;
  private static Map<String, Integer> teams;
  private static Map<String, String> colored_names;
  private static String[] team_names;
  private Time time;
  private String prefix;

  public OXECommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();

    teams = new HashMap<>();
    colored_names = new LinkedHashMap<>();
    reset();
  }

  @Default
  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("leaguemanager.command.1x8.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
  }

  @Subcommand("start|s")
  @CommandPermission("leaguemanager.command.1x8.start")
  public void onStart(CommandSender sender, String[] args) {
    if (!getUtilManager().isTaskQueued(Timer.assignedTaskId)) {
      if (args.length == 0) {
        Timer.assignedTaskId = startResult().startTask();
        getLogger().send("hoster", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(Timer.assignedTaskId)}));
      } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("stop")
  @CommandPermission("leaguemanager.command.1x8.stop")
  public void onStop(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(Timer.assignedTaskId)) {
      if (args.length == 0) {
        startResult().getAfterTimer().run();
        Bukkit.getScheduler().cancelTasks(getPlugin());
      } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("type")
  @CommandPermission("leaguemanager.command.1x8.type")
  public void onType(CommandSender sender, String[] args) {
    if (!getUtilManager().isTaskQueued(Timer.assignedTaskId)) {
      if (args.length == 1) {
        prefix = getUtilManager().color(prefix + " " + args[0]);
        getLogger().send(sender, Lang.TIMER_PREFIX_SET.getConfigValue(new String[]{prefix}));
      } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("add")
  @CommandCompletion("gray|red|gold|yellow|green|blue|light_purple|black")
  @CommandPermission("leaguemanager.command.1x8.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(Timer.assignedTaskId)) {
      if (args.length == 1) {
        if (teams.containsKey(args[0])) {
          if (!teams.get(args[0]).equals(2)) {
            teams.put(args[0], teams.get(args[0]) + 1);
            colored_names.put(args[0], ChatColor.valueOf(args[0].toUpperCase()) + String.valueOf(teams.get(args[0])));
            getLogger().send("hoster", Lang.RESULT_ADDED_LIFE.getConfigValue(new String[]{args[0]}));
          } else getLogger().send(sender, Lang.RESULT_FULL_LIVES.getConfigValue(new String[]{args[0]}));
        } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("remove|rem")
  @CommandCompletion("gray|red|gold|yellow|green|blue|light_purple|black")
  @CommandPermission("leaguemanager.command.1x8.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(Timer.assignedTaskId)) {
      if (args.length == 1) {
        if (teams.containsKey(args[0])) {
          if (!teams.get(args[0]).equals(0)) {
            teams.put(args[0], teams.get(args[0]) - 1);
            colored_names.put(args[0], ChatColor.valueOf(args[0].toUpperCase()) + String.valueOf(teams.get(args[0])));
            getLogger().send("hoster", Lang.RESULT_REMOVED_LIFE.getConfigValue(new String[]{args[0]}));
          } else getLogger().send(sender, Lang.RESULT_ELIMINATED.getConfigValue(new String[]{args[0]}));
        } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.ONE_TIMES_EIGHT_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  private Timer startResult() {
    return new Timer(getPlugin(), (int) time.toSeconds(), () -> {
      getLogger().send("default", Lang.TIMER_STARTING.getConfigValue(new String[]{getPrefix()}));
      getLogger().broadcastBar(Lang.RESULT_STARTING.getConfigValue(new String[]{getPrefix()}));
    }, () -> {
      getLogger().send("default", Lang.ONE_TIMES_EIGHT_RESULT_OVER.getConfigValue(new String[]{getPrefix(),colored_names.get("gray"),colored_names.get("red"),colored_names.get("gold"),colored_names.get("yellow"),colored_names.get("green"),colored_names.get("blue"),colored_names.get("light_purple"),colored_names.get("black")}));
      getLogger().broadcastBar(Lang.ONE_TIMES_EIGHT_RESULT_END.getConfigValue(new String[]{getPrefix(),colored_names.get("gray"),colored_names.get("red"),colored_names.get("gold"),colored_names.get("yellow"),colored_names.get("green"),colored_names.get("blue"),colored_names.get("light_purple"),colored_names.get("black")}));
      reset();
    }, (t) -> {
      teams.forEach((team, life) -> {
        if (life.equals(0)) {
          String x = ChatColor.valueOf(team.toUpperCase()) + "✕";
          if (!colored_names.get(team).equals(x))
            colored_names.put(team, ChatColor.valueOf(team.toUpperCase()) + "✕");
        }
      });
      String secondsParsed = UtilManager.formatTime(Timer.getSecondsParsed());
      getLogger().broadcastBar(Lang.ONE_TIMES_EIGHT_ACTIONBAR.getConfigValue(new String[]{getPrefix(),colored_names.get("gray"),colored_names.get("red"),colored_names.get("gold"),colored_names.get("yellow"),colored_names.get("green"),colored_names.get("blue"),colored_names.get("light_purple"),colored_names.get("black"),secondsParsed}));
    });
  }

  private void reset() {
    prefix = "&bEvent";
    time = Time.parseString("30min");
    team_names = new String[]{"gray", "red", "gold", "yellow", "green", "blue", "light_purple", "black"};
    teams.clear();
    colored_names.clear();
    for (String team : team_names) {
      teams.put(team, 2);
      colored_names.put(team, ChatColor.valueOf(team.toUpperCase()) + String.valueOf(teams.get(team)));
    }
  }
}
