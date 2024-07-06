package io.github.divinerealms.leaguemanager.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DataManager {
  @Getter private final Plugin plugin;
  @Getter @Setter private String folderName;
  @Setter @Getter private FileConfiguration config;
  private File file;

  public DataManager(Plugin plugin) {
    this.plugin = plugin;
  }

  public void createNewFile(String name, String message) {
    reloadConfig(name);
    saveConfig();
    loadConfig();

    if (message != null) getPlugin().getLogger().info(message);
  }

  public FileConfiguration getConfig(String name) {
    if (config == null) reloadConfig(name);
    return config;
  }

  public void setConfig(String folderName, String configName) {
    file = new File(getPlugin().getDataFolder() + File.separator + folderName, configName + ".yml");
    config = YamlConfiguration.loadConfiguration(file);
  }

  public void loadConfig() {
    config.options().copyDefaults(true);
    saveConfig();
  }

  public void reloadConfig(String name) {
    if (file == null) file = new File(getPlugin().getDataFolder() + File.separator + getFolderName(), name + ".yml");
    config = YamlConfiguration.loadConfiguration(file);
  }

  public void saveConfig() {
    if (config == null || file == null) return;
    try {
      getConfig().save(file);
    } catch (final IOException exception) {
      getPlugin().getLogger().log(Level.SEVERE, "Could not save config to " + file, exception);
    }
  }

  public boolean deleteFiles(String name) {
    File file = new File(getPlugin().getDataFolder() + File.separator + getFolderName(), name + ".yml");
    return file.delete();
  }

  public boolean configExists(String folderName, String name) {
    File file = new File(getPlugin().getDataFolder() + File.separator + folderName, name + ".yml");
    return file.exists();
  }

  public void copyFile(final String oldFileName, final String newFileName) {
    File oldFile = new File(getPlugin().getDataFolder() + File.separator + getFolderName(), oldFileName + ".yml");
    File newFile = new File(getPlugin().getDataFolder() + File.separator + getFolderName(), newFileName + ".yml");
    FileUtil.copy(oldFile, newFile);
  }
}
