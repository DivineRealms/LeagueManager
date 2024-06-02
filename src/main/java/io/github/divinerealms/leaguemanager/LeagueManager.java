package io.github.divinerealms.leaguemanager;

import co.aikar.commands.BukkitCommandManager;
import io.github.divinerealms.leaguemanager.commands.LMCommand;
import io.github.divinerealms.leaguemanager.commands.RostersCommand;
import io.github.divinerealms.leaguemanager.commands.VARCommand;
import io.github.divinerealms.leaguemanager.configs.Config;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.GUIManager;
import io.github.divinerealms.leaguemanager.managers.ConfigManager;
import io.github.divinerealms.leaguemanager.managers.ListenerManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Setter
@Getter
public class LeagueManager extends JavaPlugin {
  private ConfigManager messagesFile = new ConfigManager(this, "");
  private YamlConfiguration config;
  private LuckPerms luckPermsAPI = null;
  private UtilManager utilManager;
  private GUIManager guiManager;
  private ListenerManager listenerManager;
  @Getter
  private static LeagueManager instance;

  @Override
  public void onEnable() {
    instance = this;
    setupMessages();
    Config.setup(this);
    config = Config.getConfig("config.yml");

    if (!setupLuckPermsAPI()) {
      getLogger().info("&cDisabled due to no LuckPerms dependency found!");
      getServer().getPluginManager().disablePlugin(this);
    }

    utilManager = new UtilManager(this);
    guiManager = new GUIManager();
    listenerManager = new ListenerManager(this, utilManager, guiManager);

    getUtilManager().getLogger().initializeStrings();
    getUtilManager().getLogger().sendBanner();
    getLogger().info("Loading commands...");
    setup();
    getLogger().info("Loading listeners...");
    getLogger().info("Successfully enabled!");
  }

  @Override
  public void onDisable() {
    getListenerManager().unregisterListeners();
  }

  public void setup() {
    if (getListenerManager().isRegistered()) getListenerManager().unregisterListeners();
    getListenerManager().registerListeners();

    BukkitCommandManager commandManager = new BukkitCommandManager(this);
    commandManager.registerCommand(new LMCommand(getUtilManager(), this));
    commandManager.registerCommand(new VARCommand(getUtilManager()));
    commandManager.registerCommand(new RostersCommand(getUtilManager(), getGuiManager()));
  }

  public void setupMessages() {
    getMessagesFile().createNewFile("messages.yml", "Loading messages.yml", "LeagueManager Messages");
    loadMessages();
  }

  private boolean setupLuckPermsAPI() {
    RegisteredServiceProvider<LuckPerms> lpp = getServer().getServicesManager().getRegistration(LuckPerms.class);
    if (lpp != null) setLuckPermsAPI(lpp.getProvider());
    return getLuckPermsAPI() != null;
  }

  private void loadMessages() {
    Lang.setFile(getMessagesFile().getConfig("messages.yml"));

    for (final Lang value : Lang.values())
      getMessagesFile().getConfig("messages.yml").addDefault(value.getPath(), value.getDefault());

    getMessagesFile().getConfig("messages.yml").options().copyDefaults(true);
    getMessagesFile().saveConfig("messages.yml");
  }
}
