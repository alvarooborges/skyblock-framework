package net.hyze.skyblock.framework.plugin.slime;

import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.slime.events.SlimeWorldSaveEvent;
import net.hyze.slime.world.SlimeWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SlimeListener implements Listener {

    @EventHandler
    public void onIslandSave(SlimeWorldSaveEvent event) {
        SlimeWorld slimeWorld = event.getSlimeWorld();
        Printer.INFO.print("EVENT SAVE world " + slimeWorld.getWorldName());

        if(!(slimeWorld instanceof SkyBlockSlimeWorld)) {
            return;
        }

        SkyBlockSlimeWorld skyBlockSlimeWorld = (SkyBlockSlimeWorld) slimeWorld;
        Island island = SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().fetchById(skyBlockSlimeWorld.getWorldName());

        if(island != null) {
            SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().updateMillis(island, System.currentTimeMillis());
        }
    }

}
