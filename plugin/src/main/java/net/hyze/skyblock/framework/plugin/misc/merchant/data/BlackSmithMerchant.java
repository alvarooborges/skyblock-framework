package net.hyze.skyblock.framework.plugin.misc.merchant.data;

import com.google.common.collect.Lists;
import java.util.List;
import net.hyze.skyblock.framework.plugin.misc.merchant.Merchant;
import net.hyze.skyblock.framework.plugin.misc.merchant.MerchantItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlackSmithMerchant extends Merchant {

    public BlackSmithMerchant() {
        super(
                "Ferreiro",
                new Location(
                        Bukkit.getWorld("world"),
                        -12.53,
                        68,
                        -8.22,
                        -2.1F,
                        -1.2F
                ),
                "eyJ0aW1lc3RhbXAiOjE0NzEzODYxNDM3MjUsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhOWM2M2RjYTMyYWYxNzZjYTk3NmRhMGExMzA1ZDdjMTA1OTIwM2MzZWFiNmViMGZlYjljNThlZGI1ZjY4OSJ9fX0=",
                "qd552cNYOY47joF1F6V57k96Qs/3XGF8j5XkUHMc4UploDn9R4g3BnADFfptr6GD+zd6AYC01RDq3qs9l9gdViZxtcYE8Tl2twoQagcL0fckj6Xc8NGkMIWM8kNrrO/DixzXx1efG4dBMFnRA9kOSuf1Fs0aIoZCArWmo1UsLcmcNOd+EC6Bji1waIRkUXP26EOyWnFz2+OXlkLzSPaHrpvxodROWyCbQWufRK9/xG23OOzzriCknZRvvsmhDfc0hwJEDJuHSi4lyvaklOTlsufUTyfLWvDF0CVIg6xs7/Kc/mrmE1pc6mzZbFZrOWldKYbAR7nsGwHl6iRzXyq9hPbOAPTqb+Gq+5y/79F60ATF16iHEYVQmvkPLvOybmDrRgkWXUFr3SujiNcKlK5MfAPCpkGC73m8QH4we1Tv6BqHLjF0Ee5JDjwaOMCNoO0fYcTrI2EWsKIvKynLe+fBjaW4AmU+O7Jgj8VuA55wJ7RCdfYswUKAqyN80f2ikXZ7vzrle8BWps5y506A7De9bNzbhzQaDQZnGl7aW4mi58MewX6k4UG1QYBNDBDLUDdhk46sAp/hqodkzDCiRTR1sdqCSiqU+ZPhnJtxIaI6kJJwTJPkdlCeolpNL6g0vcHpSQKCn0ubu9zf+abX+wK8vkrFGpgzIrtKNVWdXdKkQAo="
        );
    }

    @Override
    public List<MerchantItem> getItems() {
        List<MerchantItem> items = Lists.newArrayList();

        items.add(new MerchantItem(new ItemStack(Material.IRON_PICKAXE), 50.0));
        items.add(new MerchantItem(new ItemStack(Material.IRON_INGOT), 15.0));
        items.add(new MerchantItem(new ItemStack(Material.IRON_BLOCK), 135.0));

        return items;
    }

}
