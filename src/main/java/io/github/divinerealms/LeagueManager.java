package io.github.divinerealms;

import io.github.divinerealms.commands.BaseCommand;
import io.github.divinerealms.commands.VARCommand;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.ConfigManager;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LeagueManager extends JavaPlugin {
  @Getter
  private final ConfigManager messagesFile = new ConfigManager(this, "", "messages.yml");
  @Getter
  @Setter
  private LuckPerms luckPermsAPI = null;
  @Getter
  @Setter
  private UtilManager utilManager;

  @Override
  public void onEnable() {
    getMessagesFile().createNewFile("Loading messages.yml", "LeagueManager Messages");
    loadMessages();

    if (!setupLuckPermsAPI()) {
      getLogger().info("&cDisabled due to no LuckPerms dependency found!");
      getServer().getPluginManager().disablePlugin(this);
    }

    setUtilManager(new UtilManager(this));
    getUtilManager().getLogger().sendBanner();
    getLogger().info("Loading commands...");
    setup();
    getLogger().info("Loading listeners...");
    getLogger().info("Successfully enabled!");
  }

  public void setup() {
    getCommand("leagueManager").setExecutor(new BaseCommand(this, getUtilManager()));
    getCommand("var").setExecutor(new VARCommand(this, getUtilManager()));
  }

  private boolean setupLuckPermsAPI() {
    RegisteredServiceProvider<LuckPerms> lpp = getServer().getServicesManager().getRegistration(LuckPerms.class);
    if (lpp != null) setLuckPermsAPI(lpp.getProvider());
    return getLuckPermsAPI() != null;
  }

  private void loadMessages() {
    Lang.setFile(getMessagesFile().getConfig());

    for (final Lang value : Lang.values())
      getMessagesFile().getConfig().addDefault(value.getPath(), value.getDefault());

    getMessagesFile().getConfig().options().copyDefaults(true);
    getMessagesFile().saveConfig();
  }
}
