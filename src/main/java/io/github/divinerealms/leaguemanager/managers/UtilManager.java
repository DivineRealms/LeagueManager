package io.github.divinerealms.leaguemanager.managers;

import io.github.divinerealms.leaguemanager.utils.Logger;
import io.github.divinerealms.leaguemanager.utils.Helper;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

@Getter
public class UtilManager {
  private final Plugin plugin;
  private Logger logger;
  private Helper helper;

  public UtilManager(final Plugin plugin) {
    this.plugin = plugin;
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
  }

  public void reload() {
    this.logger = new Logger(plugin);
    this.helper = new Helper(plugin);
  }

  public String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
}
