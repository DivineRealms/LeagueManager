package io.github.divinerealms.utils;

import io.github.divinerealms.LeagueManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Getter
public class Helper {
  private final LeagueManager plugin;
  private final UserManager userManager;
  private final GroupManager groupManager;
  private final String[] permissions = new String[]{"chatcontrol.channel.%team%",
      "chatcontrol.channel.send.%team%",
      "chatcontrol.channel.join.%team%",
      "chatcontrol.channel.join.%team%.write",
      "chatcontrol.channel.join.%team%.read",
      "chatcontrol.channel.autojoin.%team%",
      "chatcontrol.channel.autojoin.%team%.read",
      "chatcontrol.channel.leave.%team%",
      "tab.group.%team%"};
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

  public Duration getPermissionExpireTime(final UUID uniqueId, final String permission) {
    final User user = getPlayer(uniqueId);
    final Node node = user.getCachedData().getPermissionData().queryPermission(permission).node();
    assert node != null;
    return node.getExpiryDuration();
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
    final InheritanceNode inheritanceNode = InheritanceNode.builder(groupName).withContext("server", "football").build();
    getUserManager().modifyUser(uniqueId, user -> user.data().add(inheritanceNode));
  }

  public void playerRemoveGroup(final UUID uniqueId, final String groupName) {
    final InheritanceNode inheritanceNode = InheritanceNode.builder(groupName).withContext("server", "football").build();
    getUserManager().modifyUser(uniqueId, user -> user.data().remove(inheritanceNode));
  }

  public void playerAddPermission(final UUID uniqueId, final String permission) {
    final PermissionNode permissionNode = PermissionNode.builder(permission).withContext("server", "football").build();
    getUserManager().modifyUser(uniqueId, user -> user.data().add(permissionNode));
  }

  public void playerRemovePermission(final UUID uniqueId, final String permission) {
    final PermissionNode permissionNode = PermissionNode.builder(permission).withContext("server", "football").build();
    getUserManager().modifyUser(uniqueId, user -> user.data().remove(permissionNode));
  }

  public void playerAddMeta(final UUID uniqueId, final String key, final String value) {
    final MetaNode node = MetaNode.builder(key, value).withContext("server", "football").build();
    getUserManager().modifyUser(uniqueId, user -> user.data().add(node));
  }

  public void playerRemoveMeta(final UUID uniqueId, final String key) {
    final User user = getPlayer(uniqueId);
    final MetaNode node = user.getCachedData().getMetaData().queryMetaValue(key).node();
    if (node != null)
      getUserManager().modifyUser(uniqueId, u -> u.data().remove(node));
  }

  public String playerGetMeta(final UUID uniqueId, final String key) {
    final User user = getPlayer(uniqueId);
    return user.getCachedData().getMetaData().queryMetaValue(key).result();
  }

  public Group getGroup(final String groupName) {
    return getGroupManager().getGroup(groupName);
  }

  public String getGroupMeta(final String groupName, final String metaType) {
    final Group group = getGroup(groupName);
    return group.getCachedData().getMetaData().getMetaValue(metaType);
  }

  public boolean groupHasPermission(final String groupName, final String permission) {
    final Group group = getGroup(groupName);
    return group.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public void groupAddPermission(final String groupName, final String permission, final boolean toggle) {
    final PermissionNode permissionNode = PermissionNode.builder(permission).value(toggle).withContext("server", "football").build();
    getGroupManager().modifyGroup(groupName, group -> group.data().add(permissionNode));
  }

  public void groupRemovePermission(final String groupName, final String permission) {
    final PermissionNode permissionNode = PermissionNode.builder(permission).withContext("server", "football").build();
    getGroupManager().modifyGroup(groupName, group -> group.data().remove(permissionNode));
  }

  public boolean groupExists(final String groupName) {
    return getGroup(groupName) != null;
  }

  public boolean groupHasMeta(final String groupName, final String metaType) {
    final Group group = getGroup(groupName);
    final CachedMetaData metaData = group.getCachedData().getMetaData();
    return metaData.getMeta().containsKey(metaType);
  }
}
