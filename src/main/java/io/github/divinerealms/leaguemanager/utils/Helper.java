package io.github.divinerealms.leaguemanager.utils;

import io.github.divinerealms.leaguemanager.LeagueManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Getter
public class Helper {
  private final Plugin plugin;
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

  public Helper(Plugin plugin) {
    this.plugin = plugin;
    this.luckPermsAPI = LeagueManager.getInstance().getLuckPermsAPI();
    this.userManager = getLuckPermsAPI().getUserManager();
    this.groupManager = getLuckPermsAPI().getGroupManager();
  }

  public User getPlayer(UUID uniqueId) {
    CompletableFuture<User> userFuture = getUserManager().loadUser(uniqueId);
    return userFuture.join();
  }

  public Duration getPermissionExpireTime(UUID uniqueId, String permission) {
    User user = getPlayer(uniqueId);
    Node node = user.getCachedData().getPermissionData().queryPermission(permission).node();
    assert node != null;
    return node.getExpiryDuration();
  }

  public boolean playerInGroup(UUID uniqueId, String groupName) {
    User user = getPlayer(uniqueId);
    Group group = getGroup(groupName);
    return user.getInheritedGroups(user.getQueryOptions()).contains(group);
  }

  public void playerRemoveTeams(UUID uniqueId) {
    User user = getPlayer(uniqueId);
    for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
      int groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
      if (groupWeight == 100 || groupWeight == 99) playerRemoveGroup(uniqueId, group.getName(), "football");
    }
  }

  public String playerGetTeam(UUID uniqueId, int weight) {
    User user = getPlayer(uniqueId);
    for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
      int groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
      if (groupWeight == weight) return group.getName();
    }
    return "/";
  }

  public boolean playerHasTeam(UUID uniqueId) {
    User user = getPlayer(uniqueId);
    for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
      int groupWeight = group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
      if (groupWeight == 100 || groupWeight == 99) return true;
    }
    return false;
  }

  public void playerAddGroup(UUID uniqueId, String groupName, String server) {
    InheritanceNode inheritanceNode = InheritanceNode.builder(groupName).withContext("server", server).build();
    getUserManager().modifyUser(uniqueId, user -> user.data().add(inheritanceNode));
  }

  public void playerRemoveGroup(UUID uniqueId, String groupName, String server) {
    InheritanceNode inheritanceNode = InheritanceNode.builder(groupName).withContext("server", server).build();
    getUserManager().modifyUser(uniqueId, user -> user.data().remove(inheritanceNode));
  }

  public boolean playerHasPermission(UUID uniqueId, String permission) {
    User user = getUserManager().getUser(uniqueId);
    if (user == null) return false;
    CachedPermissionData cachedPermissionData = user.getCachedData().getPermissionData();
    return cachedPermissionData.checkPermission(permission).asBoolean();
  }

  public boolean playerCheckPermission(UUID uniqueId, String permission) {
    User user = getUserManager().getUser(uniqueId);
    if (user == null) return false;
    CachedPermissionData cachedPermissionData = user.getCachedData().getPermissionData();
    Node permissionNode = cachedPermissionData.queryPermission(permission).node();
    return permissionNode != null && permissionNode.getValue();
  }

  public void playerAddPermission(UUID uniqueId, String permission, String server) {
    PermissionNode permissionNode = PermissionNode.builder(permission).withContext("server", server).build();
    getUserManager().modifyUser(uniqueId, user -> user.data().add(permissionNode));
  }

  public void playerRemovePermission(UUID uniqueId, String permission, String server) {
    PermissionNode permissionNode = PermissionNode.builder(permission).withContext("server", server).build();
    getUserManager().modifyUser(uniqueId, user -> user.data().remove(permissionNode));
  }

  public void playerAddMeta(UUID uniqueId, String key, String value) {
    MetaNode node = MetaNode.builder(key, value).withContext("server", "football").build();
    getUserManager().modifyUser(uniqueId, user -> user.data().add(node));
  }

  public void playerRemoveMeta(UUID uniqueId, String key) {
    User user = getPlayer(uniqueId);
    MetaNode node = user.getCachedData().getMetaData().queryMetaValue(key).node();
    if (node != null)
      getUserManager().modifyUser(uniqueId, u -> u.data().remove(node));
  }

  public String playerGetMeta(UUID uniqueId, String key) {
    User user = getPlayer(uniqueId);
    return user.getCachedData().getMetaData().getMetaValue(key);
  }

  public boolean playerHasMeta(UUID uniqueId, String metaType) {
    User user = getPlayer(uniqueId);
    CachedMetaData metaData = user.getCachedData().getMetaData();
    return metaData.getMeta().containsKey(metaType);
  }

  public Group getGroup(String groupName) {
    return getGroupManager().getGroup(groupName);
  }

  public String getGroupMeta(String groupName, String metaType) {
    Group group = getGroup(groupName);
    return group.getCachedData().getMetaData().getMetaValue(metaType);
  }

  public boolean groupHasPermission(String groupName, String permission) {
    Group group = getGroup(groupName);
    return group.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public void groupAddPermission(String groupName, String permission, String server, boolean toggle) {
    PermissionNode permissionNode = PermissionNode.builder(permission).value(toggle).withContext("server", server).build();
    getGroupManager().modifyGroup(groupName, group -> group.data().add(permissionNode));
  }

  public void groupRemovePermission(String groupName, String permission, String server) {
    PermissionNode permissionNode = PermissionNode.builder(permission).withContext("server", server).build();
    getGroupManager().modifyGroup(groupName, group -> group.data().remove(permissionNode));
  }

  public boolean groupExists(String groupName) {
    return getGroup(groupName) != null;
  }

  public boolean groupHasMeta(String groupName, String metaType) {
    Group group = getGroup(groupName);
    CachedMetaData metaData = group.getCachedData().getMetaData();
    return metaData.getMeta().containsKey(metaType);
  }

  public int groupGetMetaWeight(String groupName) {
    Group group = getGroup(groupName);
    return group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0;
  }
}
