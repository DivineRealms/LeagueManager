package io.github.divinerealms.leaguemanager.gui.impl.statistics;

import io.github.divinerealms.leaguemanager.configs.Config;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@Getter
public class StatisticsGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final GUIManager guiManager;
  private final DataManager dataManager;
  private static final YamlConfiguration config = Config.getConfig("config.yml");

  public StatisticsGUI(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.guiManager = guiManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 6 * 9, "Statistika");
  }

  @Override
  public void decorate(Player player) {
    for (int slot = 0; slot <= 53; slot++) {
      if (slot == 10) {
        ItemStack itemStack = new ItemStack(Material.BANNER, 1, (byte) 11);
        this.addButton(slot, this.createStatsItem(itemStack, "&eŽuti Kartoni", new YellowCardsGUI(getUtilManager(), getGuiManager())));
      } else if (slot == 11) {
        ItemStack itemStack = new ItemStack(Material.BANNER, 1, (byte) 1);
        this.addButton(slot, this.createStatsItem(itemStack, "&cCrveni Kartoni", new RedCardsGUI(getUtilManager(), getGuiManager())));
      } else if (slot == 13) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND, 1);
        this.addButton(slot, this.createStatsItem(itemStack, "&bNajbolji Strelci", new ShootersGUI(getUtilManager(), getGuiManager())));
      } else if (slot == 14) {
        ItemStack itemStack = new ItemStack(Material.EMERALD, 1);
        this.addButton(slot, this.createStatsItem(itemStack, "&aNajbolji Asistenti", new AssistsGUI(getUtilManager(), getGuiManager())));
      } else if (slot == 15) {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR, 1);
        this.addButton(slot, this.createStatsItem(itemStack, "&dNajbolji Golmani", new GoalkeeperGUI(getUtilManager(), getGuiManager())));
      } else if (slot == 37) {
        this.addButton(slot, this.createHead("&fPomoćnik", "18154", getUtilManager().color("&eLevi klik &7za pregled statistike."))
            .consumer(event -> {
              Player target = (Player) event.getWhoClicked();
              target.closeInventory();
              target.performCommand("lb help");
            }));
      } else if (slot == 43) {
        this.addButton(slot, this.createHead("&cZatvorite", "3229")
            .consumer(event -> event.getWhoClicked().closeInventory()));
      } else this.addButton(slot, this.createButton("&r", (byte) 7));
    }

    super.decorate(player);
  }

  private InventoryButton createStatsItem(ItemStack itemStack, String title, InventoryGUI type, String... lore) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(getUtilManager().color(title));
    itemMeta.setLore(Arrays.asList(lore));
    itemStack.setItemMeta(itemMeta);
    return new InventoryButton()
        .creator(player -> itemStack)
        .consumer(event -> {
          Player player = (Player) event.getWhoClicked();
          player.closeInventory();
          getGuiManager().openGUI(type, player);
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
