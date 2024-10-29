package net.hyze.skyblock.framework.plugin.inventory;


import com.google.common.collect.Lists;
import java.util.List;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.skyblock.framework.api.user.attributes.AttributeData;
import net.hyze.skyblock.framework.api.user.attributes.AttributeRegistry;
import net.hyze.skyblock.framework.api.user.attributes.api.AbstractAttribute;
import net.hyze.skyblock.framework.api.user.attributes.api.BasicAttribute;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class AttributesInventory extends CustomInventory {

    public AttributesInventory(SkyBlockUser user, AttributeData data) {
        super(27, "Atributos");

        List<AbstractAttribute> basicAttributeList = Lists.newLinkedList();
        List<AbstractAttribute> computedAttributeList = Lists.newLinkedList();

        for(AbstractAttribute attribute : AttributeRegistry.getAttributes()) {
            if(attribute instanceof BasicAttribute) {
                basicAttributeList.add(attribute);
            } else {
                computedAttributeList.add(attribute);
            }
        }

        List<AbstractAttribute> list = Lists.newLinkedList();
        list.addAll(basicAttributeList);
        list.addAll(computedAttributeList);

        ItemBuilder item = ItemBuilder.of(Material.PAPER).name("&aAtributos");

        for(AbstractAttribute attribute : list) {
            String debug = attribute.getDebug(user.getHandle());

            ChatColor color = attribute instanceof BasicAttribute ? ChatColor.YELLOW : ChatColor.AQUA;
            item.lore("  " +  color + attribute.getName() + ": &f" + attribute.format(data.getValue(attribute)) + (debug == null ? "" : ChatColor.GRAY + " " + debug));
        }

        setItem(13, item.make());
    }
}
