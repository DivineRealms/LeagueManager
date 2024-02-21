package io.github.divinerealms.utils;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.function.Consumer;

public class Timer implements Runnable {
  @Getter private final Plugin plugin;
  @Getter private final BukkitScheduler scheduler;
  public static Integer assignedTaskId;
  @Getter public static int seconds;
  @Getter public static int secondsParsed = 0;
  private final Consumer<Timer> everySecond;
  @Getter private final Runnable beforeTimer;
  @Getter private final Runnable afterTimer;
  @Getter public static boolean isRunning = false;

  public Timer(final Plugin plugin, int seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<Timer> everySecond) {
    this.plugin = plugin;
    this.scheduler = plugin.getServer().getScheduler();
    Timer.seconds = seconds;
    secondsParsed = 0;
    this.beforeTimer = beforeTimer;
    this.afterTimer = afterTimer;
    this.everySecond = everySecond;
  }

  @Override
  public void run() {
    if (secondsParsed == seconds + 1) {
      afterTimer.run();

      if (assignedTaskId != null) cancelTask(assignedTaskId);
      return;
    }

    if (secondsParsed == 0) beforeTimer.run();

    everySecond.accept(this);
    secondsParsed++;
  }

  public int startTask() {
    return assignedTaskId = getScheduler().runTaskTimerAsynchronously(getPlugin(), this, 20L, 20L).getTaskId();
  }

  public void cancelTask(final int taskId) {
    getScheduler().cancelTask(taskId);
  }
}
