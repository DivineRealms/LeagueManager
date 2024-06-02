package io.github.divinerealms.leaguemanager.listeners;

import io.github.divinerealms.leaguemanager.configs.Config;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

@Getter
public class FireworkListener implements Listener {
  private static final YamlConfiguration config = Config.getConfig("config.yml");
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private List<String> disabledItems = null;

  public FireworkListener(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();

    if (config.getStringList("disabled-items") != null)
      this.disabledItems = config.getStringList("disabled-items");
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFirework(final PlayerInteractEvent event) {
    if (event.getItem() == null) return;
    if (event.isCancelled()) return;
    if (getDisabledItems() == null) return;

    final Player player = event.getPlayer();

    for (String item : getDisabledItems()) {
      if (event.getMaterial() == Material.getMaterial(item.toUpperCase())) {
        if (!getHelper().playerCheckPermission(player.getUniqueId(), "leaguemanager.footcube")) {
          getLogger().send(player, Lang.DISABLED_ITEM.getConfigValue(new String[]{item.toLowerCase()}));
          event.setCancelled(true);
        }
      }
    }
  }
}
