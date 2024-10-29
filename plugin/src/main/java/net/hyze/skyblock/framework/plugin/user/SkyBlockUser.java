package net.hyze.skyblock.framework.plugin.user;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.*;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.user.Credentialable;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.scoreboard.Boardable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "handle")
public class SkyBlockUser implements Credentialable {

    private final User handle;

    @Getter
    @Setter
    private String localIslandId;

    @Setter
    private Boardable board;

    @Setter
    private boolean locked;

    private Multimap<AppType, Location> foundedSouls = HashMultimap.create();
    private Multimap<AppType, Location> holdingSouls = HashMultimap.create();

    public Player getPlayer() {
        return Bukkit.getPlayerExact(this.handle.getNick());
    }

    @Override
    public Integer getId() {
        return this.handle.getId();
    }

    @Override
    public String getNick() {
        return this.handle.getNick();
    }
}
