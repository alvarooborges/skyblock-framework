package net.hyze.skyblock.framework.plugin.misc.customitem.data.compacted;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.AllowShapeless;
import net.hyze.skyblock.framework.plugin.misc.customitem.ShapedCraftableCustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllowShapeless
public class CobblestoneCompactedItem extends ShapedCraftableCustomItem {

    public static final String KEY = "cobblestone_compacted_item";

    public CobblestoneCompactedItem() {
        super(KEY);

        addIngredient('a', new ItemStack(Material.COBBLESTONE));

        setShape(new Character[][]{
                {null, 'a', null},
                {'a', 'a', 'a'}
        });
    }

    @Override
    protected ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.COBBLESTONE)
                .name(getDisplayName())
                .glowing(true);
    }

    @Override
    public String getDisplayName() {
        return "&bPedregulho Compactado";
    }
}
