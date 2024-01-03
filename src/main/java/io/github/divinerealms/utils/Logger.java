package io.github.divinerealms.utils;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@SuppressWarnings("unused")
public class Logger {
  private final Server server;
  private final PluginDescriptionFile description;
  private final List<String> banner = new ArrayList<>();
  private final ConsoleCommandSender consoleSender;

  public Logger(final Plugin plugin) {
    this.server = plugin.getServer();
    this.description = plugin.getDescription();
    this.consoleSender = server.getConsoleSender();
  }

  public void send(final CommandSender sender, final String message) {
    if (sender instanceof Player) sender.sendMessage(message);
    else getConsoleSender().sendMessage(message);
  }

  public void log(final String message, final String rank) {
    getServer().broadcast(message, "group." + rank);
    getConsoleSender().sendMessage(message);
  }

  public void sendActionBar(final Player player, final String message) {
    IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
    PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte) 2);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
  }

  public void broadcastBar(final String message) {
    IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
    PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte) 2);
    for (Player player : getServer().getOnlinePlayers())
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
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
