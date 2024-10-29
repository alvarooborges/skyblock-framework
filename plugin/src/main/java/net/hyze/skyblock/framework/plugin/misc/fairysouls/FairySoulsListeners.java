package net.hyze.skyblock.framework.plugin.misc.fairysouls;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class FairySoulsListeners implements Listener {

    private final FairySouls fairySoulsInstance;

    @EventHandler
    public void on(PlayerInteractEvent event) {

        if (!event.hasBlock()) {
            return;
        }

        AppType appType = CoreProvider.getApp().getType();
        Location location = event.getClickedBlock().getLocation();

        if (this.fairySoulsInstance.getSouls().get(appType)
                .stream()
                .filter(targetLocation -> targetLocation.equals(location))
                .findFirst()
                .isPresent()) {

            Player player = event.getPlayer();

            SkyBlockUser skyblockUser = SkyBlockProvider.Cache.Local.USERS.provide().get(player);

            if (skyblockUser.getFoundedSouls().containsEntry(appType, location)) {
                Message.INFO.send(player, "Esta alma já está com você, basta leva-la para o &cColetor de Almas&e.");
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 1);
                return;
            }

            Message.EMPTY.send(player, "\n&c Você encontrou uma alma!\n ");

            player.playSound(player.getLocation(), Sound.GHAST_DEATH, 10, 0.7f);

            skyblockUser.getFoundedSouls().put(appType, location);
        }

    }

}
