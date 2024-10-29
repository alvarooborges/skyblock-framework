package net.hyze.skyblock.framework.api.cache.redis;

import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.providers.RedisProvider;
import net.hyze.skyblock.framework.api.status.IslandStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@RequiredArgsConstructor
public class SkyBlockIslandsRedisCache implements RedisCache {

    private static final String ISLANDS_KEYS = "skyblock:islands";
    private static final String ISLANDS_ID_KEYS = "skyblock:islands:%s";

    /*

     */

    private final RedisProvider redis;

    public Collection<String> fetchAllIslands() {
        try (Jedis jedis = redis.provide().getResource()) {
            return jedis.smembers(ISLANDS_KEYS);
        }
    }

    public void insertIsland(String islandId, IslandStatus status) {
        try (Jedis jedis = redis.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.sadd(ISLANDS_KEYS, islandId);
            insertStatus(islandId, status, pipeline);
            pipeline.sync();
        }
    }

    public void destroyIsland(String islandId, Pipeline pipeline) {
        pipeline.srem(ISLANDS_KEYS, islandId);

        destroyStatus(islandId, pipeline);
    }

    /*

     */
    public IslandStatus getStatus(String islandId) {
        try (Jedis jedis = redis.provide().getResource()) {
            String json = jedis.get(String.format(ISLANDS_ID_KEYS, islandId));

            if (json != null) {
                return CoreConstants.JACKSON.readValue(json, IslandStatus.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

  private void insertStatus(String islandId, IslandStatus status, Pipeline pipeline) {
        try {
            pipeline.set(String.format(ISLANDS_ID_KEYS, islandId), CoreConstants.JACKSON.writeValueAsString(status));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void destroyStatus(String islandId, Pipeline pipeline) {
        pipeline.del(String.format(ISLANDS_ID_KEYS, islandId));
    }
}
