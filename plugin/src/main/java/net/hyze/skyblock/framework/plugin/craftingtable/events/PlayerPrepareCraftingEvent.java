package net.hyze.skyblock.framework.plugin.craftingtable.events;

import lombok.Getter;
import lombok.Setter;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerPrepareCraftingEvent extends CraftingEvent {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    @Setter
    private ItemStack resultPreview;

    public PlayerPrepareCraftingEvent(Player player, IRecipe recipe, ItemStack resultPreview) {
        super(player, recipe);
        this.resultPreview = resultPreview;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
