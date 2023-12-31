package io.github.divinerealms.managers;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.LineChecker;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;

@Getter
public class UtilManager {
  private final Config config;
  private final Logger logger;
  private final Helper helper;
  private final LineChecker lineChecker;

  public UtilManager(final LeagueManager plugin) {
    this.config = new Config(plugin);
    this.logger = new Logger(plugin, this);
    this.helper = new Helper(plugin);
    this.lineChecker = new LineChecker(this);
  }

  public void reloadUtils() {
    getConfig().reload();
  }
}
