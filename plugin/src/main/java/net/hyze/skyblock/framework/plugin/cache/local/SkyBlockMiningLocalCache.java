package net.hyze.skyblock.framework.plugin.cache.local;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Map;
import lombok.Getter;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.skyblock.framework.plugin.misc.mining.MiningArea;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public class SkyBlockMiningLocalCache implements LocalCache {

    @Getter
    private final Table<Long, Location, MaterialData> breakedBlocks = HashBasedTable.create();

    @Getter
    private final Map<WorldCuboid, MiningArea> miningArea = Maps.newHashMap();

    public void insertBreakedBlock(Block block) {
        this.breakedBlocks.put(System.currentTimeMillis(), block.getLocation(), new MaterialData(block.getType(), block.getData()));
    }

    public Table<Long, Location, MaterialData> getBreakedBlocks(Long delay) {
        Table<Long, Location, MaterialData> out = HashBasedTable.create();

        this.breakedBlocks.rowKeySet().stream()
                .filter(breakedTime -> (System.currentTimeMillis() - breakedTime) > delay)
                .forEach(breakedTime -> {
                    this.breakedBlocks.row(breakedTime).forEach((location, materialData) -> {
                        out.put(breakedTime, location, materialData);
                    });
                });

        return out;
    }

    public void insertMiningArea(MiningArea miningArea) {
        this.miningArea.put(miningArea.getCuboid(), miningArea);
    }

}
