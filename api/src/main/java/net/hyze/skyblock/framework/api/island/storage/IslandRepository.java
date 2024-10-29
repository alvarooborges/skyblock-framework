package net.hyze.skyblock.framework.api.island.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Collection;
import java.util.List;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MongoRepository;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.skyblock.framework.api.user.profile.ProfileUser;
import net.hyze.skyblock.framework.api.island.Island;
import net.hyze.skyblock.framework.api.utils.DocumentUtils;
import org.bson.Document;

public class IslandRepository extends MongoRepository {

    public IslandRepository(MongoDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    /*

     */

    private Island parse(Document document) {
        Island island = new Island(document.getString("island_id"), document.getBoolean("coop", false));

        document.getList("members", Document.class).stream().map(ProfileUser::new).forEach(island.getMembers()::add);

        if (document.containsKey("spawn_location")) {
            island.setSpawnLocation(DocumentUtils.getSerializedLocation(document, "spawn_location"));
        }

        island.setTimeMillis(document.get("time_millis", System.currentTimeMillis()));

        return island;
    }

    /*

     */

    public Collection<Island> fetchByIds(Collection<String> islandIds) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");

        AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
            Aggregates.match(Filters.in("island_id", islandIds))
        ));

        Collection<Island> islands = Sets.newHashSet();

        for (Document first : aggregateIterable) {
            islands.add(parse(first));
        }

        return islands;
    }

    public Island fetchById(String islandId) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");

        AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
            Aggregates.match(Filters.eq("island_id", islandId))
        ));

        Document first = aggregateIterable.first();
        if (first != null) {
            return parse(first);
        }

        return null;
    }

    /*

     */

    public void updateMillis(Island island, long millis) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");

        collection
            .updateOne(Filters.eq("island_id", island.getId()), Updates.set("time_millis", millis));
    }

    public void updateSpawn(Island island, SerializedLocation location) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");

        collection
            .updateOne(Filters.eq("island_id", island.getId()), Updates.set("spawn_location", DocumentUtils.getLocationDocument(location)));
    }

    public void removeMember(Island island, ProfileUser member) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");

        Document document = new Document();
        member.serialize(document);

        collection
            .updateOne(Filters.eq("island_id", island.getId()), Updates.pull("members", document));
    }

    public void create(Island island) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");

        Document document = new Document();
        document.put("island_id", island.getId());
        document.put("coop", island.isCoop());
        document.put("spawn_location", DocumentUtils.getLocationDocument(island.getSpawnLocation()));
        document.put("time_millis", System.currentTimeMillis());

        List<Document> members = Lists.newArrayList();

        for (ProfileUser user : island.getMembers()) {
            Document member = new Document();
            user.serialize(member);
            members.add(member);
        }

        document.put("members", members);

        collection.insertOne(document);
    }

    public void delete(Island island) {
        MongoDatabase database = this.getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_islands");
        collection.deleteOne(Filters.eq("island_id", island.getId()));
    }
}
