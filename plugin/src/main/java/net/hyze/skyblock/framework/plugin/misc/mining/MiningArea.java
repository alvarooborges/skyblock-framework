package net.hyze.skyblock.framework.plugin.misc.mining;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

@RequiredArgsConstructor
public class MiningArea {

    @Getter
    private final WorldCuboid cuboid;

    @Getter
    private final Long delay;

    private final MaterialData[] materialDatas;

    public boolean containsBlock(Block block) {
        return Stream.of(this.materialDatas)
                .filter(materialData
                        -> block.getType().equals(materialData.getItemType())
                        && block.getData() == materialData.getData()
                )
                .findFirst()
                .isPresent();
    }

}
