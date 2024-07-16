package io.github.divinerealms.leaguemanager.listeners;

import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class PlayerQuitListener implements Listener {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Logger logger;

  public PlayerQuitListener(UtilManager utilManager) {
    this.utilManager = utilManager;
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.logger = utilManager.getLogger();
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    String playerName = player.getName(), playerAddress = player.getAddress().getAddress().getHostAddress();
    getDataManager().setConfig("playerdata", player.getUniqueId().toString());

    if (getDataManager().getConfig(player.getUniqueId().toString()).get("address") == null) {
      getLogger().info("Setting IP Address for player " + playerName);
      getDataManager().getConfig(player.getUniqueId().toString()).set("address", playerAddress);
      getDataManager().saveConfig(player.getUniqueId().toString());
    }

    if (!getDataManager().getConfig(player.getUniqueId().toString()).get("address").equals(playerAddress)) {
      getLogger().info("Updating IP Address for player " + playerName + " (new IP: " + playerAddress + ").");
      getDataManager().getConfig(player.getUniqueId().toString()).set("address", playerAddress);
      getDataManager().saveConfig(player.getUniqueId().toString());
    }
  }
}
