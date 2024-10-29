package net.hyze.skyblock.framework.plugin.misc.fairysouls;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import static net.hyze.core.spigot.misc.utils.HeadTexture.TEXTURE_API_URL;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FairySoulsInventory extends CustomInventory {

    public FairySoulsInventory() {
        super(45, "Almas");

        ItemStack glass = ItemBuilder.of(Material.STAINED_GLASS_PANE)
                .durability(15)
                .name("&f")
                .make();

        for (int slot = 0; slot < 45; slot++) {
            setItem(
                    slot,
                    glass
            );
        }

        ItemStack soul = ItemBuilder.of(Material.SKULL_ITEM)
                .durability(3)
                .skullUrl(TEXTURE_API_URL + "38be8abd66d09a58ce12d377544d726d25cad7e979e8c2481866be94d3b32f")
                .name("&cColetor de Almas")
                .lore(
                        "&7Sempre que você encontrar &bAlmas",
                        "&7pelo mapa, traga elas para mim e",
                        "&7eu te darei pontos em seus",
                        "&7atributos!",
                        "",
                        "&7Almas: &c0/5",
                        "",
                        "&eClique para devolver!"
                )
                .make();

        setItem(
                22,
                soul,
                event -> {

                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();

                    player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 10, 1);
                    player.playSound(player.getLocation(), Sound.WOLF_HOWL, 10, 1);

                    Bukkit.getScheduler().runTaskLater(SkyBlockPlugin.getInstance(), () -> {

                        if (!player.isOnline()) {
                            return;
                        }

                        Message.EMPTY.send(
                                player,
                                "\n &d&lALMA ENTREGUE COM SUCESSO!"
                                + "\n &fVocê recebeu pontos de atributos permanentes!"
                                + "\n "
                                + "\n  &8+&a3 &fVida"
                                + "\n  &8+&a1 &fDefesa"
                                + "\n  &8+&a1 &fForça"
                                + "\n "
                        );

                        player.playSound(player.getLocation(), Sound.CLICK, 10, 1f);
                        player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 10, 0.5f);

                    }, 30L);

                }
        );
    }

}
