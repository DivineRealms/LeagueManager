package io.github.divinerealms;

import io.github.divinerealms.commands.BaseCommand;
import io.github.divinerealms.commands.ResultCommand;
import io.github.divinerealms.commands.TimerCommand;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.ConfigManager;
import io.github.divinerealms.managers.ListenerManager;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class LeagueManager extends JavaPlugin {
  private final ConfigManager messagesFile = new ConfigManager(this, "");
  @Setter private LuckPerms luckPermsAPI = null;
  @Setter private UtilManager utilManager;
  @Setter private ListenerManager listenerManager;

  @Override
  public void onEnable() {
    setupMessages();

    if (!setupLuckPermsAPI()) {
      getLogger().info("&cDisabled due to no LuckPerms dependency found!");
      getServer().getPluginManager().disablePlugin(this);
    }

    setUtilManager(new UtilManager(this));
    setListenerManager(new ListenerManager(this, getUtilManager()));
    getUtilManager().reloadUtils();
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
    getCommand("leagueManager").setExecutor(new BaseCommand(this, getUtilManager()));
    getCommand("timer").setExecutor(new TimerCommand(this, getUtilManager()));
    getCommand("result").setExecutor(new ResultCommand(this, getUtilManager()));

    if (getListenerManager().isRegistered()) getListenerManager().unregisterListeners();
    getListenerManager().registerListeners();

    getServer().getScheduler().runTaskTimer(this, getUtilManager().getLineChecker(), 20L, 1L);
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
