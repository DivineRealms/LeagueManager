package io.github.divinerealms.leaguemanager.gui.impl.statistics;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@Getter
public class AssistsGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  private final Logger logger;
  private final Helper helper;
  private final DataManager dataManager;
  private final String playerName;

  public AssistsGUI(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.guiManager = guiManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.playerName = guiManager.getPlayerName();
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 6 * 9, "Statistika");
  }

  @Override
  public void decorate(Player player) {

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
