package io.github.divinerealms.utils;

import io.github.divinerealms.LeagueManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Helper {
  @Getter
  private final LeagueManager plugin;
  @Getter
  private final UserManager userManager;
  @Getter
  private final GroupManager groupManager;
  @Getter
  private final String[] permissions = new String[]{"chatcontrol.channel.%team%",
      "chatcontrol.channel.send.%team%",
      "chatcontrol.channel.join.%team%",
      "chatcontrol.channel.join.%team%.write",
      "chatcontrol.channel.join.%team%.read",
      "chatcontrol.channel.autojoin.%team%",
      "chatcontrol.channel.autojoin.%team%.read",
      "chatcontrol.channel.leave.%team%",
      "tab.group.%team%"};
  @Getter
  @Setter
  private LuckPerms luckPermsAPI;

  public Helper(final LeagueManager plugin) {
    this.plugin = plugin;
    this.luckPermsAPI = plugin.getLuckPermsAPI();
    this.userManager = getLuckPermsAPI().getUserManager();
    this.groupManager = getLuckPermsAPI().getGroupManager();
  }

  public User getPlayer(final UUID uniqueId) {
    final CompletableFuture<User> userFuture = getUserManager().loadUser(uniqueId);
    return userFuture.join();
  }

  public boolean playerInGroup(final UUID uniqueId, final String groupName) {
    final User user = getPlayer(uniqueId);
    final Group group = getGroup(groupName);
    return user.getInheritedGroups(user.getQueryOptions()).contains(group);
  }

  public void playerRemoveTeams(final UUID uniqueId, final String server) {
    final OfflinePlayer player = getPlugin().getServer().getOfflinePlayer(uniqueId);
    final User user = getPlayer(player.getUniqueId());
    for (final Group group : user.getInheritedGroups(user.getQueryOptions())) {
      final int weight = 100, groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
      if (groupWeight == weight) playerRemoveGroup(player.getUniqueId(), group.getName(), server);
    }
  }

  public void playerAddGroup(final UUID uniqueId, final String groupName, final String server) {
    final OfflinePlayer player = getPlugin().getServer().getOfflinePlayer(uniqueId);
    final ConsoleCommandSender console = getPlugin().getServer().getConsoleSender();
    getPlugin().getServer().dispatchCommand(console, command(player.getName(), "add", groupName, "server=" + server));
  }

  public void playerRemoveGroup(final UUID uniqueId, final String groupName, final String server) {
    final OfflinePlayer player = getPlugin().getServer().getOfflinePlayer(uniqueId);
    final ConsoleCommandSender console = getPlugin().getServer().getConsoleSender();
    getPlugin().getServer().dispatchCommand(console, command(player.getName(), "remove", groupName, "server=" + server));
  }
  
  public Group getGroup(final String groupName) {
    return getGroupManager().getGroup(groupName);
  }

  public boolean groupHasPermission(final String groupName, final String permission) {
    final Group group = getGroup(groupName);
    return group.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public void groupAddPermission(final String groupName, final String permission) {
    getGroupManager().modifyGroup(groupName, group -> group.data().add(PermissionNode.builder(permission).withContext("server", "football").build()));
  }

  public void groupRemovePermission(final String groupName, final String permission) {
    getGroupManager().modifyGroup(groupName, group -> group.data().remove(PermissionNode.builder(permission).withContext("server", "football").build()));
  }

  public boolean groupExists(final String groupName) {
    return getGroup(groupName) != null;
  }

  public String command(final String playerName, final String action, final String groupName, final String context) {
    return String.join(" ", "lp u", playerName, "parent", action, groupName, context);
  }

  public String command(final String playerName, final String action, final String groupName, final Time time) {
    return String.join(" ", "lp u", playerName, "parent", action, groupName, String.valueOf(time.toMilliseconds()));
  }
}
