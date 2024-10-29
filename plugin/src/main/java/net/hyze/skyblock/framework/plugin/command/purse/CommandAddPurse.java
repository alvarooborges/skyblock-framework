package net.hyze.skyblock.framework.plugin.command.purse;

import com.google.common.primitives.Doubles;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.misc.currency.Currency;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.hyze.skyblock.framework.plugin.user.profile.ProfileCurrencty;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddPurse extends CustomCommand implements GroupCommandRestrictable {

    public CommandAddPurse() {
        super("add", CommandRestriction.IN_GAME);

        registerArgument(new Argument("nick", "Nick do jogador que deseja adicionar os coins.", true));
        registerArgument(new Argument("quantia", "Quantidade de coins que deseja adicionar.", true));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            Message.ERROR.send(sender, "O jogador informado não está online.");
            return;
        }

        Double amount = Doubles.tryParse(args[1]);

        if (amount == null) {
            Message.ERROR.send(sender, "O valor informado não é um double.");
            return;
        }

        SkyBlockUser skyBlockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(player);

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(skyBlockUser.getId());

        if (profileData == null) {
            Message.ERROR.send(sender, "Este jogador não tem um perfil no skyblock.");
            return;
        }

        Profile selectedProfile = profileData.getSelectedProfile();

        ProfileCurrencty.incrementPurse(skyBlockUser, selectedProfile, amount);

        Message.INFO.send(sender, String.format("Você adicionou %s na cardeira de %s.", Currency.COINS.format(amount), skyBlockUser.getNick()));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
