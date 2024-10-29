package net.hyze.skyblock.framework.plugin.misc.merchant;

import java.util.List;
import lombok.Getter;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.hyze.skyblock.framework.plugin.SkyBlockConstants;
import net.hyze.skyblock.framework.plugin.misc.npc.CustomNPC;
import org.bukkit.Location;

@Getter
public abstract class Merchant {

    private final String name;
    private final Location location;

    private final String skinValue;
    private final String skinSign;

    public Merchant(String name, Location location, String skinValue, String skinSign) {
        this.name = name;
        this.location = location;
        this.skinValue = skinValue;
        this.skinSign = skinSign;
    }

    public void setup() {
        String[] hologramText = new String[]{
            "&f&l" + this.name, SkyBlockConstants.NPC_RIGHT_CLICK_TEXT
        };

        new CustomNPC() {

            @Override
            public void on(NPCRightClickEvent event) {

                event.getClicker().openInventory(new MerchantInventory(Merchant.this));

            }

        }.skin(this.skinValue, this.skinSign)
                .hologram(hologramText)
                .spawn(this.location);
    }

    public abstract List<MerchantItem> getItems();

}
