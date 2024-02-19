package io.github.divinerealms.gui.impl;

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
import java.util.concurrent.atomic.AtomicInteger;

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
    return Bukkit.createInventory(null, 4 * 9, "Rosters GUI");
  }

  @Override
  public void decorate(Player player) {
    AtomicInteger slot = new AtomicInteger(10);
    getDataManager().setConfig("main");
    FileConfiguration main = getDataManager().getConfig("main");
    for (String team : main.getKeys(false)) {
      int teamSize = main.get(team + ".players") != null ?
          main.getConfigurationSection(team + ".players").getKeys(false).size() : 0;
      ItemStack banner = main.get(team + ".banner") != null ? (ItemStack) main.get(team + ".banner") :
          new ItemStack(Material.BANNER, 1, (byte) 15);
      this.addButton(slot.get() <= 16 ? slot.getAndIncrement() : slot.get(),
          this.createTeamItem(banner, "&f&l" + main.getString(team + ".name", "&c/"), team, "",
              getUtilManager().color("&fTag: " + main.getString(team + ".tag", "/")),
              getUtilManager().color("&fMenadžer: &a" + main.getString(team + ".manager", "/")),
              getUtilManager().color("&fKapiten: &c" + main.getString(team + ".captain", "/")), "",
              getUtilManager().color("&7&oTim ima " + teamSize + " igrača")));
    }
    getDataManager().setConfig("juniors");
    FileConfiguration juniors = getDataManager().getConfig("juniors");
    slot = new AtomicInteger(19);
    for (String team : juniors.getKeys(false)) {
      int teamSize = juniors.get(team + ".players") != null ?
          juniors.getConfigurationSection(team + ".players").getKeys(false).size() : 0;
      ItemStack banner = juniors.get(team + ".banner") != null ? (ItemStack) juniors.get(team + ".banner") :
          new ItemStack(Material.BANNER, 1, (byte) 10);
      this.addButton(slot.get() <= 25 ? slot.getAndIncrement() : slot.get(),
          this.createTeamItem(banner, "&a&l" + juniors.getString(team + ".name", "&c/"), team, "",
              getUtilManager().color("&fTag: " + juniors.getString(team + ".tag", "/")),
              getUtilManager().color("&fMenadžer: &a" + juniors.getString(team + ".manager", "/")),
              getUtilManager().color("&fKapiten: &c" + juniors.getString(team + ".captain", "/")), "",
              getUtilManager().color("&2&oTim ima " + teamSize + " igrača")));
    }
    super.decorate(player);
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
          getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), teamName), player);
        });
  }
}
