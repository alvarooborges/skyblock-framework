package net.hyze.skyblock.framework.plugin.craftingtable.recipe;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import net.hyze.skyblock.framework.plugin.misc.customitem.ShapedCraftableCustomItem;
import net.minecraft.server.v1_8_R3.CraftingManager;
import net.minecraft.server.v1_8_R3.ShapedRecipes;
import net.minecraft.server.v1_8_R3.ShapelessRecipes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeRegistry {

    public static final char WRONG_CHAR = '?';

    private static final List<IRecipe> RECIPES_LIST = Lists.newArrayList();

    static {
        List<net.minecraft.server.v1_8_R3.IRecipe> recipes = CraftingManager.getInstance().recipes;

        for (net.minecraft.server.v1_8_R3.IRecipe recipe : recipes) {
            IRecipe iRecipe = null;

            if (recipe instanceof ShapedRecipes) {
                iRecipe = new BukkitShapedRecipe((ShapedRecipes) recipe);
            } else if (recipe instanceof ShapelessRecipes) {
                iRecipe = new BukkitShapelessRecipe((ShapelessRecipes) recipe);
            }

            if (iRecipe != null) {
                registerRecipes(iRecipe);
            }
        }
    }

    public static void registerRecipes(IRecipe... recipes) {
        RECIPES_LIST.addAll(Arrays.asList(recipes));
    }

    public static IRecipe getRecipeByResult(ItemStack result) {
        CustomItem customItem = CustomItemRegistry.getByItemStack(result);

//        for (IRecipe recipe : RECIPE_MAP.values()) {
//            if (customItem != null) {
//                CustomItem resultCustomItem = CustomItemRegistry.getByItemStack(recipe.getResult());
//
//                if (resultCustomItem != null && resultCustomItem.equals(customItem)) {
//                    return recipe;
//                }
//            } else {
//                if (ItemStackUtils.isSimilar(result, recipe.getResult())) {
//                    return recipe;
//                }
//            }
//        }

        return null;
    }

    public static IRecipe getRecipeByIngredients(ItemStack[][] inputs) {
//        System.out.println("================================================");

        List<ItemStack> inputList = Lists.newArrayList();
        boolean anyInputIsItemCustom = false;

        for (ItemStack[] itemStacks : inputs) {
            for (ItemStack input : itemStacks) {
                if (input != null && input.getType() != Material.AIR) {
                    inputList.add(input);

                    if (!anyInputIsItemCustom && CustomItemRegistry.getByItemStack(input) != null) {
                        anyInputIsItemCustom = true;
                    }
                }
            }
        }

        for (IRecipe recipe : RECIPES_LIST) {

            // Bloqueando criar itens vanilla com itens customs
            if (anyInputIsItemCustom && recipe instanceof BukkitRecipe) {
                continue;
            }

            Map<Character, ItemStack> ingredients = recipe.getIngredientsMap();

            if (ingredients.isEmpty()) {
                continue;
            }

            if (recipe instanceof BukkitRecipe || recipe instanceof ShapedCraftableCustomItem) {
                if (recipe.matches(inputs)) {
                    return recipe;
                }

                continue;
            }

            if (recipe instanceof IShapedRecipe) {
                IShapedRecipe shapedRecipe = (IShapedRecipe) recipe;

                Character[][] shape = shapedRecipe.getShape();

                int minX = inputs.length;
                int minZ = inputs.length;
                int height = 0;
                int width = 0;

                for (int x = 0; x < inputs.length; x++) {
                    ItemStack[] row = inputs[x];

                    for (int z = 0; z < row.length; z++) {
                        ItemStack input = row[z];

                        if (input != null && input.getType() != Material.AIR) {
                            minX = Math.min(minX, x);
                            minZ = Math.min(minZ, z);

                            height = Math.max(height, x - minX + 1);
                            width = Math.max(width, z - minZ + 1);
                        }
                    }
                }

                Character[][] formattedInput = new Character[height][width];

                for (int x = 0; x < formattedInput.length; x++) {
                    for (int z = 0; z < formattedInput[x].length; z++) {
                        if (z >= inputs[x].length) {
                            continue;
                        }

                        ItemStack input = inputs[x + minX][z + minZ];
                        if (input == null || input.getType() == Material.AIR) {
                            continue;
                        }

                        CustomItem inputCustomItem = CustomItemRegistry.getByItemStack(input);

                        if (x < shape.length && z < shape[x].length) {
                            Character character = shape[x][z];

                            ItemStack ingredient = ingredients.get(character);

                            if (ingredient == null || input.getAmount() < ingredient.getAmount()) {
                                formattedInput[x][z] = WRONG_CHAR;
                                continue;
                            }

                            formattedInput[x][z] = character;

                            CustomItem ingredientCustomItem = CustomItemRegistry.getByItemStack(ingredient);

                            if ((ingredientCustomItem == null
                                    && inputCustomItem == null
                                    && ItemStackUtils.isSimilar(input, ingredient))
                                    || (ingredientCustomItem != null
                                    && ingredientCustomItem.equals(inputCustomItem))) {

                                formattedInput[x][z] = character;
                            } else {
                                formattedInput[x][z] = WRONG_CHAR;
                            }
                        }
                    }
                }

                if (Arrays.deepEquals(shape, formattedInput)) {
                    return recipe;
                }

                boolean allowShapeless = recipe.getClass().getAnnotation(AllowShapeless.class) != null;

                if (!allowShapeless) {
                    continue;
                }

                // verificar quantidade de item sequenciamente
                // quebrando por espaços vazios

            } else {
                Iterator<ItemStack> inputIterator = Lists.newArrayList(inputList).iterator();

                List<ItemStack> ingredientsList = Lists.newArrayList(recipe.getIngredientsMap().values());

                while (inputIterator.hasNext()) {
                    ItemStack input = inputIterator.next();

                    Iterator<ItemStack> ingredientsIterator = ingredientsList.iterator();

                    while (ingredientsIterator.hasNext()) {
                        ItemStack ingredient = ingredientsIterator.next();

                        if (input.getAmount() >= ingredient.getAmount()
                                && ItemStackUtils.isSimilar(ingredient, input)) {
                            ingredientsIterator.remove();
                            inputIterator.remove();
                            break;
                        }
                    }
                }

                if (ingredientsList.isEmpty() && inputList.isEmpty()) {
                    return recipe;
                }
            }
        }

        return null;
    }
}
