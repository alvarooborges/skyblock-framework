package net.hyze.skyblock.framework.plugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.shared.echo.packets.user.UserConnectPacket;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectHandShakePacket;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.packet.SkyBlockIslandRequestPacket;
import net.hyze.skyblock.framework.api.slime.EnumSlimeLoaders;
import net.hyze.skyblock.framework.api.status.IslandStatus;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider.Cache;
import net.hyze.skyblock.framework.plugin.slime.SkyBlockSlimeWorld;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.hyze.slime.exceptions.CorruptedWorldException;
import net.hyze.slime.exceptions.NewerFormatException;
import net.hyze.slime.exceptions.UnknownWorldException;
import net.hyze.slime.exceptions.WorldInUseException;
import net.hyze.slime.loaders.LoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

@RequiredArgsConstructor
public class SkyBlockPacketListener implements EchoListener {

    private final SkyBlockCustomPlugin plugin;

    @Subscribe
    public void on(UserConnectPacket packet) {
        CoreSpigotConstants.DEFAULT_USER_CONNECT_PACKET_CONSUMER.accept(packet);
    }

    @Subscribe
    public void on(SkyBlockIslandRequestPacket packet) {
        if (!CoreProvider.getApp().getId().equals(packet.getServerId())) {
            return;
        }

        String islandId = packet.getIslandId();
        Island island = SkyBlockApiProvider.Cache.Local.ISLANDS.provide().get(islandId);

        Printer.INFO.print("Received island packet " + islandId);
        if (island == null) {
            return;
        }

        int userId = packet.getUserId();
        SkyBlockUser skyblockUser = Cache.Local.USERS.provide().get(userId);

        World world = Bukkit.getWorld(island.getId());

        if (world == null) {
            SkyBlockSlimeWorld skyBlockSlimeWorld;
            Printer.INFO.print("Creating world...");

            try {
                byte[] worldBytes = EnumSlimeLoaders.SEAWEED.getLoader().loadWorld(island.getId(), false);
                skyBlockSlimeWorld = new SkyBlockSlimeWorld(LoaderUtils.deserializeWorld(
                    EnumSlimeLoaders.SEAWEED.getLoader(), island.getId(), worldBytes));

                plugin.getSlimeManager().generateWorld(skyBlockSlimeWorld);

                world = Bukkit.getWorld(island.getId());

                skyblockUser.setLocalIslandId(island.getId());
            } catch (IOException | UnknownWorldException | WorldInUseException | CorruptedWorldException | NewerFormatException e) {
                e.printStackTrace();

                Bukkit.unloadWorld(island.getId(), false);
                return;
            }

            world.setGameRuleValue("doDaylightCycle", "false");
            NMS.getWorld(world).paperSpigotConfig.disablePlayerCrits = true;

            long previousMillis = island.getTimeMillis();
            skyBlockSlimeWorld.setCurrentMillis(previousMillis);

            final World finalWorld = world;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Printer.INFO
                    .print("Speeding up island: " + UserCooldowns.getFormattedTimeLeft(System.currentTimeMillis() - previousMillis));
                skyBlockSlimeWorld.speedup(finalWorld, System.currentTimeMillis());

                Bukkit.getScheduler().runTask(plugin, () -> {
                    IslandStatus status = new IslandStatus(CoreProvider.getApp().getId());
                    SkyBlockApiProvider.Cache.Redis.SKYBLOCK_SERVERS.provide().insertServerIsland(status.getServerId(), islandId);
                    SkyBlockApiProvider.Cache.Redis.SKYBLOCK_ISLANDS.provide().insertIsland(islandId, status);
                    sendToIsland(finalWorld, skyblockUser, island);
                });
            });

            return;
        }

        sendToIsland(world, skyblockUser, island);
    }

    private void sendToIsland(World world, SkyBlockUser skyBlockUser, Island island) {
        Printer.INFO.print("Sending to world...");
        Cooldowns.start("INIT_ISLAND_" + world.getName(), 10, TimeUnit.SECONDS);

        final World finalWorld = world;
        plugin.getIslands().add(finalWorld);

        Player player = skyBlockUser.getPlayer();
        if (player != null) {
            SerializedLocation spawn = island.getSpawnLocation();
            player.teleport(new Location(finalWorld, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));
        } else {
            UserConnectHandShakePacket handshakePacket = new UserConnectHandShakePacket(skyBlockUser.getHandle(), CoreProvider.getApp().getId(), ConnectReason.PLUGIN);
            CoreProvider.Redis.ECHO.provide().publish(handshakePacket);
        }
    }

}
