package io.github.divinerealms.leaguemanager.gui.impl;

import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.gui.InventoryButton;
import io.github.divinerealms.leaguemanager.gui.InventoryGUI;
import io.github.divinerealms.leaguemanager.managers.GUIManager;
import io.github.divinerealms.leaguemanager.managers.RostersDataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class PerPlayerGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final GUIManager guiManager;
  private final RostersDataManager dataManager;

  public PerPlayerGUI(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.guiManager = guiManager;
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 5 * 9, "Podešavanje parametara");
  }

  @Override
  public void decorate(Player player) {
    String type, teamName = getGuiManager().getTeamName();

    if (getHelper().groupExists(teamName)) {
      if (getHelper().groupHasMeta(teamName, "team")) type = "main";
      else if (getHelper().groupHasMeta(teamName, "b")) type = "juniors";
      else type = null;

      if (type == null) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      if (teamConfig == null) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"fajl"}));
        return;
      }

      String targetName = getGuiManager().getTarget().getName();
      for (int slot = 0; slot <= 44; slot++) {
        if (slot == 10) {
          boolean isManager = teamConfig.getString(teamName + ".manager") != null && teamConfig.getString(teamName + ".manager").equals(targetName);
          this.addButton(slot, this.createHead(!isManager ? "&aPostavite za Menadžera" : "&cSkinite Menadžera",
              !isManager ? "9868" : "9335")
              .consumer(event -> {
                UUID targetUUID = getGuiManager().getTarget().getUniqueId();
                if (!isManager) {
                  teamConfig.set(teamName.toUpperCase() + ".manager", targetName);
                  getDataManager().saveConfig(type);
                  getHelper().playerAddPermission(targetUUID, "tab.group." + teamName + "-director");
                  getHelper().playerAddGroup(targetUUID, "director");
                  getLogger().send("fcfa", Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{targetName, "MANAGER", teamName.toUpperCase()}));
                } else {
                  teamConfig.set(teamName.toUpperCase() + ".manager", null);
                  getDataManager().saveConfig(type);
                  getHelper().playerRemovePermission(targetUUID, "tab.group." + teamName + "-director");
                  getHelper().playerRemoveGroup(targetUUID, "director");
                  getLogger().send("fcfa", Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{targetName, "PLAYER", teamName.toUpperCase()}));
                }
                event.getWhoClicked().closeInventory();
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 11) {
          boolean isCaptain = teamConfig.getString(teamName + ".captain") != null && teamConfig.getString(teamName + ".captain").equals(targetName);
          this.addButton(slot, this.createHead(!isCaptain ? "&aPostavite za Kapitena" : "&cSkinite Kapitena",
              !isCaptain ? "9868" : "9335")
              .consumer(event -> {
                if (!isCaptain) {
                  teamConfig.set(teamName.toUpperCase() + ".captain", targetName);
                  getDataManager().saveConfig(type);
                  getLogger().send("fcfa", Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{targetName, "CAPTAIN", teamName.toUpperCase()}));
                } else {
                  teamConfig.set(teamName.toUpperCase() + ".captain", null);
                  getDataManager().saveConfig(type);
                  getLogger().send("fcfa", Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{targetName, "PLAYER", teamName.toUpperCase()}));
                }
                event.getWhoClicked().closeInventory();
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 12) {
          boolean inTeam = teamConfig.getConfigurationSection(teamName + ".players").getKeys(false).contains(targetName);
          this.addButton(slot, this.createHead(inTeam ? "&dRaskinite Ugovor" : "&aPotpišite Igrača",
                  inTeam ? "9490" : "21771")
              .consumer(event -> {
                if (inTeam) {
                  getHelper().playerRemoveGroup(getGuiManager().getTarget().getUniqueId(), teamName);
                  teamConfig.set(teamName.toUpperCase() + ".players." + targetName, null);
                  getDataManager().saveConfig(type);
                  getLogger().send("fcfa", Lang.ROSTERS_USER_REMOVED.getConfigValue(new String[]{player.getName(), targetName, teamName.toUpperCase()}));
                } else {
                  getHelper().playerAddGroup(getGuiManager().getTarget().getUniqueId(), teamName);
                  teamConfig.set(teamName.toUpperCase() + ".players." + targetName, "/");
                  getDataManager().saveConfig(type);
                  getLogger().send("fcfa", Lang.ROSTERS_USER_ADDED.getConfigValue(new String[]{player.getName(), targetName, teamName.toUpperCase()}));
                }
                event.getWhoClicked().closeInventory();
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 13) {
          this.addButton(slot, this.createHead("&3Parametri", "10424")
              .consumer(event -> {
                event.getWhoClicked().closeInventory();
                getLogger().send(event.getWhoClicked(), Lang.ROSTERS_SET_USAGE.getConfigValue(null));
              }));
        } else if (slot == 33) {
          this.addButton(slot, this.createHead("&6Nazad", "9651")
              .consumer(event -> {
                event.getWhoClicked().closeInventory();
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 34) {
          this.addButton(slot, this.createHead("&cZatvorite", "3229")
              .consumer(event -> event.getWhoClicked().closeInventory()));
        } else this.addButton(slot, this.createButton("&r", (byte) 7));
      }
      this.addButton(28, this.createPlayerHead(teamConfig, type, "&b&l" + targetName, targetName, playerRole(teamConfig, targetName), "", getUtilManager().color("&fPozicija: &e" + teamConfig.getString(teamName + ".players." + targetName + ".position"))).consumer(event -> {}));
    }
    super.decorate(player);
  }

  private String playerRole(FileConfiguration team, String teamPlayer) {
    String player = getUtilManager().color("&7Igrač");
    String manager = team.getString(getGuiManager().getTeamName() + ".manager");
    String captain = team.getString(getGuiManager().getTeamName() + ".captain");

    if (manager != null && manager.equals(teamPlayer)) {
      return getUtilManager().color("&2Director");
    } else if (captain != null && captain.equals(teamPlayer)) {
      return getUtilManager().color("&4Kapiten");
    }

    return player;
  }

  private InventoryButton createPlayerHead(FileConfiguration team, String type, String title, String playerName, String... lore) {
    ItemStack skull = team.getItemStack(getGuiManager().getTeamName() + ".players." + playerName + ".head");
    if (skull == null) {
      ItemStack newSkull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) newSkull.getItemMeta();
      skullMeta.setOwner(playerName);
      skullMeta.setDisplayName(getUtilManager().color(title));
      newSkull.setItemMeta(skullMeta);
      team.set(getGuiManager().getTeamName() + ".players." + playerName + ".head", newSkull);
      getDataManager().saveConfig(type);
      return new InventoryButton().creator(player -> newSkull).consumer(event -> {});
    }
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    skullMeta.setDisplayName(getUtilManager().color(title));
    skullMeta.setLore(Arrays.asList(lore));
    skull.setItemMeta(skullMeta);
    return new InventoryButton().creator(player -> skull).consumer(event -> {});
  }

  private InventoryButton createButton(String title, byte damage, String... lore) {
    ItemStack button = new ItemStack(Material.STAINED_GLASS_PANE, 1, damage);
    ItemMeta buttonMeta = button.getItemMeta();
    buttonMeta.setDisplayName(getUtilManager().color(title));
    buttonMeta.setLore(Arrays.asList(lore));
    button.setItemMeta(buttonMeta);
    return new InventoryButton()
        .creator(player -> button)
        .consumer(event -> {});
  }

  private InventoryButton createHead(String title, String headId, String... lore) {
    HeadDatabaseAPI headDatabaseAPI = new HeadDatabaseAPI();
    ItemStack head = null;
    try {
      head = headDatabaseAPI.getItemHead(headId);
      ItemMeta headMeta = head.getItemMeta();
      headMeta.setDisplayName(getUtilManager().color(title));
      headMeta.setLore(Arrays.asList(lore));
      head.setItemMeta(headMeta);
    } catch (NullPointerException exception) {
      getLogger().send("helper", "nemoguće pronaći glavu " + headId);
    }
    ItemStack finalHead = head;
    return new InventoryButton()
        .creator(player -> finalHead != null ? finalHead : new ItemStack(Material.BARRIER))
        .consumer(event -> {});
  }
}
