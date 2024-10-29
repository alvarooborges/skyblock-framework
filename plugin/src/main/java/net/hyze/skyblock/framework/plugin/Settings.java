package net.hyze.skyblock.framework.plugin;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.providers.RedisProvider;

@Getter
@Builder
public class Settings {

    @NonNull
    private MongoDatabaseProvider mongoProvider;

    @NonNull
    private MysqlDatabaseProvider mysqlProvider;

    @NonNull
    private RedisProvider redisProvider;

    @NonNull
    private String mainHubFid;

}
