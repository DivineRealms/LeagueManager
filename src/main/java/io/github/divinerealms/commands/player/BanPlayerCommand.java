package io.github.divinerealms.commands.player;

import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class BanPlayerCommand implements CommandExecutor {
  @Getter private final Helper helper;
  @Getter private final Logger logger;

  public BanPlayerCommand(final UtilManager utilManager) {
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    if (!sender.hasPermission("leaguemanager.command.ban")) {
      getLogger().sendMessage(sender, "insufficient-permission");
    } else {
      if (args.length <= 2 || args[1].equalsIgnoreCase("help")) {
        getLogger().sendLongMessage(sender, "user.help");
      } else if (args.length == 4) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        final Time time = Time.parseString(args[2]);
        final String reason = args[3], permission = "leaguemanager.banned";

        if (target == null || !target.hasPlayedBefore()) {
          getLogger().sendMessage(sender, "user.not-found");
          return true;
        }

        if (args[1].equalsIgnoreCase(target.getName())) {
          final PermissionNode node = PermissionNode.builder(permission).value(false)
              .expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS)
              .withContext("server", "football").build();

          getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
            final DataMutateResult result = user.data().add(node);

            if (result.wasSuccessful()) {
              getLogger().sendMessage(sender, target.getName(), "user.ban", time, reason);
              if (target.isOnline())
                getLogger().sendMessage(target.getPlayer(), target.getName(), "user.banned", time, reason);
            } else getLogger().sendMessage(target.getName(), sender, "user.already-banned");
          });
        } else getLogger().sendLongMessage(sender, "user.usage.ban");
      } else getLogger().sendLongMessage(sender, "unknown-command");
    }
    return true;
  }
}
