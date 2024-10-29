package net.hyze.skyblock.framework.plugin.craftingtable.recipe;

import net.hyze.skyblock.framework.plugin.craftingtable.inventories.CraftInventoryCrafting;
import net.minecraft.server.v1_8_R3.InventoryCrafting;
import net.minecraft.server.v1_8_R3.ShapelessRecipes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class BukkitShapelessRecipe extends BukkitRecipe<ShapelessRecipes> {

    public BukkitShapelessRecipe(ShapelessRecipes handle) {
        super(handle);

        ShapelessRecipe bukkitRecipe = this.handle.toBukkitRecipe();

        int charI = 0;
        for (ItemStack stack : bukkitRecipe.getIngredientList()) {
            if (stack != null && stack.getType() != Material.AIR) {
                char cc = (char) charI++;
                while (cc == RecipeRegistry.WRONG_CHAR || getIngredientsMap().containsKey(cc)) {
                    cc = (char) charI++;
                }

                addIngredient(cc, stack);
            }
        }
    }

    @Override
    public boolean matches(ItemStack[][] inputs) {
        CraftInventoryCrafting crafting = this.createVirtualCrafting(inputs);

        return this.handle.a((InventoryCrafting) crafting.getMatrixInventory(), null);
    }

    @Override
    public ItemStack getResult(ItemStack[][] inputs) {
        CraftInventoryCrafting crafting = this.createVirtualCrafting(inputs);

        net.minecraft.server.v1_8_R3.ItemStack nms = this.handle.craftItem((InventoryCrafting) crafting.getMatrixInventory());

        ItemStack out = CraftItemStack.asBukkitCopy(nms);

        return out.getType() == Material.AIR ? null : out;
    }
}
