  package io.github.divinerealms.commands.player;

  import io.github.divinerealms.managers.UtilManager;
  import io.github.divinerealms.utils.Helper;
  import io.github.divinerealms.utils.Logger;
  import lombok.Getter;
  import net.luckperms.api.model.data.DataMutateResult;
  import net.luckperms.api.model.user.User;
  import net.luckperms.api.node.Node;
  import org.bukkit.Bukkit;
  import org.bukkit.OfflinePlayer;
  import org.bukkit.command.Command;
  import org.bukkit.command.CommandExecutor;
  import org.bukkit.command.CommandSender;

public class UnbanPlayerCommand implements CommandExecutor {
  @Getter private final Helper helper;
  @Getter private final Logger logger;

  public UnbanPlayerCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.unban")) {
      getLogger().sendMessage(sender, "insufficient-permission");
    } else {
      if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLongMessage(sender, "user.help");
      } else if (args.length == 2) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final String permission = "commandwhitelist.bypass.fc";

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().sendMessage(sender, "user.not-found");
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          final User user = getHelper().getPlayer(target.getUniqueId());
          final Node node = user.getCachedData().getPermissionData().queryPermission(permission).node();

          if (node != null && node.hasExpiry()) {
            getHelper().getUserManager().modifyUser(target.getUniqueId(), user1 -> {
              final DataMutateResult result = user1.data().remove(node);

              if (result.wasSuccessful()) {
                getLogger().sendMessage(target.getName(), sender, "user.unban");
                if (target.isOnline())
                  getLogger().sendMessage(target.getName(), target.getPlayer(), "user.unbanned");
              } else getLogger().sendMessage(target.getName(), sender, "user.not-banned");
            });
          } else getLogger().sendMessage(target.getName(), target.getPlayer(), "user.not-banned");
        } else getLogger().sendLongMessage(sender, "user.usage.unban");
      } else getLogger().sendLongMessage(sender, "unknown-command");
    }
    return true;
  }
}
