package io.github.divinerealms.leaguemanager.managers;

import io.github.divinerealms.leaguemanager.listeners.ChatListener;
import io.github.divinerealms.leaguemanager.listeners.GUIListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@Getter
public class ListenerManager {
  private final Plugin plugin;
  private final PluginManager pluginManager;
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  @Setter private boolean registered = false;

  public ListenerManager(final Plugin plugin, final UtilManager utilManager, final GUIManager guiManager) {
    this.plugin = plugin;
    this.pluginManager = plugin.getServer().getPluginManager();
    this.utilManager = utilManager;
    this.guiManager = guiManager;
  }

  public void registerListeners() {
    setRegistered(true);
    getPluginManager().registerEvents(new ChatListener(getUtilManager()), getPlugin());
    getPluginManager().registerEvents(new GUIListener(getGuiManager()), getPlugin());
  }

  public void unregisterListeners() {
    setRegistered(false);
    HandlerList.unregisterAll(getPlugin());
  }
}
