package net.hyze.skyblock.framework.plugin.inventory;

import java.io.IOException;
import java.util.Iterator;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.world.materialdata.SerializedMaterialData;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.core.spigot.world.materialdata.unserializer.BukkitMaterialDataParser;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.slime.EnumSlimeLoaders;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import net.hyze.skyblock.framework.api.user.profile.ProfileUser;
import net.hyze.skyblock.framework.api.utils.SeaweedUtils;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import net.hyze.skyblock.framework.plugin.utils.AttributeUtils;
import net.hyze.skyblock.framework.plugin.utils.IslandUtils;
import net.hyze.slime.exceptions.UnknownWorldException;
import net.hyze.slime.exceptions.WorldInUseException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.material.MaterialData;

public class ProfileInventory extends CustomInventory {

    private static final int MAX_PROFILES = 5;

    public ProfileInventory(SkyBlockUser user) {
        super(36, "Perfis");

        int index = 11;

        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
        Profile selectedProfile = profileData.getSelectedProfile();
        Iterator<Profile> iterator = profileData.getProfiles().iterator();

        boolean locked = false;

        for (int i = 0; i < MAX_PROFILES; i++) {
            if (iterator.hasNext()) {
                Profile profile = iterator.next();
                Island island = SkyBlockApiProvider.Cache.Local.ISLANDS.provide().get(profile.getIslandId());

                boolean selected = profile.equals(selectedProfile);

                SerializedMaterialData serializedMaterialData = profile.getIcon();
                MaterialData materialData = BukkitMaterialDataParser.getInstance().apply(serializedMaterialData);

                ItemBuilder builder = new ItemBuilder(materialData.getItemType(), materialData.getData());
                builder.name((selected ? ChatColor.GOLD : ChatColor.GREEN) + profile.getName());
                builder.lore(String.format("&fID da Ilha: &7%s", profile.getIslandId() + (island.isCoop() ? " &8[Co-op]" : "")), "",
                    (selected ? "&eSelecionado!" : "&eClique esquerdo para selecionar."));

                if (!selected) {
                    builder.lore("&7Clique direito para remover.");
                }

                builder.glowing(selected);

                setItem(index + i, builder.make(), (event) -> {
                    if (!selected) {
                        if (event.isRightClick()) {
                            event.getWhoClicked().openInventory(new ConfirmInventory(
                                (event2) -> deleteProfile(event2, user, profile),
                                (event2) -> event2.getWhoClicked().openInventory(new ProfileInventory(user)),
                                new ItemBuilder(materialData.getItemType(), profile.getIcon().getData())
                                    .name((selected ? ChatColor.GOLD : ChatColor.GREEN) + profile.getName()).make()
                            ).make("Ao aceitar a sua ilha será destruida PERMANENTEMENTE!"));

                            return;
                        }

                        selectProfile(event, user, profile);
                    }
                });

            } else {

                ItemBuilder builder = new ItemBuilder(locked ? Material.STONE_BUTTON : Material.WOOD_BUTTON)
                    .name(String.format((locked ? ChatColor.RED : ChatColor.YELLOW) + "Slot %d", i + 1))
                    .lore(locked ? "&7Bloqueado" : "&7Clique para criar um novo perfil.");
                setItem(index + i, builder.make(), locked ? null : (event) -> {
                    newProfile(event, user);
                });

                locked = true;

            }
        }

    }

    /*

     */

    private static void deleteProfile(InventoryClickEvent event, SkyBlockUser user, Profile profile) {
        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());
        if (profileData.getProfiles().size() <= 1) {
            return;
        }

        profileData.getProfiles().remove(profile);
        SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().remove(user.getId(), profile);

        ProfileUser profileUser = new ProfileUser(user.getId(), profile.getId());
        Player player = (Player) event.getWhoClicked();

        Island island = SkyBlockApiProvider.Cache.Local.ISLANDS.provide().get(profile.getIslandId());
        island.getMembers().remove(profileUser);

        if (island.getMembers().isEmpty()) {
            SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().delete(island);

            try {
                EnumSlimeLoaders.SEAWEED.getLoader().deleteWorld(island.getId());
            } catch (IOException | UnknownWorldException e) {
                e.printStackTrace();
            }

        } else {
            SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().removeMember(island, profileUser);
        }

        player.closeInventory();
        Message.ERROR.send(player, "Você eliminou o seu perfil.");

        if (user.getLocalIslandId().equals(profile.getIslandId())) {
            TeleportManager.teleport(user.getHandle(), AppType.SKYBLOCK_HUB, ConnectReason.PLUGIN, null);
            return;
        }
    }

    private static void selectProfile(InventoryClickEvent event, SkyBlockUser user, Profile profile) {
        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());

        profileData.setSelectedProfile(profile);
        SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().select(user.getId(), profile);

        Player player = (Player) event.getWhoClicked();
        player.closeInventory();

        AttributeUtils.manageAttributes(profile);

        Island island = SkyBlockApiProvider.Cache.Local.ISLANDS.provide().get(profile.getIslandId());
        IslandUtils.sendToIsland(player, user.getHandle(), island);

        //event.getWhoClicked().openInventory(new ProfileInventory(user));
    }

    private static void newProfile(InventoryClickEvent event, SkyBlockUser user) {
        Player player = (Player) event.getWhoClicked();
        ProfileData profileData = SkyBlockApiProvider.Cache.Local.USERS_PROFILES.provide().get(user.getId());

        try {
            String islandId = SeaweedUtils.newIslandId();

            Profile profile = Profile.empty(islandId);
            profileData.getProfiles().add(profile);
            profileData.setSelectedProfile(profile);

            Island island = new Island(islandId, false, new ProfileUser(user.getId(), profile.getId()));
            SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().create(island);

            SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().insert(user.getId(), profile);
            SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().select(user.getId(), profile);

            AttributeUtils.manageAttributes(profile);

            player.closeInventory();
            IslandUtils.sendToIsland(player, user.getHandle(), island);
        } catch (IOException | UnknownWorldException | WorldInUseException ex) {
            ex.printStackTrace();

            player.closeInventory();
            Message.ERROR.send(player, "Ocorreu um erro a criar uma nova ilha.");
            return;
        }
    }
}
