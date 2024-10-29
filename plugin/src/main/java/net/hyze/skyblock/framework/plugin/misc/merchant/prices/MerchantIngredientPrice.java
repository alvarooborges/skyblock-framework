package net.hyze.skyblock.framework.plugin.misc.merchant.prices;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MerchantIngredientPrice {

    public static final Map<ItemStack, Double> INGREDIENT_PRICES = Maps.newHashMap();

    static {
        INGREDIENT_PRICES.put(new ItemStack(Material.STICK), 5.0);
        INGREDIENT_PRICES.put(new ItemStack(Material.IRON_INGOT), 10.0);
    }

    public static Double getIngredientPrice(ItemStack itemStack) {
        return INGREDIENT_PRICES.getOrDefault(itemStack, 1.0);
    }
    
}
