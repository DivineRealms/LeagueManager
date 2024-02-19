package io.github.divinerealms.gui.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.gui.InventoryButton;
import io.github.divinerealms.gui.InventoryGUI;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class PerRosterGUI extends InventoryGUI {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final RostersDataManager dataManager;
  private final String team;

  public PerRosterGUI(UtilManager utilManager, String team) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
    this.team = team;
  }

  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, "Roster");
  }

  @Override
  public void decorate(Player player) {
    AtomicInteger slot = new AtomicInteger(10);
    if (getHelper().groupExists(getTeam())) {
      String type = null;
      if (getHelper().groupHasMeta(getTeam(), "team")) type = "main";
      else if (getHelper().groupHasMeta(getTeam(), "b")) type = "juniors";
      if (type == null) {
        getLogger().send(player, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{getTeam().toUpperCase()}));
        return;
      }
      getDataManager().setConfig(type);
      FileConfiguration team = getDataManager().getConfig(type);
      for (String teamPlayer : team.getConfigurationSection(getTeam() + ".players").getKeys(false)) {
        this.addButton(slot.get() <= 16 ? slot.getAndIncrement() : slot.get(),
            this.createPlayerHead("&b&l" + teamPlayer, teamPlayer,
                team.getString(getTeam() + ".manager").equals(teamPlayer) ?
                    getUtilManager().color("&aDirektor") : getUtilManager().color("&7Igrač"), "",
                getUtilManager().color("&fPozicija: &e" + team.getString(getTeam() + ".players." + teamPlayer))));
      }
    }
    super.decorate(player);
  }

  private InventoryButton createPlayerHead(String title, String playerName, String... lore) {
    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    skullMeta.setOwner(playerName);
    skullMeta.setDisplayName(getUtilManager().color(title));
    skullMeta.setLore(Arrays.asList(lore));
    skull.setItemMeta(skullMeta);
    return new InventoryButton()
        .creator(player -> skull)
        .consumer(event -> {
          Player player = (Player) event.getWhoClicked();
          player.closeInventory();
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
