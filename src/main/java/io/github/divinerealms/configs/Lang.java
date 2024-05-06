package io.github.divinerealms.configs;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public enum Lang {
  ON("on", "&a&luključen"),
  OFF("off", "&c&lisključen"),
  RELOAD("reload", "&a▎ &fPlugin osvežen!"),
  UNKNOWN_COMMAND("unknown-command", "&4▎ &cNepoznata komanda."),
  INSUFFICIENT_PERMISSION("insufficient-permission", "&4▎ &cNedovoljno dozvola."),
  INGAME_ONLY("ingame-only", "&4▎ &cKomanda se može koristiti samo u igri."),
  TOGGLE("toggle", "&a▎ &fFootcube toggled &e{0} &fby &b{1}&f."),
  FOOTCUBE_DISABLED("footcube-disabled", "&4▎ &cFootcube je trenutno isključen."),
  INVALID_TIME("invalid-time", "&4▎ &cArgument za vreme \"&e{0}&c\" nije validan."),
  HELP("help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&e &lPomoćnik za LeagueManager Komande",
      "&r &r",
      "&6 /&elm reload &7- &fOsvežite plugin.",
      "&6 /&elm toggle &7- &fIsključite/uključite FC.",
      "&6 /&elm ban&6/&eunban &7- &f(Un)Ban igrača iz FC.",
      "&6 /&elm suspend&6/&eunsuspend &7- &f(Un)Suspend igrača iz Lige.",
      "&r &r", 
      "&e &lRosters Komande:&r",
      "&6 /&erosters&6|&ert &3[&bgui&3]",
      "&6 /&erosters&6|&ert help",
      "&r &r", 
      "&a &lVAR Komande:&r",
      "&6 /&evar add &2<&aigrač&2> &3[&bvreme&3]",
      "&6 /&evar remove &2<&aigrač&2>",
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
      "&4▎ &cNepoznat argument. Koristite:",
      "&4▎ &6/&elm ban &2<&aigrač&2> <&atrajanje&2> &3[&brazlog&3]")),
  USER_USAGE_SUSPEND("user.usage.suspend", String.join(System.lineSeparator(),
      "&4▎ &cNepoznat argument. Koristite:",
      "&4▎ &6/&elm suspend &2<&aigrač&2> <&aduration&2> &3[&breason&3]")),
  USER_USAGE_UNBAN("user.usage.unban", String.join(System.lineSeparator(),
      "&4▎ &cNepoznat argument. Koristite:",
      "&4▎ &6/&elm unban &2<&aigrač&2>")),
  USER_USAGE_UNSUSPEND("user.usage.unsuspend", String.join(System.lineSeparator(),
      "&4▎ &cNepoznat argument. Koristite:",
      "&4▎ &6/&elm unsuspend &2<&aigrač&2>")),
  VAR_GIVEN_ACCESS("var.given-access", "&a▎ &b{0} &fsuccessfully &agot &faccess to VAR."),
  VAR_GIVEN_ACCESS_1("var.given-access-1", "&a▎ &b{0} &fsuccessfully &agot &faccess to VAR for &e{1}&f."),
  VAR_REMOVED_ACCESS("var.removed-access", "&a▎ &b{0} &fSuccessfully &clost &faccess to VAR."),
  VAR_ALREADY_HAS_ACCESS("var.already-has-access", "&4▎ &b{0} &calready has access to VAR."),
  VAR_NO_ACCESS("var.no-access", "&4▎ &b{0} &cdoesn't have access to VAR."),
  VAR_USAGE_ADD("var.usage.add", String.join(System.lineSeparator(),
      "&4▎ &cNepoznat argument. Koristite:",
      "&4▎ &6/&evar add &2<&aigrač&2> &3[&bvreme&3]")),
  VAR_USAGE_REMOVE("var.usage.remove", String.join(System.lineSeparator(),
      "&4▎ &cNepoznat argument. Koristite:",
      "&4▎ &6/&evar remove &2<&aigrač&2>")),
  VAR_HELP("var.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&e &lPomoćnik za VAR Komande",
      "&r &r",
      "&6 /&evar add &2<&aigrač&2> &3[&bvreme&3]",
      "&6 /&evar remove &2<&aigrač&2>",
      "&r &r",
      "&7 &oSve komande imaju [TAB] completion, koristite ovo za pomoć!",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
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
  ROSTERS_TEAM_DELETED("rosters.team-deleted", "&a▎ &b{0} &fje obrisao/la tim {1}&f.");

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
