package net.hyze.skyblock.framework.plugin.misc.npc.cache;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.skyblock.framework.plugin.misc.npc.CustomNPC;

public class CustomNPCLocalCache implements LocalCache {

    @Getter
    private final Map<UUID, CustomNPC> cache = Maps.newHashMap();

    public void registerCustomNPC(CustomNPC customNPC) {
        cache.put(customNPC.getCitizensInstance().getUniqueId(), customNPC);
    }

}
