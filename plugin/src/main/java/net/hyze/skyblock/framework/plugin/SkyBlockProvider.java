package net.hyze.skyblock.framework.plugin;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.hyze.core.shared.contracts.Provider;
import net.hyze.core.shared.providers.*;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.plugin.cache.local.SkyBlockMiningLocalCache;
import net.hyze.skyblock.framework.plugin.cache.local.SkyBlockUserLocalCache;
import net.hyze.skyblock.framework.plugin.misc.npc.cache.CustomNPCLocalCache;
import net.hyze.skyblock.framework.plugin.user.data.storage.UserDataRepository;

public class SkyBlockProvider {

    public static final List<Provider<?>> PROVIDERS = Lists.newLinkedList();

    static {
        PROVIDERS.add(Cache.Local.USERS);
        PROVIDERS.add(Cache.Local.MINING);
    }

    public static void prepare(MongoDatabaseProvider MONGO_PROVIDER, MysqlDatabaseProvider MYSQL_PROVIDER, RedisProvider REDIS_PROVIDER) {
        SkyBlockApiProvider.prepare(MONGO_PROVIDER, MYSQL_PROVIDER, REDIS_PROVIDER);

        PROVIDERS.stream().filter(Objects::nonNull).forEach(provider_ -> {
            provider_.prepare();
        });

        Repositories.USER_DATA.prepare();
    }

    public static class Database {

    }

    public static class Redis {

    }

    public static class Repositories {

        public static final MongoRepositoryProvider<UserDataRepository> USER_DATA = new MongoRepositoryProvider<>(
                () -> SkyBlockApiProvider.Database.MONGO_SKYBLOCK,
                UserDataRepository.class
        );
    }

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<SkyBlockUserLocalCache> USERS = new LocalCacheProvider(new SkyBlockUserLocalCache());

            public static final LocalCacheProvider<CustomNPCLocalCache> NPC = new LocalCacheProvider(new CustomNPCLocalCache());

            public static final LocalCacheProvider<SkyBlockMiningLocalCache> MINING = new LocalCacheProvider(new SkyBlockMiningLocalCache());

        }

        public static class Redis {

        }
    }

}
