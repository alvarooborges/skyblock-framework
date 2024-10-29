package net.hyze.skyblock.framework.plugin.misc.merchant;

import java.util.List;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.skyblock.framework.plugin.misc.currency.Currency;
import net.hyze.skyblock.framework.plugin.misc.merchant.prices.MerchantPrice;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MerchantInventory extends PaginateInventory {

    private final int[] frameSlots = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 17, 26, 35, 44, 45, 46, 47, 48, 50, 51, 52, 53
    };

    private final int hopperSlot = 49;

    public MerchantInventory(Merchant merchant) {
        super(merchant.getName());

        merchant.getItems().forEach(item -> {

            addItem(
                    addItemBuyLore(item.getItem(), item.getPrice()),
                    event -> {
                        Player player = (Player) event.getWhoClicked();
                        PlayerInventory playerInventory = player.getInventory();

                        playerInventory.addItem(addItemSellLore(item.getItem()));

                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 2);
                    }
            );

        });

        for (int slot : this.frameSlots) {
            setItem(
                    slot,
                    ItemBuilder.of(Material.STAINED_GLASS_PANE)
                    .name("&f")
                    .durability(7)
                    .make()
            );
        }

        updateHopperSlot();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        updateItemsFromPlayerInventory(player, true);
        super.onOpen(event);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        updateItemsFromPlayerInventory(player, false);
        super.onClose(event);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        event.setCancelled(true);

        Inventory currentInventory = event.getClickedInventory();

        if (currentInventory != null && currentInventory.getType().equals(InventoryType.PLAYER)) {

            ItemStack itemRaw = currentInventory.getItem(event.getSlot());

            if (itemRaw != null) {
                ItemStack item = removeItemLore(itemRaw);

                Double price = MerchantPrice.getCachedPrice(item);

                if (price != null) {
                    setItem(
                            this.hopperSlot,
                            addItemBuyLore(item, price),
                            onClick -> {
                                currentInventory.addItem(addItemSellLore(item));
                                updateHopperSlot();

                                Player player = (Player) onClick.getWhoClicked();
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 2);
                            }
                    );

                    currentInventory.setItem(
                            event.getSlot(),
                            null
                    );

                    Player player = (Player) event.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 2);
                    return;
                }
            }
        }

        super.onClick(event);
    }

    private void updateHopperSlot() {
        setItem(
                this.hopperSlot,
                ItemBuilder.of(Material.HOPPER)
                .name("&aVender Item")
                .lore(
                        "&7Clique nos itens que estão no seu",
                        "inventário para vender para esta loja."
                )
                .make()
        );
    }

    private void updateItemsFromPlayerInventory(Player player, boolean addLore) {

        PlayerInventory playerInventory = player.getInventory();

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack item = playerInventory.getItem(slot);

            if (item != null) {

                if (addLore) {
                    playerInventory.setItem(slot, addItemSellLore(item));
                } else {
                    playerInventory.setItem(slot, removeItemLore(item));
                }
            }
        }

    }

    private ItemStack addItemBuyLore(ItemStack item, double value) {
        return ItemBuilder.of(item)
                .lore(
                        "",
                        "&6Valor: &f" + Currency.COINS.format(value),
                        "&eClique para comprar."
                )
                .make();
    }

    private ItemStack addItemSellLore(ItemStack item) {
        Double price = MerchantPrice.getCachedPrice(item);

        String[] lore;

        if (price == null) {
            lore = new String[]{
                "",
                "&cOps, este item não pode",
                "&cser vendido."
            };
        } else {
            lore = new String[]{
                "",
                "&6Valor: &f" + Currency.COINS.format(price),
                "&eClique para vender."
            };
        }

        return ItemBuilder.of(item)
                .lore(lore)
                .make();
    }

    private ItemStack removeItemLore(ItemStack item) {
        ItemBuilder itemBuilder = ItemBuilder.of(item);

        List<String> lore = itemBuilder.lore();

        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);

        itemBuilder.lore(true, lore.stream().toArray(String[]::new));

        return itemBuilder.make();
    }

//    /**
//     * Testando.
//     *
//     * @return
//     */
//    private double getPrice(ItemStack item) {
//        List<Recipe> recipeList = Bukkit.getRecipesFor(item);
//
//        if (recipeList == null || recipeList.isEmpty()) {
//            return MerchantIngredientPrice.getIngredientPrice(item);
//        }
//
//        Recipe recipe = recipeList.get(0);
//        AtomicDouble price = new AtomicDouble();
//
//        if (recipe instanceof ShapedRecipe) {
//            ShapedRecipe shapedRecipes = (ShapedRecipe) recipe;
//
//            shapedRecipes.getIngredientMap().values().forEach(itemStack -> {
//                price.addAndGet(MerchantIngredientPrice.getIngredientPrice(itemStack));
//            });
//        } else if (recipe instanceof ShapelessRecipe) {
//            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
//
//            shapelessRecipe.getIngredientList().forEach(itemStack -> {
//                price.addAndGet(MerchantIngredientPrice.getIngredientPrice(itemStack));
//            });
//        }
//
//        return price.get() * item.getAmount();
//    }
}
