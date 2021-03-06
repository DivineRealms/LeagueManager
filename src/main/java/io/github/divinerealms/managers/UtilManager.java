package io.github.divinerealms.managers;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Messages;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;

public class UtilManager {
  @Getter private final Messages messages;
  @Getter private final Logger logger;
  @Getter private final Helper helper;

  public UtilManager(final LeagueManager plugin) {
    this.messages = new Messages(plugin);
    this.logger = new Logger(plugin, this);
    this.helper = new Helper(plugin);
  }

  public void reloadUtils() {
    getMessages().reload();
    getLogger().reload();
  }
}
