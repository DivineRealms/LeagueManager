package io.github.divinerealms.managers;

import io.github.divinerealms.listeners.ChatListener;
import io.github.divinerealms.listeners.GUIListener;
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
  @Setter private boolean registered = false;

  public ListenerManager(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.pluginManager = plugin.getServer().getPluginManager();
    this.utilManager = utilManager;
  }

  public void registerListeners() {
    setRegistered(true);
    getPluginManager().registerEvents(new ChatListener(getUtilManager()), getPlugin());
    getPluginManager().registerEvents(new GUIListener(new GUIManager()), getPlugin());
  }

  public void unregisterListeners() {
    setRegistered(false);
    HandlerList.unregisterAll(getPlugin());
  }
}
