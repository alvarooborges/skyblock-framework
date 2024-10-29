package net.hyze.skyblock.framework.plugin.utils;

import java.io.IOException;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.CoreProvider.Redis;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.packet.SkyBlockJoinQueuePacket;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.api.user.profile.ProfileUser;
import net.hyze.skyblock.framework.api.utils.SeaweedUtils;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.inventory.IslandInventory;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.hyze.slime.exceptions.UnknownWorldException;
import net.hyze.slime.exceptions.WorldInUseException;
import org.bukkit.entity.Player;

public class IslandUtils {

    public static void sendToIslandByCommand(Player player, User user) {
        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
        if (profileData == null) { // TODO MOVER PARA A LOGICA INICIAL DO SKYBLOCK
            try {
                String islandId = SeaweedUtils.newIslandId();
                profileData = ProfileData.empty(islandId);

                Island island = new Island(islandId, false, new ProfileUser(user.getId(), profileData.getSelectedProfile().getId()));
                SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().create(island);

                SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().create(user.getId(), profileData);
            } catch (IOException | UnknownWorldException | WorldInUseException ex) {
                ex.printStackTrace();
                Message.ERROR.send(player, "Ocorreu um erro a criar uma nova ilha.");
                return;
            }
        }

        SkyBlockUser skyBlockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(user);
        String islandId = profileData.getSelectedProfile().getIslandId();
        Island island = SkyBlockApiProvider.Cache.Local.ISLANDS.provide().get(islandId);

        if (AppType.SKYBLOCK_ISLANDS.isCurrent() && skyBlockUser.getLocalIslandId().equals(islandId)) {
            player.openInventory(new IslandInventory(skyBlockUser, island));
            return;
        }

        IslandUtils.sendToIsland(player, user, island);
    }

    public static void sendToIsland(Player player, User user, Island island) {
        Server server = CoreProvider.getApp().getServer();
        String masterId = String.format("skyblock-master-%s", server.getAbbreviation());

        AppStatus status_ = CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(masterId, AppStatus.class);
        if (status_ == null) {
            Message.ERROR.send(player, "O servidor de ilhas está temporariamente desativado. Tente novamente mais tarde.");
            return;
        }

        App app = new App(status_.getAppId(), "Skyblock", status_.getType(), status_.getAddress(), status_.getServer());

        SkyBlockJoinQueuePacket packet = new SkyBlockJoinQueuePacket(user.getId(), island.getId());
        Redis.ECHO.provide().publish(packet, app, (response) -> {
            if (response.isSuccess()) {
                Message.SUCCESS.send(player, "Sucesso! Sua posição na fila: &e#" + response.getQueuePosition());
            } else {
                Message.ERROR.send(player, "O servidor de ilhas está temporariamente desativado. Tente novamente mais tarde.");
            }
        });
    }

}
