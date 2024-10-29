package net.hyze.skyblock.framework.plugin.misc.merchant.prices;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Iterator;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MerchantPrice {

    private static final Map<ItemStack, Double> CACHED_PRICES = Maps.newHashMap();

    static {
        /**
         * Cadastra todos os preços de itens que são craftáveis.
         */
        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            AtomicDouble price = new AtomicDouble();

            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shapedRecipes = (ShapedRecipe) recipe;

                shapedRecipes.getIngredientMap().values().forEach(itemStack -> {
                    price.addAndGet(MerchantIngredientPrice.getIngredientPrice(itemStack));
                });
            } else if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;

                shapelessRecipe.getIngredientList().forEach(itemStack -> {
                    price.addAndGet(MerchantIngredientPrice.getIngredientPrice(itemStack));
                });
            }

            if (price.get() > 0.0) {
                CACHED_PRICES.put(recipe.getResult(), price.get());
            }
        }

        /**
         * Cadastra todos os preços customizados de itens que não são
         * craftáveis.
         */
        MerchantIngredientPrice.INGREDIENT_PRICES.forEach((itemStack, price) -> {
            CACHED_PRICES.put(itemStack, price);
        });
    }

    public static Double getCachedPrice(ItemStack itemStack) {
        for (ItemStack targetItemStack : CACHED_PRICES.keySet()) {

            if (targetItemStack.getType().equals(itemStack.getType())
                    && targetItemStack.getDurability() == itemStack.getDurability()) {
                return CACHED_PRICES.get(targetItemStack) * itemStack.getAmount();
            }

        }

        return null;
    }

}
