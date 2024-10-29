package net.hyze.skyblock.framework.plugin.misc.mining;

import com.google.common.collect.Table;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.cache.local.SkyBlockMiningLocalCache;
import org.bukkit.Location;
import org.bukkit.material.MaterialData;

public class MiningRunnable implements Runnable {

    @Override
    public void run() {

        SkyBlockMiningLocalCache miningLocalCache = SkyBlockProvider.Cache.Local.MINING.provide();

        Table<Long, Location, MaterialData> breakedBlocks = miningLocalCache.getBreakedBlocks(15000L);

        breakedBlocks.rowKeySet().stream().forEach(breakedTime -> {
            breakedBlocks.row(breakedTime).forEach((location, materialData) -> {
                location.getBlock().setType(materialData.getItemType());
                location.getBlock().setData(materialData.getData());
                miningLocalCache.getBreakedBlocks().remove(breakedTime, location);
            });
        });

    }

}
