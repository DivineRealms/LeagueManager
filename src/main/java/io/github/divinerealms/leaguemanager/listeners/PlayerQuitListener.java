package io.github.divinerealms.leaguemanager.listeners;

import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class PlayerQuitListener implements Listener {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Logger logger;

  public PlayerQuitListener(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.logger = utilManager.getLogger();
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String playerName = player.getName();
    getDataManager().setFolderName("playerdata");
    getDataManager().setConfig(playerName);
    FileConfiguration playerData = getDataManager().getConfig(playerName);
    playerData.set("address", player.getAddress().getAddress().getHostAddress());
    getDataManager().saveConfig(playerName);
  }
}
