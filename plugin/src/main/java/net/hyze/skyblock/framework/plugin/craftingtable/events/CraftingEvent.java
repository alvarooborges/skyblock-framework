package net.hyze.skyblock.framework.plugin.craftingtable.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public abstract class CraftingEvent extends Event implements Cancellable {
    private final Player player;
    private final IRecipe recipe;

    @Getter
    @Setter
    private boolean cancelled;
}
