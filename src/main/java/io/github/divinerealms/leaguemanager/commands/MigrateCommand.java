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
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;

@SuppressWarnings("deprecation")
@Getter
@CommandAlias("migrate")
public class MigrateCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Helper helper;
  private final Logger logger;
  private final String folderName = "playerdata";

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

      if (!getDataManager().configExists(getFolderName(), oldNick)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      getDataManager().setConfig(getFolderName(), oldNick);
      String senderIP = player.getAddress().getAddress().getHostAddress();
      String oldNickIP = getDataManager().getConfig().getString("address");
      if (!senderIP.equals(oldNickIP)) {
        getLogger().send(sender, Lang.MIGRATE_SAME.getConfigValue(null));
        return;
      }

      getDataManager().copyFile(oldNick, sender.getName());

      if (getDataManager().deleteFiles(oldNick)) {
        getLogger().info("Deleted playerdata file for &c" + oldNick);
        }

      getDataManager().setConfig(getFolderName(), sender.getName());
      getDataManager().getConfig().set("name", sender.getName());

      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(sender.getName());
      skull.setItemMeta(skullMeta);
      getDataManager().getConfig().set("head", skull);

      getDataManager().saveConfig();

      Collection<Node> nodes = getHelper().getPlayer(offlinePlayer.getUniqueId()).getNodes();
      getHelper().getUserManager().modifyUser(player.getUniqueId(), user -> {
        for (Node node : nodes) user.data().add(node);
      });

      getLogger().send(sender, Lang.MIGRATED.getConfigValue(null));
      getLogger().send("fcfa", Lang.MIGRATED_NOTIFY.getConfigValue(new String[]{sender.getName(),oldNick}));

      getHelper().getUserManager().modifyUser(offlinePlayer.getUniqueId(), user -> user.data().clear());
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

      if (!getDataManager().configExists(getFolderName(), oldNick)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      getDataManager().setConfig(getFolderName(), oldNick);
      String targetIP = target.getAddress().getAddress().getHostAddress();
      String oldNickIP = getDataManager().getConfig().getString("address");
      if (!targetIP.equals(oldNickIP)) {
        getLogger().send(sender, Lang.MIGRATE_SAME.getConfigValue(null));
        return;
      }

      getDataManager().copyFile(oldNick, target.getName());

      if (getDataManager().deleteFiles(oldNick)) {
        getLogger().info("Deleted playerdata file for &c" + oldNick);
      }

      getDataManager().setConfig(getFolderName(), target.getName());
      getDataManager().getConfig().set("name", target.getName());

      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(target.getName());
      skull.setItemMeta(skullMeta);
      getDataManager().getConfig().set("head", skull);

      getDataManager().saveConfig();

      Collection<Node> nodes = getHelper().getPlayer(offlinePlayer.getUniqueId()).getNodes();
      getHelper().getUserManager().modifyUser(target.getUniqueId(), user -> {
        for (Node node : nodes) user.data().add(node);
      });

      getLogger().send(target, Lang.MIGRATED.getConfigValue(null));
      getLogger().send("fcfa", Lang.MIGRATED_OTHER.getConfigValue(new String[]{sender.getName(),target.getName(),oldNick}));

      getHelper().getUserManager().modifyUser(offlinePlayer.getUniqueId(), user -> user.data().clear());
    }
  }
}
