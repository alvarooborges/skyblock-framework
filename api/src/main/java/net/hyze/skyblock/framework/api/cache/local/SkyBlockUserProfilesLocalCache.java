package net.hyze.skyblock.framework.api.cache.local;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider.Repositories;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;

public class SkyBlockUserProfilesLocalCache implements LocalCache {

    private final Cache<Integer, ProfileData> CACHE_BY_ID = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build();

    public ProfileData get(@NonNull int userId) {
        ProfileData profileData = CACHE_BY_ID.getIfPresent(userId);

        if (profileData == null) {
            profileData = Repositories.SKYBLOCK_USERS.provide().profiles().fetch(userId);

            if(profileData != null) {
                put(userId, profileData);
            }
        }

        return profileData;
    }

    public void put(@NonNull int userId, @NonNull ProfileData data) {
        CACHE_BY_ID.put(userId, data);
    }

    public void remove(Integer id) {
      CACHE_BY_ID.invalidate(id);
    }
}