package net.hyze.skyblock.framework.plugin.inventory;

import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class IslandInventory extends CustomInventory {

    public IslandInventory(SkyBlockUser user, Island island) {
        super(54, "Menu de Ilha");

        setItem(10, new ItemBuilder(Material.FEATHER).name("&aDefinir spawn da ilha").lore("&7Clique para definir").make(), (event) -> {
            Player player = (Player) event.getWhoClicked();
            Location location = player.getLocation();

            player.closeInventory();
            island.setSpawnLocation(
                new SerializedLocation("world", location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));

            SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().updateSpawn(island, island.getSpawnLocation());
            Message.SUCCESS.send(player, "Spawn atualizado com sucesso.");
        });
    }
}
