package io.github.divinerealms.configs;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {
  RELOAD("reload", "&a▎ &fPlugin reloaded!"),
  UNKNOWN_COMMAND("unknown-command", "&4▎ &cUnknown command."),
  INSUFFICIENT_PERMISSION("insufficient-permission", "&4▎ &cInsufficient permission."),
  INGAME_ONLY("ingame-only", "&4▎ &cThis command can be used only in game."),
  TOGGLE("toggle", "&a▎ &fFootCube toggled &e{0}&f."),
  HELP("help", String.join(System.getProperty("line.separator"),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rHelp command.",
      "&r &r",
      "&6 /&elm reload &7- &fReloads the plugin.",
      "&6 /&elm toggle &7- &fToggles FootCube.",
      "&r &r", "&e &lTeam Commands:&r",
      "&6 /&elm ct&6|&ecreateteam &2<&ateam-name&2> <&acolored-tag&2>",
      "&7   - &fCreate a team with the colored tag (ex cel and &4C&0E&4L&r).",
      "&7   - &fExample: &6/&elm ct nkp &0N&fK&0P",
      "&7   - &fExample 2: &6/&elm ct vksb &1F&fK&1B",
      "&6 /&elm dt&6|&edeleteteam &2<&ateam-name&2>",
      "&7   - &fDelete an existing team.",
      "&r &r", "&b &lUser Commands:&r",
      "&6 /&elm st&6|&esetteam &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fAdd player to a team (ex cel) with the branch.",
      "&7   - &fExample: &6/&elm st Neeonn nkp",
      "&7   - &fExample: &6/&elm st 1nvader vksb",
      "&6 /&elm ut&6|&eunsetteam &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fRemove player from a team.",
      "&6 /&elm ban &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]",
      "&7   - &fBan player from FC. Example: &6/&elm ban Neeonn 2h Rage Quit",
      "&6 /&elm unban &2<&aplayer-name&2>",
      "&7   - &fUnban player from FC.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  TEAM_CREATED("team.created", "&a▎ &fTeam {0} &fhas been successfully &2created&f!"),
  TEAM_DELETED("team.deleted", "&a▎ &fTeam {0} &fhas been successfully &8deleted&f!"),
  TEAM_NOT_FOUND("team.not-found", "&4▎ &cTeam {0} &cwasn't found!"),
  TEAM_ALREADY_DEFINED("team.already-defined", "&4▎ &cTeam {0} &calready exists!"),
  TEAM_USAGE_CREATE("team.usage.create", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm ct&6|&ecreateteam &2<&ateam-name&2> <&acolored-tag&2>")),
  TEAM_USAGE_DELETE("team.usage.delete", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm dt&6|&edeleteteam &2<&ateam-name&2>")),
  TEAM_HELP("team.help", String.join(System.getProperty("line.separator"),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rTeam commands.",
      "&r &r",
      "&6 /&elm ct&6|&ecreateteam &2<&ateam-name&2> <&acolored-tag&2>",
      "&7   - &fCreate a team with the colored tag (ex cel and &4C&0E&4L&r).",
      "&7   - &fExample: &6/&elm ct nkp &0N&fK&0P",
      "&7   - &fExample 2: &6/&elm ct vksb &1F&fK&1B",
      "&6 /&elm dt&6|&edeleteteam &2<&ateam-name&2>",
      "&7   - &fDelete an existing team.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  USER_NOT_FOUND("user.not-found", "&4▎ &cPlayer wasn't found."),
  USER_ADDED_TO_TEAM("user.added-to-team", "&a▎ &fPlayer &b{0} &fhas been successfully &aadded &fto team &e{1}&f!"),
  USER_REMOVED_FROM_A_TEAM("user.removed-from-a-team", "&a▎ &fPlayer &b{0} &fhas been successfully &cremoved &ffrom team &e{1}&f!"),
  USER_NOT_IN_THAT_TEAM("user.not-in-that-team", "&4▎ &cPlayer &b{0} &cis not in the &e{1} &cteam!"),
  USER_ALREADY_IN_THAT_TEAM("user.already-in-that-team", "&4▎ &cPlayer &b{0} &cis already in &e{1} &cteam!"),
  USER_BAN("user.ban", "&a▎ &fPlayer &b{0} &fhas been successfully banned for &e{1}&f for '&4{2}&f'!"),
  USER_BANNED("user.banned", "&4▎ &cYou have been banned from FC for &e{0} &cfor '&4{1}&c'!"),
  USER_UNBAN("user.unban", "&a▎ &fPlayer &b{0} &fhas been successfully unbanned!"),
  USER_UNBANNED("user.unbanned", "&8▎ &aYou've been unbanned from FC!"),
  USER_NOT_BANNED("user.not-banned", "&4▎ &cPlayer &b{0} &cis not banned from FC!"),
  USER_ALREADY_BANNED("user.already-banned", "&4▎ &cPlayer &b{0} &cis already banned from FC! Unban them first."),
  USER_USAGE_SET("user.usage.set", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm st&6|&esetteam &2<&aplayer-name&2> <&ateam-name&2>")),
  USER_USAGE_UNSET("user.usage.unset", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm ut&6|&eunsetteam &2<&aplayer-name&2> <&ateam-name&2>")),
  USER_USAGE_BAN("user.usage.ban", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm ban &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]")),
  USER_USAGE_UNBAN("user.usage.unban", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm unban &2<&aplayer-name&2>")),
  USER_HELP("user.help", String.join(System.getProperty("line.separator"),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rUser commands.",
      "&r &r",
      "&6 /&elm st&6|&esetteam &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fAdd player to a team (ex cel) with the branch.",
      "&7   - &fExample: &6/&elm st Neeonn nkp",
      "&7   - &fExample: &6/&elm st 1nvader vksb",
      "&6 /&elm ut&6|&eunsetteam &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fRemove player from a team.",
      "&6 /&elm ban &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]",
      "&7   - &fBan player from FC. Example: &6/&elm ban Neeonn 2h Rage Quit",
      "&6 /&elm unban &2<&aplayer-name&2>",
      "&7   - &fUnban player from FC.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  VAR_GIVEN_ACCESS("var.given-access", "&a▎ &b{0} &fsuccessfully &agot &faccess to VAR."),
  VAR_GIVEN_ACCESS_1("var.given-access-1", "&a▎ &b{0} &fsuccessfully &agot &faccess to VAR for &e{1}&f."),
  VAR_REMOVED_ACCESS("var.removed-access", "&a▎ &b{0} &fSuccessfully &clost &faccess to VAR."),
  VAR_ALREADY_HAS_ACCESS("var.already-has-access", "&4▎ &b{0} &calready has access to VAR."),
  VAR_NO_ACCESS("var.no-access", "&4▎ &b{0} &cdoesn't have access to VAR."),
  VAR_USAGE_ADD("var.usage.add", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&evar add&6|&eset &2<&aplayer-name&2> &3[&btime&3]")),
  VAR_USAGE_REMOVE("var.usage.remove", String.join(System.getProperty("line.separator"),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&evar remove&6|&eunset &2<&aplayer-name&2>")),
  VAR_HELP("var.help", String.join(System.getProperty("line.separator"),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rVAR commands.",
      "&r &r",
      "&6 /&evar add&6|&eset &2<&aplayer-name&2> &3[&btime&3]",
      "&7   - &fGive access to VAR.",
      "&6 /&evar remove&6|&eunset &2<&aplayer-name&2>",
      "&7   - &fRemove access to VAR.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r"));

  private static FileConfiguration LANG;
  private final String path, def;

  Lang(final String path, final String start) {
    this.path = path;
    this.def = start;
  }

  public static void setFile(final FileConfiguration config) {
    LANG = config;
  }

  public String getPath() {
    return this.path;
  }

  public String getDefault() {
    return this.def;
  }

  public String getConfigValue(final String[] args) {
    String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

    if (args == null) return value;
    else {
      if (args.length == 0) return value;
      for (int i = 0; i < args.length; i++) value = value.replace("{" + i + "}", args[i]);
    }

    return value;
  }
}
