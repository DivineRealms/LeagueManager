package io.github.divinerealms.utils;

import io.github.divinerealms.LeagueManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Helper {
  @Getter @Setter private LuckPerms luckPermsAPI;
  @Getter private final UserManager userManager;
  @Getter private final GroupManager groupManager;
  @Getter private final String[] permissions = new String[] {
      "chatcontrol.channel.%team%", "chatcontrol.channel.send.%team%", "chatcontrol.channel.join.%team%",
      "chatcontrol.channel.join.%team%.write", "chatcontrol.channel.join.%team%.read",
      "chatcontrol.channel.autojoin.%team%", "chatcontrol.channel.autojoin.%team%.read",
      "chatcontrol.channel.leave.%team%",
  };

  public Helper(final LeagueManager plugin) {
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

  public void playerRemoveTeams(final UUID uniqueId) {
    final User user = getPlayer(uniqueId);
    for (final Group group : user.getInheritedGroups(user.getQueryOptions())) {
      final int weight = 100, groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
      if (groupWeight == weight) playerRemoveGroup(uniqueId, group.getName());
    }
  }

  public void playerAddGroup(final UUID uniqueId, final String groupName) {
    final Group group = getGroup(groupName);
    getUserManager().modifyUser(uniqueId, user -> user.data().add(
        InheritanceNode.builder(group).withContext("server", "football").build()));
  }

  public void playerRemoveGroup(final UUID uniqueId, final String groupName) {
    final Group group = getGroup(groupName);
    getUserManager().modifyUser(uniqueId, user -> user.data().remove(
        InheritanceNode.builder(group).withContext("server", "football").build()));
  }

  public Group getGroup(final String groupName) {
    return getGroupManager().getGroup(groupName);
  }

  public boolean groupHasPermission(final String groupName, final String permission) {
    final Group group = getGroup(groupName);
    return group.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public void groupAddPermission(final String groupName, final String permission) {
    getGroupManager().modifyGroup(groupName, group -> group.data().add(
        PermissionNode.builder(permission).withContext("server", "football").build()));
  }

  public void groupRemovePermission(final String groupName, final String permission) {
    getGroupManager().modifyGroup(groupName, group -> group.data().remove(
        PermissionNode.builder(permission).withContext("server", "football").build()));
  }

  public boolean groupExists(final String groupName) {
    return getGroup(groupName) != null;
  }
}
