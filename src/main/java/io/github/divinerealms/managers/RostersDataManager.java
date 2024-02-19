package io.github.divinerealms.managers;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class RostersDataManager {
  @Getter private final Plugin plugin;
  private final String folderName = "data";
  private FileConfiguration config;
  private File file;

  public RostersDataManager(Plugin plugin) {
    this.plugin = plugin;
  }

  public void createNewFile(String name, String message) {
    reloadConfig(name);
    saveConfig(name);
    loadConfig(name);

    if (message != null) getPlugin().getLogger().info(message);
  }

  public FileConfiguration getConfig(String name) {
    if (config == null) reloadConfig(name);
    return config;
  }

  public void setConfig(String name) {
    file = new File(getPlugin().getDataFolder() + File.separator + folderName, name + ".yml");
    config = YamlConfiguration.loadConfiguration(file);
  }

  public void loadConfig(String name) {
    config.options().copyDefaults(true);
    saveConfig(name);
  }

  public void reloadConfig(String name) {
    if (file == null) file = new File(getPlugin().getDataFolder() + File.separator + folderName, name + ".yml");
    config = YamlConfiguration.loadConfiguration(file);
  }

  public void saveConfig (String name) {
    if (config == null || file == null) return;
    try {
      getConfig(name).save(file);
    } catch (final IOException exception) {
      getPlugin().getLogger().log(Level.SEVERE, "Could not save config to " + file, exception);
    }
  }

  public boolean deleteFiles(String name) {
    File teamFile = new File(getPlugin().getDataFolder() + File.separator + folderName, name + ".yml");
    return teamFile.delete();
  }

  public boolean configExists(String name) {
    File teamFile = new File(getPlugin().getDataFolder() + File.separator + folderName, name + ".yml");
    return teamFile.exists();
  }
}
