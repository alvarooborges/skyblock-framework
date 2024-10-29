package net.hyze.skyblock.framework.plugin.misc.woodcut;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class WoodCutListeners implements Listener {

    private final WoodCutArea[] woodCurtAreas;

    private final List<Material> materials = Lists.newArrayList(
            Material.LEAVES,
            Material.LEAVES_2,
            Material.LOG,
            Material.LOG_2
    );

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void on(BlockBreakEvent event) {
        Block block = event.getBlock();

        for (WoodCutArea woodCutArea : this.woodCurtAreas) {

            WorldCuboid cuboid = woodCutArea.getCuboid();

            if (cuboid.contains(block.getLocation(), true) && this.materials.contains(block.getType())) {
                event.setCancelled(false);
                break;
            }
        }
    }

}
