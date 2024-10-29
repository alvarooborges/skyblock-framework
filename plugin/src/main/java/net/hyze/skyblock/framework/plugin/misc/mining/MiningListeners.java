package net.hyze.skyblock.framework.plugin.misc.mining;

import java.util.Map;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void on(BlockBreakEvent event) {
        Map<WorldCuboid, MiningArea> miningAreas = SkyBlockProvider.Cache.Local.MINING.provide().getMiningArea();

        Block block = event.getBlock();

        for (WorldCuboid cuboid : miningAreas.keySet()) {
            if (cuboid.contains(block.getLocation(), true)) {
                if (block.getType().equals(Material.COBBLESTONE)) {

                    Bukkit.getScheduler().runTaskLater(SkyBlockPlugin.getInstance(), () -> {
                        block.setType(Material.BEDROCK);
                    }, 1L);

                    event.setCancelled(false);
                    break;
                }

                MiningArea miningArea = miningAreas.get(cuboid);

                if (miningArea.containsBlock(block)) {
                    SkyBlockProvider.Cache.Local.MINING.provide().insertBreakedBlock(block);

                    Bukkit.getScheduler().runTaskLater(SkyBlockPlugin.getInstance(), () -> {
                        block.setType(Material.COBBLESTONE);
                    }, 1L);

                    event.setCancelled(false);
                    break;
                }

                event.setCancelled(true);
                break;
            }
        }
    }

}
