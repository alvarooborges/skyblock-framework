package net.hyze.skyblock.framework.plugin.misc.npc;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import net.hyze.core.spigot.misc.hologram.Hologram;
import net.hyze.core.spigot.misc.hologram.HologramPosition;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.skyblock.framework.plugin.SkyBlockProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

public abstract class CustomNPC {

    private String name;
    private String skinValue;
    private String skinSign;
    private List<String> hologramText;

    private boolean lookClose = false;
    private Hologram hologram;

    @Getter
    private NPC citizensInstance;

    public void spawn(Location location) {

        if (this.citizensInstance != null) {
            this.citizensInstance.destroy();
        }

        if (this.hologram != null) {
            this.hologram.destroy();
        }

        if (this.name == null) {
            this.name = "hyze_npc";
        }

        this.citizensInstance = SkyBlockPlugin.getInstance().getNpcRegistry().createNPC(EntityType.PLAYER, this.name);

        this.citizensInstance.getTrait(LookClose.class).lookClose(this.lookClose);

        MetadataStore data = this.citizensInstance.data();

        data.setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, this.skinValue);
        data.setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, this.skinSign);

        data.setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
        data.set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        Property localData = new Property("textures",
                data.get(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA),
                data.get(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA));

        try {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(SkyBlockPlugin.getInstance(), () -> {

                SkinnableEntity entity = (SkinnableEntity) this.citizensInstance.getEntity();
                Property skinProperty = localData;

                if (entity == null || entity.getProfile() == null) {
                    return;
                }

                GameProfile profile = entity.getProfile();

                Property current = Iterables.getFirst(profile.getProperties().get("textures"), null);
                if (current != null
                        && current.getValue().equals(skinProperty.getValue())
                        && (current.getSignature() != null && current.getSignature().equals(skinProperty.getSignature()))) {
                    return;
                }

                profile.getProperties().removeAll("textures");
                profile.getProperties().put("textures", skinProperty);

            }, 0L, 20L);

            Bukkit.getScheduler().runTaskLater(SkyBlockPlugin.getInstance(), () -> task.cancel(), 20L * 20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.citizensInstance.spawn(location);

        CustomNPCScoreboard.getTeam().addEntry(this.name);

        SkyBlockProvider.Cache.Local.NPC.provide().registerCustomNPC(this);

        if (this.hologramText != null) {

            this.hologram = new Hologram(HologramPosition.UP);

            Lists.reverse(this.hologramText).forEach(text -> this.hologram.line(text));

            this.hologram.spawn(location.clone().add(0, 1.8, 0));

        }
    }

    public CustomNPC name(String name) {
        this.name = name;
        return this;
    }

    public CustomNPC skin(String skinValue, String skinSign) {
        this.skinValue = skinValue;
        this.skinSign = skinSign;
        return this;
    }

    public CustomNPC lookClose(boolean value) {
        this.lookClose = value;
        return this;
    }

    public CustomNPC hologram(String... text) {
        this.hologramText = Arrays.asList(text);
        return this;
    }

    public void teleport(Location location) {
        spawn(location);
    }

    public void on(NPCRightClickEvent event) {

    }

}
