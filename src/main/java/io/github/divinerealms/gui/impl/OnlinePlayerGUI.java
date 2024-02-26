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
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@Getter
public class OnlinePlayerGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final RostersDataManager dataManager;
  private final GUIManager guiManager;

  public OnlinePlayerGUI(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
    this.guiManager = guiManager;
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, "Izaberite igrača...");
  }

  @Override
  public void decorate(Player player) {
    for (int slot = 0; slot <= 35; slot++) {
      if (slot >= 27 && !(slot >= 33 && slot <= 34)) {
        this.addButton(slot, this.createButton("&r", (byte) 7));
      } else if (slot == 33) {
        this.addButton(slot, this.createButton("&6Nazad", (byte) 1)
            .consumer(event -> {
              event.getWhoClicked().closeInventory();
              getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), getGuiManager().getTeamName(), getGuiManager()), (Player) event.getWhoClicked());
            }));
      } else if (slot == 34) {
        this.addButton(slot, this.createButton("&cZatvorite", (byte) 14)
            .consumer(event -> event.getWhoClicked().closeInventory()));
      }
    }

    String type = null;
    int slot = 0;

    if (getHelper().groupHasMeta(getGuiManager().getTeamName(), "team")) type = "main";
    else if (getHelper().groupHasMeta(getGuiManager().getTeamName(), "b")) type = "juniors";

    if (type == null) {
      getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
      User user = getHelper().getPlayer(onlinePlayer.getUniqueId());
      for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
        final int groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
        if (groupWeight == 100 || groupWeight == 99) {
          getLogger().send(player, Lang.ROSTERS_NO_FA.getConfigValue(null));
          player.closeInventory();
          return;
        }
      }

      this.addButton(slot, this.createPlayerHead("&a" + onlinePlayer.getName(), onlinePlayer.getName())
          .consumer(event -> {
            getGuiManager().setTarget(onlinePlayer);
            Player target = (Player) event.getWhoClicked();
            target.closeInventory();
            getGuiManager().openGUI(new PerPlayerGUI(getUtilManager(), getGuiManager()), target);
          }));
      slot++;
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
}
