package net.hyze.skyblock.framework.plugin.misc.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomNPCListeners implements Listener {

    @EventHandler
    public void on(NPCRightClickEvent event) {
        CustomNPC customNPC = SkyBlockProvider.Cache.Local.NPC.provide().getCache().get(event.getNPC().getUniqueId());
        customNPC.on(event);
    }

}
