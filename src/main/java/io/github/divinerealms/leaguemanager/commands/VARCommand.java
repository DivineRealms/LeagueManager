package io.github.divinerealms.leaguemanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.divinerealms.leaguemanager.configs.Lang;
import io.github.divinerealms.leaguemanager.utils.Logger;
import io.github.divinerealms.leaguemanager.utils.Time;
import io.github.divinerealms.leaguemanager.managers.UtilManager;
import io.github.divinerealms.leaguemanager.utils.Helper;
import lombok.Getter;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

@Getter
@CommandAlias("var")
@CommandPermission("leaguemanager.command.var")
public class VARCommand extends BaseCommand {
  private final Logger logger;
  private final Helper helper;

  public VARCommand(final UtilManager utilManager) {
    this.logger = utilManager.getLogger();
    this.helper = utilManager.getHelper();
  }

  @Default
  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("leaguemanager.command.var.help")
  public void onUnknown(CommandSender sender) {
    getLogger().send(sender, Lang.VAR_HELP.getConfigValue(null));
  }

  @Subcommand("add")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.var.add")
  public void onAdd(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.VAR_USAGE_ADD.getConfigValue(null));
    } else if (args.length == 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        User user = getHelper().getPlayer(target.getUniqueId());
        boolean hasAccess = user.getCachedData().getPermissionData().checkPermission("vulcan.bypass.client-brand.whitelist").asBoolean();
        if (!hasAccess) {
          Time time = Time.parseString("1h");
          Node node = Node.builder("vulcan.bypass.client-brand.whitelist").value(true).expiry(time.toMilliseconds(), TimeUnit.MILLISECONDS).build();
          DataMutateResult result = user.data().add(node);
          if (result.wasSuccessful()) {
            getLogger().send("fcfa", Lang.VAR_GIVEN_ACCESS_1.getConfigValue(new String[]{target.getName(), time.toString()}));
          } else {
            getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
          }
        } else getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.VAR_HELP.getConfigValue(null));
  }

  @Subcommand("remove")
  @CommandCompletion("@players")
  @CommandPermission("leaguemanager.command.var.remove")
  public void onRemove(CommandSender sender, String[] args) {
    if (args.length < 1) {
      getLogger().send(sender, Lang.VAR_USAGE_REMOVE.getConfigValue(null));
    } else if (args.length == 2) {
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

      if (target != null && target.hasPlayedBefore()) {
        User user = getHelper().getPlayer(target.getUniqueId());
        Node node = user.getCachedData().getPermissionData().queryPermission("vulcan.bypass.client-brand.whitelist").node();
        if (node != null && node.hasExpiry()) {
          DataMutateResult result = user.data().remove(node);
          if (result.wasSuccessful()) {
            getLogger().send("fcfa", Lang.VAR_REMOVED_ACCESS.getConfigValue(new String[]{target.getName()}));
          } else {
            getLogger().send(sender, Lang.VAR_ALREADY_HAS_ACCESS.getConfigValue(new String[]{target.getName()}));
          }
        } else getLogger().send(sender, Lang.VAR_NO_ACCESS.getConfigValue(new String[]{target.getName()}));
      } else getLogger().send(sender, Lang.USER_NOT_FOUND.getConfigValue(null));
    } else getLogger().send(sender, Lang.VAR_HELP.getConfigValue(null));
  }
}
