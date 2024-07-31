package io.github.divinerealms.leaguemanager.configs;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public enum Lang {
  PREFIX_DEFAULT("prefix.default", "&8▎ &f"),
  PREFIX_ERROR("prefix.error", "&4▎ &c"),
  PREFIX_SUCCESS("prefix.success", "&a▎ &f"),
  PREFIX_INFO("prefix.info", "{prefix_info} &f"),
  PREFIX_WARN("prefix.warn", "&6▎ &e"),
  ON("on", "&a&luključen"),
  OFF("off", "&c&lisključen"),
  RELOAD("reload", "{prefix_success}Plugin osvežen!"),
  UNKNOWN_COMMAND("unknown-command", "{prefix_error}Nepoznata komanda."),
  INSUFFICIENT_PERMISSION("insufficient-permission", "{prefix_error}Nedovoljno dozvola."),
  INGAME_ONLY("ingame-only", "{prefix_error}Komanda se može koristiti samo u igri."),
  CLEARED_CUBES("cleared-cubes", "{prefix_success}Obrisano &e{0} lopti &fsa zona treniranja. &bℹ"),
  CLEARED_CUBES_USAGE("cleared-cubes-usage", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm spa &2<&anaziv&2>")),
  PRACTICE_AREA_SET("practice-area-set", "{prefix_success}Uspešno postavljena lokacija &b{0}&f (&o{1}, {2}, {3}&f)."),
  PRACTICE_AREAS_EMPTY("practice-areas-empty", "{prefix_error}Nedefinisane zone treniranja. Ne brišemo lopte."),
  TOGGLE("toggle", String.join(System.lineSeparator(),
      "&6▎ &r",
      "&6▎ &e   &lFootcube je {0}",
      "&6▎ &f   &fOd strane: &b{1}&f.",
      "&6▎ &r")),
  FOOTCUBE_DISABLED("footcube-disabled", "{prefix_error}Footcube je trenutno isključen."),
  INVALID_TIME("invalid-time", String.join(System.lineSeparator(),
      "{prefix_error}Argument za vreme \"&e{0}&c\" nije validan.",
      "{prefix_error}Primer: &e20min")),
  INVALID_VALUE("invalid-value", "{prefix_error}Argument nije validan. Koristite samo brojeve."),
  HELP("help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f Pomoćnik za LeagueManager Komande",
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
  DISABLED_ITEM("disabled-item", "{prefix_error}Ne možete koristiti {0} dok je FC isključen!"),
  USER_BAN("user.ban", "{prefix_success}&b{0} &fje banovan iz FC-a na &e{1}&f zbog '&4{2}&f'!"),
  USER_BANNED("user.banned", "{prefix_error}Banovani ste iz FC-a na &e{0} &czbog '&4{1}&c'!"),
  USER_REMOVED_UNBANNED("user.removed-unbanned", "{prefix_error}Igraču &b{0} &cje istekao FC ban."),
  USER_STILL_BANNED("user.still-banned", String.join(System.lineSeparator(),
      "{prefix_error}Banovali ste iz fudbala!",
      "{prefix_error}Ban vam ističe za: &e{0}&c.")),
  USER_CHECKBAN("user.checkban", String.join(System.lineSeparator(),
      "&4▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
      "&b {0} &fje &cbanovan iz FC &fod strane &e{1}&f.",
      "&f Razlog: &n{2}&f.",
      "&f Trajanje bana: &e{3} &7(&oističe za &e&o{4}&7)&f.",
      "&4▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")),
  USER_CHECKSUSPEND("user.checksuspend", String.join(System.lineSeparator(),
      "&4▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
      "&b {0} &fje &csuspendovan iz Lige &fod strane &e{1}&f.",
      "&f Razlog: &n{2}&f.",
      "&f Trajanje suspenzije: &e{3} &7(&oističe za &e&o{4}&7)&f.",
      "&4▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")),
  USER_UNBAN("user.unban", "{prefix_success}&b{0} &fje unbanovan!"),
  USER_UNBANNED("user.unbanned", "{prefix_default}&aUnbanovani ste iz FC-a!"),
  USER_NOT_BANNED("user.not-banned", "{prefix_error}&b{0} &cnije banovan iz FC!"),
  USER_ALREADY_BANNED("user.already-banned", "{prefix_error}&b{0} &cje već banovan iz FC-a! Unban ga prvo."),
  USER_SUSPEND("user.suspend", "{prefix_success}&b{0} &fje suspendovan iz Lige na &e{1}&f zbog '&4{2}&f'!"),
  USER_SUSPENDED("user.suspended", "{prefix_error}Suspendovani ste iz lige na &e{0} &czbog '&4{1}&c'!"),
  USER_ALREADY_SUSPENDED("user.already-suspended", "{prefix_error}&b{0} &cje već suspendovan iz Lige! Unsuspend ga prvo."),
  USER_UNSUSPEND("user.unsuspend", "{prefix_success}&b{0} &fje unsuspendovan iz lige!"),
  USER_UNSUSPENDED("user.unbanned", "{prefix_default}&aUnsuspendovani ste iz lige!"),
  USER_NOT_SUSPENDED("user.not-suspended", "{prefix_error}&b{0} &cnije suspendovan!"),
  USER_NOT_FOUND("user.not-found", "{prefix_error}Uneti igrač ne postoji."),
  USER_USAGE_BAN("user.usage.ban", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm ban &2<&aigrač&2> <&atrajanje&2> &3[&brazlog&3]")),
  USER_USAGE_SUSPEND("user.usage.suspend", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm suspend &2<&aigrač&2> <&aduration&2> &3[&breason&3]")),
  USER_USAGE_UNBAN("user.usage.unban", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm unban &2<&aigrač&2>")),
  USER_USAGE_CHECKBAN("user.usage.checkban", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm checkban &2<&aigrač&2>")),
  USER_USAGE_UNSUSPEND("user.usage.unsuspend", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm unsuspend &2<&aigrač&2>")),
  USER_USAGE_CHECKSUSPEND("user.usage.checksuspend", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&elm checksuspend &2<&aigrač&2>")),
  VAR_GIVEN_ACCESS("var.given-access", "{prefix_success}&b{0} &fje dobio/la pristup VAR-u."),
  VAR_GIVEN_ACCESS_1("var.given-access-1", "{prefix_success}&b{0} &fje dobio/la pristup VAR-u na &e{1}&f."),
  VAR_REMOVED_ACCESS("var.removed-access", "{prefix_success}&b{0} &fnema više pristup VAR-u."),
  VAR_ALREADY_HAS_ACCESS("var.already-has-access", "{prefix_error}&b{0} &cveć ima pristup VAR-u."),
  VAR_NO_ACCESS("var.no-access", "{prefix_error}&b{0} &cnema pristup VAR-u."),
  VAR_USAGE_ADD("var.usage.add", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&evar add &2<&aigrač&2> &3[&bvreme&3]")),
  VAR_USAGE_REMOVE("var.usage.remove", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&evar remove &2<&aigrač&2>")),
  VAR_HELP("var.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f Pomoćnik za VAR Komande",
      "&r &r",
      "&6 /&evar add &2<&aigrač&2> &3[&bvreme&3]",
      "&6 /&evar remove &2<&aigrač&2>",
      "&r &r",
      "&7 &oSve komande imaju [TAB] completion, koristite ovo za pomoć!",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  MIGRATE_HELP("migrate.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f Pomoćnik za Migrate Komandu",
      "&r &r",
      "&6 /&emigrate &2<&astari-nick&2> &7- &fPrebacite tim sa starog nicka.",
      "&6 /&emigrate &2<&aigrač&2> <&astari-nick&2>",
      "&r &r",
      "&7 &oSve komande imaju [TAB] completion, koristite ovo za pomoć!",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  MIGRATED("migrate.success", "{prefix_success}Uspešno ste migrirali sve svoje podatke sa svog starog nicka."),
  MIGRATE_SAME("migrate.same", "{prefix_error}Možete migrirati samo naloge sa svoje IP adrese!"),
  MIGRATE_SAME_NICK("migrate.same-kick", "{prefix_error}Ne možete migrirati isti nalog..."),
  MIGRATED_NOTIFY("migrate.success-notify", "{prefix_success}&b{0} &fse migrirao sa starog nicka &c{1}&f."),
  MIGRATED_OTHER("migrate.success-other", "{prefix_success}&e{0} &fje migrirao &b{1} sa starog nicka &c{2}&f."),
  ROSTERS_HELP("rosters.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f Pomoćnik za Rosters Komande",
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
      "{prefix_info} &fDodajte igrače u tim komandom: &6/&ert add &2<&atim&2> <&aigrač&2>",
      "{prefix_info} &fPrimer: &6/&ert add ADM Neeonn")),
  ROSTERS_SET_USAGE("rosters.set.usage", String.join(System.lineSeparator(),
      "{prefix_info} &fPodesite parametre komandama:",
      "{prefix_info}&fZa tim: &6/&ert set &2<&atim&2> <&aname&2|&atag&2> <&aunos&2>",
      "{prefix_info}&fPrimer: &6/&ert set ADM name CD Admirense",
      "{prefix_info}&fZa igrača: &6/&ert set &2<&aigrač&2> <&acountry&2|&anumber&2|&aposition&2|&acontract&2> <&aunos&2>",
      "{prefix_info}&fPrimer: &6/&ert set Neeonn country Srbija",
      "{prefix_info}&fPrimer: &6/&ert set Neeonn position CB|CM",
      "{prefix_info}&fPrimer: &6/&ert set Neeonn contract 1")),
  ROSTERS_GENERATED_FILES("rosters.generated-files", "{prefix_success}Uspešno generisani fajlovi za tim {0}&f."),
  ROSTERS_NOT_FOUND("rosters.not-found", "{prefix_error}Uneti {0} ne postoji."),
  ROSTERS_DELETED_FILES("rosters.deleted-files", "{prefix_success}Uspešno obrisani fajlovi za tim {0}&f."),
  ROSTERS_FILE_NOT_FOUND("rosters.file-not-found", "{prefix_error}Fajl {0} nije pronađen. Kreiramo ga sada..."),
  ROSTERS_SET_ROLE("rosters.set-role", "{prefix_success}Igrač &b{0} &fuspešno postavljen/a kao &e{1} &fza tim {2}&f!"),
  ROSTERS_NOT_ROLE("rosters.not-role", "{prefix_error}Igrač nema role."),
  ROSTERS_SET("rosters.set", "{prefix_success}&b{0} &fje postavio/la &e{1} &fza {2} {3} &fna: &e{4}&f."),
  ROSTERS_NOT_BANNER("rosters.not-banner", "{prefix_error}Morate držati banner u ruci..."),
  ROSTERS_BANNER_SET("rosters.banner-set", "{prefix_success}Uspešno postavljena zastava za klub {0}&f."),
  ROSTERS_USER_ADDED("rosters.user-added", "{prefix_success}&b{0} &fje dodao/la &b{1} &fu tim {2}&f."),
  ROSTERS_USER_REMOVED("rosters.user-removed", "{prefix_success}&b{0} &fje izbacio/la &b{1} &fiz tima {2}&f."),
  ROSTERS_USER_NOT_IN_TEAM("rosters.user-not-in-team", "{prefix_error}&b{0} &cnije u navedenom timu."),
  ROSTERS_USER_ALREADY_IN_TEAM("rosters.user-already-in-team", "{prefix_error}&b{0} &cje već u navedenom timu."),
  ROSTERS_TEAM_CREATED("rosters.team-created", "{prefix_success}&b{0} &fje napravio/la tim {1}&f."),
  ROSTERS_TEAM_DELETED("rosters.team-deleted", "{prefix_success}&b{0} &fje obrisao/la tim {1}&f."),
  STATISTICS("statistics.info", String.join(System.lineSeparator(),
      "&9▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f {0}",
      "&r &r",
      "&f Broj Golova: &b{1}",
      "&f Broj Asistencija: &a{2}",
      "&f Broj Žutih Kartona: &e{3}",
      "&f Broj Crvenih Kartona: &c{4}",
      "&f Broj \"Clean Sheets\": &d{5}",
      "&9▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  STATISTICS_SELF_TITLE("statistics.self-title", "Vaša statistika u Ligaškim utakmicama:"),
  STATISTICS_OTHER_TITLE("statistics.other-title", "Statistika igrača &b{0} &fu Ligaškim utakmicama:"),
  STATISTICS_USAGE_ADD("statistics.usage.add", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&estats add &2<&aigrač&2> <&avrsta&2> <&aiznos&2>")),
  STATISTICS_USAGE_REMOVE("statistics.usage.add", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&estats remove &2<&aigrač&2> <&avrsta&2> <&aiznos&2>")),
  STATISTICS_USAGE_SET("statistics.usage.add", String.join(System.lineSeparator(),
      "{prefix_error}Nepoznat argument. Koristite:",
      "{prefix_error}&6/&estats set &2<&aigrač&2> <&avrsta&2> <&aiznos&2>")),
  STATISTICS_ADDED("statistics.added", "{prefix_success}Uspešno ste dodali &e{0} {1} &figraču &b{2}&f."),
  STATISTICS_REMOVED("statistics.removed", "{prefix_success}Uspešno ste uklonili &e{0} {1} &figraču &b{2}&f."),
  STATISTICS_SET("statistics.set", "{prefix_success}Uspešno ste postavili &e{0} {1} &figraču &b{2}&f."),
  LEADERBOARD_DEFAULT("leaderboard.default", String.join(System.lineSeparator(),
      "{prefix_error}Izaberite vrstu statistike.",
      "{prefix_error}Vrste: &egoals, assists, yellow-cards, red-cards, clean-sheets")),
  LEADERBOARD_GOALS("leaderboard.goals", String.join(System.lineSeparator(),
      "&9▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f Igrači sa najviše postignutih golova:",
      "&r &r",
      "&r 1. &b{0} &7- &b{1} golova",
      "&r 2. &b{2} &7- &b{3} golova",
      "&r 3. &b{4} &7- &b{5} golova",
      "&r 4. &b{6} &7- &b{7} golova",
      "&r 5. &b{8} &7- &b{9} golova",
      "&r 6. &b{10} &7- &b{11} golova",
      "&r 7. &b{12} &7- &b{13} golova",
      "&r 8. &b{14} &7- &b{15} golova",
      "&r 9. &b{16} &7- &b{17} golova",
      "&r 10. &b{18} &7- &b{19} golova",
      "&9▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),

  TIMER_HELP("timer.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&f Pomoćnik za Timer Komande.",
      "&r &r",
      "&6 /&etimer start &2<&avreme&2> &3[&bprefix&3]",
      "&6 /&etimer stop",
      "&r &r",
      "&7 &oSve komande imaju [TAB] completion, koristite ovo za pomoć!",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  TIMER_CREATE("timer.create", "{prefix_success}Uspešno pokrenut timer sa ID-em &e{0}&f."),
  TIMER_STOP("timer.stop", "{prefix_success}Isključen timer sa ID-em &e{0}&f."),
  TIMER_STARTING("timer.starting", String.join(System.lineSeparator(),
      "&6▎ &r",
      "&6▎ &r    &l{0} &8- &e&lMEČ POČINJE!",
      "&6▎ &r")),
  TIMER_END("timer.end", "{0} &6┃ &fTimer završen!"),
  TIMER_OVER("timer.over", "{prefix_success}Timer sa ID-em &e{0} &fje završen!"),
  TIMER_CURRENT_TIME("timer.current-time", "{0} &7┃ &fTrenutno vreme: &e{1}/{2}"),
  TIMER_NOT_AVAILABLE("timer.not-available", "{prefix_error}Nijedan timer ne radi trenutno."),
  TIMER_ALREADY_RUNNING("timer.already-running", String.join(System.lineSeparator(),
      "{prefix_error}Već postoji jedan timer ili niste podesili sve.",
      "{prefix_error}Podesite &6/&ers teams &ci &6/&ers time 20min&c.")),
  TIMER_PREFIX_SET("timer.prefix-set", "{prefix_success}Prefix podešen na {0}&f."),
  TIMER_TEAMS_SET("timer.teams-set", "{prefix_success}Timovi podešeni na {0} &f(home) i {1}&f (away)."),
  TIMER_TIME_SET("timer.time-set", "{prefix_success}Vreme podešeno na {0}&f."),
  RESULT_HELP("result.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&fPomoćnik za Result Komande:",
      "&r &r",
      "&f &lKomande Podešavanja:",
      "&6 /&ers prefix &2<&aprefix&2> &7(&f/rs prefix &b&lEvent&7)",
      "&6 /&ers teams &2<&ahome&2> <&aaway&2> &7(&f/rs teams nkp vtz&7)",
      "&6 /&ers time &2<&atime&2> &7(&f/rs time 20min&7)",
      "&r &r",
      "&e &lKomande Kontrole:",
      "&6 /&ers start&6|&estop",
      "&6 /&ers pause&6|&eresume",
      "&7 - &fKoristite &epause &fna kraju prvog poluvremena.",
      "&7 - &fKoristite &eresume &fza početak drugog poluvremena.",
      "&6 /&ers extend &2<&atime&2> &7(&f/rs extend 2min30s&7)",
      "&6 /&ers add&6|&erem&6(&e&oove&6) &ehome&6|&eaway &7(&f/rs add vtz&7)",
      "&r &r",
      "&7 &oSve komande imaju [TAB] completion, koristite ovo za pomoć!",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  RESULT_ADD("result.add", String.join(System.lineSeparator(),
      "&6▎ &r",
      "&6▎ &e  &lGOOOOOOOOOOOOL!",
      "&6▎ &b  {0} &rje postigao gol za {1} &rtim!",
      "&6▎ &r")),
  RESULT_ADD_ASSIST("result.add-assist", String.join(System.lineSeparator(),
      "    &6▎ &r",
      "&6▎ &e  &lGOOOOOOOOOOOOL!",
      "&6▎ &b  {0} &rje postigao gol za {1} &rtim!",
      "&6▎ &f  Asistent: &b{2}",
      "&6▎ &r")),
  RESULT_CAPTAINS_SET("result.captains-set", "{prefix_success}Kapiten home &b{0}&f, away &b{1}&f. Podešeno uspešno."),
  RESULT_FULL_LIVES("result.full-lives", "{prefix_error}&f{0} &cveć ima sve živote."),
  RESULT_ELIMINATED("result.eliminated", "{prefix_error}&f{0} &cje već eliminisan."),
  RESULT_ADDED_LIFE("result.added-life", "{prefix_success}Dodat jedan život za tim {0}&f!"),
  RESULT_REMOVED_LIFE("result.removed-life", "{prefix_success}Skinut jedan život za tim {0}&f!"),
  RESULT_REMOVE("result.remove", "{prefix_success}Skinut jedan gol za tim {0}&f."),
  RESULT_USAGE("result.add-usage", "{prefix_error}Morate navesti tim."),
  RESULT_STARTING("result.starting", "{0} &7┃ &fMeč počinje!"),
  RESULT_ACTIONBAR("result.actionbar", "{0} &7┃ &f{1} &e{2} - {3} &f{4} &7┃ &f{5}{6}/{7}{8}"),
  RESULT_ACTIONBAR_HT("result.actionbar-ht", "{0} &7┃ &f{1} &e{2} - {3} &f{4} &7┃ &fPoluvreme!"),
  RESULT_END("result.end", "{0} &7┃ &fMeč završen! Rezultat: &f{1} &e{2} &f- &e{3} &f{4}&f."),
  RESULT_HALFTIME("result.halftime", String.join(System.lineSeparator(),
      "&6▎ &r",
      "&6▎ &e  &lPOLUVREME! {0}",
      "&6▎ &f  Rezultat: &f{2} &e{3} &f- &e{4} &f{5}",
      "&6▎ &r")),
  RESULT_SECONDHALF("result.secondhalf", String.join(System.lineSeparator(),
      "&6▎ &r",
      "&6▎ &e  &lDRUGO POLUVREME! {0}",
      "&6▎ &f  Rezultat: &f{2} &e{3} &f- &e{4} &f{5}",
      "&6▎ &r")),
  RESULT_OVER("result.over", String.join(System.lineSeparator(),
      "&6▎ &r",
      "&6▎ &e  &lMEČ ZAVRŠEN! {0}",
      "&6▎ &f  Rezultat: &f{2} &e{3} &f- &e{4} &f{5}",
      "&6▎ &r")),
  TWO_TIMES_FOUR_HELP("result.2x4.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&6 /&e2x4 start&6|&estop",
      "&6 /&e2x4 type &2<&atype&2> &7- &fEvent type (&o2x4/3x4/...&f).",
      "&6 /&e2x4 add&6/&erem team_color &7(&f/2x4 add blue&7)",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  TWO_TIMES_FOUR_ACTIONBAR("result.2x4.actionbar", "{0} &7┃ &fRezultat: &b{1} &c{2} &a{3} &e{4} &7┃ &fVreme: &e{5}"),
  TWO_TIMES_FOUR_RESULT_OVER("result.2x4.over", "&6▎ {0} &6┃ &fMeč završen! Rezultat: &b{1} &c{2} &a{3} &e{4}&f!"),
  TWO_TIMES_FOUR_RESULT_END("result.2x4.end", "{0} &6┃ &fMeč završen! Rezultat: &b{1} &c{2} &a{3} &e{4}"),
  ONE_TIMES_EIGHT_HELP("result.1x8.help", String.join(System.lineSeparator(),
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r",
      "&6 /&e1x8 start&6|&estop",
      "&6 /&e1x8 type &2<&atype&2> &7- &fEvent type (1x8/2x8/...&f).",
      "&6 /&e1x8 add&6/&erem team_color &7(&f/1x8 rem red&7)",
      "&7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬&r")),
  ONE_TIMES_EIGHT_ACTIONBAR("result.1x8.actionbar", "{0} &7┃ &fRezultat: &8{1} &c{2} &6{3} &e{4} &a{5} &b{6} &d{7} &0{8} &7┃ &fVreme: &e{9}"),
  ONE_TIMES_EIGHT_RESULT_OVER("result.1x8.over", "&6▎ {0} &6┃ &fMeč završen! Rezultat: &8{1} &c{2} &6{3} &e{4} &a{5} &b{6} &d{7} &0{8}&f!"),
  ONE_TIMES_EIGHT_RESULT_END("result.1x8.end", "{0} &6┃ &fMeč završen! Rezultat: &8{1} &c{2} &6{3} &e{4} &a{5} &b{6} &d{7} &0{8}"),
  WEBHOOK_NOT_SETUP("webhook.not-setup", "{prefix_error}Niste dodali URL za Discord Webhook u config.yml!"),
  WEBHOOK_PREFIX_NOT_SETUP("webhook.not-setup-prefix", "{prefix_error}Namestite prefix prvo."),
  WEBHOOK_TEAMS_SET("webhook.teams-set", "{0} Podešeni timovi: **{1} - {2}**."),
  WEBHOOK_MATCH_START("webhook.match-start", "Početak utakmice **{0} - {1}**."),
  WEBHOOK_MATCH_ENDED("webhook.match-ended", "Kraj utakmice, rezultat: **{0} {1} - {2} {3}**. Trajanje: **`{4}`**."),
  WEBHOOK_MATCH_HALFTIME("webhook.match-halftime", "Poluvreme, rezultat: **{0} {1} - {2} {3}**."),
  WEBHOOK_MATCH_SECONDHALF("webhook.match-secondhalf", "Početak drugog poluvremena, rezultat: **{0} {1} - {2} {3}**."),
  WEBHOOK_MATCH_SCORE_COLOR("webhook.match-score.color", "#ffb80c"),
  WEBHOOK_MATCH_SCORE_AUTHOR_NAME("webhook.match-score.author.name", "GOOOOL za {0}"),
  WEBHOOK_MATCH_SCORE_AUTHOR_ICON("webhook.match-score.author.icon", "https://cdn.discordapp.com/emojis/1067135709644865637.webp"),
  WEBHOOK_MATCH_SCORE_DESC("webhook.match-score.description", "<:ArrowRightGray:813815804768026705>Strelac: **{0}**{7}<:ArrowRightGray:813815804768026705>Rezultat: **{3} {4} - {5} {6}**\\n<:ArrowRightGray:813815804768026705>Vreme: `{2}`"),
  WEBHOOK_MATCH_ASSIST("webhook.match-assist", "<:ArrowRightGray:813815804768026705>Strelac: **{0}**\\n<:ArrowRightGray:813815804768026705>Asistent: **{3}**\\n<:ArrowRightGray:813815804768026705>Rezultat: **{4} {5} - {6} {7}**\\n<:ArrowRightGray:813815804768026705>Vreme: `{2}`");

  private static FileConfiguration LANG;
  private final String path, def;

  Lang(String path, String start) {
    this.path = path;
    this.def = start;
  }

  public static void setFile(FileConfiguration config) {
    LANG = config;
  }

  public String getDefault() {
    return this.def;
  }

  public String getConfigValue(String[] args) {
    String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

    if (value.startsWith("{prefix_")) {
      if (value.startsWith("{prefix_success}")) {
        value = value.replace("{prefix_success}", PREFIX_SUCCESS.getConfigValue(null));
      } else if (value.startsWith("{prefix_info}")) {
        value = value.replace("{prefix_info}", PREFIX_INFO.getConfigValue(null));
      } else if (value.startsWith("{prefix_error}")) {
        value = value.replace("{prefix_error}", PREFIX_ERROR.getConfigValue(null));
      } else if (value.startsWith("{prefix_warn}")) {
        value = value.replace("{prefix_warn}", PREFIX_WARN.getConfigValue(null));
      } else if (value.startsWith("{prefix_default}")) {
        value = value.replace("{prefix_default}", PREFIX_DEFAULT.getConfigValue(null));
      }
    }

    if (args == null) return value;
    else {
      if (args.length == 0) return value;
      for (int i = 0; i < args.length; i++) value = value.replace("{" + i + "}", args[i]);
      value = ChatColor.translateAlternateColorCodes('&', value);
    }

    return value;
  }
}
