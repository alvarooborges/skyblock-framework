package net.hyze.skyblock.framework.plugin.misc.woodcut;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Location;

public class WoodCutRunnable implements Runnable {

    private final WoodCutArea[] woodCutAreas;

    private final Map<String, List<Location>> randomLocations = Maps.newHashMap();

    public WoodCutRunnable(WoodCutArea[] woodCutAreas) {
        this.woodCutAreas = woodCutAreas;

        Stream.of(this.woodCutAreas).forEach(woodCutArea -> {
            this.randomLocations.put(
                    woodCutArea.getId(),
                    Lists.newArrayList(woodCutArea.getLocations().keySet())
            );

            woodCutArea.getLocations().forEach((location, schematic) -> {
                pasteSchematic(location, schematic);
            });
        });
    }

    @Override
    public void run() {

        Stream.of(this.woodCutAreas).forEach(woodCutArea -> {

            List<Location> locations = this.randomLocations.getOrDefault(woodCutArea.getId(), Lists.newArrayList(woodCutArea.getLocations().keySet()));

            Collections.shuffle(locations);

            Location location = locations.remove(0);
            WoodCutSchematic schematic = woodCutArea.getLocations().get(location);

            pasteSchematic(location, schematic);

            if (locations.isEmpty()) {
                this.randomLocations.put(
                        woodCutArea.getId(),
                        Lists.newArrayList(woodCutArea.getLocations().keySet())
                );
            }

        });

    }

    private void pasteSchematic(Location location, WoodCutSchematic schematic) {
        try {
            Vector to = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            World weWorld = new BukkitWorld(location.getWorld());
            WorldData worldData = weWorld.getWorldData();

            Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematic.getFile())).read(worldData);

            EditSession extent = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
            AffineTransform transform = new AffineTransform();

            ForwardExtentCopy copy = new ForwardExtentCopy(clipboard, clipboard.getRegion(), clipboard.getOrigin(), extent, to);
            if (!transform.isIdentity()) {
                copy.setTransform(transform);
            }

            copy.setSourceMask(new ExistingBlockMask(clipboard));

            Operations.completeLegacy(copy);
            extent.flushQueue();

        } catch (IOException | MaxChangedBlocksException ex) {
            Logger.getLogger(WoodCutRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
