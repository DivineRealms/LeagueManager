package io.github.divinerealms.leaguemanager.utils;

import io.github.divinerealms.leaguemanager.configs.Config;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

@Getter
public class CubeCleaner {
  private final static YamlConfiguration config = Config.getConfig("config.yml");
  private final UtilManager utilManager;
  private final Logger logger;
  private final Plugin plugin;
  private int minutes = 5;
  private int removeInterval = minutes * 60 * 20;
  public boolean empty;
  public int amount = 0;

  public CubeCleaner(UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.plugin = utilManager.getPlugin();

    if (!config.contains("practice-areas")) {
      getLogger().info(Lang.PRACTICE_AREAS_EMPTY.getConfigValue(null));
      return;
    }

    this.minutes = config.getInt("clear-cube-interval", 5);
    this.removeInterval = minutes * 60 * 20;
  }

  public void clearCubes() {
    for (String locName : config.getConfigurationSection("practice-areas").getKeys(false)) {
      Location location = (Location) config.get("practice-areas." + locName);
      Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 100, 100, 100);

      this.empty = true;
      for (Entity entity : nearbyEntities) {
        if (entity instanceof Slime) {
          this.amount++;
          entity.remove();
          this.empty = false;
        }
      }
    }
  }
}
