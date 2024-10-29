package net.hyze.skyblock.framework.plugin;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.Echo;
import net.hyze.core.spigot.CustomPlugin;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.skyblock.framework.plugin.command.*;
import net.hyze.skyblock.framework.plugin.command.purse.CommandPurse;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.IRecipe;
import net.hyze.skyblock.framework.plugin.craftingtable.recipe.RecipeRegistry;
import net.hyze.skyblock.framework.plugin.listeners.player.ChatListeners;
import net.hyze.skyblock.framework.plugin.listeners.player.HumanDataListener;
import net.hyze.skyblock.framework.plugin.listeners.player.PlayerConnectionListener;
import net.hyze.skyblock.framework.plugin.misc.customitem.SkyBlockCustomItems;
import net.hyze.skyblock.framework.plugin.misc.npc.CustomNPCListeners;
import net.hyze.skyblock.framework.plugin.misc.npc.CustomNPCScoreboard;
import net.hyze.skyblock.framework.plugin.misc.npc.CustomNPCStorage;
import net.hyze.skyblock.framework.plugin.slime.SlimeListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SkyBlockPlugin extends CustomPlugin {

    @Getter
    public static SkyBlockPlugin instance;

    //
    @Getter
    private NPCRegistry npcRegistry;

    public SkyBlockPlugin() {
        super(false);
    }

    /*

     */
    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;
        npcRegistry = CitizensAPI.createNamedNPCRegistry("HYZE", new CustomNPCStorage());

        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomNPCListeners(), this);
        Bukkit.getPluginManager().registerEvents(new HumanDataListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListeners(), this);

        CommandRegistry.registerCommand(new CommandIsland());
        CommandRegistry.registerCommand(new CommandCoop(this));
        CommandRegistry.registerCommand(new CommandArmorTest(this));
        CommandRegistry.registerCommand(new CommandItemTest(this));
        CommandRegistry.registerCommand(new CommandPurse());

        Plugin worldEditPlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin != null) {
            CommandRegistry.registerCommand(new CommandSchemTest());
        }

        Bukkit.getPluginManager().registerEvents(new SlimeListener(), this);
        Bukkit.getPluginManager().registerEvents(
                new net.hyze.skyblock.framework.plugin.craftingtable.listeners.PlayerInteractListener(),
                this
        );

        CustomNPCScoreboard.setup();

        for (SkyBlockCustomItems item : SkyBlockCustomItems.values()) {
            if (item.isEnabled()) {
                CustomItem customItem = item.getCustomItem();
                CustomItemRegistry.registerCustomItem(customItem);

                if (customItem instanceof IRecipe) {
                    RecipeRegistry.registerRecipes((IRecipe) customItem);
                }
            }
        }

        Echo echo = CoreProvider.Redis.ECHO.provide();
        echo.registerListener(new ConnectManager());
    }
}
