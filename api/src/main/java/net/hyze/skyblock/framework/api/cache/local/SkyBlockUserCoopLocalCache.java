package net.hyze.skyblock.framework.api.cache.local;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.coop.CoopData;

public class SkyBlockUserCoopLocalCache implements LocalCache {

    private final Cache<Integer, CoopData> CACHE_BY_ID = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.SECONDS)
        .build();

    public CoopData get(@NonNull int userId) {
        CoopData coopData = CACHE_BY_ID.getIfPresent(userId);

        if (coopData == null) {
            coopData = SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().coop().fetch(userId);
            put(userId, coopData);
        }

        return coopData;
    }

    public void put(@NonNull int userId, @NonNull CoopData data) {
        CACHE_BY_ID.put(userId, data);
    }

    public void remove(Integer id) {
        CACHE_BY_ID.invalidate(id);
    }
}