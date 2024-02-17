package io.github.divinerealms.gui.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.divinerealms.gui.InventoryButton;
import io.github.divinerealms.gui.InventoryGUI;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class RostersGUI extends InventoryGUI {
  private final Logger logger;

  public RostersGUI(final UtilManager utilManager) {
    this.logger = utilManager.getLogger();
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, "Rosters GUI");
  }

  @Override
  public void decorate(Player player) {
//    Material material = Material.STAINED_GLASS_PANE;
//    this.addButton(10, this.createHead(ChatColor.WHITE + "" + ChatColor.BOLD + "Serverliga", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDcxMDEzODQxNjUyODg4OTgxNTU0OGI0NjIzZDI4ZDg2YmJiYWU1NjE5ZDY5Y2Q5ZGJjNWFkNmI0Mzc0NCJ9fX0=", "", ChatColor.WHITE + "Timovi u glavnoj ligi."));
//    this.addButton(11, this.createPlaceholders(material));
//    this.addButton(19, this.createHead(ChatColor.WHITE + "" + ChatColor.BOLD + "Serverliga " + ChatColor.GREEN + ChatColor.BOLD + "B", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQzNTlhZWI3NjdlOTE0NTIxZmY1YjlkMmM2ODVkYjU0ODcxZGMzZjdlOGY3MmY2ZWI4YzFmMmNhOGMyZDIifX19", "", ChatColor.WHITE + "Timovi u B ligi."));
//    this.addButton(20, this.createPlaceholders(material));
    super.decorate(player);
  }

  private InventoryButton createHead(String title, String headTexture, String... lore) {
    ItemStack skull = this.applySkinTexture(headTexture);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    skullMeta.setDisplayName(title);
    skullMeta.setLore(Arrays.asList(lore));
    skull.setItemMeta(skullMeta);
    return new InventoryButton()
        .creator(player -> skull)
        .consumer(event -> getLogger().send(event.getWhoClicked(), ""));
  }

  private InventoryButton createPlaceholders(ItemStack itemStack) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(ChatColor.RESET + "");
    itemStack.setItemMeta(itemMeta);
    return new InventoryButton()
        .creator(player -> itemStack)
        .consumer(event -> getLogger().send(event.getWhoClicked(), ""));
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
