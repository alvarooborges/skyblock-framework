package net.hyze.skyblock.framework.api.cache.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.providers.RedisProvider;
import net.hyze.core.shared.redis.RedisScriptManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@RequiredArgsConstructor
public class SkyBlockServersRedisCache implements RedisCache {

  private static final String SERVERS_KEY = "skyblock:servers";
  private static final String SERVERS_ISLANDS_KEY = "skyblock:servers:%s:islands";

  private static final String SLAVES_KEY = "skyblock:slaves";
  private static final String SLAVES_SERVERS_KEY = "skyblock:slaves:%s:servers";

  private static final String ID_KEY = "skyblock:id";

  /*

   */

  private final RedisProvider redis;

  private final SkyBlockIslandsRedisCache islandsCache;

  public Collection<String> fetchAllServers() {
    try (Jedis jedis = redis.provide().getResource()) {
      return jedis.smembers(SERVERS_KEY);
    }
  }

  public void insertServer(String server) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      pipeline.sadd(SERVERS_KEY, server);
      pipeline.sync();
    }
  }

  public void destroyServer(String server) {
    Collection<String> islands = fetchAllServerIslands(server);

    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();

      for(String islandId : islands) {
        destroyServerIsland(server, islandId, pipeline);
      }

      pipeline.srem(SERVERS_KEY, server);
      pipeline.sync();
    }
  }

  /*

   */

  public Collection<String> fetchAllSlaveServers(String slaveId) {
    try (Jedis jedis = redis.provide().getResource()) {
      return jedis.smembers(String.format(SLAVES_SERVERS_KEY, slaveId));
    }
  }

  public void insertSlaveServer(String slaveId, String server) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      pipeline.sadd(String.format(SLAVES_SERVERS_KEY, slaveId), server);
      pipeline.sync();
    }
  }

  public void destroySlaveServer(String slaveId, String server) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      pipeline.srem(String.format(SLAVES_SERVERS_KEY, slaveId), server);
      pipeline.sync();
    }
  }

  /*

   */

  public Integer countAllServerIslands(String serverId) {
    try (Jedis jedis = redis.provide().getResource()) {
      return jedis.scard(String.format(SERVERS_ISLANDS_KEY, serverId)).intValue();
    }
  }

  public Collection<String> fetchAllServerIslands(String serverId) {
    try (Jedis jedis = redis.provide().getResource()) {
      return jedis.smembers(String.format(SERVERS_ISLANDS_KEY, serverId));
    }
  }

  public void insertServerIsland(String serverId, String islandId) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      pipeline.sadd(String.format(SERVERS_ISLANDS_KEY, serverId), islandId);
      pipeline.sync();
    }
  }

  public void destroyServerIsland(String serverId, String islandId) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      destroyServerIsland(serverId, islandId, pipeline);
      pipeline.sync();
    }
  }

  public void destroyServerIsland(String serverId, String islandId, Pipeline pipeline) {
    pipeline.srem(String.format(SERVERS_ISLANDS_KEY, serverId), islandId);
    islandsCache.destroyIsland(islandId, pipeline);
  }

  /*

   */

  public Collection<String> fetchAllSlaves() {
    try (Jedis jedis = redis.provide().getResource()) {
      return jedis.smembers(SLAVES_KEY);
    }
  }

  public void insertSlave(String slave) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      pipeline.sadd(SLAVES_KEY, slave);
      pipeline.sync();
    }
  }

  public void destroySlave(String slave) {
    try (Jedis jedis = redis.provide().getResource()) {
      Pipeline pipeline = jedis.pipelined();
      pipeline.srem(SLAVES_KEY, slave);
      pipeline.sync();
    }
  }

  /*

   */

  public String fetchLightestServer() {
    try (Jedis jedis = redis.provide().getResource()) {
      return Optional.ofNullable(RedisScriptManager
          .execute(jedis, "skyblock/fetchLightestServer.lua", Collections.emptyList(),
              Collections.emptyList())).map(String::valueOf).orElse(null);
    }
  }

  public String fetchLightestSlave() {
    try (Jedis jedis = redis.provide().getResource()) {
      return Optional.ofNullable(RedisScriptManager
          .execute(jedis, "skyblock/fetchLightestSlave.lua", Collections.emptyList(),
              Collections.emptyList())).map(String::valueOf).orElse(null);
    }
  }

  public int getNextId() {
    try (Jedis jedis = redis.provide().getResource()) {
      Long id = jedis.incr(ID_KEY);

      if (id > 500) {
        jedis.set(ID_KEY, "1");
        return 1;
      }

      return id.intValue();
    }
  }
}
