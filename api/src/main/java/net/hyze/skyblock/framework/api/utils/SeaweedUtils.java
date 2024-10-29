package net.hyze.skyblock.framework.api.utils;

import java.io.IOException;
import net.hyze.seaweedfs.client.AssignParams;
import net.hyze.seaweedfs.client.Assignment;
import net.hyze.seaweedfs.client.SeaweedFile;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider.Database;
import net.hyze.skyblock.framework.api.slime.EnumSlimeLoaders;
import net.hyze.slime.exceptions.UnknownWorldException;
import net.hyze.slime.exceptions.WorldInUseException;

public class SeaweedUtils {

    public static String newIslandId() throws UnknownWorldException, WorldInUseException, IOException {
        String templateFid = "1,08b1857945";
        byte[] templateData = EnumSlimeLoaders.SEAWEED.getLoader().loadWorld(templateFid, true);

        Assignment assignment = Database.SEAWEED_SKYBLOCK.assign(new AssignParams("skyblock-islands", null));
        SeaweedFile seaweedFile = assignment.getSeaweedFile();

        String islandId = seaweedFile.getFid();
        Database.SEAWEED_SKYBLOCK.write(seaweedFile, assignment.getVolumeLocation(), templateData, islandId + ".slime");
        return islandId;
    }

}
