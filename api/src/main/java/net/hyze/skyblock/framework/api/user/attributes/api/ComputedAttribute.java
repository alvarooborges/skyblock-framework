package net.hyze.skyblock.framework.api.user.attributes.api;

import net.hyze.skyblock.framework.api.user.attributes.AttributeData;

public abstract class ComputedAttribute<V> extends AbstractAttribute<V> {

    public ComputedAttribute(String id, String name, String[] description) {
        super(id, name, description);
    }

    public abstract V getValue(AttributeData data);
}
