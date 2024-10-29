package net.hyze.skyblock.framework.plugin.craftingtable.events;

import lombok.Getter;
import lombok.Setter;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftingEvent extends CraftingEvent {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    @Setter
    private ItemStack result;

    public PlayerCraftingEvent(Player player, IRecipe recipe, ItemStack result) {
        super(player, recipe);
        this.result = result;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
