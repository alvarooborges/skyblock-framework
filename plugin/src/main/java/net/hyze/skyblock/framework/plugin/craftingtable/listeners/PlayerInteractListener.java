package net.hyze.skyblock.framework.plugin.craftingtable.listeners;

import net.hyze.skyblock.framework.plugin.craftingtable.inventories.CraftingTableInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getClickedBlock().getType() == Material.WORKBENCH && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);

            player.closeInventory();
            player.openInventory(new CraftingTableInventory(player));
        }
    }
}
