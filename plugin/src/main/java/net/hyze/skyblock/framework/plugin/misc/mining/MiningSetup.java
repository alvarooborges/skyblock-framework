package net.hyze.skyblock.framework.plugin.misc.mining;

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.cache.local.SkyBlockMiningLocalCache;
import org.bukkit.Bukkit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MiningSetup {

    public static void setup(MiningArea... miningArea) {

        SkyBlockMiningLocalCache miningLocalCache = SkyBlockProvider.Cache.Local.MINING.provide();
        Stream.of(miningArea).forEach(target -> miningLocalCache.insertMiningArea(target));

        Bukkit.getPluginManager().registerEvents(new MiningListeners(), SkyBlockPlugin.getInstance());

        Bukkit.getScheduler().runTaskTimer(SkyBlockPlugin.getInstance(), new MiningRunnable(), 0, 20L);

    }

}
