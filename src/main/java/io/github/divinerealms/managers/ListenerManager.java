package io.github.divinerealms.managers;

import io.github.divinerealms.listeners.ChatListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {
  @Getter
  private final Plugin plugin;
  @Getter
  private final PluginManager pluginManager;
  @Getter
  private final UtilManager utilManager;
  @Getter
  @Setter
  private boolean registered = false;

  public ListenerManager(final Plugin plugin, final UtilManager utilManager) {
    this.plugin = plugin;
    this.pluginManager = plugin.getServer().getPluginManager();
    this.utilManager = utilManager;
  }

  public void registerListeners() {
    setRegistered(true);
    getPluginManager().registerEvents(new ChatListener(getUtilManager()), getPlugin());
  }

  public void unregisterListeners() {
    setRegistered(false);
    HandlerList.unregisterAll(getPlugin());
  }
}
