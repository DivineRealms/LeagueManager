package io.github.divinerealms.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.configs.Lang;
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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
  public void onOpen(Player player) {
    getGuiManager().openGUI(new RostersGUI(getUtilManager()), player);
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
    if (args.length >= 2 && args.length <= 3) {
      String name = args[0].toLowerCase(), tag = args[1];
      if (!getHelper().groupExists(name)) {
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
      } else getLogger().send(sender, Lang.TEAM_ALREADY_DEFINED.getConfigValue(new String[]{name.toUpperCase()}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("delete")
  @CommandCompletion("main|juniors")
  @CommandPermission("leaguemanager.command.rosters.delete")
  public void onDelete(CommandSender sender, String[] args) {
    if (args.length == 2) {
      String name = args[0].toLowerCase(), type = args[1].toLowerCase();
      if (getHelper().getGroupManager().isLoaded(name)) {
        getHelper().getGroupManager().deleteGroup(getHelper().getGroup(name));
        getLogger().send(sender, Lang.TEAM_DELETED.getConfigValue(new String[]{name.toUpperCase()}));
      } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
      if (type.equalsIgnoreCase("main") || type.equalsIgnoreCase("juniors")) {
        if (getDataManager().configExists(type)) {
          getDataManager().setConfig(type);
          FileConfiguration teamConfigs = getDataManager().getConfig(type);
          if (teamConfigs.get(name.toUpperCase()) != null) {
            teamConfigs.set(name.toUpperCase(), null);
            getDataManager().saveConfig(type);
            getLogger().send(sender, Lang.ROSTERS_DELETED_FILES.getConfigValue(new String[]{name.toUpperCase()}));
          } else getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
        } else getLogger().send(sender, Lang.ROSTERS_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
      } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("add")
  @CommandCompletion("@players|GK|CB|CM|ST")
  @CommandPermission("leaguemanager.command.rosters.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (args.length == 3) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
      String name = args[0].toLowerCase(), position = args[2], type = null;
      if (target != null && target.hasPlayedBefore()) {
        if (args[1].equalsIgnoreCase(target.getName())) {
          if (args[0].equalsIgnoreCase(name) && getHelper().groupExists(name)) {
            if (getHelper().groupHasMeta(name, "team")) type = "main";
            else if (getHelper().groupHasMeta(name, "b")) type = "juniors";
            if (type != null) {
              if (!getHelper().playerInGroup(target.getUniqueId(), name)) {
                String[] positions = {"GK","CB","CM","ST","CDM"};
                if (Arrays.stream(positions).anyMatch(position::contains)) {
                  getDataManager().setConfig(type);
                  FileConfiguration teamConfig = getDataManager().getConfig(type);
                  if (type.equals("main")) getHelper().playerRemoveTeams(target.getUniqueId());
                  getHelper().playerAddGroup(target.getUniqueId(), name);
                  teamConfig.set(name.toUpperCase() + ".players." + target.getName(), position.toUpperCase());
                  getDataManager().saveConfig(name);
                  getLogger().send(sender, Lang.USER_ADDED_TO_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
                } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
              } else getLogger().send(sender, Lang.USER_ALREADY_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
            } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
          } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
        } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(new String[]{args[0]}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("setrole")
  @CommandCompletion("@players|manager|captain|player")
  @CommandPermission("leaguemanager.command.rosters.setrole")
  public void onSetRole(CommandSender sender, String[] args) {
    if (args.length == 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String role = args[1], type = null;
      if (target != null && target.hasPlayedBefore()) {
        if (role.equalsIgnoreCase("manager") || role.equalsIgnoreCase("captain") || role.equalsIgnoreCase("player")) {
          if (getHelper().playerHasMeta(target.getUniqueId(), "team")) type = "main";
          else if (getHelper().playerHasMeta(target.getUniqueId(), "b")) type = "juniors";
          if (type != null) {
            String name = getHelper().playerGetTeam(target.getUniqueId());
            if (name != null) {
              getDataManager().setConfig(type);
              FileConfiguration teamConfig = getDataManager().getConfig(type);
              if (!role.equalsIgnoreCase("player")) {
                teamConfig.set(name.toUpperCase() + "." + role.toLowerCase(), target.getName());
                getDataManager().saveConfig(type);
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
                }
              }
              if (role.equalsIgnoreCase("manager")) {
                getHelper().playerAddPermission(target.getUniqueId(), "tab.group." + name + "-director");
                getHelper().playerAddGroup(target.getUniqueId(), "director");
              }
              getLogger().send(sender, Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{target.getName(), role.toUpperCase(), name.toUpperCase()}));
            } else
              getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), "that"}));
          } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
        } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("setposition")
  @CommandCompletion("@players|GK|CB|CM|ST")
  @CommandPermission("leaguemanager.command.rosters.setposition")
  public void onSetPosition(CommandSender sender, String[] args) {
    if (args.length == 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String position = args[1], type = null;
      if (target != null && target.hasPlayedBefore()) {
        if (getHelper().playerHasMeta(target.getUniqueId(), "team")) type = "main";
        else if (getHelper().playerHasMeta(target.getUniqueId(), "b")) type = "juniors";
        if (type != null) {
          String name = getHelper().playerGetTeam(target.getUniqueId());
          if (name != null) {
            String[] positions = {"GK","CB","CM","ST","CDM"};
            if (Arrays.stream(positions).anyMatch(position::contains)) {
              getDataManager().setConfig(type);
              FileConfiguration teamConfig = getDataManager().getConfig(type);
              teamConfig.set(name.toUpperCase() + ".players." + target.getName(), position);
              getDataManager().saveConfig(type);
              getLogger().send(sender, Lang.ROSTERS_SET_ROLE.getConfigValue(new String[]{target.getName(), position.toUpperCase(), name.toUpperCase()}));
            } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
          } else getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(), "that"}));
        } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("remove")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.rosters.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (args.length == 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
      String name = args[0].toLowerCase(), type = null;
      if (target != null && target.hasPlayedBefore()) {
        if (args[1].equalsIgnoreCase(target.getName())) {
          if (args[0].equalsIgnoreCase(name) && getHelper().groupExists(name)) {
            if (getHelper().playerInGroup(target.getUniqueId(), name)) {
              getHelper().playerRemoveGroup(target.getUniqueId(), name);
              if (getHelper().groupHasMeta(name, "team")) type = "main";
              else if (getHelper().groupHasMeta(name, "b")) type = "juniors";
              if (type != null) {
                getDataManager().setConfig(type);
                FileConfiguration teamConfig = getDataManager().getConfig(type);
                teamConfig.set(name.toUpperCase() + ".players." + target.getName(), null);
                getDataManager().saveConfig(type);
                getLogger().send(sender, Lang.USER_REMOVED_FROM_A_TEAM.getConfigValue(new String[]{target.getName(), name.toUpperCase()}));
              } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
            } else getLogger().send(sender, Lang.USER_NOT_IN_THAT_TEAM.getConfigValue(new String[]{target.getName(),name.toUpperCase()}));
          } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
        } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(new String[]{args[0]}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }

  @Subcommand("generate")
  @CommandPermission("leaguemanager.command.rosters.generate")
  public void onGenerate(CommandSender sender, String[] args) {
    if (args.length <= 2) {
      String name = args[0].toLowerCase();
      if (!getDataManager().configExists(name)) {
        if (getHelper().groupExists(name)) {
          if (args.length == 1) {
            getDataManager().createNewFile(name, "Created config for team " + name.toUpperCase());
            getDataManager().setConfig(name);
            FileConfiguration teamConfig = getDataManager().getConfig(name);
            teamConfig.set("name", name.toUpperCase());
            teamConfig.set("tag", getHelper().groupHasMeta(name, "team") ?
                getHelper().getGroupMeta(name, "team") : "/");
            teamConfig.set("type", "main");
            getDataManager().saveConfig(name);
          } else if (args[1].equalsIgnoreCase("b")) {
            getDataManager().createNewFile(name, "Created config for team " + name.toUpperCase());
            getDataManager().setConfig(name);
            FileConfiguration teamConfig = getDataManager().getConfig(name);
            teamConfig.set("name", name.toUpperCase());
            teamConfig.set("tag", getHelper().groupHasMeta(name, "b") ?
                getHelper().getGroupMeta(name, "b") : "/");
            teamConfig.set("type", "b");
            getDataManager().saveConfig(name);
          } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
          getLogger().send(sender, Lang.ROSTERS_GENERATED_FILES.getConfigValue(new String[]{name.toUpperCase()}));
        } else getLogger().send(sender, Lang.TEAM_NOT_FOUND.getConfigValue(new String[]{name.toUpperCase()}));
      } else getLogger().send(sender, Lang.TEAM_ALREADY_DEFINED.getConfigValue(new String[]{name.toUpperCase()}));
    } else getLogger().send(sender, Lang.ROSTERS_HELP.getConfigValue(null));
  }
}
