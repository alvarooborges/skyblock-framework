package net.hyze.skyblock.framework.api.user.attributes;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.hyze.skyblock.framework.api.user.attributes.api.AbstractAttribute;

public class AttributeRegistry {

    private static final Map<String, AbstractAttribute> registry = Maps.newLinkedHashMap();

    /*

     */


    public static AbstractAttribute getAttribute(String id) {
        return registry.get(id);
    }

    public static void registerAttribute(AbstractAttribute attribute) {
        registry.put(attribute.getId(), attribute);
    }

    public static Collection<AbstractAttribute> getAttributes() {
        return registry.values();
    }
}
