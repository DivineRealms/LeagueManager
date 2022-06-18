package io.github.divinerealms.utils;

import io.github.divinerealms.configs.Config;
import io.github.divinerealms.configs.Messages;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Logger {
  @Getter private final Server server;
  @Getter private final PluginDescriptionFile description;
  @Getter private final List<String> banner = new ArrayList<>();
  @Getter private final ConsoleCommandSender consoleSender;
  @Getter private final Config config;
  @Getter private final Messages messages;
  @Getter @Setter private String prefix;

  public Logger(final Plugin plugin, final UtilManager utilManager) {
    this.server = plugin.getServer();
    this.description = plugin.getDescription();
    this.consoleSender = server.getConsoleSender();
    this.config = utilManager.getConfig();
    this.messages = utilManager.getMessages();
  }

  public void reload() {
    setPrefix(getMessages().getString("prefix"));
  }

  public void info(final String message) {
    getConsoleSender().sendMessage(getMessages().colorizeMessage(getPrefix() + message));
  }

  public void send(final CommandSender sender, final String path) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      player.sendMessage(getMessages().colorize(path));
    } else getConsoleSender().sendMessage(getMessages().colorize(path));
  }

  public void send(final CommandSender sender, final String path, final String teamTag) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      player.sendMessage(getMessages().colorizeTeam(path, teamTag));
    } else getConsoleSender().sendMessage(getMessages().colorizeTeam(path, teamTag));
  }

  public void send(final CommandSender sender, final String playerName, final String path, final String teamTag) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      player.sendMessage(getMessages().colorizeUser(playerName, path, teamTag));
    } else getConsoleSender().sendMessage(getMessages().colorizeUser(playerName, path, teamTag));
  }

  public void sendLong(final CommandSender sender, final String path) {
    final List<String> list = getMessages().getStringList(path);
    for (final String message : list) {
      if (sender instanceof Player) {
        final Player player = (Player) sender;
        player.sendMessage(getMessages().colorizeMessage(message));
      } else getConsoleSender().sendMessage(getMessages().colorizeMessage(message));
    }
  }

  public void sendBanner() {
    final List<String> authors = getDescription().getAuthors();
    final String formattedAuthors = authors.stream().map(String::valueOf).collect(Collectors.joining(", "));
    final String pluginName = getDescription().getFullName();
    final String serverName = getServer().getName();
    final String version = getServer().getBukkitVersion();
    final String serverNameVersion = serverName + " - " + version;

    getBanner().add("&r");
    getBanner().add("&d  88     &e8b    d8   &2" + pluginName);
    getBanner().add("&d  88     &e88b  d88   &5Authors: &d" + formattedAuthors);
    getBanner().add("&d  88  .o &e88YbdP88");
    getBanner().add("&d  88ood8 &e88 YY 88   &8Running on " + serverNameVersion);
    getBanner().add("&r");

    for (final String message : getBanner())
      getConsoleSender().sendMessage(getMessages().colorizeMessage(message));
  }
}
