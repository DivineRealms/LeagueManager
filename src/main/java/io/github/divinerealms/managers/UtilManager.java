package io.github.divinerealms.managers;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.ChatColor;

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

  public String color(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
}
