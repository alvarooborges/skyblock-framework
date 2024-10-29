package net.hyze.skyblock.framework.plugin.craftingtable;

import org.bukkit.inventory.ItemStack;

public class CraftingManager {

    public static ItemStack[] flattenInputs(ItemStack[][] inputs) {
        ItemStack[] out = new ItemStack[inputs.length * inputs.length];

        int i = 0;
        for (ItemStack[] stacks : inputs) {
            for (ItemStack stack : stacks) {
                out[i] = stack;
            }
        }

        return out;
    }
}
