package net.hyze.skyblock.framework.api.user.attributes.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.user.User;

@RequiredArgsConstructor
public abstract class AbstractAttribute<V> {

    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final String[] description;

    public String format(V value) {
        return value.toString();
    }

    public String getDebug(User user) {
        return null;
    }

    /*

     */

    protected static String formatPercent(Double value) {
        return String.format("%d%%", (int) (value * 100));
    }

}
