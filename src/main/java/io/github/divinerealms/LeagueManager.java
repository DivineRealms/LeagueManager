package io.github.divinerealms;

import io.github.divinerealms.commands.BaseCommand;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LeagueManager extends JavaPlugin {
  @Getter @Setter private UtilManager utilManager;
  @Getter @Setter private LuckPerms luckPermsAPI = null;
  @Getter @Setter private static Permission perms = null;

  @Override
  public void onEnable() {
    setUtilManager(new UtilManager(this));
    getUtilManager().reloadUtils();
    getUtilManager().getLogger().sendBanner();
    getUtilManager().getLogger().info("Loading commands...");
    getUtilManager().getLogger().info("Loading listeners...");
    setup();
    getUtilManager().getLogger().info("Successfully enabled!");
  }

  public void reload() {
    getUtilManager().reloadUtils();
    setup();
  }

  private void setup() {
    getCommand("leagueManager").setExecutor(new BaseCommand(this, getUtilManager()));

    if (!setupPermissions()) {
      getUtilManager().getLogger().info("&cDisabled due to no Vault dependency found!");
      getServer().getPluginManager().disablePlugin(this);
    }

    if (!setupLuckPermsAPI()) {
      getUtilManager().getLogger().info("&cDisabled due to no LuckPerms dependency found!");
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  private boolean setupPermissions() {
    RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
    if (rsp != null)
      setPerms(rsp.getProvider());
    return getPerms() != null;
  }

  private boolean setupLuckPermsAPI() {
    RegisteredServiceProvider<LuckPerms> lpp = getServer().getServicesManager().getRegistration(LuckPerms.class);
    if (lpp != null)
      setLuckPermsAPI(lpp.getProvider());
    return getLuckPermsAPI() != null;
  }
}
