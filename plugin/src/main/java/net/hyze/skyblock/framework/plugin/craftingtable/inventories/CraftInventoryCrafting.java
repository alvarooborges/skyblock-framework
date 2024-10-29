package net.hyze.skyblock.framework.plugin.craftingtable.inventories;

import net.minecraft.server.v1_8_R3.InventoryCraftResult;

public class CraftInventoryCrafting extends org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCrafting {

    public CraftInventoryCrafting() {
        super(new InventoryCrafting(), new InventoryCraftResult());

        InventoryCrafting crafting = (InventoryCrafting) inventory;
        crafting.resultInventory = getResultInventory();
    }

    public InventoryCrafting getHandle() {
        return (InventoryCrafting) getMatrixInventory();
    }
}
