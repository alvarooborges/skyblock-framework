package net.hyze.skyblock.framework.plugin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.CoreProvider.Redis;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.spigot.CustomPlugin;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.slime.EnumSlimeLoaders;
import net.hyze.skyblock.framework.api.status.SkyBlockStatus;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider.Cache.Local;
import net.hyze.skyblock.framework.plugin.command.CommandAttributes;
import net.hyze.skyblock.framework.plugin.command.CommandMigrate;
import net.hyze.skyblock.framework.plugin.command.CommandProfile;
import net.hyze.skyblock.framework.plugin.slime.SkyBlockSlimeWorld;
import net.hyze.slime.SlimeManager;
import net.hyze.slime.exceptions.CorruptedWorldException;
import net.hyze.slime.exceptions.NewerFormatException;
import net.hyze.slime.exceptions.UnknownWorldException;
import net.hyze.slime.exceptions.WorldInUseException;
import net.hyze.slime.loaders.LoaderUtils;
import net.hyze.slime.world.SlimeWorld;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class SkyBlockCustomPlugin extends CustomPlugin {

    protected final Server server;

    protected final Settings settings;

    //
    @Getter
    private SlimeManager slimeManager;

    //
    @Getter
    private SkyBlockStatus status;

    //
    @Getter
    private final Set<World> islands = Sets.newHashSet();

    private BukkitTask worldCleanupTask;

    public SkyBlockCustomPlugin(@NonNull Server server, Settings settings) {
        super(true);

        this.server = server;
        this.settings = settings;

        this.slimeManager = new SlimeManager(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (AppType.SKYBLOCK_ISLANDS.isCurrent()) {
            String slaveId = System.getProperty("net.hyze.skyblock.slave");

            App app = CoreProvider.getApp();
            app.setStatus(status = new SkyBlockStatus(
                    slaveId == null ? "NULL" : slaveId,
                    app.getId(),
                    app.getType(),
                    app.getServer(),
                    app.getAddress(),
                    System.currentTimeMillis(), 0, 0, 0, false
            ));
        }

        SkyBlockProvider.prepare(settings.getMongoProvider(), settings.getMysqlProvider(), settings.getRedisProvider());

        String worldFid = this.settings.getMainHubFid();

        try {
            SlimeWorld world;

            if (!AppType.SKYBLOCK_ISLANDS.isCurrent()) {
                byte[] data = EnumSlimeLoaders.SEAWEED.getLoader().loadWorld(worldFid, true);

                SkyBlockSlimeWorld skyBlockSlimeWorld = new SkyBlockSlimeWorld(LoaderUtils
                        .deserializeWorld(EnumSlimeLoaders.SEAWEED.getLoader(), "world", data));
                skyBlockSlimeWorld.setCurrentMillis(System.currentTimeMillis());

                world = skyBlockSlimeWorld;
            } else {
                world = new SlimeWorld(EnumSlimeLoaders.SEAWEED.getLoader(),
                        "world",
                        Maps.newHashMap(),
                        new NBTTagCompound(),
                        false);
            }

            world.getProperties().setReadOnly(true);

            this.slimeManager.setDefaultWorlds(world, null, null);
        } catch (IOException | UnknownWorldException | WorldInUseException | CorruptedWorldException | NewerFormatException ex) {
            ex.printStackTrace();
            Bukkit.shutdown();
            return;
        }
    }

    @Override
    public void onEnable() {
        this.slimeManager.init();

        super.onEnable();

        World defaultWorld = Bukkit.getWorld("world");
        defaultWorld.setGameRuleValue("doDaylightCycle", "false");
        NMS.getWorld(defaultWorld).paperSpigotConfig.disablePlayerCrits = true;

        this.worldCleanupTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            Iterator<World> iterator = islands.iterator();
            while (iterator.hasNext()) {
                World world = iterator.next();

                if (Cooldowns.hasEnded("INIT_ISLAND_" + world.getName()) && world.getPlayers().isEmpty()) {
                    iterator.remove();
                    unloadWorld(world);
                }
            }
        }, 0L, 20 * 3);

        CommandRegistry.registerCommand(new CommandProfile(this));
        CommandRegistry.registerCommand(new CommandMigrate(this));
        CommandRegistry.registerCommand(new CommandAttributes(this));

        Redis.ECHO.provide().registerListener(new SkyBlockPacketListener(this));

        if (AppType.SKYBLOCK_ISLANDS.isCurrent()) {
            SkyBlockApiProvider.Cache.Redis.SKYBLOCK_SERVERS.provide().insertServer(CoreProvider.getApp().getId());
            SkyBlockApiProvider.Cache.Redis.SKYBLOCK_SERVERS.provide().insertSlaveServer(status.getSlaveId(), CoreProvider.getApp().getId());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.worldCleanupTask != null) {
            worldCleanupTask.cancel();
        }

        for (World world : this.islands) {
            unloadWorld(world);
        }

        this.islands.clear();
    }

    protected void unloadWorld(World world) {
        Island island = SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().fetchById(world.getName());
        Bukkit.unloadWorld(world, (island != null));

        SkyBlockApiProvider.Cache.Redis.SKYBLOCK_SERVERS.provide().destroyServerIsland(CoreProvider.getApp().getId(), world.getName());
    }
}
