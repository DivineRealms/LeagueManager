package io.github.divinerealms.configs;

import io.github.divinerealms.managers.ConfigManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Messages extends ConfigManager {
  @Getter private final String name = "messages.yml";
  @Getter private final ConsoleCommandSender consoleSender;
  @Getter @Setter private FileConfiguration messages;
  @Getter @Setter private String prefix;

  public Messages(final Plugin plugin) {
    super(plugin, "messages.yml");
    this.consoleSender = plugin.getServer().getConsoleSender();
  }

  public void reload() {
    reloadConfig(getName());
    setMessages(getConfig(getName()));
    setPrefix(getString("prefix"));
  }

  public String colorizeMessage(final String message) {
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String colorizeMessage(final String message, final String teamTag) {
    final String formattedMessage = message.replace("%teamTag%", teamTag);
    return ChatColor.translateAlternateColorCodes('&', formattedMessage);
  }

  public String colorize(final String path) {
    final String message = getString(path)
        .replace("%prefix%", getPrefix());
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String colorize(final String playerName, final String path, final String teamTag) {
    final String message = getString(path)
        .replace("%prefix%", getPrefix())
        .replace("%player%", playerName)
        .replace("%teamTag%", teamTag);
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String colorize(final String path, final String teamTag) {
    final String message = getString(path)
        .replace("%prefix%", getPrefix())
        .replace("%teamTag%", teamTag);
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String colorizeState(final String path, final String state) {
    final String message = getString(path)
        .replace("%prefix%", getPrefix())
        .replace("%state%", state);
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String getString(final String path) {
    return getMessages().getString(path, getNotFound(path, getName()));
  }

  public List<String> getStringList(final String path) {
    final List<String> list = getMessages().getStringList(path);
    return new ArrayList<>(list);
  }
}
