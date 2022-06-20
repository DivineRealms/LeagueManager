package io.github.divinerealms;

import io.github.divinerealms.commands.BaseCommand;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LeagueManager extends JavaPlugin {
  @Getter @Setter private LuckPerms luckPermsAPI = null;
  @Getter @Setter private UtilManager utilManager;

  @Override
  public void onEnable() {
    if (!setupLuckPermsAPI()) {
      getLogger().info("&cDisabled due to no LuckPerms dependency found!");
      getServer().getPluginManager().disablePlugin(this);
    }

    setUtilManager(new UtilManager(this));
    getUtilManager().reloadUtils();
    getUtilManager().getLogger().sendBanner();
    getUtilManager().getLogger().info("Loading commands...");
    setup();
    getUtilManager().getLogger().info("Loading listeners...");
    getUtilManager().getLogger().info("Successfully enabled!");
  }

  public void reload() {
    getUtilManager().reloadUtils();
    setup();
  }

  private void setup() {
    getCommand("leagueManager").setExecutor(new BaseCommand(this, getUtilManager()));
  }

  private boolean setupLuckPermsAPI() {
    RegisteredServiceProvider<LuckPerms> lpp = getServer().getServicesManager().getRegistration(LuckPerms.class);
    if (lpp != null)
      setLuckPermsAPI(lpp.getProvider());
    return getLuckPermsAPI() != null;
  }
}
