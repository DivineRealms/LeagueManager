package io.github.divinerealms.managers;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.configs.Messages;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Helper;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class UtilManager {
  @Getter private final Messages messages;
  @Getter private final Config config;
  @Getter private final Logger logger;
  @Getter private final Helper helper;

  public UtilManager(final LeagueManager plugin) {
    this.messages = new Messages(plugin);
    this.config = new Config(plugin);
    this.logger = new Logger(plugin, this);
    this.helper = new Helper(plugin, this);
  }

  public void reloadUtils() {
    getMessages().reload();
    getConfig().reload();
    getLogger().reload();
  }
}
