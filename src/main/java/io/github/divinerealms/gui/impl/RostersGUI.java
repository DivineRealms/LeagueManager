package io.github.divinerealms.gui.impl;

import io.github.divinerealms.gui.InventoryButton;
import io.github.divinerealms.gui.InventoryGUI;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class RostersGUI extends InventoryGUI {
  private final Logger logger;

  public RostersGUI(final UtilManager utilManager) {
    this.logger = utilManager.getLogger();
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 3 * 9, "Rosters GUI");
  }

  @Override
  public void decorate(Player player) {
    Material material = Material.STAINED_GLASS_PANE;
    this.addButton(11, this.createPlaceholders(material));
    super.decorate(player);
  }

  private InventoryButton createPlaceholders(Material material) {
    return new InventoryButton()
        .creator(player -> new ItemStack(material))
        .consumer(event -> {
          Player player = (Player) event.getWhoClicked();
          getLogger().send(player, "clicked");
        });
  }
}
