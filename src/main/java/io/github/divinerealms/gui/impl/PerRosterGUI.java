package io.github.divinerealms.gui.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class PerRosterGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final RostersDataManager dataManager;
  private final String team;
  private final GUIManager guiManager;

  public PerRosterGUI(UtilManager utilManager, String team, GUIManager guiManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
    this.team = team;
    this.guiManager = guiManager;
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, "Roster GUI");
  }

  @Override
  public void decorate(Player player) {
    for (int slot = 0; slot <= 35; slot++) {
      if (slot <= 9 || slot == 17 || slot == 18 || slot >= 26 && slot != 29 && slot != 31 && slot != 33) {
        this.addButton(slot, this.createButton("&r", (byte) 7));
      } else if (slot == 29) this.addButton(slot, this.createButton("&6Nazad", (byte) 1)
          .consumer(event -> {
            event.getWhoClicked().closeInventory();
            getGuiManager().openGUI(new RostersGUI(getUtilManager(), getGuiManager()), (Player) event.getWhoClicked());
          }));
      else if (slot == 31) this.addButton(slot, this.createButton("&f" + getTeam() + " &fStadion", (byte) 0, getUtilManager().color("&7Klik za teleport!"))
          .consumer(event -> {
            Player target = (Player) event.getWhoClicked();
            target.closeInventory();
            target.performCommand("warp " + getTeam() + "top");
          }));
      else if (slot == 33) this.addButton(slot, this.createButton("&cZatvorite", (byte) 14).consumer(event -> event.getWhoClicked().closeInventory()));
    }

    int slot = 9;
    String type = null;

    if (getHelper().groupExists(getTeam())) {
      if (getHelper().groupHasMeta(getTeam(), "team")) type = "main";
      else if (getHelper().groupHasMeta(getTeam(), "b")) type = "juniors";

      if (type == null) {
        getLogger().send(player, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{getTeam().toUpperCase()}));
        return;
      }

      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      if (teamConfig == null) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{type.toUpperCase()}));
        return;
      }

      boolean managerFound = false;
      boolean captainFound = false;

      for (String teamPlayer : teamConfig.getConfigurationSection(getTeam() + ".players").getKeys(false)) {
        if (playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&2Director"))) {
          this.addButton(10, this.createPlayerHead(teamConfig, type, "&a&l" + teamPlayer, teamPlayer, playerRole(teamConfig, teamPlayer), "", getUtilManager().color("&fPozicija: &e" + teamConfig.getString(getTeam() + ".players." + teamPlayer + ".position"))));
          managerFound = true;
        } else if (playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&4Kapiten"))) {
          this.addButton(11, this.createPlayerHead(teamConfig, type, "&c&l" + teamPlayer, teamPlayer, playerRole(teamConfig, teamPlayer), "", getUtilManager().color("&fPozicija: &e" + teamConfig.getString(getTeam() + ".players." + teamPlayer + ".position"))));
          captainFound = true;
        }
      }

      for (String teamPlayer : teamConfig.getConfigurationSection(getTeam() + ".players").getKeys(false)) {
        if (!playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&2Director")) && !playerRole(teamConfig, teamPlayer).equals(getUtilManager().color("&4Kapiten"))) {
          while (slot == 9 || slot == 17 || slot == 18 || managerFound && slot == 10 || captainFound && slot == 11) slot++;
          if (slot > 25) break;
          this.addButton(slot, this.createPlayerHead(teamConfig, type, "&b&l" + teamPlayer, teamPlayer, playerRole(teamConfig, teamPlayer), "", getUtilManager().color("&fPozicija: &e" + teamConfig.getString(getTeam() + ".players." + teamPlayer + ".position"))));
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

  private InventoryButton createPlayerHead(FileConfiguration team, String type, String title, String playerName, String... lore) {
    ItemStack skull = team.getItemStack(getTeam() + ".players." + playerName + ".head");
    if (skull == null) {
      ItemStack newSkull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) newSkull.getItemMeta();
      skullMeta.setOwner(playerName);
      newSkull.setItemMeta(skullMeta);
      team.set(getTeam() + ".players." + playerName + ".head", newSkull);
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

  @SuppressWarnings("unused")
  private ItemStack applySkinTexture(String headTexture) {
    final ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    assert skullMeta != null;
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    profile.getProperties().put("textures", new Property("textures", headTexture));
    try {
      Field profileField = skullMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(skullMeta, profile);
    } catch (NoSuchFieldException | IllegalAccessException exception) {
      System.out.println(exception.getMessage());
    }
    skull.setItemMeta(skullMeta);
    return skull;
  }
}
