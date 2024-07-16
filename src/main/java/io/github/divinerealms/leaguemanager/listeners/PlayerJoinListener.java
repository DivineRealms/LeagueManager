package io.github.divinerealms.leaguemanager.listeners;

import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.UUID;

@Getter
public class PlayerJoinListener implements Listener {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Helper helper;
  private final Logger logger;

  public PlayerJoinListener(UtilManager utilManager) {
    this.utilManager = utilManager;
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    String playerName = player.getName(), folderName = "playerdata";
    UUID playerUUID = player.getUniqueId();

    if (!getDataManager().configExists(folderName, playerUUID.toString())) {
      if (getDataManager().configExists("playerdataold", playerName)) {
        File oldFile = new File(getUtilManager().getPlugin().getDataFolder() + File.separator + "playerdataold", playerName + ".yml");
        File newFile = new File(getUtilManager().getPlugin().getDataFolder() + File.separator + folderName, playerUUID + ".yml");
        FileUtil.copy(oldFile, newFile);
        getLogger().info("Migrated playerdata file for &b" + playerName + " (&o" + playerUUID + "&b)");
        return;
      } else {
        getDataManager().createNewFile(playerUUID.toString(), null);
      }
      getLogger().info("Creating playerdata file for &b" + playerName + " (&o" + playerUUID + "&b)");
    }

    getDataManager().setConfig(folderName, playerUUID.toString());
    getDataManager().getConfig(playerUUID.toString()).set("name", playerName);

    if (getDataManager().getConfig(playerUUID.toString()).get("head") == null) {
      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(player.getName());
      skull.setItemMeta(skullMeta);
      getLogger().info("Setting head for player " + playerName);
      getDataManager().getConfig(playerUUID.toString()).set("head", skull);
    }

    String[] stats = new String[]{"goals","assists","yellow-cards","red-cards","clean-sheets"};
    for (String each : stats) {
      if (!getDataManager().getConfig(playerUUID.toString()).contains(each)) {
        getLogger().info("Setting " + each + " for player " + playerName);
        getDataManager().getConfig(playerUUID.toString()).set(each, 0);
      }
    }

    if (!player.hasPermission("leaguemanager.banned") && getDataManager().getConfig(playerUUID.toString()).get("ban") != null) {
      getLogger().info("Removing ban strings from player's " + playerName + " config.");
      getDataManager().getConfig(playerUUID.toString()).set("ban", null);
    }

    if (!player.hasPermission("group.suspend") && getDataManager().getConfig(playerUUID.toString()).get("suspend") != null) {
      getLogger().info("Removing suspend strings from player's " + playerName + " config.");
      getDataManager().getConfig(playerUUID.toString()).set("suspend", null);
    }

    if (getDataManager().getPlayerName(playerUUID) == null) {
      getDataManager().addPlayerUUID(playerUUID, playerName);
    }
    getDataManager().saveConfig(playerUUID.toString());
  }
}
