package net.hyze.skyblock.framework.plugin.misc.customitem;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NonNull;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class CraftableCustomItem extends CustomItem implements IRecipe {

    @Getter
    private Map<Character, ItemStack> ingredientsMap = Maps.newHashMap();

    public CraftableCustomItem(@NonNull String key) {
        super(key);
    }
}
