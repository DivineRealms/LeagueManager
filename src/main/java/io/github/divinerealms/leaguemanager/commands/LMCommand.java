package io.github.divinerealms.leaguemanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.leaguemanager.LeagueManager;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import io.github.divinerealms.leaguemanager.utils.Time;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
@Getter
@CommandAlias("leaguemanager|lm")
public class LMCommand extends BaseCommand {
  private final LeagueManager instance;
  private final UtilManager utilManager;
  private final Logger logger;
  private final Helper helper;
  private final DataManager dataManager;

  public LMCommand(final UtilManager utilManager, final LeagueManager instance) {
    this.instance = instance;
    this.utilManager = utilManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
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
    getUtilManager().reload();
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
          String reason = "Kršenje pravila";
          if (args.length != 2) {
            reason = StringUtils.join(args, ' ', 2, args.length);
          }
          getLogger().send(sender, Lang.USER_BAN.getConfigValue(new String[]{target.getName(), time.toString(), reason}));
          if (target.isOnline()) {
            getLogger().send(target.getPlayer(), Lang.USER_BANNED.getConfigValue(new String[]{time.toString(), reason}));
          }
          getDataManager().setFolderName("playerdata");
          if (getDataManager().configExists(target.getName())) {
            getDataManager().setConfig(target.getName());
            FileConfiguration playerData = getDataManager().getConfig(target.getName());
            playerData.set("ban.time", time.toString());
            playerData.set("ban.reason", reason);
            playerData.set("ban.executor", sender.getName());
            getDataManager().saveConfig(target.getName());
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
            getDataManager().setFolderName("playerdata");
            if (getDataManager().configExists(target.getName())) {
              getDataManager().setConfig(target.getName());
              FileConfiguration playerData = getDataManager().getConfig(target.getName());
              playerData.set("ban", null);
              getDataManager().saveConfig(target.getName());
            }
            getLogger().send(sender, Lang.USER_UNBAN.getConfigValue(new String[]{target.getName()}));
            if (target.isOnline()) getLogger().send(target.getPlayer(), Lang.USER_UNBANNED.getConfigValue(null));
            getHelper().getUserManager().saveUser(user);
          } else getLogger().send(sender, Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("checkban")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.checkban")
  public void onCheckBan(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.USER_USAGE_CHECKBAN.getConfigValue(null));
    } else if (args.length == 1) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        User user = getHelper().getPlayer(target.getUniqueId());
        Node node = user.getCachedData().getPermissionData().queryPermission("leaguemanager.banned").node();

        if (node != null && node.hasExpiry()) {
          getDataManager().setFolderName("playerdata");
          getDataManager().setConfig(target.getName());
          FileConfiguration playerData = getDataManager().getConfig(target.getName());
          String reason = playerData.getString("ban.reason");
          String time = playerData.getString("ban.time");
          String executor = playerData.getString("ban.executor");
          String expiry = new Time(node.getExpiryDuration().getSeconds(), TimeUnit.SECONDS).toString();
          getLogger().send(sender, Lang.USER_CHECKBAN.getConfigValue(new String[]{target.getName(),executor,reason,time,expiry}));
        } else getLogger().send(sender, Lang.USER_NOT_BANNED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    }
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
          String reason = "Kršenje pravila";
          if (args.length != 2) {
            reason = StringUtils.join(args, ' ', 2, args.length);
          }
          getLogger().send(sender, Lang.USER_SUSPEND.getConfigValue(new String[]{target.getName(), time.toString(), reason}));
          if (target.isOnline())
            getLogger().send(target.getPlayer(), Lang.USER_SUSPENDED.getConfigValue(new String[]{time.toString(), reason}));
          getDataManager().setFolderName("playerdata");
          if (getDataManager().configExists(target.getName())) {
            getDataManager().setConfig(target.getName());
            FileConfiguration playerData = getDataManager().getConfig(target.getName());
            playerData.set("suspend.time", time.toString());
            playerData.set("suspend.reason", reason);
            playerData.set("suspend.executor", sender.getName());
            getDataManager().saveConfig(target.getName());
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
            getDataManager().setFolderName("playerdata");
            if (getDataManager().configExists(target.getName())) {
              getDataManager().setConfig(target.getName());
              FileConfiguration playerData = getDataManager().getConfig(target.getName());
              playerData.set("suspend", null);
              getDataManager().saveConfig(target.getName());
            }
            getLogger().send(sender, Lang.USER_UNSUSPEND.getConfigValue(new String[]{target.getName()}));
            if (target.isOnline()) getLogger().send(target.getPlayer(), Lang.USER_UNSUSPENDED.getConfigValue(null));
            getHelper().getUserManager().saveUser(user);
          } else getLogger().send(sender, Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
        } else getLogger().send(sender, Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("checksuspend")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.checksuspend")
  public void onCheckSuspend(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.USER_USAGE_CHECKSUSPEND.getConfigValue(null));
    } else if (args.length == 1) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        User user = getHelper().getPlayer(target.getUniqueId());
        Node node = user.getCachedData().getPermissionData().queryPermission("group.suspend").node();

        if (node != null && node.hasExpiry()) {
          getDataManager().setFolderName("playerdata");
          getDataManager().setConfig(target.getName());
          FileConfiguration playerData = getDataManager().getConfig(target.getName());
          String reason = playerData.getString("suspend.reason");
          String time = playerData.getString("suspend.time");
          String executor = playerData.getString("suspend.executor");
          String expiry = new Time(node.getExpiryDuration().getSeconds(), TimeUnit.SECONDS).toString();
          getLogger().send(sender, Lang.USER_CHECKSUSPEND.getConfigValue(new String[]{target.getName(),executor,reason,time,expiry}));
        } else getLogger().send(sender, Lang.USER_NOT_SUSPENDED.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    }
  }

  private String[] startupBanner() {
    return new String[]{"&8▎ &r","&8▎ &d  88       &e8b      d8","&8▎ &d  88       &e88b   d88   &a" + getLogger().getPluginName(),"&8▎ &d  88    .o &e88YbdP88   &3Authors: &b" + getLogger().getAuthors(),"&8▎ &d  88ood8 &e88 Y||Y 88","&8▎ &r"};
  }
}
