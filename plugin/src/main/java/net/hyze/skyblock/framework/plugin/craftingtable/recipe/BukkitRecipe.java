package net.hyze.skyblock.framework.plugin.craftingtable.recipe;

import lombok.Getter;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import net.hyze.skyblock.framework.plugin.craftingtable.inventories.CraftInventoryCrafting;
import net.minecraft.server.v1_8_R3.InventoryCrafting;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public abstract class BukkitRecipe<T extends net.minecraft.server.v1_8_R3.IRecipe> extends Recipe {

    @Getter
    protected final T handle;

    public BukkitRecipe(T handle) {
        this.handle = handle;
    }

    @Override
    public ItemStack[] getRemainingItems(ItemStack[][] inputs) {
        CraftInventoryCrafting crafting = createVirtualCrafting(inputs);

        net.minecraft.server.v1_8_R3.ItemStack[] nmsStacks = this.handle.b((InventoryCrafting) crafting.getMatrixInventory());

        for (int index = 0; index < nmsStacks.length; index++) {
            net.minecraft.server.v1_8_R3.ItemStack item = crafting.getHandle().getItem(index);
            net.minecraft.server.v1_8_R3.ItemStack craftResult = nmsStacks[index];

            if (item != null) {
                crafting.getHandle().splitStack(index, 1);
            }

            if (craftResult != null) {
                if (crafting.getHandle().getItem(index) == null) {
                    crafting.getHandle().setItem(index, craftResult);
                }
            }
        }

        return ItemStackUtils.asBukkitCopy(crafting.getHandle().getContents(), true);
    }

    protected CraftInventoryCrafting createVirtualCrafting(ItemStack[][] inputs) {
        CraftInventoryCrafting crafting = new CraftInventoryCrafting();

        int slot = 0;

        for (ItemStack[] input : inputs) {
            for (ItemStack ingredient : input) {
                if (ingredient != null && ingredient.getType() != Material.AIR) {
                    crafting.getMatrixInventory().setItem(slot++, CraftItemStack.asNMSCopy(ingredient));
                } else {
                    crafting.getMatrixInventory().setItem(slot++, null);
                }
            }
        }

        return crafting;
    }
}
