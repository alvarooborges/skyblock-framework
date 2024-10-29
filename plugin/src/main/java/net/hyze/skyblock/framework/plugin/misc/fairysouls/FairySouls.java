package net.hyze.skyblock.framework.plugin.misc.fairysouls;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.skyblock.framework.plugin.SkyBlockConstants;
import net.hyze.skyblock.framework.plugin.SkyBlockPlugin;
import net.hyze.skyblock.framework.plugin.misc.npc.CustomNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@NoArgsConstructor
public class FairySouls {

    @Getter
    private final Multimap<AppType, Location> souls = HashMultimap.create();

    public FairySouls souls(AppType appType, String world, double x, double y, double z) {
        this.souls.put(appType, new Location(Bukkit.getWorld(world), x, y, z));
        return this;
    }

    public void prepare(AppType appType, Location fairyLocation) {

        /**
         * Registra os listeners de quando o jogador interage com as almas.
         */
        Bukkit.getPluginManager().registerEvents(new FairySoulsListeners(this), SkyBlockPlugin.getInstance());

        /**
         * Só spawna o NPC caso seja no mesmo App Type da localização dele.
         */
        if (!CoreProvider.getApp().getType().equals(appType)) {
            return;
        }

        String[] hologramText = new String[]{
            "&f&lColetor de Almas", SkyBlockConstants.NPC_RIGHT_CLICK_TEXT
        };

        String skinValue = "eyJ0aW1lc3RhbXAiOjE1Njc4MDM3OTQ0MDksInByb2ZpbGVJZCI6IjJjMTA2NGZjZDkxNzQyODI4NGUzYmY3ZmFhN2UzZTFhIiwicHJvZmlsZU5hbWUiOiJOYWVtZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWEyM2M4Y2RlMjk0M2M4NDI0OWRlODM1MWJjMzU0MGJlNWY4YWZhYWJhOGIyY2IwMzJmYzVhY2FkNzhhMjY5YiJ9fX0=";
        String skinSign = "jORkgt2cT6xu3AYAC8oHw3iUGwlTeFHrwsFVqVjlmKzUikllIcoA6lNHpUoXoK9G578StblydFdSuItH8FfQHGeEQIaKeD1u/fworJEoihhUsPJvyEMy08377l0vZ2VMdg5hu2gm2xofDSPLwQ3kKB5alOcoY7f2q2B9LSqCq8LJYdz2h7y0jYG8Vi1AYObZK76M+KqUiJIYuWiNtuekfo90662Ankl23zJ4jcPmaQSO+b9oJWSAu8tJqT56a01sycs1bw4u1xyYif1IKVf+yZ60V8imqDo6h/1qWy/M6lsMliaGhJJh+byR5rT+Y5Q+XqyNZAvGG8kAELpNJabxbBOJdcrQH93zMwxCnJ/1HCFi/cpWkfD9E8pfLYkflBEoZMrMQy4rRCVKqYzqnxARxw5c/Oq+Ika2k5yrM4tqPw4sGXOtjHQGFPlYeFaU7wn9nOOxxjEYy7TVzylioNEzKu86OgqmDB1m/X2n4+cu3njVkYZAErLad/3aXTazY3pcyqOIL2M0IwtfLcUmMnHyFg66WgaXdJxW+CTbZjZmX77UfuDdYBnGD4x8/LMzPvXWERghO2BuxExHR5Xv78IQWTfRf2Uz41HiTnVS1tRN8hVzZM5YOj4DkrG8byojDbGdvM3NyCluvtCX+vxHmHvlSL5Rqz8+bW3yT+43pyPKs5A=";

        new CustomNPC() {

            @Override
            public void on(NPCRightClickEvent event) {

                event.getClicker().openInventory(new FairySoulsInventory());

            }

        }.skin(skinValue, skinSign)
                .hologram(hologramText)
                .spawn(fairyLocation);

    }

}
