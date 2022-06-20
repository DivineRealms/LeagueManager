package io.github.divinerealms.configs;

import io.github.divinerealms.managers.ConfigManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class Config extends ConfigManager {
  @Getter private final String name = "config.yml";
  @Getter @Setter private FileConfiguration config;

  public Config(final Plugin plugin) {
    super(plugin, "config.yml");
  }

  public void reload() {
    reloadConfig(getName());
    setConfig(getConfig(getName()));
  }

  public boolean getBoolean(final String path) {
    return getConfig().getBoolean(path, false);
  }

  public int getInt(final String path) {
    return getConfig().getInt(path, 0);
  }

  public double getDouble(final String path) {
    return getConfig().getDouble(path, 0);
  }

  public String getString(final String path) {
    return getConfig().getString(path, getNotFound(path, getName()));
  }

  public void set(final String path, final String string) {
    getConfig().set(path, string);
    saveConfig("config.yml");
  }
}