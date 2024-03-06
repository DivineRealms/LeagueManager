package io.github.divinerealms.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.configs.Lang;
import io.github.divinerealms.gui.impl.PerRosterGUI;
import io.github.divinerealms.gui.impl.RostersGUI;
import io.github.divinerealms.managers.GUIManager;
import io.github.divinerealms.managers.RostersDataManager;
import io.github.divinerealms.managers.UtilManager;
import io.github.divinerealms.utils.Helper;
import io.github.divinerealms.utils.Logger;
import lombok.Getter;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.WeightNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@Getter
@CommandAlias("rosters|rt")
@CommandPermission("leaguemanager.command.rosters")
public class RostersCommand extends BaseCommand {
  private final UtilManager utilManager;
  private final GUIManager guiManager;
  private final Logger logger;
  private final Helper helper;
  private final RostersDataManager dataManager;

  public RostersCommand(final UtilManager utilManager, final GUIManager guiManager) {
    this.utilManager = utilManager;
    this.guiManager = guiManager;
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
    this.dataManager = new RostersDataManager(utilManager.getPlugin());
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
        String type = "main";
        if (getDataManager().configExists(type)) {
          getDataManager().setConfig(type);
          FileConfiguration mainTeams = getDataManager().getConfig(type);
          mainTeams.set(name.toUpperCase() + ".tag", tag);
          getDataManager().saveConfig(type);
        } else {
          getDataManager().createNewFile(type, "Created config file for type " + name.toUpperCase());
          getLogger().send(sender, Lang.ROSTERS_FILE_NOT_FOUND.getConfigValue(new String[]{type.toUpperCase()}));
        }
      } else if (args[2].equalsIgnoreCase("b")) {
        group.data().add(WeightNode.builder(99).withContext("server", "football").build());
        group.data().add(MetaNode.builder("b", tag).withContext("server", "football").build());
        String type = "juniors";
        if (getDataManager().configExists(type)) {
          getDataManager().setConfig(type);
          FileConfiguration juniorTeams = getDataManager().getConfig(type);
          juniorTeams.set(name.toUpperCase() + ".tag", tag);
          getDataManager().saveConfig(type);
        } else {
          getDataManager().createNewFile(type, "Created config file for type " + name.toUpperCase());
          getLogger().send(sender, Lang.ROSTERS_FILE_NOT_FOUND.getConfigValue(new String[]{type.toUpperCase()}));
        }
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
  @CommandCompletion("main|juniors")
  @CommandPermission("leaguemanager.command.rosters.delete")
  public void onDelete(CommandSender sender, String[] args) {
    if (args.length != 2) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String name = args[0].toLowerCase(), type = args[1].toLowerCase();
    if (getHelper().getGroupManager().isLoaded(name)) {
      getHelper().getGroupManager().deleteGroup(getHelper().getGroup(name));
      getLogger().send("fcfa", Lang.ROSTERS_TEAM_DELETED.getConfigValue(new String[]{sender.getName(), name.toUpperCase()}));
    } else getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
    if (type.equalsIgnoreCase("main") || type.equalsIgnoreCase("juniors")) {
      if (!getDataManager().configExists(type)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
        return;
      }
      getDataManager().setConfig(type);
      FileConfiguration teamConfigs = getDataManager().getConfig(type);
      if (teamConfigs.get(name.toUpperCase()) != null) {
        teamConfigs.set(name.toUpperCase(), null);
        getDataManager().saveConfig(type);
        getLogger().send("fcfa", Lang.ROSTERS_DELETED_FILES.getConfigValue(new String[]{name.toUpperCase()}));
      } else getLogger().send("fcfa", Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
    } else getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
  }

  @Subcommand("add")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (args.length < 2) {
      getLogger().send(sender, Lang.ROSTERS_ADD_USAGE.getConfigValue(null));
      return;
    }

    String teamName = args[0].toLowerCase(), type = null;
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
      return;
    }

    if (!getHelper().groupExists(teamName)) {
      getLogger().send(sender, Lang.ROSTERS_USER_NOT_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    if (getHelper().groupHasMeta(teamName, "team")) type = "main";
    else if (getHelper().groupHasMeta(teamName, "b")) type = "juniors";

    if (type == null) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    if (getHelper().playerInGroup(target.getUniqueId(), teamName)) {
      getLogger().send(sender, Lang.ROSTERS_USER_ALREADY_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    if (getDataManager().configExists(type)) {
      getDataManager().setConfig(type);
      FileConfiguration config = getDataManager().getConfig(type);
      ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
      SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
      skullMeta.setOwner(target.getName());
      skull.setItemMeta(skullMeta);
      config.set(teamName.toUpperCase() + ".players." + target.getName() + ".head", skull);
      getDataManager().saveConfig(type);
      if (type.equals("main")) getHelper().playerRemoveTeams(target.getUniqueId());
      getHelper().playerAddGroup(target.getUniqueId(), teamName);
      getLogger().send("fcfa", Lang.ROSTERS_USER_ADDED.getConfigValue(new String[]{sender.getName(), target.getName(), teamName.toUpperCase()}));
    } else {
      getDataManager().createNewFile(target.getName(), "Created config file for player " + target.getName());
      getLogger().send(sender, Lang.ROSTERS_FILE_NOT_FOUND.getConfigValue(new String[]{target.getName()}));
    }
  }

  @Subcommand("remove")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (args.length < 2) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String teamName = args[0].toLowerCase(), type = null;
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
      return;
    }

    if (!getHelper().groupExists(teamName)) {
      getLogger().send(sender, Lang.ROSTERS_USER_NOT_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    if (getHelper().groupHasMeta(teamName, "team")) type = "main";
    else if (getHelper().groupHasMeta(teamName, "b")) type = "juniors";

    if (type == null) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    if (getDataManager().configExists(type)) {
      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      teamConfig.set(teamName.toUpperCase() + ".players." + target.getName(), null);
      getDataManager().saveConfig(type);
      getLogger().send("fcfa", Lang.ROSTERS_USER_REMOVED.getConfigValue(new String[]{sender.getName(), target.getName(), teamName.toUpperCase()}));
    } else getLogger().send("fcfa", Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
  }

  @Subcommand("set")
  @CommandCompletion("@players|name|tag|country|number|position|contract")
  @CommandPermission("leaguemanager.command.rosters.set")
  public void onSet(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
      return;
    }

    String team = args[0].toLowerCase(), type = null, arg = args[1].toLowerCase();

    if (getHelper().groupExists(team)) {
      if (getHelper().groupHasMeta(team, "team")) type = "main";
      else if (getHelper().groupHasMeta(team, "b")) type = "juniors";

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
      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      teamConfig.set(team.toUpperCase() + "." + arg, arg.equalsIgnoreCase("name") ? name : tag);
      getDataManager().saveConfig(type);
      getLogger().send("fcfa", Lang.ROSTERS_SET.getConfigValue(new String[]{sender.getName(), args[1].toUpperCase(), "tim", team.toUpperCase(), name}));
    } else {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String[] arguments = {"country","number","position","contract"};
      String value = args[2];
      if (Arrays.stream(arguments).noneMatch(arg::contains)) {
        getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
        return;
      }

      if (!getHelper().playerGetMeta(target.getUniqueId(), "team").equals("&8&oNema")) {
        type = "main";
        team = getHelper().playerGetTeam(target.getUniqueId(), 100);
      } else if (!getHelper().playerGetMeta(target.getUniqueId(), "b").equals("&r")) {
        type = "juniors";
        team = getHelper().playerGetTeam(target.getUniqueId(), 99);
      }

      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);

      if (!args[1].equalsIgnoreCase("position") && args.length > 3) {
        getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
        return;
      }

      switch (args[1].toLowerCase()) {
        case "country":
          teamConfig.set(team.toUpperCase() + ".players." + target.getName() + ".country", String.valueOf(args[2]));
          break;
        case "number":
          teamConfig.set(team.toUpperCase() + ".players." + target.getName() + ".number", Integer.valueOf(args[2]));
          break;
        case "contract":
          teamConfig.set(team.toUpperCase() + ".players." + target.getName() + ".contract", Integer.valueOf(args[2]));
          break;
        case "position":
          String position = StringUtils.join(args, ' ', 2, args.length);
          teamConfig.set(team.toUpperCase() + ".players." + target.getName() + ".position", position);
          break;
        default:
          getLogger().send(sender, Lang.ROSTERS_SET_USAGE.getConfigValue(null));
          break;
      }
      getDataManager().saveConfig(type);
      getLogger().send(sender, Lang.ROSTERS_SET.getConfigValue(new String[]{sender.getName(), args[1].toUpperCase(), "igrača", target.getName(), value}));
    }
  }

  @Subcommand("setposition")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.setposition")
  public void onSetPosition(CommandSender sender, String[] args) {
    if (args.length != 3) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String teamName = args[0], position = args[2];
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"igrač"}));
      return;
    }

