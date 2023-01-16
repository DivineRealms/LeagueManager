package io.github.divinerealms.listeners;

import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
  @Getter
  private final UtilManager utilManager;
  @Getter
  private final Logger logger;

  public ChatListener(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreprocess(final PlayerCommandPreprocessEvent event) {
    final Player player = event.getPlayer();
    final String message = event.getMessage().toLowerCase();

    final Pattern FC_STORE = Pattern.compile("^/(fc|footcube(:footcube|:fc|)) store");
    final Pattern FC_COMMANDS = Pattern.compile("^/(footcube(:footcube|:fc|)|fc|tkp|takeplace|best|stats|leave|2v2|3v3|4v4)");

    final Matcher matcherStore = FC_STORE.matcher(message);
    final Matcher matcherCommands = FC_COMMANDS.matcher(message);

    if (event.isCancelled()) return;

    if (matcherStore.find()) {
      getLogger().send(player, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      event.setCancelled(true);
    }

    if (matcherCommands.find()) {
      if (!player.hasPermission("commandwhitelist.bypass.fc")) {
        getLogger().send(player, Lang.FOOTCUBE_DISABLED.getConfigValue(null));
        event.setCancelled(true);
      }

      if (player.hasPermission("footcube.banned")) {
        getLogger().send(player, Lang.USER_STILL_BANNED.getConfigValue(null));
        event.setCancelled(true);
      }
    }
  }
}
