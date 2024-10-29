package net.hyze.skyblock.framework.plugin.craftingtable.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface IRecipe {

    Map<Character, ItemStack> getIngredientsMap();

    boolean matches(ItemStack[][] inputs);

    ItemStack getResult(ItemStack[][] inputs);

    ItemStack[] getRemainingItems(ItemStack[][] inputs);

    default void addIngredient(char c, ItemStack ingredient) {
        getIngredientsMap().put(c, ingredient);
    }
}
