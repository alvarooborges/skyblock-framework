package net.hyze.skyblock.framework.plugin.craftingtable.inventories;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.skyblock.framework.plugin.craftingtable.events.PlayerCraftingEvent;
import net.hyze.skyblock.framework.plugin.craftingtable.events.PlayerPrepareCraftingEvent;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IRecipe;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.RecipeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

public class CraftingTableInventory extends CustomInventory {

    private static int[] MATRIX = {
            10, 11, 12,
            19, 20, 21,
            28, 29, 30
    };

    private static int RESULT_SLOT = 24;

    private static int FALLBACK_SLOT = 49;

    private static int[] FEEDBACK_SLOTS = {
            45, 46, 47, 48, 50, 51, 52, 53
    };

    private static ItemStack EMPTY_RESULT = ItemBuilder.of(Material.BARRIER)
            .name("&cTitle")
            .lore("Lore", "lore.")
            .make();

    private final Player player;

    private IRecipe currentRecipe;

    public CraftingTableInventory(Player player) {
        this(player, null);
    }

    public CraftingTableInventory(Player player, Supplier<Inventory> fallback) {
        super(54, "Criar item");
        this.player = player;

        // Definindo background
        {
            ItemBuilder backgroundItem = ItemBuilder
                    .of(Material.STAINED_GLASS_PANE, (short) 15)
                    .name(" ");

            for (int i = 0; i < getSize(); i++) {
                setItem(i, backgroundItem.make());
            }
        }

        // definindo slot de fallback (voltar ou fechar)
        {
            Inventory fallbackInventory = Optional.ofNullable(fallback)
                    .map(Supplier::get)
                    .orElse(null);

            if (fallbackInventory != null) {
                setItem(FALLBACK_SLOT, BACK_ARROW, () -> player.openInventory(fallbackInventory));
            } else {
                setItem(FALLBACK_SLOT, ItemBuilder.of(Material.BARRIER).name("&cFechar").make(), player::closeInventory);
            }
        }

        // atualizando slots de feedback
        updateFeedbackSlots(false);

        // definindo resultado

        setItem(RESULT_SLOT, EMPTY_RESULT);

        for (int slot : MATRIX) {
            setItem(slot, null);
        }
    }

    private void updateFeedbackSlots(boolean success) {
        ItemBuilder feedbackItem = ItemBuilder
                .of(Material.STAINED_GLASS_PANE, (short) (success ? 5 : 14))
                .name(" ");

        for (int slot : FEEDBACK_SLOTS) {
            setItem(slot, feedbackItem.make());
        }
    }

    private void setMatrix(ItemStack[] matrix) {
        for (int i = 0; i < MATRIX.length; i++) {
            setItem(MATRIX[i], matrix[i]);
        }
    }

    private ItemStack[] getMatrix() {
        ItemStack[] matrix = new ItemStack[MATRIX.length];

        for (int i = 0; i < MATRIX.length; i++) {
            matrix[i] = getContents()[MATRIX[i]];
        }

        return matrix;
    }

    private ItemStack[][] getInputs() {
        ItemStack[][] inputs = new ItemStack[3][3];

        int x = 0;
        int z = 0;

        for (int i = 0; i < MATRIX.length; i++) {
            ItemStack ingredient = getContents()[MATRIX[i]];

            if (ingredient != null && ingredient.getType() == Material.AIR) {
                ingredient = null;
            }

            inputs[x][z++] = ingredient;

            if (z == 3) {
                x++;
                z = 0;
            }
        }

        return inputs;
    }

    private void sendErrorFeedback() {
        setItem(RESULT_SLOT, EMPTY_RESULT);
        updateFeedbackSlots(false);
    }

