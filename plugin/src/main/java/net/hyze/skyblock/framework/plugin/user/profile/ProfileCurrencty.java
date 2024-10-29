package net.hyze.skyblock.framework.plugin.user.profile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileCurrencty {

    public static void incrementPurse(SkyBlockUser skyBlockUser, Profile selectedProfile, double value) {
        selectedProfile.addPurse(value);
        SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().incrementPurse(skyBlockUser.getId(), selectedProfile, value);
    }

}
