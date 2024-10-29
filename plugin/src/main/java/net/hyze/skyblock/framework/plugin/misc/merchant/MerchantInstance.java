package net.hyze.skyblock.framework.plugin.misc.merchant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.skyblock.framework.plugin.misc.merchant.data.BlackSmithMerchant;

@Getter
@RequiredArgsConstructor
public enum MerchantInstance {

    BLACK_SMITH(new BlackSmithMerchant());

    private final Merchant merchantInstance;

    public static void setup() {
        for (MerchantInstance merchantInstance : MerchantInstance.values()) {
            merchantInstance.getMerchantInstance().setup();
        }
    }

}
