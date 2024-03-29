package io.github.divinerealms.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.LeagueManager;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import io.github.divinerealms.utils.Time;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Getter
@CommandAlias("leaguemanager|lm")
public class LMCommand extends BaseCommand {
  private final LeagueManager instance;
  private final Logger logger;
  private final Helper helper;

  public LMCommand(final UtilManager utilManager, final LeagueManager instance) {
    this.instance = instance;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
  }

  @Default
  public void onBase(CommandSender sender) {
    if (sender instanceof Player)
      for (String message : startupBanner())
        getLogger().send(sender, ChatColor.translateAlternateColorCodes('&', message));
    else getLogger().sendBanner();
  }

  @CatchUnknown
  public void onUnknown(CommandSender sender) {
    getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("help")
  @CommandPermission("leaguemanager.command.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.HELP.getConfigValue(null));
  }

  @Subcommand("reload")
  @CommandPermission("leaguemanager.command.reload")
  public void onReload(CommandSender sender) {
    getInstance().setupMessages();
    getInstance().setup();
    getLogger().send(sender, Lang.RELOAD.getConfigValue(null));
  }

  @Subcommand("toggle")
  @CommandPermission("leaguemanager.command.toggle")
  public void onToggle(CommandSender sender) {
    String state;
    Server server = getInstance().getServer();
    if (getHelper().groupHasPermission("default", "leaguemanager.footcube")) {
      state = Lang.OFF.getConfigValue(null);
      getHelper().groupAddPermission("default", "leaguemanager.footcube", false);
    } else {
      state = Lang.ON.getConfigValue(null);
      getHelper().groupAddPermission("default", "leaguemanager.footcube", true);
    }
    server.broadcastMessage(Lang.TOGGLE.getConfigValue(new String[]{state,sender.getName()}));
  }

  @Subcommand("ban")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.ban")
  public void onBan(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.USER_USAGE_BAN.getConfigValue(null));
    } else if (args.length >= 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        Time time = Time.parseString("5min");
        User user = getHelper().getPlayer(target.getUniqueId());

        try {
          time = Time.parseString(args[1]);
        } catch (Time.TimeParseException | NullPointerException e) {
          getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
        }

        Node node = Node.builder("leaguemanager.banned").value(true).expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS).withContext("server", "football").build();
        DataMutateResult result = user.data().add(node);

        if (result.wasSuccessful()) {
          if (args.length == 2) {
            getLogger().send(sender, Lang.USER_BAN.getConfigValue(new String[]{target.getName(), time.toString(), "Kršenje pravila"}));
            if (target.isOnline())
              getLogger().send(target.getPlayer(), Lang.USER_BANNED.getConfigValue(new String[]{time.toString(), "Kršenje pravila"}));
          } else {
            String reason = StringUtils.join(args, ' ', 2, args.length);
            getLogger().send(sender, Lang.USER_BAN.getConfigValue(new String[]{target.getName(), time.toString(), reason}));
            if (target.isOnline())
              getLogger().send(target.getPlayer(), Lang.USER_BANNED.getConfigValue(new String[]{time.toString(), reason}));
          }
          getHelper().getUserManager().saveUser(user);
        } else getLogger().send(sender, Lang.USER_ALREADY_BANNED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.USER_USAGE_BAN.getConfigValue(null));
  }

  @Subcommand("unban")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.unban")
  public void onUnban(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.USER_USAGE_UNBAN.getConfigValue(null));
    } else if (args.length == 1) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        User user = getHelper().getPlayer(target.getUniqueId());
        Node node = user.getCachedData().getPermissionData().queryPermission("leaguemanager.banned").node();

        if (node != null && node.hasExpiry()) {
          DataMutateResult result = user.data().remove(node);
          if (result.wasSuccessful()) {
            getLogger().send(sender, Lang.USER_UNBAN.getConfigValue(new String[]{target.getName()}));
            if (target.isOnline()) getLogger().send(target.getPlayer(), Lang.USER_UNBANNED.getConfigValue(null));
            getHelper().getUserManager().saveUser(user);
          } else getLogger().send(sender, Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("suspend")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.suspend")
  public void onSuspend(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.USER_USAGE_SUSPEND.getConfigValue(null));
    } else if (args.length >= 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        Time time = Time.parseString("5min");
        User user = getHelper().getPlayer(target.getUniqueId());

        try {
          time = Time.parseString(args[1]);
        } catch (Time.TimeParseException | NullPointerException e) {
          getLogger().send(sender, Lang.INVALID_TIME.getConfigValue(null));
        }

        Node node = Node.builder("group.suspend").value(true).expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS).withContext("server", "football").build();
        DataMutateResult result = user.data().add(node);

        if (result.wasSuccessful()) {
          if (args.length == 2) {
            getLogger().send(sender, Lang.USER_SUSPEND.getConfigValue(new String[]{target.getName(), time.toString(), "Kršenje pravila"}));
            if (target.isOnline())
              getLogger().send(target.getPlayer(), Lang.USER_SUSPENDED.getConfigValue(new String[]{time.toString(), "Kršenje pravila"}));
          } else {
            String reason = StringUtils.join(args, ' ', 2, args.length);
            getLogger().send(sender, Lang.USER_SUSPEND.getConfigValue(new String[]{target.getName(), time.toString(), reason}));
            if (target.isOnline())
              getLogger().send(target.getPlayer(), Lang.USER_SUSPENDED.getConfigValue(new String[]{time.toString(), reason}));
          }
          getHelper().getUserManager().saveUser(user);
        } else getLogger().send(sender, Lang.USER_ALREADY_SUSPENDED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.USER_USAGE_SUSPEND.getConfigValue(null));
  }

  @Subcommand("unsuspend")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.unsuspend")
  public void onUnSuspend(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.USER_USAGE_UNSUSPEND.getConfigValue(null));
    } else if (args.length == 1) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        User user = getHelper().getPlayer(target.getUniqueId());
        Node node = user.getCachedData().getPermissionData().queryPermission("group.suspend").node();

        if (node != null && node.hasExpiry()) {
          DataMutateResult result = user.data().remove(node);
          if (result.wasSuccessful()) {
            getLogger().send(sender, Lang.USER_UNSUSPEND.getConfigValue(new String[]{target.getName()}));
            if (target.isOnline()) getLogger().send(target.getPlayer(), Lang.USER_UNSUSPENDED.getConfigValue(null));
            getHelper().getUserManager().saveUser(user);
          } else getLogger().send(sender, Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  private String[] startupBanner() {
    return new String[]{"&8▎ &r","&8▎ &d  88       &e8b      d8","&8▎ &d  88       &e88b   d88   &a" + getLogger().getPluginName(),"&8▎ &d  88    .o &e88YbdP88   &3Authors: &b" + getLogger().getAuthors(),"&8▎ &d  88ood8 &e88 Y||Y 88","&8▎ &r"};
  }
}