    private void update() {
        ItemStack[][] inputs = getInputs();

        this.currentRecipe = RecipeRegistry.getRecipeByIngredients(inputs);

        if (this.currentRecipe == null) {
            sendErrorFeedback();
            return;
        }

        ItemStack resultPreview = this.currentRecipe.getResult(inputs);

        if (resultPreview == null || resultPreview.getType() == Material.AIR) {
            sendErrorFeedback();
            return;
        }

        PlayerPrepareCraftingEvent prepareEvent = new PlayerPrepareCraftingEvent(
                this.player, this.currentRecipe, resultPreview.clone()
        );

        Bukkit.getPluginManager().callEvent(prepareEvent);

        if (prepareEvent.isCancelled()) {
            this.currentRecipe = null;
            resultPreview = null;
        } else {
            resultPreview = prepareEvent.getResultPreview();
        }

        if (this.currentRecipe == null || resultPreview == null) {
            sendErrorFeedback();
            return;
        }

        updateFeedbackSlots(true);

        setItem(RESULT_SLOT, resultPreview, (event) -> {

            Supplier<Boolean> runnable = () -> {
                ItemStack[][] stacksInputs = getInputs();

                if (this.currentRecipe == null
                        || this.currentRecipe != RecipeRegistry.getRecipeByIngredients(stacksInputs)) {
                    this.currentRecipe = null;
                    sendErrorFeedback();
                    return false;
                }

                if (!currentRecipe.matches(stacksInputs)) {
                    this.currentRecipe = null;
                    sendErrorFeedback();
                    return false;
                }

                ItemStack result = currentRecipe.getResult(stacksInputs);

                if (result == null) {
                    this.currentRecipe = null;
                    sendErrorFeedback();
                    return false;
                }

                PlayerCraftingEvent craftingEvent = new PlayerCraftingEvent(
                        this.player, this.currentRecipe, result.clone()
                );

                Bukkit.getPluginManager().callEvent(craftingEvent);

                result = craftingEvent.getResult();

                if (craftingEvent.isCancelled() && result == null || result.getType() == Material.AIR) {
                    return false;
                }

                event.setCancelled(true);

                if (event.isShiftClick()) {
                    if (InventoryUtils.fits(player.getInventory(), result)) {
                        player.getInventory().addItem(craftingEvent.getResult());
                        setMatrix(this.currentRecipe.getRemainingItems(getInputs()));
                        return true;
                    }
                } else if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                    event.getWhoClicked().setItemOnCursor(result);

                    setMatrix(this.currentRecipe.getRemainingItems(getInputs()));
                    return false;
                } else {
                    if (ItemStackUtils.isSimilar(event.getCursor(), result)) {
                        int newAmount = event.getCursor().getAmount() + result.getAmount();

                        if (newAmount <= event.getCursor().getType().getMaxStackSize()) {
                            ItemStack newCursor = event.getCursor().clone();
                            newCursor.setAmount(newAmount);

                            event.getWhoClicked().setItemOnCursor(newCursor);

                            setMatrix(this.currentRecipe.getRemainingItems(getInputs()));
                            return false;
                        }
                    }
                }

                return false;
            };

            if (event.isShiftClick()) {
                boolean canNextCraft = true;
                while (canNextCraft && this.currentRecipe != null) {
                    canNextCraft = runnable.get();
                    update();
                }
            } else {
                runnable.get();
                update();
            }
        });

        player.updateInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        for (int slot : MATRIX) {
            ItemStack ingredient = getContents()[slot];
            if (ingredient != null) {
                if (InventoryUtils.fits(player.getInventory(), ingredient)) {
                    player.getInventory().addItem(ingredient);
                } else {
                    player.getWorld().dropItem(player.getLocation(), ingredient);
                }
            }

            setItem(slot, null);
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        Bukkit.getScheduler().runTask(SkyBlockPlugin.getInstance(), this::update);

        INVENTORY_SLOTS:
        for (int slot : event.getRawSlots()) {
            if (slot >= 54) {
                continue;
            }

            for (int gridSlot : MATRIX) {
                if (slot == gridSlot) {
                    continue INVENTORY_SLOTS;
                }
            }

            event.setCancelled(true);
            break;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(SkyBlockPlugin.getInstance(), this::update);

        if (event.getClickedInventory() == null
                || event.getClickedInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(false);
            return;
        }

        for (int slot : MATRIX) {
            if (slot == event.getSlot()) {
                event.setCancelled(false);
                return;
            }
        }

        event.setCancelled(true);

        if (event.getSlot() == RESULT_SLOT) {
            ClickListener listener = getListener(RESULT_SLOT);
            if (listener != null) {
                if (listener instanceof ConsumerClickListener) {
                    ((ConsumerClickListener) listener).accept(event);
                } else if (listener instanceof RunnableClickListener) {
                    ((RunnableClickListener) listener).run();
                }
            }
        }
    }

    @Override
    public void onRegroupItem(InventoryRegroupItemEvent event) {
        for (int i = 0; i < getSize(); i++) {
            event.getIgnoredSlots().add(i);
        }

        for (int slot : MATRIX) {
            event.getIgnoredSlots().remove((Integer) slot);
        }

        if (this.currentRecipe != null) {
            Bukkit.getScheduler().runTask(SkyBlockPlugin.getInstance(), this::update);
        }
    }
}
