package io.github.divinerealms.listeners;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class ChatListener implements Listener {
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;

  public ChatListener(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreprocess(final PlayerCommandPreprocessEvent event) {
    final Player player = event.getPlayer();
    final String message = event.getMessage().toLowerCase();

    final Pattern FC_STORE = Pattern.compile("^/(fc|footcube(:footcube|:fc|)) store");
    final Pattern FC_COMMANDS = Pattern.compile("^/(footcube(:footcube|:fc|)|fc|takeplace|best|stats|fc join)");
    final Pattern FC_ADMIN = Pattern.compile("^/((clear|)cube(s|))");

    final Matcher matcherStore = FC_STORE.matcher(message);
    final Matcher matcherCommands = FC_COMMANDS.matcher(message);
    final Matcher matcherAdmin = FC_ADMIN.matcher(message);

    if (event.isCancelled()) return;

    if (matcherStore.find()) {
      getLogger().send(player, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      event.setCancelled(true);
    }

    if (matcherCommands.find()) {
      if (!player.hasPermission("commandwhitelist.bypass.fc")) {
        if (player.hasPermission("group.fcba")) return;
        else {
          getLogger().send(player, Lang.FOOTCUBE_DISABLED.getConfigValue(null));
          event.setCancelled(true);
        }
      }

      if (player.hasPermission("footcube.banned")) {
        final Duration expiry = getHelper().getPermissionExpireTime(player.getUniqueId(), "footcube.banned");
        String duration;
        try {
          duration = new Time(expiry.toMillis()).toString();
        } catch (Time.TimeParseException | NullPointerException e) {
          return;
        }
        getLogger().send(player, Lang.USER_STILL_BANNED.getConfigValue(new String[]{duration}));
        event.setCancelled(true);
      }
    }

    if (matcherAdmin.find()) {
      if (!player.hasPermission("leaguemanager.footcube.admin")) {
        getLogger().send(player, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
        event.setCancelled(true);
      }
    }
  }
}
