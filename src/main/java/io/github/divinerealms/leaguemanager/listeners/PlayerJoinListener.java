package io.github.divinerealms.leaguemanager.listeners;

import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@Getter
public class PlayerJoinListener implements Listener {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Helper helper;
  private final Logger logger;

  public PlayerJoinListener(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String playerName = player.getName();
    getDataManager().setFolderName("playerdata");
    if (!getDataManager().configExists(playerName)) {
      getDataManager().createNewFile(playerName, null);
      getLogger().info("Creating playerdata file for &b" + player.getName());
      getDataManager().setConfig(playerName);
      FileConfiguration playerData = getDataManager().getConfig(playerName);
      playerData.set("name", player.getName());
      if (playerData.get("head") == null) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(player.getName());
        skull.setItemMeta(skullMeta);
        playerData.set("head", skull);
      }
      if (!player.hasPermission("leaguemanager.banned") && playerData.get("ban") != null) {
        playerData.set("ban", null);
      }
      getDataManager().saveConfig(playerName);
    }
  }
}
