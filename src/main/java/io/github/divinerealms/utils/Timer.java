package io.github.divinerealms.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.function.Consumer;

@Getter
public class Timer implements Runnable {
  private final Plugin plugin;
  private final BukkitScheduler scheduler;
  @Setter private Integer assignedTaskId;
  private final int seconds;
  private int secondsParsed;
  private final Consumer<Timer> everySecond;
  private final Runnable beforeTimer;
  private final Runnable afterTimer;

  public Timer(final Plugin plugin, int seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<Timer> everySecond) {
    this.plugin = plugin;
    this.scheduler = plugin.getServer().getScheduler();
    this.seconds = seconds;
    this.secondsParsed = 0;
    this.beforeTimer = beforeTimer;
    this.afterTimer = afterTimer;
    this.everySecond = everySecond;
  }

  @Override
  public void run() {
    if (getSecondsParsed() == getSeconds() + 1) {
      getAfterTimer().run();
      if (getAssignedTaskId() != null)
        cancelTask(getAssignedTaskId());
      return;
    }

    if (getSecondsParsed() < 1) getBeforeTimer().run();

    getEverySecond().accept(this);
    secondsParsed++;
  }

  public void scheduleTimer() {
    setAssignedTaskId(getScheduler().scheduleSyncRepeatingTask(getPlugin(), this, 20L, 20L));
  }

  public void cancelTask(final int taskId) {
    getScheduler().cancelTask(taskId);
  }
}
