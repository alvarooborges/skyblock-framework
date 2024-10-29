package net.hyze.skyblock.framework.plugin.command;

import java.io.IOException;
import java.util.Collections;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.CoreProvider.Cache;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.echo.packets.SendMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.user.coop.CoopData;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.api.user.profile.ProfileUser;
import net.hyze.skyblock.framework.api.utils.SeaweedUtils;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.slime.exceptions.UnknownWorldException;
import net.hyze.slime.exceptions.WorldInUseException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandCoop extends CustomCommand implements GroupCommandRestrictable {

    private final SkyBlockPlugin plugin;

    public CommandCoop(SkyBlockPlugin plugin) {
        super("coop", CommandRestriction.IN_GAME);

        this.plugin = plugin;

        registerArgument(new Argument("nick", "Jogador a iniciar um novo perfil coop"));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        Player player = ((Player) sender);
        String targetName = args[0];

        if (targetName.equalsIgnoreCase(player.getName())) {
            Message.ERROR.send(sender, "Você não pode interagir consigo mesmo!");
            return;
        }

        User targetUser = Cache.Local.USERS.provide().get(targetName);

        if (targetUser == null) {
            Message.ERROR.send(sender, "Esse jogador não existe!");
            return;
        }

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
        if (profileData == null) {
            Message.ERROR.send(sender, "Você não tem um perfil de skyblock.");
            return;
        }

        ProfileData targetProfileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(targetUser.getId());
        if (targetProfileData == null) {
            Message.ERROR.send(sender, "Esse jogador não tem um perfil de skyblock.");
            return;
        }

        if (profileData.getProfiles().size() >= 5) {
            Message.ERROR.send(sender, "Você não tem vagas de perfil.");
            return;
        }

        if (targetProfileData.getProfiles().size() >= 5) {
            Message.ERROR.send(sender, "Esse jogador não tem vagas de perfil.");
            return;
        }

        CoopData coopData = SkyBlockApiProvider.Cache.Local.USERS_COOP.provide().get(user.getId());
        if (coopData.getSentInvitations().contains(targetUser.getId())) {
            Message.ERROR.send(sender, "Você já convidou esse jogador!");
            return;
        }

        CoopData targetCoopData = SkyBlockApiProvider.Cache.Local.USERS_COOP.provide().get(targetUser.getId());

        if (coopData.getReceivedInvitations().contains(targetUser.getId())) {
            targetCoopData.getSentInvitations().remove(user.getId());
            coopData.getReceivedInvitations().remove(targetUser.getId());

            SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().coop().remove(targetUser.getId(), user.getId());

            // Profile management

            try {
                String islandId = SeaweedUtils.newIslandId();
                Profile profile = Profile.empty(islandId);
                Profile targetProfile = Profile.empty(islandId);

                profileData.getProfiles().add(profile);
                targetProfileData.getProfiles().add(targetProfile);

                Island island = new Island(islandId, true, new ProfileUser(targetUser.getId(), targetProfile.getId()), new ProfileUser(user.getId(), profile.getId()));
                SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().create(island);

                SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().insert(user.getId(), profile);
                SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().insert(targetUser.getId(), targetProfile);

                SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().create(user.getId(), profileData);
            } catch (IOException | UnknownWorldException | WorldInUseException ex) {
                ex.printStackTrace();
                Message.ERROR.send(player, "Ocorreu um erro a criar uma nova ilha.");
                return;
            }

            //

            Message.SUCCESS.send(sender, "Convite aceitado com sucesso!");

            ComponentBuilder builder = new ComponentBuilder(
                String.format("%s &aceitou o seu pedido para uma sessão de coop\n", user.getHighestGroup().getDisplayTag(user.getNick())));

            SendMessagePacket sendMessagePacket = new SendMessagePacket(Collections.singleton(targetUser), builder.create());
            CoreProvider.Redis.ECHO.provide().publish(sendMessagePacket);
            return;
        }

        targetCoopData.getReceivedInvitations().add(user.getId());
        coopData.getSentInvitations().add(targetUser.getId());
        SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().coop().invite(user.getId(), targetUser.getId());

        Message.SUCCESS.send(sender, "Convite enviado com sucesso!");

        ComponentBuilder builder = new ComponentBuilder(
            String.format("%s &econvidou você para uma sessão de coop", user.getHighestGroup().getDisplayTag(user.getNick())))
            .append("\nClique ").color(ChatColor.YELLOW)
            .append("AQUI").color(ChatColor.GREEN).bold(true).event(new ClickEvent(Action.RUN_COMMAND, "/coop " + user.getNick()))
            .append(" para aceitar!").color(ChatColor.YELLOW);

        SendMessagePacket sendMessagePacket = new SendMessagePacket(Collections.singleton(targetUser), builder.create());
        CoreProvider.Redis.ECHO.provide().publish(sendMessagePacket);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
