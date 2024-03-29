package io.github.divinerealms.configs;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public enum Lang {
  ON("on", "&a&luključen"),
  OFF("off", "&c&lisključen"),
  RELOAD("reload", "&a▎ &fPlugin reloaded!"),
  UNKNOWN_COMMAND("unknown-command", "&4▎ &cUnknown command."),
  INSUFFICIENT_PERMISSION("insufficient-permission", "&4▎ &cInsufficient permission."),
  INGAME_ONLY("ingame-only", "&4▎ &cThis command can be used only in game."),
  TOGGLE("toggle", "&a▎ &fFootcube toggled &e{0} &fby &b{1}&f."),
  FOOTCUBE_DISABLED("footcube-disabled", "&4▎ &cFootcube is currently disabled."),
  INVALID_TIME("invalid-time", "&4▎ &cYour time argument \"&e{0}&c\" is invalid."),
  HELP("help", String.join(System.lineSeparator(),
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
      "&6 /&elm sm&6|&esetmanager &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fMake player a team manager.",
      "&6 /&elm um&6|&eunsetmanager &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fRemove manager role from player.",
      "&6 /&elm ban &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]",
      "&7   - &fBan player from FC. Example: &6/&elm ban Neeonn 2h Rage Quit",
      "&6 /&elm unban &2<&aplayer-name&2>",
      "&7   - &fUnban player from FC.",
      "&6 /&elm varadd&6|&eva &2<&aplayer-name&2> &3[&bduration&3]",
      "&7   - &fGive player access to VAR.",
      "&6 /&elm varremove&6|&evr &2<&aplayer-name&2>",
      "&7   - &fRemove VAR access from a player.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  USER_BAN("user.ban", "&a▎ &fPlayer &b{0} &fhas been successfully banned for &e{1}&f for '&4{2}&f'!"),
  USER_BANNED("user.banned", "&4▎ &cYou have been banned from FC for &e{0} &cfor '&4{1}&c'!"),
  USER_STILL_BANNED("user.still-banned", String.join(System.lineSeparator(),
      "&4▎ &cYou are banned from FC!",
      "&4▎ &cYour ban will expire in: &e{0}&c.")),
  USER_UNBAN("user.unban", "&a▎ &fPlayer &b{0} &fhas been successfully unbanned!"),
  USER_UNBANNED("user.unbanned", "&8▎ &aYou've been unbanned from FC!"),
  USER_NOT_BANNED("user.not-banned", "&4▎ &cPlayer &b{0} &cis not banned from FC!"),
  USER_ALREADY_BANNED("user.already-banned", "&4▎ &cPlayer &b{0} &cis already banned from FC! Unban them first."),
  USER_SUSPEND("user.suspend", "&a▎ &fPlayer &b{0} &fhas been successfully suspended for &e{1}&f for '&4{2}&f'!"),
  USER_SUSPENDED("user.suspended", "&4▎ &cYou have been suspended from participating in league matches for &e{0} &cfor '&4{1}&c'!"),
  USER_ALREADY_SUSPENDED("user.already-suspended", "&4▎ &cPlayer &b{0} &cis already suspended! Unsuspend them first."),
  USER_UNSUSPEND("user.unsuspend", "&a▎ &fPlayer &b{0} &fhas been successfully unsuspended!"),
  USER_UNSUSPENDED("user.unbanned", "&8▎ &aYou've been unsuspended!"),
  USER_NOT_SUSPENDED("user.not-suspended", "&4▎ &cPlayer &b{0} &cis not suspended!"),
  USER_NOT_FOUND("user.not-found", "&4▎ &cUneti igrač ne postoji."),
  USER_USAGE_BAN("user.usage.ban", String.join(System.lineSeparator(),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm ban &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]")),
  USER_USAGE_SUSPEND("user.usage.suspend", String.join(System.lineSeparator(),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm suspend &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]")),
  USER_USAGE_UNBAN("user.usage.unban", String.join(System.lineSeparator(),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm unban &2<&aplayer-name&2>")),
  USER_USAGE_UNSUSPEND("user.usage.unsuspend", String.join(System.lineSeparator(),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm unsuspend &2<&aplayer-name&2>")),
  USER_HELP("user.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rUser commands.",
      "&r &r",
      "&6 /&elm st&6|&esetteam &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fAdd player to a team (ex cel) with the branch.",
      "&7   - &fExample: &6/&elm st Neeonn nkp",
      "&7   - &fExample: &6/&elm st 1nvader vksb",
      "&6 /&elm ut&6|&eunsetteam &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fRemove player from a team.",
      "&6 /&elm sm&6|&esetmanager &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fMake player a team manager.",
      "&6 /&elm um&6|&eunsetmanager &2<&aplayer-name&2> <&ateam-name&2>",
      "&7   - &fRemove manager role from player.",
      "&6 /&elm ban &2<&aplayer-name&2> <&aduration&2> &3[&breason&3]",
      "&7   - &fBan player from FC. Example: &6/&elm ban Neeonn 2h Rage Quit",
      "&6 /&elm unban &2<&aplayer-name&2>",
      "&7   - &fUnban player from FC.",
      "&6 /&elm varadd&6|&eva &2<&aplayer-name&2> &3[&bduration&3]",
      "&7   - &fGive player access to VAR.",
      "&6 /&elm varremove&6|&evr &2<&aplayer-name&2>",
      "&7   - &fRemove VAR access from a player.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  VAR_GIVEN_ACCESS("var.given-access", "&a▎ &b{0} &fsuccessfully &agot &faccess to VAR."),
  VAR_GIVEN_ACCESS_1("var.given-access-1", "&a▎ &b{0} &fsuccessfully &agot &faccess to VAR for &e{1}&f."),
  VAR_REMOVED_ACCESS("var.removed-access", "&a▎ &b{0} &fSuccessfully &clost &faccess to VAR."),
  VAR_ALREADY_HAS_ACCESS("var.already-has-access", "&4▎ &b{0} &calready has access to VAR."),
  VAR_NO_ACCESS("var.no-access", "&4▎ &b{0} &cdoesn't have access to VAR."),
  VAR_USAGE_ADD("var.usage.add", String.join(System.lineSeparator(),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm varadd&6|&eva &2<&aplayer-name&2> &3[&btime&3]")),
  VAR_USAGE_REMOVE("var.usage.remove", String.join(System.lineSeparator(),
      "&4▎ &cInvalid arguments. Usage:",
      "&4▎ &6/&elm varremove&6|&evr &2<&aplayer-name&2>")),
  VAR_HELP("var.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rVAR commands.",
      "&r &r",
      "&6 /&elm varadd&6|&eva &2<&aplayer-name&2> &3[&btime&3]",
      "&7   - &fGive access to VAR.",
      "&6 /&elm varremove&6|&evr &2<&aplayer-name&2>",
      "&7   - &fRemove access to VAR.",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  TIMER_HELP("timer.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&d &lLeague&e&lManager &7- &rTimer Commands.",
      "&r &r",
      "&6 /&etimer start &2<&atime&2> &3[&bprefix&3]",
      "&6 /&etimer stop",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  TIMER_CREATE("timer.create", "&a▎ &fSuccessfully started timer with id &e{0}&f."),
  TIMER_STOP("timer.stop", "&a▎ &fSuccessfully stopped timer with id &e{0}&f."),
  TIMER_STARTING("timer.starting", "&6▎ {0} &6┃ &fHost starting!"),
  TIMER_END("timer.end", "{0} &6┃ &fTimer finished!"),
  TIMER_OVER("timer.over", "&a▎ &fTimer with id &e{0} &fis over!"),
  TIMER_CURRENT_TIME("timer.current-time", "{0} &7┃ &fCurrent Time: &e{1}/{2}"),
  TIMER_NOT_AVAILABLE("timer.not-available", "&4▎ &cThere aren't any timers running."),
  TIMER_ALREADY_RUNNING("timer.already-running", "&4▎ &cThere already is a timer running."),
  TIMER_PREFIX_SET("timer.prefix-set", "&a▎ &fPrefix successfully set to {0}&f."),
  TIMER_TEAMS_SET("timer.teams-set", "&a▎ &fTeams successfully set to {0} &fand {1}&f."),
  TIMER_TIME_SET("timer.time-set", "&a▎ &fTime successfully set to {0}&f."),
  TIMER_RESET("timer.reset", "&a▎ &fTimer command successfully reset!"),
  TIMER_NOT_SETUP("timer.not-setup", "&4▎ &cYou haven't configured teams, time or prefix."),
  RESULT_HELP("result.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f &lSetup Commands:",
      "&6 /&ers prefix &2<&aprefix&2> &7(&f/rs prefix &b&lEvent&7)",
      "&6 /&ers teams &2<&ahome&2> <&aaway&2> &7(&f/rs teams nkp vtz&7)",
      "&6 /&ers time &2<&atime&2> &7(&f/rs time 20min&7)",
      "&r &r",
      "&e &lControl Commands:",
      "&6 /&ers start&6|&estop",
      "&6 /&ers pause&6|&eresume",
      "&7 - &fUse &epause &fat the end of the First Half.",
      "&7 - &fUse &eresume &fat the beginning of the Second Half.",
      "&6 /&ers extend &2<&atime&2> &7(&f/rs extend 2min30s&7)",
      "&6 /&ers add&6|&erem&6(&e&oove&6) &ehome&6|&eaway &7(&f/rs add vtz&7)",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  RESULT_ADD("result.add", "&a▎ &fTeam {1}&f has scored! Scorer: &b{0}&f."),
  RESULT_ADD_ASSIST("result.add-assist", "&a▎ &fTeam {1}&f has scored! Scorer: &b{0}&f, Assist: &b{2}&f."),
  RESULT_CAPTAINS_SET("result.captains-set", "&a▎ &fKapiten home &b{0}&f, away &b{1}&f. Podešeno uspešno."),
  RESULT_FULL_LIVES("result.full-lives", "&4▎ &cTeam {0} &calready has all lives."),
  RESULT_ELIMINATED("result.eliminated", "&4▎ &cTeam {0} &cis already eliminated."),
  RESULT_RESET("result.reset", "&a▎ &fTeams are reset!"),
  RESULT_ADDED_LIFE("result.added-life", "&a▎ &fAdded one life for team {0}&f!"),
  RESULT_REMOVED_LIFE("result.removed-life", "&a▎ &fRemoved one life from the team {0}&f!"),
  RESULT_REMOVE("result.remove", "&a▎ &fRemoved one goal for the team {0}&f."),
  RESULT_USAGE("result.add-usage", "&4▎ &cYou need to specify a team."),
  RESULT_STARTING("result.starting", "{0} &7┃ &fHost starting!"),
  RESULT_ACTIONBAR("result.actionbar", "{0} &7┃ &f{1} &e{2} - {3} &f{4} &7┃ &f{5}{6}/{7}{8}"),
  RESULT_ACTIONBAR_HT("result.actionbar-ht", "{0} &7┃ &f{1} &e{2} - {3} &f{4} &7┃ &fHalf Time!"),
  RESULT_END("result.end", "{0} &7┃ &fMatch finished! Result: &f{1} &e{2} &f- &e{3} &f{4}&f."),
  RESULT_HALFTIME("result.halftime", "&6▎ {0} &6┃ &fHalf Time! Result: &f{1} &e{2} &f- &e{3} &f{4}&f."),
  RESULT_SECONDHALF("result.secondhalf", "&6▎ {0} &6┃ &fMatch continuing - Second Half! Result: &f{1} &e{2} &f- &e{3} &f{4}&f."),
  RESULT_OVER("result.over", "&6▎ {0} &6┃ &fMatch finished! Result: &f{1} &e{2} &f- &e{3} &f{4}&f."),
  RESULT_MANAGER_FORMAT("result.manager-format", "&a&l{0}"),
  RESULT_INVALID_LINEUP("result.invalid-lineup", String.join(System.lineSeparator(),
      "&4▎ &cLoše napisana postava.",
      "&4▎ &cKucajte: &epostava gk cb cb2 cm st")),
  RESULT_LINEUP("result.lineup", String.join(System.lineSeparator(),
      "&6▎ &fPostava tima {0} &f(&omenadžer &e&o{1}&f):",
      "&6▎ &fGK: &b{2}&f, CB: &b{3}&f, CB2: &b{4}&f, CM: &b{5}&f, ST: &b{6}&f.")),
  RESULT_INVALID_SUB("result.invalid-sub", String.join(System.lineSeparator(),
      "&4▎ &cLoše napisana izmena.",
      "&4▎ &cKucajte: &eizmena igrac_koji_izlazi igrac_koji_ulazi")),
  RESULT_SUB("result.sub", String.join(System.lineSeparator(),
      "&6▎ &fIzmena za tim {0} &f(&omenadžer &e&o{1}&f):",
      "&6▎ &fOUT: &b{2}&f, IN: &b{3}&f.")),
  TWO_TIMES_FOUR_HELP("result.2x4.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&6 /&e2x4 start&6|&estop",
      "&6 /&e2x4 type &2<&atype&2> &7- &fEvent type (&o2x4/3x4/...&f).",
      "&6 /&e2x4 add&6/&erem team_color &7(&f/2x4 add blue&7)",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  TWO_TIMES_FOUR_ACTIONBAR("result.2x4.actionbar", "{0} &7┃ &fResult: &b{1} &c{2} &a{3} &e{4} &7┃ &fTime: &e{5}"),
  TWO_TIMES_FOUR_RESULT_OVER("result.2x4.over", "&6▎ {0} &6┃ &fMatch over! Result: &b{1} &c{2} &a{3} &e{4}&f!"),
  TWO_TIMES_FOUR_RESULT_END("result.2x4.end", "{0} &6┃ &fMatch over! Result: &b{1} &c{2} &a{3} &e{4}"),
  ONE_TIMES_EIGHT_HELP("result.1x8.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&6 /&e1x8 start&6|&estop",
      "&6 /&e1x8 type &2<&atype&2> &7- &fEvent type (1x8/2x8/...&f).",
      "&6 /&e1x8 add&6/&erem team_color &7(&f/1x8 rem red&7)",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  ONE_TIMES_EIGHT_ACTIONBAR("result.1x8.actionbar", "{0} &7┃ &fResult: &8{1} &c{2} &6{3} &e{4} &a{5} &b{6} &d{7} &0{8} &7┃ &fTime: &e{9}"),
  ONE_TIMES_EIGHT_RESULT_OVER("result.1x8.over", "&6▎ {0} &6┃ &fMatch over! Result: &8{1} &c{2} &6{3} &e{4} &a{5} &b{6} &d{7} &0{8}&f!"),
  ONE_TIMES_EIGHT_RESULT_END("result.1x8.end", "{0} &6┃ &fMatch over! Result: &8{1} &c{2} &6{3} &e{4} &a{5} &b{6} &d{7} &0{8}"),
  TIMER_ADDED_EXTRA_TIME("result.extra-time-added", "&a▎ &fAdded &e{0} &fof extra time."),
  WEBHOOK_NOT_SETUP("webhook.not-setup", "&4▎ &cNiste dodali URL za Discord Webhook u config.yml!"),
  WEBHOOK_TEAMS_SET("webhook.teams-set", "Podešeni timovi: **{0} - {1}**."),
  WEBHOOK_TEAM_LINEUP("webhook.team-lineup", "**{0}** postave: `[CAP]` **{1}**, `[GK]` **{2}**, `[CB]` **{3}**, `[CB2]` **{4}**, `[CM]` **{5}**, `[ST]` **{6}**"),
  WEBHOOK_TEAM_SUB("webhook.team-sub", "**{0}** izmena: `[OUT]` **{1}**, `[IN]` **{2}**"),
  WEBHOOK_MATCH_START("webhook.match-start", "Početak utakmice **{0} - {1}**."),
  WEBHOOK_MATCH_ENDED("webhook.match-ended", "Kraj utakmice, rezultat: **{0} {1} - {2} {3}**. Trajanje: **`{4}`**."),
  WEBHOOK_MATCH_HALFTIME("webhook.match-halftime", "Poluvreme, rezultat: **{0} {1} - {2} {3}**."),
  WEBHOOK_MATCH_SECONDHALF("webhook.match-secondhalf", "Početak drugog poluvremena, rezultat: **{0} {1} - {2} {3}**."),
  WEBHOOK_MATCH_SCORE("webhook.match-score", "**GOOL!** Scorer: **{0}** (**{1}** - **`{2}`**)!"),
  WEBHOOK_MATCH_ASSIST("webhook.match-assist", "**GOOL!** Scorer: **{0}** (**{1}** - **`{2}`**)! Asistent: **{3}**."),
  WEBHOOK_PLAYER_NOT_IN_TEAM("webhook.player-not-in-team", "&4▎ &cIgrač &b{0} &cnije u {1} &ctimu!"),
  ROSTERS_HELP("rosters.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&e &lPomoćnik za Rosters Komande",
      "&r &r",
      "&6 /&erosters&6|&ert &3[&bgui&3]",
      "&6 /&erosters&6|&ert help",
      "&6 /&erosters&6|&ert create &2<&aimeTima&2> <&atag&2> &3[&bb&3]",
      "&6 /&erosters&6|&ert delete &2<&aimeTima&2>",
      "&6 /&erosters&6|&ert set &2<&aimeTima&2> <&aname&2|&atag&2> <&anazivTima&2|&atagTima&2>",
      "&6 /&erosters&6|&ert createbanner &2<&aimeTima&2>",
      "&6 /&erosters&6|&ert add &2<&aimeTima&2> <&aigrač&2>",
      "&6 /&erosters&6|&ert remove &2<&aimeTima&2> <&aigrač&2>",
      "&6 /&erosters&6|&ert setposition &2<&aimeTima&2> <&aigrač&2> <&apozicija&2>",
      "&r &r",
      "&7 &oSve komande imaju [TAB] completion, koristite ovo za pomoć!",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  ROSTERS_ADD_USAGE("rosters.add.usage", String.join(System.lineSeparator(),
      "&b▎ &fDodajte igrače u tim komandom: &6/&ert add &2<&atim&2> <&aigrač&2>",
      "&b▎ &fPrimer: &6/&ert add ADM Neeonn")),
  ROSTERS_SET_USAGE("rosters.set.usage", String.join(System.lineSeparator(),
      "&b▎ &fPodesite parametre komandama:",
      "&b▎ &fZa tim: &6/&ert set &2<&atim&2> <&aname&2|&atag&2> <&aunos&2>",
      "&b▎ &fPrimer: &6/&ert set ADM name CD Admirense",
      "&b▎ &fZa igrača: &6/&ert set &2<&aigrač&2> <&acountry&2|&anumber&2|&aposition&2|&acontract&2> <&aunos&2>",
      "&b▎ &fPrimer: &6/&ert set Neeonn country Srbija",
      "&b▎ &fPrimer: &6/&ert set Neeonn position CB|CM",
      "&b▎ &fPrimer: &6/&ert set Neeonn contract 1")),
  ROSTERS_GENERATED_FILES("rosters.generated-files", "&a▎ &fUspešno generisani fajlovi za tim {0}&f."),
  ROSTERS_NOT_FOUND("rosters.not-found", "&4▎ &cUneti {0} ne postoji."),
  ROSTERS_DELETED_FILES("rosters.deleted-files", "&a▎ &fUspešno obrisani fajlovi za tim {0}&f."),
  ROSTERS_FILE_NOT_FOUND("rosters.file-not-found", "&4▎ &cFajl {0} nije pronađen. Kreiramo ga sada..."),
  ROSTERS_SET_ROLE("rosters.set-role", "&a▎ &fIgrač &b{0} &fuspešno postavljen/a kao &e{1} &fza tim {2}&f!"),
  ROSTERS_NOT_ROLE("rosters.not-role", "&4▎ &cIgrač nema role."),
  ROSTERS_SET("rosters.set", "&a▎ &b{0} &fje postavio/la &e{1} &fza {2} {3} &fna: &e{4}&f."),
  ROSTERS_NOT_BANNER("rosters.not-banner", "&4▎ &cMorate držati banner u ruci..."),
  ROSTERS_BANNER_SET("rosters.banner-set", "&a▎ &fUspešno postavljena zastava za klub {0}&f."),
  ROSTERS_USER_ADDED("rosters.user-added", "&a▎ &b{0} &fje dodao/la &b{1} &fu tim {2}&f."),
  ROSTERS_USER_REMOVED("rosters.user-removed", "&a▎ &b{0} &fje izbacio/la &b{1} &fiz tima {2}&f."),
  ROSTERS_USER_NOT_IN_TEAM("rosters.user-not-in-team", "&4▎ &b{0} &cnije u navedenom timu."),
  ROSTERS_USER_ALREADY_IN_TEAM("rosters.user-already-in-team", "&4▎ &b{0} &cje već u navedenom timu."),
  ROSTERS_TEAM_CREATED("rosters.team-created", "&a▎ &b{0} &fje napravio/la tim {1}&f."),
  ROSTERS_TEAM_DELETED("rosters.team-deleted", "&a▎ &b{0} &fje obrisao/la tim {1}&f."),
  ROSTERS_NO_FA("rosters.no-fa", "&4▎ &cSvi online igrači već imaju tim.");

  private static FileConfiguration LANG;
  private final String path, def;

  Lang(final String path, final String start) {
    this.path = path;
    this.def = start;
  }

  public static void setFile(final FileConfiguration config) {
    LANG = config;
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
      value = ChatColor.translateAlternateColorCodes('&', value);
    }

    return value;
  }
}
