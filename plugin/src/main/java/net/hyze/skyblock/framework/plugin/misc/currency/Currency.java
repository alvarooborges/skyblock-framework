package net.hyze.skyblock.framework.plugin.misc.currency;

import java.text.NumberFormat;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
public enum Currency {

    COINS(
            "Coin",
            "Coins",
            ChatColor.GREEN
    );

    private final String singular;
    private final String plural;
    private final ChatColor color;

    public String format(double amount) {
        String str = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(amount).replace("R$ ", "");
        return str;
    }

    public String formatWithPrefix(double amount) {
        return String.format("%s %s", format(amount), (amount > 1 ? this.plural : this.singular));
    }

}
