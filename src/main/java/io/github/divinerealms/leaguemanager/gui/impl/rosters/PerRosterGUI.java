package io.github.divinerealms.leaguemanager.gui.impl.rosters;

import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.gui.InventoryButton;
import io.github.divinerealms.leaguemanager.gui.InventoryGUI;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.GUIManager;
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

@SuppressWarnings("deprecation")
@Getter
public class PerRosterGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  private final Logger logger;
  private final Helper helper;
  private final DataManager dataManager;
  private final String team;

  public PerRosterGUI(UtilManager utilManager, GUIManager guiManager) {
    this.utilManager = utilManager;
    this.guiManager = guiManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.team = guiManager.getTeamName();
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 6 * 9, "Informacije o Rosteru");
  }

  @Override
  public void decorate(Player player) {
    for (int slot = 0; slot <= 53; slot++) {
      if (slot == 37) {
        this.addButton(slot, this.createHead("&f" + getTeam() + " Stadion", "43317", getUtilManager().color("&7Klik za teleport!"))
            .consumer(event -> {
              Player target = (Player) event.getWhoClicked();
              target.closeInventory();
              target.performCommand("warp " + getTeam() + "top");
            }));
      } else if (slot == 38) {
        this.addButton(slot, hasAccess(player) ?
            this.createHead("&ePodešavanje rostera", "49956", getUtilManager().color("&7Klik da podesite"), getUtilManager().color("&7parametre tima."))
                .consumer(event -> {
                  if (!hasAccess(player)) return;
                  event.getWhoClicked().closeInventory();
                  getLogger().send(player, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
                }) :
            this.createButton("&r", (byte) 7));
      } else if (slot == 39) {
        this.addButton(slot, hasAccess(player) ?
            this.createHead("&aPotpišite igrača", "9885", getUtilManager().color("&7Klik da vidite kako"), getUtilManager().color("&7potpisati igrače."))
                .consumer(event -> {
                  if (!hasAccess(player)) return;
                  event.getWhoClicked().closeInventory();
                  getLogger().send(player, Lang.ROSTERS_ADD_USAGE.getConfigValue(null));
                }) :
            this.createButton("&r", (byte) 7));
      } else if (slot == 42) {
        this.addButton(slot, this.createHead("&6Nazad", "9651")
            .consumer(event -> {
              event.getWhoClicked().closeInventory();
              getGuiManager().openGUI(new RostersGUI(getUtilManager(), getGuiManager()), (Player) event.getWhoClicked());
            }));
      } else if (slot == 43) {
        this.addButton(slot, this.createHead("&cZatvorite", "3229")
            .consumer(event -> event.getWhoClicked().closeInventory()));
      } else this.addButton(slot, this.createButton("&r", (byte) 7));
    }

    int slot = 10;
    String type;

    if (getHelper().groupExists(getTeam())) {
      type = getHelper().groupGetMetaWeight(getTeam()) == 100 ? "main" :
          getHelper().groupGetMetaWeight(getTeam()) == 99 ? "juniors" : null;

      if (type == null) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      getDataManager().setFolderName("teamdata");
      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      if (teamConfig == null) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"fajl"}));
        return;
      }

      boolean managerFound = false;
      boolean captainFound = false;

      for (String teamPlayer : teamConfig.getStringList(getTeam() + ".players")) {
        getDataManager().setFolderName("playerdata");
        getDataManager().setConfig(teamPlayer);
        FileConfiguration playerData = getDataManager().getConfig(teamPlayer);
        if (playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&2Director"))) {
          this.addButton(10, this.createPlayerHead(playerData, teamPlayer, "&a&l" + teamPlayer, teamPlayer, playerRole(teamConfig, teamPlayer), "",
                  getUtilManager().color("&fPozicija: &e" + playerData.getString("position", "/")),
                  getUtilManager().color("&fDržava: &e" + playerData.getString("country", "/")),
                  getUtilManager().color("&fBroj: &e" + playerData.getInt("number", 0)))
              .consumer(event -> {
                Player target = (Player) event.getWhoClicked();
                if (!hasAccess(target)) return;
                getGuiManager().setTarget(Bukkit.getOfflinePlayer(teamPlayer));
                target.closeInventory();
                getGuiManager().openGUI(new PerPlayerGUI(getUtilManager(), getGuiManager()), target);
              }));
          managerFound = true;
        } else if (playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&4Kapiten"))) {
          this.addButton(11, this.createPlayerHead(playerData, teamPlayer, "&c&l" + teamPlayer, teamPlayer, playerRole(teamConfig, teamPlayer), "",
                  getUtilManager().color("&fPozicija: &e" + playerData.getString("position", "/")),
                  getUtilManager().color("&fDržava: &e" + playerData.getString("country", "/")),
                  getUtilManager().color("&fBroj: &e" + playerData.getInt("number", 0)),
                  getUtilManager().color("&fUgovor: &e" + playerData.getInt("contract", 0) + " sezone"))
              .consumer(event -> {
                Player target = (Player) event.getWhoClicked();
                if (!hasAccess(target)) return;
                getGuiManager().setTarget(Bukkit.getOfflinePlayer(teamPlayer));
                target.closeInventory();
                getGuiManager().openGUI(new PerPlayerGUI(getUtilManager(), getGuiManager()), target);
              }));
          captainFound = true;
        }
      }

      for (String teamPlayer : teamConfig.getStringList(getTeam() + ".players")) {
        getDataManager().setFolderName("playerdata");
        getDataManager().setConfig(teamPlayer);
        FileConfiguration playerData = getDataManager().getConfig(teamPlayer);
        if (!playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&2Director")) && !playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&4Kapiten"))) {
          while (slot == 17 || slot == 18 || slot == 26 || slot == 10 && managerFound || captainFound && slot == 11) slot++;
          if (slot > 25) break;
          this.addButton(slot, this.createPlayerHead(playerData, teamPlayer, "&b&l" + teamPlayer, teamPlayer, playerRole(teamConfig, teamPlayer), "",
                  getUtilManager().color("&fPozicija: &e" + playerData.getString("position", "")),
                  getUtilManager().color("&fDržava: &e" + playerData.getString("country", "/")),
                  getUtilManager().color("&fBroj: &e" + playerData.getInt("number", 0)),
                  getUtilManager().color("&fUgovor: &e" + playerData.getInt("contract", 0) + " sezone"))
              .consumer(event -> {
                Player target = (Player) event.getWhoClicked();
                if (!hasAccess(target)) return;
                getGuiManager().setTarget(Bukkit.getOfflinePlayer(teamPlayer));
                target.closeInventory();
                getGuiManager().openGUI(new PerPlayerGUI(getUtilManager(), getGuiManager()), target);
              }));
          slot++;
        }
      }
    }
    super.decorate(player);
  }

  private String playerRole(FileConfiguration team, String teamPlayer) {
    String player = getUtilManager().color("&7Igrač");
    String manager = team.getString(getTeam() + ".manager");
    String captain = team.getString(getTeam() + ".captain");

    if (manager != null && manager.equals(teamPlayer)) {
      return getUtilManager().color("&2Director");
    } else if (captain != null && captain.equals(teamPlayer)) {
      return getUtilManager().color("&4Kapiten");
    }

    return player;
  }

  private boolean hasAccess(Player player) {
    return getHelper().playerInGroup(player.getUniqueId(), "fcfa");
  }

  private InventoryButton createPlayerHead(FileConfiguration playerData, String name, String title, String playerName, String... lore) {
    ItemStack skull = playerData.getItemStack("head");
    if (skull == null) {
      ItemStack newSkull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) newSkull.getItemMeta();
      skullMeta.setOwner(playerName);
      skullMeta.setDisplayName(getUtilManager().color(title));
      newSkull.setItemMeta(skullMeta);
      playerData.set("head", newSkull);
      getDataManager().saveConfig(name);
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
