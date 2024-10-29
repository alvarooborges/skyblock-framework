package net.hyze.skyblock.framework.plugin.craftingtable.inventories;

import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.inventory.InventoryView;

public class InventoryCrafting extends net.minecraft.server.v1_8_R3.InventoryCrafting {

    public InventoryCrafting() {
        super(new Container() {
            public boolean a(EntityHuman entityhuman) {
                return false;
            }

            @Override
            public InventoryView getBukkitView() {
                return null;
            }
        }, 3, 3, null);
    }
}
