package io.github.divinerealms.gui.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.divinerealms.gui.InventoryButton;
import io.github.divinerealms.gui.InventoryGUI;
import io.github.divinerealms.managers.RostersDataManager;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class RostersGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final RostersDataManager dataManager;

  public RostersGUI(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
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
      this.addButton(slot.get() <= 16 ? slot.getAndIncrement() : slot.get(),
          this.createTeamItem(new ItemStack(Material.BANNER, 1, (byte) 15),
              getUtilManager().color(main.getString(team + ".tag"))));
    }
    getDataManager().setConfig("juniors");
    FileConfiguration juniors = getDataManager().getConfig("juniors");
    slot = new AtomicInteger(19);
    for (String team : juniors.getKeys(false)) {
      this.addButton(slot.get() <= 25 ? slot.getAndIncrement() : slot.get(),
          this.createTeamItem(new ItemStack(Material.BANNER, 1, (byte) 10),
              getUtilManager().color(juniors.getString(team + ".tag"))));
    }
    super.decorate(player);
  }

  private InventoryButton createTeamItem(ItemStack itemStack, String title, String... lore) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(title);
    itemMeta.setLore(Arrays.asList(lore));
    itemStack.setItemMeta(itemMeta);
    return new InventoryButton()
        .creator(player -> itemStack)
        .consumer(event -> {
          //todo
        });
  }

  private InventoryButton createHead(String title, String headTexture, String... lore) {
    ItemStack skull = this.applySkinTexture(headTexture);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    skullMeta.setDisplayName(title);
    skullMeta.setLore(Arrays.asList(lore));
    skull.setItemMeta(skullMeta);
    return new InventoryButton()
        .creator(player -> skull)
        .consumer(event -> {
          //todo
        });
  }

  private InventoryButton createPlaceholders(ItemStack itemStack) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(ChatColor.RESET + "");
    itemStack.setItemMeta(itemMeta);
    return new InventoryButton()
        .creator(player -> itemStack)
        .consumer(event -> {
          //todo
        });
  }

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
