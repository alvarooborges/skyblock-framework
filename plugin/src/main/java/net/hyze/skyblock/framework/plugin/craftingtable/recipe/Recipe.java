package net.hyze.skyblock.framework.plugin.craftingtable.recipe;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public abstract class Recipe implements IRecipe {

    @Getter
    private final Map<Character, ItemStack> ingredientsMap = Maps.newHashMap();


}
