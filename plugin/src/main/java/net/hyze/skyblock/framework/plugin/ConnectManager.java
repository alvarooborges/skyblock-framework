package net.hyze.skyblock.framework.plugin;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.shared.echo.packets.user.connect.ConnectConsent;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectHandShakeErrorPacket;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectHandShakePacket;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.HumanDataSaveEvent;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

public class ConnectManager implements EchoListener {

    @Subscribe
    public void on(UserConnectHandShakeErrorPacket packet) {
        if (!CoreProvider.getApp().isSame(packet.getAppId())) {
            return;
        }

        Player player = Bukkit.getPlayerExact(packet.getUser().getNick());

        if (player == null || !player.isOnline()) {
            return;
        }

        SkyBlockUser user = SkyBlockProvider.Cache.Local.USERS.provide().get(packet.getUser());
        user.setLocked(false);
    }

    @Subscribe
    public void on(UserConnectHandShakePacket packet) {
        if (packet.getProxyConsent() != ConnectConsent.ALLOWED) {
            return;
        }

        boolean changed = false;

        // Target
        if (CoreProvider.getApp().isSame(packet.getAppId())
                && packet.getTargetAppConsent() == ConnectConsent.PENDING) {
            packet.setTargetAppConsent(ConnectConsent.ALLOWED);
            changed = true;
        }

        if (packet.getCurrentAppConsent() == ConnectConsent.PENDING
                && packet.getTargetAppConsent() == ConnectConsent.ALLOWED) {
            Player player = Bukkit.getPlayerExact(packet.getUser().getNick());

            if (player != null && player.isOnline()) {
                SkyBlockUser user = SkyBlockProvider.Cache.Local.USERS.provide().get(packet.getUser());

                if (packet.getReason() == ConnectReason.PLUGIN) {
                    CombatManager.untag(user.getHandle());
                }

                player.closeInventory();

                user.setLocked(true);

                // Salvando dados do jogador
                {
                    EntityHuman human = ((CraftPlayer) player).getHandle();

                    NBTTagCompound compound = new NBTTagCompound();

                    human.e(compound);

                    HumanDataSaveEvent dataSaveEvent = new HumanDataSaveEvent(human, compound);

                    Bukkit.getServer().getPluginManager().callEvent(dataSaveEvent);
                }

                packet.setCurrentAppConsent(ConnectConsent.ALLOWED);
                changed = true;
            }
        }

        if (changed) {
            CoreProvider.Redis.ECHO.provide().publish(packet);
        }
    }
}
