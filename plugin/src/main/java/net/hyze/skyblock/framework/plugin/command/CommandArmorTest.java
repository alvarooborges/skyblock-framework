package net.hyze.skyblock.framework.plugin.command;

import java.util.function.Function;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class CommandArmorTest extends CustomCommand implements GroupCommandRestrictable {

    private Player player;
    private ArmorStand stand;

    Function<Location, Location> getLocation = targetLocation -> {
        return targetLocation.clone().add(0, 1.5, 0);
    };

    public CommandArmorTest(SkyBlockPlugin plugin) {
        super("armortest", CommandRestriction.IN_GAME);
//        
        Bukkit.getPluginManager().registerEvents(new Listeners(), plugin);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        this.player = ((Player) sender);

        World world = player.getWorld();

        this.stand = (ArmorStand) world.spawnEntity(
                getLocation.apply(player.getLocation()),
                EntityType.ARMOR_STAND
        );

        stand.setGravity(false);
        stand.setHelmet(new ItemStack(Material.GRASS));
        stand.setVisible(false);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    private class Listeners implements Listener {

        @EventHandler
        public void on(PlayerMoveEvent event) {

            if (player != null && stand != null) {
                stand.teleport(getLocation.apply(player.getLocation()));
            }

        }

    }

}
