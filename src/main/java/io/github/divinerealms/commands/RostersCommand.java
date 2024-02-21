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
        getLogger().send(player, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{teamName}));
        return;
      }

      getGuiManager().openGUI(new PerRosterGUI(getUtilManager(), teamName, getGuiManager()), player);
    } else getLogger().send(player, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("leaguemanager.command.rosters.help")
  public void onHelp(CommandSender sender) {
    getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("create")
  @CommandCompletion("b")
  @CommandPermission("leaguemanager.command.rosters.create")
  public void onCreate(CommandSender sender, String[] args) {
    if (!(args.length >= 2 && args.length <= 3)) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    String name = args[0].toLowerCase(), tag = args[1];
    if (getHelper().groupExists(name)) {
      getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
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
      } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));

      for (String permission : getHelper().getPermissions()) {
        permission = permission.replace("%team%", name.toLowerCase());
        group.data().add(PermissionNode.builder(permission).withContext("server", "football").build());
      }

      getLogger().send("fcfa", Lang.TEAM_CREATED.getConfigValue(new String[]{tag}));
      return group;
    }).thenCompose(getHelper().getGroupManager()::saveGroup);
  }

  @Subcommand("delete")
  @CommandCompletion("main|juniors")
  @CommandPermission("leaguemanager.command.rosters.delete")
  public void onDelete(CommandSender sender, String[] args) {
    if (args.length != 2) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    String name = args[0].toLowerCase(), type = args[1].toLowerCase();
    if (getHelper().getGroupManager().isLoaded(name)) {
      getHelper().getGroupManager().deleteGroup(getHelper().getGroup(name));
      getLogger().send(sender, Lang.TEAM_DELETED.getConfigValue(new String[]{name.toUpperCase()}));
    } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
    if (type.equalsIgnoreCase("main") || type.equalsIgnoreCase("juniors")) {
      if (!getDataManager().configExists(type)) {
        getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
        return;
      }
      getDataManager().setConfig(type);
      FileConfiguration teamConfigs = getDataManager().getConfig(type);
      if (teamConfigs.get(name.toUpperCase()) != null) {
        teamConfigs.set(name.toUpperCase(), null);
        getDataManager().saveConfig(type);
        getLogger().send(sender, Lang.ROSTERS_DELETED_FILES.getConfigValue(new String[]{name.toUpperCase()}));
      } else getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("set")
  @CommandCompletion("name|tag")
  @CommandPermission("leaguemanager.command.rosters.set")
  public void onSet(CommandSender sender, String[] args) {
    if (args.length < 2) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    String team = args[0].toLowerCase(), type = null;
    if (getHelper().groupHasMeta(team, "team")) type = "main";
    else if (getHelper().groupHasMeta(team, "b")) type = "juniors";

    if (type == null) {
      getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{team.toUpperCase()}));
      return;
    }

    if (!getHelper().groupExists(team)) {
      getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{team.toUpperCase()}));
      return;
    }

    if (args[1].equalsIgnoreCase("name")) {
      String name = StringUtils.join(args, ' ', 2, args.length);
      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      teamConfig.set(team.toUpperCase() + ".name", name);
      getDataManager().saveConfig(type);
      getLogger().send(sender, Lang.ROSTERS_SET.getConfigValue(new String[]{args[1].toUpperCase(),team.toUpperCase(),name}));
    } else if (args[1].equalsIgnoreCase("tag")) {
      String tag = args[2];
      getDataManager().setConfig(type);
      FileConfiguration teamConfig = getDataManager().getConfig(type);
      teamConfig.set(team.toUpperCase() + ".tag", tag);
      getDataManager().saveConfig(type);
      getLogger().send(sender, Lang.ROSTERS_SET.getConfigValue(new String[]{args[1].toUpperCase(),team.toUpperCase(),tag}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("add")
  @CommandCompletion("@players|GK|CB|CM|ST")
  @CommandPermission("leaguemanager.command.rosters.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (args.length != 3) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    String name = args[0].toLowerCase(), position = args[2], type = null;
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(new String[]{args[0]}));
      return;
    }

    if (!getHelper().groupExists(name)) {
      getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
      return;
    }

    if (args[0].equalsIgnoreCase(name)) {
      if (args[1].equalsIgnoreCase(target.getName())) {
        if (getHelper().groupHasMeta(name, "team")) type = "main";
        else if (getHelper().groupHasMeta(name, "b")) type = "juniors";

        if (type == null) {
          getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
          return;
        }

        if (!getHelper().playerInGroup(target.getUniqueId(), name)) {
          getDataManager().setConfig(type);
          FileConfiguration teamConfig = getDataManager().getConfig(type);
          if (type.equals("main")) getHelper().playerRemoveTeams(target.getUniqueId());
          getHelper().playerAddGroup(target.getUniqueId(), name);
          teamConfig.set(name.toUpperCase() + ".players." + target.getName() + ".position", position.toUpperCase());
          ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
          SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
          skullMeta.setOwner(target.getName());
          skull.setItemMeta(skullMeta);
          teamConfig.set(name.toUpperCase() + ".players." + target.getName() + ".head", skull);
          getDataManager().saveConfig(name);
          getLogger().send(sender, Lang.USER_ADDED_TO_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
        } else getLogger().send(sender, Lang.USER_ALREADY_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
      } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("setrole")
  @CommandCompletion("@players|manager|captain|player")
  @CommandPermission("leaguemanager.command.rosters.setrole")
  public void onSetRole(CommandSender sender, String[] args) {
    if (args.length != 2) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
    String role = args[1].toLowerCase(), type;
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(new String[]{args[0]}));
      return;
    }

    String[] roles = {"manager","captain","player"};
    if (Arrays.stream(roles).noneMatch(role::contains)) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    if (getHelper().playerHasMeta(target.getUniqueId(), "b")) type = "juniors";
    else type = "main";

    String name = type.equals("juniors") ? getHelper().playerGetTeam(target.getUniqueId(), 99) : getHelper().playerGetTeam(target.getUniqueId(), 100);

    if (name == null) {
      getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), "that"}));
      return;
    }

    getDataManager().setConfig(type);
    FileConfiguration teamConfig = getDataManager().getConfig(type);
    if (!role.equalsIgnoreCase("player")) {
      teamConfig.set(name.toUpperCase() + "." + role.toLowerCase(), target.getName());
      getDataManager().saveConfig(type);
      getLogger().send(sender, Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{target.getName(), role.toUpperCase(), name.toUpperCase()}));
    } else {
      if (teamConfig.getString(name.toUpperCase() + ".manager") != null ||
          teamConfig.getString(name.toUpperCase() + ".captain") != null) {
        if (teamConfig.getString(name.toUpperCase() + ".manager").equals(target.getName())) {
          teamConfig.set(name.toUpperCase() + ".manager", null);
          getHelper().playerRemovePermission(target.getUniqueId(), "tab.group." + name + "-director");
          getHelper().playerRemoveGroup(target.getUniqueId(), "director");
        } else if (teamConfig.getString(name.toUpperCase() + ".captain").equals(target.getName()))
          teamConfig.set(name.toUpperCase() + ".captain", null);
        getDataManager().saveConfig(type);
        getLogger().send(sender, Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{target.getName(), role.toUpperCase(), name.toUpperCase()}));
      }
    }

    if (role.equalsIgnoreCase("manager")) {
      getHelper().playerAddPermission(target.getUniqueId(), "tab.group." + name + "-director");
      getHelper().playerAddGroup(target.getUniqueId(), "director");
    }
  }

  @Subcommand("setposition")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.setposition")
  public void onSetPosition(CommandSender sender, String[] args) {
    if (args.length != 2) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
    String position = args[1], type = null;
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(new String[]{args[0]}));
      return;
    }

    if (getHelper().playerHasMeta(target.getUniqueId(), "team")) type = "main";
    else if (getHelper().playerHasMeta(target.getUniqueId(), "b")) type = "juniors";

    if (type == null) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    String name = type.equals("juniors") ? getHelper().playerGetTeam(target.getUniqueId(), 99) : getHelper().playerGetTeam(target.getUniqueId(), 100);
    if (name == null) {
      getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), "that"}));
      return;
    }

    getDataManager().setConfig(type);
    FileConfiguration teamConfig = getDataManager().getConfig(type);
    teamConfig.set(name.toUpperCase() + ".players." + target.getName() + ".position", position.toUpperCase());
    getDataManager().saveConfig(type);
    getLogger().send(sender, Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{target.getName(), position.toUpperCase(), name.toUpperCase()}));
  }

  @Subcommand("remove")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (args.length != 2) {
      getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }
    String name = args[0].toLowerCase(), type = null;
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if (target == null || !target.hasPlayedBefore()) {
      getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(new String[]{args[0]}));
      return;
    }

    if (!getHelper().groupExists(name)) {
      getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
      return;
    }

    if (args[0].equalsIgnoreCase(name)) {
      if (args[1].equalsIgnoreCase(target.getName())) {
        if (!getHelper().playerInGroup(target.getUniqueId(), name)) {
          getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
          return;
        }

        getHelper().playerRemoveGroup(target.getUniqueId(), name);
        if (getHelper().groupHasMeta(name, "team")) type = "main";
        else if (getHelper().groupHasMeta(name, "b")) type = "juniors";

        if (type == null) {
          getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
          return;
        }

        getDataManager().setConfig(type);
        FileConfiguration teamConfig = getDataManager().getConfig(type);
        teamConfig.set(name.toUpperCase() + ".players." + target.getName(), null);
        getDataManager().saveConfig(type);
        getLogger().send(sender, Lang.USER_REMOVED_FROM_A_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
      } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("createbanner")
  @CommandPermission("leaguemanager.command.rosters.createbanner")
  public void onCreateBanner(Player player, String[] args) {
    if (args.length != 1) {
      getLogger().send(player, Lang.ROSTERS_HELP.getConfigValue(null));
      return;
    }

    String name = args[0].toLowerCase(), type = null;
    if (!getHelper().groupExists(name)) {
      getLogger().send(player, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
      return;
    }

    if (getHelper().groupHasMeta(name, "team")) type = "main";
    else if (getHelper().groupHasMeta(name, "b")) type = "juniors";

    if (type == null) {
      getLogger().send(player, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
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
