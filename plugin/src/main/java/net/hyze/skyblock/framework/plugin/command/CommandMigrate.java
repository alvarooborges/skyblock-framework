package net.hyze.skyblock.framework.plugin.command;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider.Repositories;
import net.hyze.skyblock.framework.plugin.SkyBlockCustomPlugin;
import org.bukkit.command.CommandSender;


public class CommandMigrate extends CustomCommand implements GroupCommandRestrictable  {

  private final SkyBlockCustomPlugin plugin;

  public CommandMigrate(SkyBlockCustomPlugin plugin) {
    super("migrate", CommandRestriction.CONSOLE, "migrar");

    this.plugin = plugin;
  }

  @Override
  public void onCommand(CommandSender sender, User user, String label, String[] args) {
    Repositories.SKYBLOCK_USERS.provide().profiles().migrate();
  }

  @Override
  public Group getGroup() {
    return Group.GAME_MASTER;
  }
}
