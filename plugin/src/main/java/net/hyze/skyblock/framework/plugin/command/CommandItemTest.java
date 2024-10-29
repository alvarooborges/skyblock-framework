package net.hyze.skyblock.framework.plugin.command;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.skyblock.framework.plugin.test.CustomItemTest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

public class CommandItemTest extends CustomCommand implements GroupCommandRestrictable {

  public CommandItemTest(SkyBlockPlugin plugin) {
    super("itemtest", CommandRestriction.IN_GAME);
  }

  @Override
  public void onCommand(CommandSender sender, User user, String label, String[] args) {
    Player player = (Player) sender;
    Location location = player.getLocation();
    ItemStack item = player.getItemInHand();

    if(item == null || item.getType() == Material.AIR) {
        Message.ERROR.send(sender, "Você não tem um item na mão!");
        return;
    }

    CustomItemTest customItem = new CustomItemTest(NMS.getWorld(location.getWorld()), location.getX(),
        location.getY(), location.getZ(), CraftItemStack.asNMSCopy(item));
    customItem.world.addEntity(customItem, SpawnReason.CUSTOM);

    Message.SUCCESS.send(sender, "Sucesso.");
  }

  @Override
  public Group getGroup() {
    return Group.GAME_MASTER;
  }

}
