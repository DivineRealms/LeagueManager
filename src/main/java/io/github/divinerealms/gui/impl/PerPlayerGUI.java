package io.github.divinerealms.gui.impl;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.gui.InventoryButton;
import io.github.divinerealms.gui.InventoryGUI;
import io.github.divinerealms.managers.GUIManager;
import io.github.divinerealms.managers.RostersDataManager;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
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
  private final RostersDataManager dataManager;
  private final GUIManager guiManager;

  public PerPlayerGUI(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
    this.guiManager = guiManager;
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 3 * 9, "Kontrolisanje igrača...");
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
      for (int slot = 0; slot <= 26; slot++) {
        if (slot <= 9 || slot == 17 || slot >= 18 && !(slot >= 24 && slot <= 25)) {
          this.addButton(slot, this.createButton("&r", (byte) 7));
        } else if (slot == 10) {
          boolean isManager = teamConfig.getString(teamName + ".manager") != null && teamConfig.getString(teamName + ".manager").equals(targetName);
          this.addButton(slot, this.createButton(!isManager ? "&aPostavite za Menadžera" : "&cSkinite Menadžera",
              !isManager ? (byte) 5 : (byte) 6)
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
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), teamName, getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 11) {
          boolean isCaptain = teamConfig.getString(teamName + ".captain") != null && teamConfig.getString(teamName + ".captain").equals(targetName);
          this.addButton(slot, this.createButton(!isCaptain ? "&aPostavite za Kapitena" : "&cSkinite Kapitena",
              !isCaptain ? (byte) 5 : (byte) 6)
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
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), teamName, getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 24) {
          this.addButton(slot, this.createButton("&6Nazad", (byte) 1)
              .consumer(event -> {
                event.getWhoClicked().closeInventory();
                getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), teamName, getGuiManager()), (Player) event.getWhoClicked());
              }));
        } else if (slot == 25) {
          this.addButton(slot, this.createButton("&cZatvorite", (byte) 14)
              .consumer(event -> event.getWhoClicked().closeInventory()));
        }
      }

      this.addButton(13, this.createPlayerHead("&a" + targetName, targetName)
          .consumer(event -> {
            Player target = (Player) event.getWhoClicked();

          }));
    } else {
      type = null;
    }
    super.decorate(player);
  }

  private InventoryButton createPlayerHead(String title, String playerName, String... lore) {
    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    skullMeta.setOwner(playerName);
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
}
