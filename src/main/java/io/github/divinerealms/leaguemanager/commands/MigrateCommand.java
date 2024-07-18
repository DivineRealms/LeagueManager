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
import java.util.UUID;

@SuppressWarnings("deprecation")
@Getter
@CommandAlias("migrate")
public class MigrateCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final DataManager dataManager;
  private final Helper helper;
  private final Logger logger;
  private final String playerDataFolder = "playerdata";

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

      UUID oldPlayerUUID = getPlayerUUID(oldNick);
      if (oldPlayerUUID == null) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      String targetIP = ((Player) sender).getAddress().getAddress().getHostAddress();
      String oldNickIP = getDataManager().getConfig(oldPlayerUUID.toString()).getString("address");
      if (!targetIP.equals(oldNickIP)) {
        getLogger().send(sender, Lang.MIGRATE_SAME.getConfigValue(null));
        return;
      }

      UUID newPlayerUUID = player.getUniqueId();

      if (!getDataManager().configExists(playerDataFolder, oldPlayerUUID.toString())) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      // Copy oldNick's data to sender's data
      getDataManager().copyFile(oldPlayerUUID.toString(), newPlayerUUID.toString());

      // Delete oldNick's data
      getDataManager().deleteFiles(oldPlayerUUID.toString());

      // Set sender's data
      getDataManager().setConfig(playerDataFolder, newPlayerUUID.toString());
      getDataManager().getConfig(newPlayerUUID.toString()).set("name", player.getName());
      getDataManager().removePlayerUUID(oldPlayerUUID);
      getDataManager().addPlayerUUID(newPlayerUUID, player.getName());

      // Set sender's skull
      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(player.getName());
      skull.setItemMeta(skullMeta);
      getDataManager().getConfig(newPlayerUUID.toString()).set("head", skull);

      // Save sender's config
      getDataManager().saveConfig(newPlayerUUID.toString());

      // Migrate permissions from oldNick to sender
      Collection<Node> nodes = getHelper().getPlayer(oldPlayerUUID).getNodes();
      getHelper().getUserManager().modifyUser(newPlayerUUID, user -> {
        for (Node node : nodes) user.data().add(node);
      });

      // Inform sender
      getLogger().send(sender, Lang.MIGRATED.getConfigValue(null));
      getLogger().send("fcfa", Lang.MIGRATED_NOTIFY.getConfigValue(new String[]{sender.getName(), oldNick}));

      // Clear oldNick's permissions
      getHelper().getUserManager().modifyUser(oldPlayerUUID, user -> user.data().clear());
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

      UUID oldPlayerUUID = getPlayerUUID(oldNick);
      if (oldPlayerUUID == null) {
        getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
        return;
      }

      UUID newPlayerUUID = target.getUniqueId();

      if (!getDataManager().configExists(playerDataFolder, oldPlayerUUID.toString())) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
        return;
      }

      // Check IP matching
      String targetIP = target.getAddress().getAddress().getHostAddress();
      String oldNickIP = getDataManager().getConfig(oldPlayerUUID.toString()).getString("address");
      if (!targetIP.equals(oldNickIP)) {
        getLogger().send(sender, Lang.MIGRATE_SAME.getConfigValue(null));
        return;
      }

      // Copy oldNick's data to target's data
      getDataManager().copyFile(oldPlayerUUID.toString(), newPlayerUUID.toString());

      // Delete oldNick's data
      getDataManager().deleteFiles(oldPlayerUUID.toString());

      // Set target's data
      getDataManager().setConfig(playerDataFolder, newPlayerUUID.toString());
      getDataManager().getConfig(newPlayerUUID.toString()).set("name", target.getName());
      getDataManager().removePlayerUUID(oldPlayerUUID);
      getDataManager().addPlayerUUID(newPlayerUUID, target.getName());

      // Set target's skull
      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(target.getName());
      skull.setItemMeta(skullMeta);
      getDataManager().getConfig(newPlayerUUID.toString()).set("head", skull);

      // Save target's config
      getDataManager().saveConfig(newPlayerUUID.toString());

      // Migrate permissions from oldNick to target
      Collection<Node> nodes = getHelper().getPlayer(oldPlayerUUID).getNodes();
      getHelper().getUserManager().modifyUser(newPlayerUUID, user -> {
        for (Node node : nodes) user.data().add(node);
      });

      // Inform target
      getLogger().send(target, Lang.MIGRATED.getConfigValue(null));
      getLogger().send("fcfa", Lang.MIGRATED_OTHER.getConfigValue(new String[]{sender.getName(), target.getName(), oldNick}));

      // Clear oldNick's permissions
      getHelper().getUserManager().modifyUser(oldPlayerUUID, user -> user.data().clear());
    }
  }

  private UUID getPlayerUUID(String playerName) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
    if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
      return offlinePlayer.getUniqueId();
    }
    return null;
  }
}
