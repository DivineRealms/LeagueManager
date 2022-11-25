package io.github.divinerealms.utils;

import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import org.bukkit.ChatColor;
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
  @Getter
  private final Server server;
  @Getter
  private final PluginDescriptionFile description;
  @Getter
  private final List<String> banner = new ArrayList<>();
  @Getter
  private final ConsoleCommandSender consoleSender;

  public Logger(final Plugin plugin, final UtilManager utilManager) {
    this.server = plugin.getServer();
    this.description = plugin.getDescription();
    this.consoleSender = server.getConsoleSender();
  }

  public void send(final CommandSender sender, final String message) {
    if (sender instanceof Player) sender.sendMessage(message);
    else getConsoleSender().sendMessage(message);
  }

  public void log(final String message) {
    getServer().broadcast(message, "group.fcfa");
    getConsoleSender().sendMessage(message);
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
      getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
  }
}
