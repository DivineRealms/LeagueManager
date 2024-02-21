package io.github.divinerealms.commands.timers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import io.github.divinerealms.utils.Timer;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@Getter
@CommandAlias("timer")
@CommandPermission("leaguemanager.command.timer")
public class TimerCommand extends BaseCommand {
  private final Plugin plugin;
  private final UtilManager utilManager;
  private final Logger logger;
  private Time time;
  private String prefix;
  private int timerId;

  public TimerCommand(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();

    reset();
  }

  @Default
  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("leaguemanager.command.timer.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
  }

  @Subcommand("start|s")
  @CommandPermission("leaguemanager.command.timer.start")
  public void onStart(CommandSender sender, String[] args) {
    if (!getUtilManager().isTaskQueued(getTimerId()) && isSetup() && !Timer.isRunning() && args.length == 0) {
      timerId = startTimer().startTask();
      Timer.isRunning = true;
      getLogger().send("hoster", Lang.TIMER_CREATE.getConfigValue(new String[]{String.valueOf(getTimerId())}));
    } else getLogger().send(sender, Lang.TIMER_ALREADY_RUNNING.getConfigValue(null));
  }

  @Subcommand("stop")
  @CommandPermission("leaguemanager.command.timer.stop")
  public void onStop(CommandSender sender, String[] args) {
    if (getUtilManager().isTaskQueued(getTimerId()) && isSetup() && args.length == 0) {
      getLogger().send(sender, Lang.TIMER_STOP.getConfigValue(new String[]{String.valueOf(getTimerId())}));
      startTimer().cancelTask(getTimerId());
    } else getLogger().send(sender, Lang.TIMER_NOT_AVAILABLE.getConfigValue(null));
  }

  @Subcommand("time")
  @CommandPermission("leaguemanager.command.timer.setup")
  public void onTime(CommandSender sender, String[] args) {
    if (args.length == 1) {
      try {
        time = Time.parseString(args[0]);
        getLogger().send(sender, Lang.TIMER_TIME_SET.getConfigValue(new String[]{time.toString()}));
      } catch (Time.TimeParseException | NullPointerException e) {
        getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
      }
    } else getLogger().send(sender, Lang.TIMER_HELP.getConfigValue(null));
  }

  @Subcommand("prefix")
  @CommandPermission("leaguemanager.command.timer.setup")
  public void onPrefix(CommandSender sender, String[] args) {
    prefix = getUtilManager().color(StringUtils.join(args, " ", 0, args.length));
    getLogger().send(sender, Lang.TIMER_PREFIX_SET.getConfigValue(new String[]{prefix}));
  }

  private Timer startTimer() {
    return new Timer(getPlugin(), (int) getTime().toSeconds(), () ->
        getLogger().send("default", Lang.TIMER_STARTING.getConfigValue(new String[]{getPrefix()})), () -> {
      getLogger().send("default", Lang.TIMER_OVER.getConfigValue(new String[]{String.valueOf(getTimerId())}));
      getLogger().broadcastBar(Lang.TIMER_END.getConfigValue(new String[]{getPrefix()}));
      Timer.isRunning = false;
    }, (t) -> {
      String secondsParsed = UtilManager.formatTime(Timer.getSecondsParsed());
      String seconds = UtilManager.formatTime(Timer.getSeconds());
      getLogger().broadcastBar(Lang.TIMER_CURRENT_TIME.getConfigValue(new String[]{getPrefix(), secondsParsed, seconds}));
    });
  }

  private boolean isSetup() {
    return getTime() != null;
  }

  private void reset() {
    time = Time.parseString("20min");
    prefix = "&bEvent";
  }
}
