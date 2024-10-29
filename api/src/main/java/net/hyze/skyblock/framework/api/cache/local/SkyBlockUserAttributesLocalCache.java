package net.hyze.skyblock.framework.api.cache.local;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider.Repositories;
import net.hyze.skyblock.framework.api.user.attributes.AttributeData;
import net.hyze.skyblock.framework.api.user.profile.Profile;

public class SkyBlockUserAttributesLocalCache implements LocalCache {

    private final Cache<Profile, AttributeData> CACHE_BY_ID = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

    public AttributeData get(@NonNull Profile profile) {
        AttributeData attributeData = CACHE_BY_ID.getIfPresent(profile);

        if (attributeData == null) {
            attributeData = Repositories.SKYBLOCK_USERS.provide().profiles().attributes().fetch(profile);

            if(attributeData != null) {
                put(profile, attributeData);
            }
        }

        return attributeData;
    }

    public void put(@NonNull Profile profile, @NonNull AttributeData data) {
        CACHE_BY_ID.put(profile, data);
    }

    public void remove(Profile profile) {
      CACHE_BY_ID.invalidate(profile);
    }
}