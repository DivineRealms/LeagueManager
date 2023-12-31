package io.github.divinerealms.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class Timer implements Runnable {
  private final Plugin plugin;
  @Getter private Integer assignedTaskId;
  @Getter private int seconds;
  @Getter private int secondsParsed;
  private final Consumer<Timer> everySecond;
  private final Runnable beforeTimer;
  private final Runnable afterTimer;

  public Timer(Plugin plugin, int seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<Timer> everySecond) {
    this.plugin = plugin;
    this.seconds = seconds;
    this.secondsParsed = 0;
    this.beforeTimer = beforeTimer;
    this.afterTimer = afterTimer;
    this.everySecond = everySecond;
  }

  @Override
  public void run() {
    if (secondsParsed == seconds + 1) {
      afterTimer.run();

      if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
      return;
    }

    if (secondsParsed < 1) beforeTimer.run();

    everySecond.accept(this);
    secondsParsed++;
  }

  public void scheduleTimer() {
    this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
  }
}
