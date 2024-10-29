package net.hyze.skyblock.framework.plugin.craftingtable.recipe;

import lombok.Getter;
import net.hyze.skyblock.framework.plugin.craftingtable.inventories.CraftInventoryCrafting;
import net.minecraft.server.v1_8_R3.InventoryCrafting;
import net.minecraft.server.v1_8_R3.ShapedRecipes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

public class BukkitShapedRecipe extends BukkitRecipe<ShapedRecipes> implements IShapedRecipe {

    @Getter
    private final Character[][] shape;

    public BukkitShapedRecipe(ShapedRecipes handle) {
        super(handle);

        ShapedRecipe bukkitRecipe = this.handle.toBukkitRecipe();

        this.shape = new Character[bukkitRecipe.getShape().length][];

        for (int x = 0; x < shape.length; x++) {
            String[] row = bukkitRecipe.getShape()[x].split("");

            shape[x] = new Character[row.length];

            for (int z = 0; z < shape[x].length; z++) {
                char c = row[z].toCharArray()[0];
                ItemStack ingredient = bukkitRecipe.getIngredientMap().get(c);
                if (ingredient != null && ingredient.getType() != Material.AIR) {
                    shape[x][z] = c;
                } else {
                    shape[x][z] = null;
                }
            }
        }

        for (Map.Entry<Character, ItemStack> entry : bukkitRecipe.getIngredientMap().entrySet()) {
            if (entry.getValue() != null && entry.getValue().getType() != Material.AIR) {
                this.addIngredient(entry.getKey(), entry.getValue());
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
