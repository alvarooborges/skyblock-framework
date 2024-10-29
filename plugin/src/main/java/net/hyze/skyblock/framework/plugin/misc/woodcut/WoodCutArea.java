package net.hyze.skyblock.framework.plugin.misc.woodcut;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public class WoodCutArea {

    private final String id;

    private final Map<Location, WoodCutSchematic> locations = Maps.newHashMap();

    private final WorldCuboid cuboid;

    public WoodCutArea tree(Location location, WoodCutSchematic schematic) {
        this.locations.put(location, schematic);
        return this;
    }

}
