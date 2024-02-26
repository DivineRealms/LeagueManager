package io.github.divinerealms;

import co.aikar.commands.BukkitCommandManager;
import io.github.divinerealms.commands.LMCommand;
import io.github.divinerealms.commands.RostersCommand;
import io.github.divinerealms.commands.VARCommand;
import io.github.divinerealms.commands.timers.OXECommand;
import io.github.divinerealms.commands.timers.ResultCommand;
import io.github.divinerealms.commands.timers.TXFCommand;
import io.github.divinerealms.commands.timers.TimerCommand;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.ConfigManager;
import io.github.divinerealms.managers.GUIManager;
import io.github.divinerealms.managers.ListenerManager;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
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

  @Override
  public void onEnable() {
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
    commandManager.enableUnstableAPI("help");
    commandManager.registerCommand(new LMCommand(getUtilManager(), this));
    commandManager.registerCommand(new VARCommand(getUtilManager()));
    commandManager.registerCommand(new ResultCommand(this, getUtilManager()));
    commandManager.registerCommand(new TimerCommand(this, getUtilManager()));
    commandManager.registerCommand(new OXECommand(this, getUtilManager()));
    commandManager.registerCommand(new TXFCommand(this, getUtilManager()));
    commandManager.registerCommand(new RostersCommand(getUtilManager(), getGuiManager()));

    Bukkit.getScheduler().runTaskTimer(this, getUtilManager().getLineChecker(), 20L, 1L);
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
    Lang.setFile(getMessagesFile().getConfig("libs/messages.yml"));

    for (final Lang value : Lang.values())
      getMessagesFile().getConfig("libs/messages.yml").addDefault(value.getPath(), value.getDefault());

    getMessagesFile().getConfig("libs/messages.yml").options().copyDefaults(true);
    getMessagesFile().saveConfig("libs/messages.yml");
  }
}
