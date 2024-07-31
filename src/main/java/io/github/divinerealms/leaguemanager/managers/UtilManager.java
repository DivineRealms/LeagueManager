package io.github.divinerealms.leaguemanager.managers;

import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.utils.CubeCleaner;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UtilManager {
  private final Plugin plugin;
  private final Logger logger;
  private final Helper helper;
  private final CubeCleaner cubeCleaner;
  @Setter private boolean fcEnabled = true;
  @Getter private boolean debug = true;

  public UtilManager(Plugin plugin) {
    this.plugin = plugin;
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
    this.cubeCleaner = new CubeCleaner(this);

    getPlugin().getServer().getScheduler().runTaskTimer(getPlugin(), () -> {
      getCubeCleaner().clearCubes();
      if (!getCubeCleaner().isEmpty()) {
        getLogger().broadcast(Lang.CLEARED_CUBES.getConfigValue(new String[]{String.valueOf(getCubeCleaner().getAmount())}));
      }
    }, 20L, getCubeCleaner().getRemoveInterval());
  }

  public String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public boolean isTaskQueued(final Integer taskId) {
    if (taskId != null) return getPlugin().getServer().getScheduler().isQueued(taskId);
    else return false;
  }

  public static String formatTime(int time) {
    return LocalTime.MIDNIGHT.plus(Duration.ofSeconds(time)).format(DateTimeFormatter.ofPattern("mm:ss"));
  }
}