    if (!getHelper().groupExists(teamName)) {
      getLogger().send(sender, Lang.ROSTERS_USER_NOT_IN_TEAM.getConfigValue(new String[]{target.getName()}));
      return;
    }

    String type = null;
    if (getHelper().groupHasMeta(teamName, "team")) type = "main";
    else if (getHelper().groupHasMeta(teamName, "b")) type = "juniors";

    if (type == null) {
      getLogger().send(sender, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    getDataManager().setConfig(type);
    FileConfiguration teamConfig = getDataManager().getConfig(type);
    teamConfig.set(teamName.toUpperCase() + ".players." + target.getName() + ".position", position.toUpperCase());
    getDataManager().saveConfig(type);
    getLogger().send("fcfa", Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{target.getName(), position.toUpperCase(), teamName.toUpperCase()}));
  }

  @Subcommand("createbanner|banner|cb")
  @CommandPermission("leaguemanager.command.rosters.createbanner")
  public void onCreateBanner(Player player, String[] args) {
    if (args.length != 1) {
      getLogger().send(player, Lang.UNKNOWN_COMMAND.getConfigValue(null));
      return;
    }

    String name = args[0].toLowerCase(), type = null;
    if (!getHelper().groupExists(name)) {
      getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    if (getHelper().groupHasMeta(name, "team")) type = "main";
    else if (getHelper().groupHasMeta(name, "b")) type = "juniors";

    if (type == null) {
      getLogger().send(player, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{"tim"}));
      return;
    }

    if (!player.getInventory().getItemInHand().getType().equals(Material.BANNER)) {
      getLogger().send(player, Lang.ROSTERS_NOT_BANNER.getConfigValue(null));
      return;
    }

    getDataManager().setConfig(type);
    FileConfiguration teamConfig = getDataManager().getConfig(type);
    ItemStack banner = player.getInventory().getItemInHand();
    ItemMeta bannerMeta = banner.getItemMeta();

    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
    banner.setItemMeta(bannerMeta);
    banner.setAmount(1);

    teamConfig.set(name.toUpperCase() + ".banner", banner);
    getDataManager().saveConfig(type);
    getLogger().send(player, Lang.ROSTERS_BANNER_SET.getConfigValue(new String[]{name.toUpperCase()}));
  }
}
