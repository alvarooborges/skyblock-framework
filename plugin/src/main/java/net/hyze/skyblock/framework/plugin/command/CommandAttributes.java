package net.hyze.skyblock.framework.plugin.command;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.attributes.AttributeData;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.plugin.SkyBlockCustomPlugin;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.inventory.AttributesInventory;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandAttributes extends CustomCommand implements GroupCommandRestrictable {

    private final SkyBlockCustomPlugin plugin;

    public CommandAttributes(SkyBlockCustomPlugin plugin) {
        super("attributes", CommandRestriction.IN_GAME, "atributos");

        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        Player player = (Player) sender;

        SkyBlockUser skyblockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(user);

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(skyblockUser.getId());
        Profile profile = profileData.getSelectedProfile();

        AttributeData attributeData = SkyBlockApiProvider.Cache.Local.USERS_ATTRIBUTES.provide().get(profile);
        player.openInventory(new AttributesInventory(skyblockUser, attributeData));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
