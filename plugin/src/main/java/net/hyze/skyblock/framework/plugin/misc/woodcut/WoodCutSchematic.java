package net.hyze.skyblock.framework.plugin.misc.woodcut;

import java.io.File;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;

@RequiredArgsConstructor
public enum WoodCutSchematic {

    TYPE_01("schematics/skyblock/trees/01.schematic"),
    TYPE_02("schematics/skyblock/trees/02.schematic"),
    TYPE_03("schematics/skyblock/trees/03.schematic"),
    TYPE_04("schematics/skyblock/trees/04.schematic");

    private final String directory;

    public File getFile() {
        return new File(CoreConstants.CLOUD_DIRECTORY, this.directory);
    }

}
