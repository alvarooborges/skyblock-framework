package net.hyze.skyblock.framework.api;

import com.google.common.collect.Lists;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.providers.LocalCacheProvider;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.providers.MongoRepositoryProvider;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.providers.RedisCacheProvider;
import net.hyze.core.shared.providers.RedisProvider;
import net.hyze.seaweedfs.client.WeedFSClient;
import net.hyze.seaweedfs.client.WeedFSClientBuilder;
import net.hyze.skyblock.framework.api.cache.local.SkyBlockIslandLocalCache;
import net.hyze.skyblock.framework.api.cache.local.SkyBlockUserAttributesLocalCache;
import net.hyze.skyblock.framework.api.cache.local.SkyBlockUserCoopLocalCache;
import net.hyze.skyblock.framework.api.cache.local.SkyBlockUserProfilesLocalCache;
import net.hyze.skyblock.framework.api.cache.redis.SkyBlockIslandsRedisCache;
import net.hyze.skyblock.framework.api.cache.redis.SkyBlockServersRedisCache;
import net.hyze.skyblock.framework.api.island.storage.IslandRepository;
import net.hyze.skyblock.framework.api.user.storage.UserRepository;

public class SkyBlockApiProvider {

    public static final List<Provider<?>> PROVIDERS = Lists.newLinkedList();

    static {
        PROVIDERS.add(Cache.Local.ISLANDS);
        PROVIDERS.add(Cache.Local.USERS_PROFILES);
        PROVIDERS.add(Cache.Local.USERS_ATTRIBUTES);
        PROVIDERS.add(Cache.Local.USERS_COOP);

        PROVIDERS.add(Repositories.SKYBLOCK_USERS);
        PROVIDERS.add(Repositories.SKYBLOCK_ISLANDS);
    }

    public static void prepare(MongoDatabaseProvider MONGO_PROVIDER, MysqlDatabaseProvider MYSQL_PROVIDER, RedisProvider REDIS_PROVIDER) {
        Database.MONGO_SKYBLOCK = MONGO_PROVIDER;
        Redis.REDIS_SKYBLOCK = REDIS_PROVIDER;
        Database.MYSQL_SKYBLOCK = MYSQL_PROVIDER;

        Database.MONGO_SKYBLOCK.prepare();
        Redis.REDIS_SKYBLOCK.prepare();
        Database.MYSQL_SKYBLOCK.prepare();

        PROVIDERS.stream().filter(Objects::nonNull).forEach(provider_ -> {
            provider_.prepare();
        });

        Cache.Redis.SKYBLOCK_ISLANDS = new RedisCacheProvider<>(new SkyBlockIslandsRedisCache(Redis.REDIS_SKYBLOCK));
        Cache.Redis.SKYBLOCK_SERVERS = new RedisCacheProvider<>(new SkyBlockServersRedisCache(Redis.REDIS_SKYBLOCK, Cache.Redis.SKYBLOCK_ISLANDS.provide()));
    }

    public static class Database {

        public static MysqlDatabaseProvider MYSQL_SKYBLOCK;

        public static MongoDatabaseProvider MONGO_SKYBLOCK;

        public static WeedFSClient SEAWEED_SKYBLOCK;

        static {
            try {
                SEAWEED_SKYBLOCK = WeedFSClientBuilder.createBuilder().setMasterUrl(new URL("http://149.56.28.162:9333")).build();
            } catch (MalformedURLException ignored) {
            }
        }

    }

    public static class Redis {

        public static RedisProvider REDIS_SKYBLOCK;

    }

    public static class Repositories {

        public static MongoRepositoryProvider<UserRepository> SKYBLOCK_USERS = new MongoRepositoryProvider<>(
                () -> Database.MONGO_SKYBLOCK,
                UserRepository.class
        );

        public static MongoRepositoryProvider<IslandRepository> SKYBLOCK_ISLANDS = new MongoRepositoryProvider<>(
                () -> Database.MONGO_SKYBLOCK,
                IslandRepository.class
        );

    }

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<SkyBlockIslandLocalCache> ISLANDS = new LocalCacheProvider(new SkyBlockIslandLocalCache());

            public static final LocalCacheProvider<SkyBlockUserProfilesLocalCache> USERS_PROFILES = new LocalCacheProvider(new SkyBlockUserProfilesLocalCache());

            public static final LocalCacheProvider<SkyBlockUserAttributesLocalCache> USERS_ATTRIBUTES = new LocalCacheProvider(new SkyBlockUserAttributesLocalCache());

            public static final LocalCacheProvider<SkyBlockUserCoopLocalCache> USERS_COOP = new LocalCacheProvider(new SkyBlockUserCoopLocalCache());

        }

        public static class Redis {

            public static RedisCacheProvider<SkyBlockIslandsRedisCache> SKYBLOCK_ISLANDS;

            public static RedisCacheProvider<SkyBlockServersRedisCache> SKYBLOCK_SERVERS;

        }
    }

}
