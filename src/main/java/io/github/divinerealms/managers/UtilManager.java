package io.github.divinerealms.managers;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UtilManager {
  private final LeagueManager plugin;
  private Logger logger;
  private Helper helper;

  public UtilManager(final LeagueManager plugin) {
    this.plugin = plugin;
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
  }

  public void reload() {
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
  }

  public boolean isTaskQueued(final Integer taskId) {
    if (taskId != null) return getPlugin().getServer().getScheduler().isQueued(taskId);
    else return false;
  }

  public String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static String formatTime(int time) {
    return LocalTime.MIDNIGHT.plus(Duration.ofSeconds(time)).format(DateTimeFormatter.ofPattern("mm:ss"));
  }
}
