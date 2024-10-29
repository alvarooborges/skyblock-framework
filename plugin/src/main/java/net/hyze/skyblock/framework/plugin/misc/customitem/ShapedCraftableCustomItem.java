package net.hyze.skyblock.framework.plugin.misc.customitem;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public abstract class ShapedCraftableCustomItem extends CraftableCustomItem implements IShapedRecipe {

    @Setter
    @Getter
    private Character[][] shape;

    public ShapedCraftableCustomItem(@NonNull String key) {
        super(key);
    }


    @Override
    public ItemStack[] getRemainingItems(ItemStack[][] inputs) {
        return null;
    }


    @Override
    public boolean matches(ItemStack[][] inputs) {
        for (int x = 0; x < inputs.length; x++) {
            for (int z = 0; z < inputs[x].length; z++) {
                if (this.matches(inputs, x, z)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(ItemStack[][] inputs, int posX, int posZ) {
        int recipeHeight = this.getHeight();
        int recipeWidth = this.getWidth();

        Character[][] shapeInput = new Character[recipeHeight][recipeWidth];

        for (int x = 0; x < inputs.length; x++) {
            for (int z = 0; z < inputs[x].length; z++) {

                ItemStack input = inputs[x][z];

                if (x < posX && z < posZ) {
                    if (input != null) {
                        return false;
                    }
                }

                if (input == null || input.getType() == Material.AIR) {
                    continue;
                }

                Character character = shape[x][z];

                ItemStack ingredient = getIngredientsMap().get(character);

                if (ingredient == null) {
                    continue;
                }

                if (input.getAmount() < ingredient.getAmount() || !ItemStackUtils.isSimilar(input, ingredient)) {
                    shapeInput[x][z] = '?';
                }

                shapeInput[x][z] = character;
            }
        }

        System.out.println(Arrays.deepToString(this.shape));
        System.out.println(Arrays.deepToString(shapeInput));
        return Arrays.deepEquals(this.shape, shapeInput);
    }

    private int getWidth() {
        int width = 0;

        for (Character[] characters : shape) {
            width = Math.max(width, characters.length);
        }

        return width;
    }

    private int getHeight() {
        return shape.length;
    }

    @Override
    public ItemStack getResult(ItemStack[][] inputs) {
        return this.asItemStack();
    }
}
