package net.hyze.skyblock.framework.plugin.command.purse;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.plugin.misc.currency.Currency;
import org.bukkit.command.CommandSender;

public class CommandPurse extends CustomCommand implements GroupCommandRestrictable {

    public CommandPurse() {
        super("carteira", CommandRestriction.IN_GAME);

        registerSubCommand(new CommandAddPurse());
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
        if (profileData == null) {
            Message.ERROR.send(sender, "Você não tem um perfil no skyblock.");
            return;
        }

        Profile selectedProfile = profileData.getSelectedProfile();

        Message.INFO.send(sender, String.format("Você tem %s em sua carteira.", Currency.COINS.format(selectedProfile.getPurse())));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
