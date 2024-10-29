package net.hyze.skyblock.framework.plugin.misc.woodcut;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import org.bukkit.Bukkit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WoodCutSetup {

    public static void setup(WoodCutArea... woodCurtAreas) {
        Bukkit.getScheduler().runTaskTimer(SkyBlockPlugin.getInstance(), new WoodCutRunnable(woodCurtAreas), 0L, 10 * 20L);
        Bukkit.getPluginManager().registerEvents(new WoodCutListeners(woodCurtAreas), SkyBlockPlugin.getInstance());
    }

}
