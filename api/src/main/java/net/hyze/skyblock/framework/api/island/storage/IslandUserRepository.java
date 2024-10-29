package net.hyze.skyblock.framework.api.island.storage;

import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import java.util.Collections;
import java.util.List;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MongoRepository;
import org.bson.Document;

public class IslandUserRepository extends MongoRepository {

    public IslandUserRepository(MongoDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insert(int userId, String islandId) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_user_islands");

        collection.updateOne(Filters.eq("user_id", userId), Updates.addToSet("islands", islandId),
            new UpdateOptions().upsert(true));
    }

    public void remove(int userId, String islandId) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_user_islands");

        collection.updateOne(Filters.eq("user_id", userId), Updates.pull("islands", islandId),
            new UpdateOptions().upsert(true));
    }

    public List<String> fetch(int userId) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_user_islands");

        AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
            Aggregates.match(Filters.eq("user_id", userId)),
            Aggregates.project(Projections.include("islands"))
        ));

        Document first = aggregateIterable.first();
        if(first != null && first.containsKey("islands")) {
            return first.getList("islands", String.class);
        }

        return Collections.emptyList();
    }

}
