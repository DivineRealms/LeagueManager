package io.github.divinerealms.utils;

import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Helper {
  @Getter @Setter private LuckPerms luckPermsAPI;
  @Getter private final Config config;
  @Getter private final String[] permissions = new String[] {
      "chatcontrol.channel.autoJoin.%team%", "chatcontrol.channel.autoJoin.%team%.write",
      "chatcontrol.channel.autoJoin.%team%.read", "chatcontrol.channel.join.%team%",
      "chatcontrol.channel.join.%team%.write", "chatcontrol.channel.join.%team%.read",
      "chatcontrol.channel.send.%team%", "chatcontrol.channel.%team%"
  };

  public Helper(final LeagueManager plugin, final UtilManager utilManager) {
    this.luckPermsAPI = plugin.getLuckPermsAPI();
    this.config = utilManager.getConfig();
  }

  public User getPlayer(final UUID uniqueId) {
    final UserManager userManager = getLuckPermsAPI().getUserManager();
    final CompletableFuture<User> userFuture = userManager.loadUser(uniqueId);

    return userFuture.join();
  }

  public boolean hasPermission(final UUID uniqueId, final String permission) {
    final User user = getPlayer(uniqueId);
    return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public void addPermission(final UUID uniqueId, final String permission) {
    final User user = getPlayer(uniqueId);
    user.data().add(PermissionNode.builder(permission).build());
  }

  public void unsetPermission(final UUID uniqueId, final String permission) {
    final User user = getPlayer(uniqueId);
    user.data().remove(PermissionNode.builder(permission).build());
  }

  public void setMeta(final UUID uniqueId, final String type, final String value) {
    final User user = getPlayer(uniqueId);
    final MetaNode metaNode = MetaNode.builder(type, value).build();
    user.data().clear(NodeType.META.predicate(metaNode1 -> metaNode1.getMetaKey().equalsIgnoreCase(type)));
    user.data().add(metaNode);
  }

  public void unsetMeta(final UUID uniqueId, final String type) {
    final User user = getPlayer(uniqueId);
    user.data().clear(NodeType.META.predicate(metaNode -> metaNode.getMetaKey().equalsIgnoreCase(type)));
  }

  public void saveUser(final UUID uniqueId) {
    final User user = getPlayer(uniqueId);
    getLuckPermsAPI().getUserManager().saveUser(user);
  }
}
