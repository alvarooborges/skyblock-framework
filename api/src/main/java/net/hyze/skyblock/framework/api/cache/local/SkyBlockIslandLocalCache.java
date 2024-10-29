package net.hyze.skyblock.framework.api.cache.local;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.island.Island;

public class SkyBlockIslandLocalCache implements LocalCache {

  private final Cache<String, Island> CACHE_BY_ID = Caffeine.newBuilder()
      .expireAfterWrite(2, TimeUnit.SECONDS)
      .build();

  public Island get(@NonNull String islandId) {
    Island island = CACHE_BY_ID.getIfPresent(islandId);

    if(island == null) {
      island = SkyBlockApiProvider.Repositories.SKYBLOCK_ISLANDS.provide().fetchById(islandId);

      if(island != null) {
        put(island);
      }
    }

    return island;
  }

  public void put(@NonNull Island island) {
    CACHE_BY_ID.put(island.getId(), island);
  }

}