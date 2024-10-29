package net.hyze.skyblock.framework.plugin.misc.customitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.skyblock.framework.plugin.misc.customitem.data.compacted.CobblestoneCompactedItem;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum SkyBlockCustomItems {

    COBBLESTONE_COMPACTED(new CobblestoneCompactedItem());

    private final CustomItem customItem;
    private boolean enabled = true;
}
