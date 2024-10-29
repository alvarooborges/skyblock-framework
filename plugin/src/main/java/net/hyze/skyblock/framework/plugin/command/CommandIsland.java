package net.hyze.skyblock.framework.plugin.command;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;

public class CommandIsland extends CustomCommand {

    public CommandIsland() {
        super("ilha", CommandRestriction.IN_GAME);

        /* registerSubCommand(new CustomCommand("deletar", CommandRestriction.IN_GAME, "delete") {
      @Override
      public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;
        World world = player.getWorld();

        if(!plugin.getIslands().contains(world)) {
          Message.ERROR.send(sender, "Você não está em uma ilha de skyblock");
          return;
        }

        String fid = world.getName();
        Repositories.SKYBLOCK_ISLANDS.provide().removeById(fid);

        try {
          SeaweedFile file = new SeaweedFile(fid);
          List<VolumeLocation> locations = Database.SEAWEED_SKYBLOCK.lookup(file.getVolumeId());
          if(locations.size() != 0) {
            Database.SEAWEED_SKYBLOCK.delete(file, locations.get(0));
          }
        } catch(IOException ex) {
          ex.printStackTrace();
        }

        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        Bukkit.unloadWorld(world, false);

        Message.SUCCESS.send(sender, "Sucesso!");
      }
    }); */
    }

    @Override
    public void onCommand(CommandSender sender, User user, String label, String[] args) {
        Message.INFO.send(sender, "Utilize o comando: &f/warp ilha");
        //IslandUtils.sendToIslandByCommand((Player) sender, user);
    }

}
