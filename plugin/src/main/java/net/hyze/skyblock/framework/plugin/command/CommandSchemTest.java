package net.hyze.skyblock.framework.plugin.command;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import java.io.File;
import java.io.IOException;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSchemTest extends CustomCommand implements GroupCommandRestrictable {

    public CommandSchemTest() {
        super("schemtest", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        try {

            File schem = new File(CoreConstants.CLOUD_DIRECTORY, "schematics/skyblock/trees/01.schematic");

            WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 10000);

            CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(schem).load(schem);

            clipboard.paste(session, new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()), true);

        } catch (MaxChangedBlocksException | IOException | DataException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
