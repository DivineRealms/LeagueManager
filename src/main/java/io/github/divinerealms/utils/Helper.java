package io.github.divinerealms.utils;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Helper {
  @Getter @Setter private LuckPerms luckPermsAPI;
  @Getter private final UserManager userManager;
  @Getter private final GroupManager groupManager;
  @Getter private final Config config;
  @Getter private final String[] permissions = new String[] {
      "chatcontrol.channel.%team%", "chatcontrol.channel.send.%team%", "chatcontrol.channel.join.%team%",
      "chatcontrol.channel.join.%team%.write", "chatcontrol.channel.join.%team%.read",
      "chatcontrol.channel.autojoin.%team%", "chatcontrol.channel.autojoin.%team%.write",
      "chatcontrol.channel.autojoin.%team%.read", "chatcontrol.channel.leave.%team%",
  };

  public Helper(final LeagueManager plugin, final UtilManager utilManager) {
    this.luckPermsAPI = plugin.getLuckPermsAPI();
    this.userManager = getLuckPermsAPI().getUserManager();
    this.groupManager = getLuckPermsAPI().getGroupManager();
    this.config = utilManager.getConfig();
  }

  public User getPlayer(final UUID uniqueId) {
    final CompletableFuture<User> userFuture = getUserManager().loadUser(uniqueId);
    return userFuture.join();
  }

  public Group getGroup(final String groupName) {
    return getGroupManager().getGroup(groupName);
  }

  public boolean hasPermission(final UUID uniqueId, final String permission) {
    final User user = getPlayer(uniqueId);
    return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public boolean groupHasPermission(final String groupName, final String permission) {
    final Group group = getGroup(groupName);
    return group.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public void addPermission(final UUID uniqueId, final String permission) {
    getUserManager().modifyUser(uniqueId, user -> user.data().add(PermissionNode.builder(permission).build()));
  }

  public void groupAddPermission(final String groupName, final String permission) {
    getGroupManager().modifyGroup(groupName, group -> group.data().add(PermissionNode.builder(permission).build()));
  }

  public void unsetPermission(final UUID uniqueId, final String permission) {
    getUserManager().modifyUser(uniqueId, user -> user.data().remove(PermissionNode.builder(permission).build()));
  }

  public void groupRemovePermission(final String groupName, final String permission) {
    getGroupManager().modifyGroup(groupName, group -> group.data().remove(PermissionNode.builder(permission).build()));
  }

  public void setMeta(final UUID uniqueId, final String type, final String value) {
    final MetaNode metaNode = MetaNode.builder(type, value).build();
    getUserManager().modifyUser(uniqueId, user -> {
      user.data().clear(NodeType.META.predicate(metaNode1 -> metaNode1.getMetaKey().equalsIgnoreCase(type)));
      user.data().add(metaNode);
    });
  }

  public void unsetMeta(final UUID uniqueId, final String type) {
    getUserManager().modifyUser(uniqueId, user ->
        user.data().clear(NodeType.META.predicate(metaNode -> metaNode.getMetaKey().equalsIgnoreCase(type))));
  }
}
