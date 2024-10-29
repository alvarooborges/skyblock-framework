package net.hyze.skyblock.framework.plugin.listeners.player;

import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.event.HumanDataLoadEvent;
import org.bukkit.craftbukkit.v1_8_R3.event.HumanDataSaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HumanDataListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(HumanDataLoadEvent event) {
        event.setCancelled(true);

        SkyBlockUser user = SkyBlockProvider.Cache.Local.USERS.provide().get(event.getHuman().getName());

        if (user != null) {
            ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());

            NBTTagCompound compound = SkyBlockProvider.Repositories.USER_DATA.provide()
                    .fetch(profileData.getSelectedProfile());

            if (compound != null) {
                event.getHuman().f(compound);

                event.setResult(compound);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(HumanDataSaveEvent event) {
        event.setCancelled(true);

        Player player = (Player) event.getHuman().getBukkitEntity();

        if (player.isOnline()) {
            SkyBlockUser user = SkyBlockProvider.Cache.Local.USERS.provide().get(event.getHuman().getName());

            if (user != null) {
                NBTTagCompound compound = event.getCompound();

                ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
                SkyBlockProvider.Repositories.USER_DATA.provide().update(profileData.getSelectedProfile(), compound);
            }
        }
    }
}
