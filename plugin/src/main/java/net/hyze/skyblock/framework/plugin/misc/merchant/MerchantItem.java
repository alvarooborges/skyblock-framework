package net.hyze.skyblock.framework.plugin.misc.merchant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class MerchantItem {
    
    private final ItemStack item;
    private final Double price;

}
