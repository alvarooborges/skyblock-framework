package net.hyze.skyblock.framework.plugin.listeners.player;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.hyze.skyblock.framework.plugin.utils.AttributeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();
        SkyBlockUser skyblockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(name);

        SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().remove(skyblockUser.getId());

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(skyblockUser.getId());

        if (profileData == null || profileData.getSelectedProfile() == null) {
            event.disallow(Result.KICK_OTHER, "Você não tem um perfil de skyblock. Informe um administrador!");
            return;
        }

        SkyBlockApiProvider.Cache.Local.USERS_ATTRIBUTES.provide().remove(profileData.getSelectedProfile());
    }

    @EventHandler
    public void on(PlayerSpawnLocationEvent event) {
        if (!AppType.SKYBLOCK_ISLANDS.isCurrent()) {
            return;
        }

        Player player = event.getPlayer();
        SkyBlockUser skyblockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(player);

        if (skyblockUser.getLocalIslandId() == null) {
            return;
        }

        Island island = SkyBlockApiProvider.Cache.Local.ISLANDS.provide()
                .get(skyblockUser.getLocalIslandId());
        SerializedLocation spawn = island.getSpawnLocation();

        World world = Bukkit.getWorld(island.getId());
        world.getWorldBorder().setSize(200);

        Location location = new Location(
                world,
                spawn.getX(),
                spawn.getY(),
                spawn.getZ(),
                spawn.getYaw(),
                spawn.getPitch()
        );

        event.setSpawnLocation(location);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        SkyBlockUser skyblockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(player);

        if (skyblockUser == null) {
            return;
        }

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(skyblockUser.getId());
        Profile profile = profileData.getSelectedProfile();

        AttributeUtils.manageAttributes(profile);

        SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().updateLastApp(skyblockUser.getId(), profile, CoreProvider.getApp().getType());

    }

}
