package net.hyze.skyblock.framework.plugin.command;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.plugin.SkyBlockCustomPlugin;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.inventory.ProfileInventory;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandProfile extends CustomCommand implements GroupCommandRestrictable {

    private final SkyBlockCustomPlugin plugin;

    public CommandProfile(SkyBlockCustomPlugin plugin) {
        super("profile", CommandRestriction.IN_GAME, "perfil");

        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        Player player = ((Player) sender);
        SkyBlockUser skyBlockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(user);

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
        if (profileData == null) {
            Message.ERROR.send(sender, "Você não tem um perfil no skyblock.");
            return;
        }

        player.openInventory(new ProfileInventory(skyBlockUser));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
