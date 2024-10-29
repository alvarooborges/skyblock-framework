package net.hyze.skyblock.framework.api.user.attributes.api;

import lombok.Getter;

public class BasicAttribute<V> extends AbstractAttribute<V> {

    @Getter
    private final V defaultValue;

    public BasicAttribute(String id, String name, String[] description, V defaultValue) {
        super(id, name, description);

        this.defaultValue = defaultValue;
    }
}
