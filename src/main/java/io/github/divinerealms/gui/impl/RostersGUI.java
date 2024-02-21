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

import java.util.Arrays;

@Getter
public class RostersGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final RostersDataManager dataManager;
  private final GUIManager guiManager;

  public RostersGUI(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
    this.guiManager = guiManager;
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, "Roster GUI");
  }

  @Override
  public void decorate(Player player) {
    for (int slot = 0; slot <= 35; slot++) {
      if (slot <= 9 || slot == 17 || slot == 18 || slot >= 26 && slot != 31) {
        if (!processTeamSlot(slot) && !closeButtonSlot(slot)) this.addButton(slot, this.createButton("&r", (byte) 7));
      } else if (slot == 31) this.addButton(slot, this.createButton("&cZatvorite", (byte) 14).consumer(event -> event.getWhoClicked().closeInventory()));
    }

    processTeamConfig("main", 10, player);
    processTeamConfig("juniors", 19, player);

    super.decorate(player);
  }

  private void processTeamConfig(String configType, int slot, Player player) {
    getDataManager().setConfig(configType);
    FileConfiguration config = getDataManager().getConfig(configType);
    if (config == null) {
      getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{configType.toUpperCase()}));
      return;
    }

    for (String teamName : config.getKeys(false)) {
      if ((configType.equals("main") && getHelper().groupHasMeta(teamName, "team")) ||
          (configType.equals("juniors") && getHelper().groupHasMeta(teamName, "b"))) {

        int teamSize = config.get(teamName + ".players") != null ? config.getConfigurationSection(teamName + ".players").getKeys(false).size() : 0;
        ItemStack banner = config.get(teamName + ".banner") != null ? (ItemStack) config.get(teamName + ".banner") : new ItemStack(Material.BANNER, 1, (byte) (configType.equals("main") ? 15 : 10));

        String teamDisplayName = (configType.equals("main") ? "&f&l" : "&a&l") + config.getString(teamName + ".name", "&c/");
        String tag = getUtilManager().color("&fTag: " + config.getString(teamName + ".tag", "/"));
        String manager = getUtilManager().color("&fMenadžer: &a" + config.getString(teamName + ".manager", "/"));
        String captain = getUtilManager().color("&fKapiten: &c" + config.getString(teamName + ".captain", "/"));
        String teamInfo = getUtilManager().color("&7&oTim ima " + teamSize + " igrača");

        this.addButton(slot <= (configType.equals("main") ? 16 : 25) ? slot++ : slot, this.createTeamItem(banner, teamDisplayName, teamName, "", tag, manager, captain, "", teamInfo));
      }
    }
  }

  private InventoryButton createTeamItem(ItemStack itemStack, String title, String teamName, String... lore) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(getUtilManager().color(title));
    itemMeta.setLore(Arrays.asList(lore));
    itemStack.setItemMeta(itemMeta);
    return new InventoryButton()
        .creator(player -> itemStack)
        .consumer(event -> {
          Player player = (Player) event.getWhoClicked();
          player.closeInventory();
          getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), teamName, getGuiManager()), player);
        });
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

  private boolean processTeamSlot(int slot) {
    return slot == 10 || slot == 19;
  }

  private boolean closeButtonSlot(int slot) {
    return slot == 31;
  }
}
