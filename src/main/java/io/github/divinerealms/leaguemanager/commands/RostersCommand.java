package io.github.divinerealms.leaguemanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.gui.impl.PerRosterGUI;
import io.github.divinerealms.leaguemanager.gui.impl.RostersGUI;
import io.github.divinerealms.leaguemanager.managers.DataManager;
import io.github.divinerealms.leaguemanager.managers.GUIManager;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import io.github.divinerealms.leaguemanager.utils.Logger;
import lombok.Getter;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.WeightNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
@Getter
@CommandAlias("rosters|rt")
@CommandPermission("leaguemanager.command.rosters")
public class RostersCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  private final Logger logger;
  private final Helper helper;
  private final DataManager dataManager;
  private final String teamData = "teamdata";
  private final String playerData = "playerdata";

  public RostersCommand(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.guiManager = guiManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new DataManager(utilManager.getPlugin());
  }

  @Default
  @Subcommand("gui")
  @CommandPermission("leaguemanager.command.rosters")
  public void onOpen(Player player, String[] args) {
    if (args.length == 0) {
      getGuiManager().openGUI(new RostersGUI(getUtilManager(), getGuiManager()), player);
    } else if (args.length == 1) {
      String teamName = args[0].toUpperCase();

      if (!getHelper().groupExists(teamName.toLowerCase())) {
        getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      getGuiManager().setTeamName(teamName);
      getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), getGuiManager()), player);
    } else getLogger().send(player, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @CatchUnknown
  @HelpCommand
  @CommandPermission("leaguemanager.command.rosters.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("create")
  @CommandCompletion("b")
  @CommandPermission("leaguemanager.command.rosters.create")
  public void onCreate(CommandSender sender, String[] args) {
    if (!(args.length >= 2 && args.length <= 3)) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String name = args[0].toLowerCase(), tag = args[1];
    if (getHelper().groupExists(name)) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    getHelper().getGroupManager().createAndLoadGroup(name).thenApplyAsync(group -> {
      if (args.length == 2) {
        group.data().add(WeightNode.builder(100).withContext("server", "football").build());
        group.data().add(MetaNode.builder("team", tag).withContext("server", "football").build());

        String team = name.toUpperCase(), type = "main";
        if (!getDataManager().configExists(getTeamData(), type)) {
          getDataManager().createNewFile(type, null);
          getLogger().info("Creating team data file for &e" + type);
          getLogger().send(sender, Lang.ROSTERS_FILE_NOT_FOUND.getConfigValue(new String[]{type}));
        }

        getDataManager().setConfig(getTeamData(), type);
        getDataManager().getConfig(type).set(team + ".name", name.toUpperCase());
        getDataManager().getConfig(type).set(team + ".tag", tag);
        List<String> players = new ArrayList<>();
        getDataManager().getConfig(type).set(team + ".players", players);
        getDataManager().saveConfig(type);
      } else if (args[2].equalsIgnoreCase("b")) {
        group.data().add(WeightNode.builder(99).withContext("server", "football").build());
        group.data().add(MetaNode.builder("b", tag).withContext("server", "football").build());

        String team = name.toUpperCase(), type = "juniors";
        if (!getDataManager().configExists(getTeamData(), type)) {
          getDataManager().createNewFile(type, null);
          getLogger().info("Creating data file for &e" + type);
          getLogger().send(sender, Lang.ROSTERS_FILE_NOT_FOUND.getConfigValue(new String[]{type}));
        }

        getDataManager().setConfig(getTeamData(), type);
        getDataManager().getConfig(type).set(team + ".name", team);
        getDataManager().getConfig(type).set(team + ".tag", tag);
        List<String> players = new ArrayList<>();
        getDataManager().getConfig(type).set(team + ".players", players);
        getDataManager().saveConfig(type);
      } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));

      for (String permission : getHelper().getPermissions()) {
        permission = permission.replace("%team%", name.toLowerCase());
        group.data().add(PermissionNode.builder(permission).withContext("server", "football").build());
      }

      getLogger().send("fcfa", Lang.ROSTERS_TEAM_CREATED.getConfigValue(new String[]{tag}));
      return group;
    }).thenCompose(getHelper().getGroupManager()::saveGroup);
  }

  @Subcommand("delete")
  @CommandPermission("leaguemanager.command.rosters.delete")
  public void onDelete(CommandSender sender, String[] args) {
    if (args.length != 1) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String name = args[0].toUpperCase(), type = null;
    if (getHelper().getGroupManager().isLoaded(name)) {
      type = getHelper().groupGetMetaWeight(name) == 100 ? "main" :
          getHelper().groupGetMetaWeight(name) == 99 ? "juniors" : null;

      getHelper().getGroupManager().deleteGroup(getHelper().getGroup(name));
      getLogger().send("fcfa", Lang.ROSTERS_TEAM_DELETED.getConfigValue(new String[]{sender.getName(), name}));
    } else getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));

    if (!getDataManager().configExists(getTeamData(), type)) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    if (type == null) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    getDataManager().setConfig(getTeamData(), type);
    if (getDataManager().getConfig(type).get(name) != null) {
      getDataManager().getConfig(type).set(name, null);
      getDataManager().saveConfig(type);
      getLogger().send("fcfa", Lang.ROSTERS_DELETED_FILES.getConfigValue(new String[]{name.toUpperCase()}));
    } else getLogger().send("fcfa", Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
  }

  @Subcommand("add")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (args.length < 2) {
      getLogger().send(sender, Lang.ROSTERS_ADD_USAGE.getConfigValue(null));
      return;
    }

    String team = args[0].toUpperCase(), type;
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
      return;
    }

    if (!getHelper().groupExists(team)) {
      getLogger().send(sender, Lang.ROSTERS_USER_NOT_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    if (getHelper().playerInGroup(target.getUniqueId(), team)) {
      getLogger().send(sender, Lang.ROSTERS_USER_ALREADY_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    if (!getDataManager().configExists(getPlayerData(), target.getUniqueId().toString())) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
      return;
    }

    Player player = (Player) sender;

    if (hasAccess(player) || isManager(player, team)) {
      getDataManager().setConfig(getPlayerData(), target.getUniqueId().toString());
      getDataManager().getConfig(target.getUniqueId().toString()).set("team", team.toUpperCase());
      getDataManager().saveConfig(target.getUniqueId().toString());

      type = getHelper().groupGetMetaWeight(team) == 100 ? "main" :
          getHelper().groupGetMetaWeight(team) == 99 ? "juniors" : null;

      if (!getDataManager().configExists(getTeamData(), type)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      if (type == null) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      getDataManager().setConfig(getTeamData(), type);
      List<String> players = getDataManager().getConfig(type).getStringList(team + ".players");
      players.add(target.getName());
      getDataManager().getConfig(type).set(team + ".players", players);
      getDataManager().saveConfig(type);

      if (getHelper().groupGetMetaWeight(team) == 100) {
        getHelper().playerRemoveTeams(target.getUniqueId());
      }

      getHelper().playerAddGroup(target.getUniqueId(), team, "football");
      getLogger().send("fcfa", Lang.ROSTERS_USER_ADDED.getConfigValue(new String[]{sender.getName(), target.getName(), team.toUpperCase()}));
    } else getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
  }

  @Subcommand("remove")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (args.length < 2) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String team = args[0].toUpperCase(), type;
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
      return;
    }

    if (!getHelper().groupExists(team)) {
      getLogger().send(sender, Lang.ROSTERS_USER_NOT_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    getDataManager().setConfig(getPlayerData(), target.getName());
    Player player = (Player) sender;

    if (hasAccess(player) || isManager(player, team)) {
      getDataManager().getConfig(target.getUniqueId().toString()).set("team", null);
      getDataManager().saveConfig(target.getUniqueId().toString());

      type = getHelper().groupGetMetaWeight(team) == 100 ? "main" :
          getHelper().groupGetMetaWeight(team) == 99 ? "juniors" : null;

      getDataManager().setConfig(getTeamData(), type);
      List<String> players = getDataManager().getConfig(type).getStringList(team + ".players");
      players.remove(target.getName());
      getDataManager().getConfig(type).set(team + ".players", players);
      getDataManager().saveConfig(type);

      getHelper().playerRemoveGroup(target.getUniqueId(), team, "football");
      getLogger().send("fcfa", Lang.ROSTERS_USER_REMOVED.getConfigValue(new String[]{sender.getName(), target.getName(), team.toUpperCase()}));
    } else getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
  }

  @Subcommand("set")
  @CommandCompletion("@players|name|tag|country|number|position|contract")
  @CommandPermission("leaguemanager.command.rosters.set")
  public void onSet(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
      return;
    }

    String team = args[0].toUpperCase(), arg = args[1].toLowerCase();

    if (getHelper().groupExists(team)) {
      String type = getHelper().groupGetMetaWeight(team) == 100 ? "main" :
          getHelper().groupGetMetaWeight(team) == 99 ? "juniors" : null;

      if (!getDataManager().configExists(getTeamData(), type)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      if (type == null) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }

      String[] arguments = {"name","tag"};
      if (Arrays.stream(arguments).noneMatch(arg::contains)) {
        getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
        return;
      }

      if (!args[1].equalsIgnoreCase("name") && args.length > 3) {
        getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
        return;
      }

      String name = StringUtils.join(args, ' ', 2, args.length), tag = args[2];
      getDataManager().setConfig(getTeamData(), type);
      Player player = (Player) sender;

      if (hasAccess(player) || isManager(player, team)) {
        getDataManager().getConfig(type).set(team.toUpperCase() + "." + arg, arg.equalsIgnoreCase("name") ? name : tag);
        getDataManager().saveConfig(type);
        getLogger().send(sender, Lang.ROSTERS_SET.getConfigValue(new String[]{sender.getName(), args[1].toUpperCase(), "tim", team.toUpperCase(), name}));
      } else getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    } else {
      Player target = Bukkit.getPlayer(args[0]);
      String[] arguments = {"country", "number", "position", "contract"};
      String value = args[2];

      if (Arrays.stream(arguments).noneMatch(arg::contains)) {
        getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
        return;
      }

      if (!getHelper().playerGetMeta(target.getUniqueId(), "team").equals("&8&oNema")) {
        team = getHelper().playerGetTeam(target.getUniqueId(), 100);
      } else if (!getHelper().playerGetMeta(target.getUniqueId(), "b").equals("&r")) {
        team = getHelper().playerGetTeam(target.getUniqueId(), 99);
      }

      UUID playerUUID = target.getUniqueId();

      getDataManager().setConfig(getPlayerData(), playerUUID.toString());
      Player player = (Player) sender;

      if (hasAccess(player) || isManager(player, team)) {
        if (!args[1].equalsIgnoreCase("position") && args.length > 3) {
          getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
          return;
        }

        switch (args[1].toLowerCase()) {
          case "country":
            getDataManager().getConfig(playerUUID.toString()).set("country", String.valueOf(args[2]));
            break;
          case "number":
            getDataManager().getConfig(playerUUID.toString()).set("number", Integer.valueOf(args[2]));
            break;
          case "contract":
            getDataManager().getConfig(playerUUID.toString()).set("contract", Integer.valueOf(args[2]));
            break;
          case "position":
            String position = StringUtils.join(args, ' ', 2, args.length);
            getDataManager().getConfig(playerUUID.toString()).set("position", position);
            break;
          default:
            getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
            break;
        }

        getDataManager().saveConfig(playerUUID.toString());
        getLogger().send(sender, Lang.ROSTERS_SET.getConfigValue(new String[]{sender.getName(), args[1].toUpperCase(), "igrača", target.getName(), value}));
      } else getLogger().send(sender, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
    }
  }

  @Subcommand("createbanner|banner|cb")
  @CommandPermission("leaguemanager.command.rosters.createbanner")
  public void onCreateBanner(Player player, String[] args) {
    if (args.length != 1) {
      getLogger().send(player, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String team = args[0].toLowerCase();
    if (!getHelper().groupExists(team)) {
      getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    if (!player.getInventory().getItemInHand().getType().equals(Material.BANNER)) {
      getLogger().send(player, Lang.ROSTERS_NOT_BANNER.getConfigValue(null));
      return;
    }

    String type = getHelper().groupGetMetaWeight(team) == 100 ? "main" :
        getHelper().groupGetMetaWeight(team) == 99 ? "juniors" : null;

    getDataManager().setConfig(getTeamData(), type);
    if (hasAccess(player) || isManager(player, team)) {
      ItemStack banner = player.getInventory().getItemInHand();
      ItemMeta bannerMeta = banner.getItemMeta();

      bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
      banner.setItemMeta(bannerMeta);
      banner.setAmount(1);

      getDataManager().getConfig(type).set(team + ".banner", banner);
      getDataManager().saveConfig(type);
      getLogger().send(player, Lang.ROSTERS_BANNER_SET.getConfigValue(new String[]{team.toUpperCase()}));
    } else getLogger().send(player, Lang.INSUFFICIENT_PERMISSION.getConfigValue(null));
  }

  private boolean hasAccess(Player player) {
    return getHelper().playerInGroup(player.getUniqueId(), "fcfa");
  }

  private boolean isManager(Player player, String team) {
    return getHelper().playerHasPermission(player.getUniqueId(), "tab.group." + team.toLowerCase() + "-director");
  }
}
