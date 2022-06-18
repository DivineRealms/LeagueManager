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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Helper {
  @Getter @Setter private LuckPerms luckPermsAPI;
  @Getter private final Config config;
  @Getter private final String[] permissions = new String[] {
      "chatcontrol.channel.autojoin.%team%", "chatcontrol.channel.autojoin.%team%.write",
      "chatcontrol.channel.autojoin.%team%.read", "chatcontrol.channel.join.%team%",
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

  public void addToTeam(final UUID uniqueId, final String team, final String leagueType) {
    final User user = getPlayer(uniqueId);
    for (final String permission : getPermissions())
      user.data().add(Node.builder(permission.replace("%team%", team)).build());
    final String teamTag = getConfig().getString(team);
    final MetaNode metaNode = MetaNode.builder("team", teamTag).build();
    user.data().clear(NodeType.META.predicate(metaNode1 -> metaNode1.getMetaKey().equalsIgnoreCase("team")));
    if (leagueType.equalsIgnoreCase("main")) user.data().add(metaNode);
    else if (leagueType.equalsIgnoreCase("juniors")) user.data().add(MetaNode.builder("team", teamTag + " &aB").build());
    getLuckPermsAPI().getUserManager().saveUser(user);
  }

  public void removeFromATeam(final UUID uniqueId, final String team) {
    final User user = getPlayer(uniqueId);
    for (final String permission : getPermissions())
      user.data().remove(Node.builder(permission.replace("%team%", team)).build());
    user.data().clear(NodeType.META.predicate(metaNode1 -> metaNode1.getMetaKey().equalsIgnoreCase("team")));
    getLuckPermsAPI().getUserManager().saveUser(user);
  }
}
