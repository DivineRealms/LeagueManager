package io.github.divinerealms.leaguemanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings("deprecation")
@Getter
@CommandAlias("migrate")
public class MigrateCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Helper helper;
  private final Logger logger;

  public MigrateCommand(final UtilManager utilManager) {
    this.utilManager = utilManager;
    this.dataManager = new DataManager(utilManager.getPlugin());
    this.helper = utilManager.getHelper();
    this.logger = utilManager.getLogger();
  }

  @Default
  @CatchUnknown
  public void onMigrate(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.MIGRATE_HELP.getConfigValue(null));
    } else if (args.length == 1) {
      if (!(sender instanceof Player)) {
        getLogger().send(sender, Lang.INGAME_ONLY.getConfigValue(null));
        return;
      }

      String oldNick = args[0];
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(oldNick);
      Player player = (Player) sender;
      if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      if (sender.getName().equalsIgnoreCase(offlinePlayer.getName())) {
        getLogger().send(sender, Lang.MIGRATE_SAME_NICK.getConfigValue(null));
        return;
      }

      getDataManager().setFolderName("playerdata");
      getDataManager().setConfig(oldNick);
      if (!getDataManager().configExists(oldNick)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      FileConfiguration oldConfig = getDataManager().getConfig(oldNick);
      String senderIP = player.getAddress().getAddress().getHostAddress();
      String oldNickIP = oldConfig.getString("address");
      if (!senderIP.equals(oldNickIP)) {
        getLogger().send(sender, Lang.MIGRATE_SAME.getConfigValue(null));
        return;
      }

      getDataManager().copyFile("playerdata", oldNick, sender.getName());
      if (getDataManager().deleteFiles("playerdata", oldNick))
        getLogger().info("Deleted playerdata file for &c" + oldNick);
      getDataManager().setConfig(sender.getName());
      FileConfiguration playerData = getDataManager().getConfig(sender.getName());
      playerData.set("name", sender.getName());
      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(sender.getName());
      skull.setItemMeta(skullMeta);
      playerData.set("head", skull);
      getDataManager().saveConfig(sender.getName());
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp u " + oldNick + " clone " + sender.getName());
      getLogger().send(sender, Lang.MIGRATED.getConfigValue(null));
      getLogger().send("fcfa", Lang.MIGRATED_NOTIFY.getConfigValue(new String[]{sender.getName(),oldNick}));
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp u " + oldNick + " clear");
    } else if (args.length == 2 && sender.hasPermission("group.mod")) {
      String oldNick = args[0];
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(oldNick);
      if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      Player target = Bukkit.getPlayer(args[1]);
      if (target == null || !target.hasPlayedBefore()) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      getDataManager().setFolderName("playerdata");
      getDataManager().setConfig(oldNick);
      if (!getDataManager().configExists(oldNick)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      getDataManager().copyFile("playerdata", oldNick, target.getName());
      if (getDataManager().deleteFiles("playerdata", oldNick))
        getLogger().info("Deleted playerdata file for &c" + oldNick);
      getDataManager().setConfig(target.getName());
      FileConfiguration playerData = getDataManager().getConfig(target.getName());
      playerData.set("name", target.getName());
      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(target.getName());
      skull.setItemMeta(skullMeta);
      playerData.set("head", skull);
      getDataManager().saveConfig(target.getName());
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp u " + oldNick + " clone " + target.getName());
      getLogger().send(target, Lang.MIGRATED.getConfigValue(null));
      getLogger().send("fcfa", Lang.MIGRATED_OTHER.getConfigValue(new String[]{sender.getName(),target.getName(),oldNick}));
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp u " + oldNick + " clear");
    }
  }
}
