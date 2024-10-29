package net.hyze.skyblock.framework.plugin.user.data.storage;

import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MongoRepository;
import net.hyze.core.spigot.misc.utils.NBTTagCompoundUtils;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bson.Document;

public class UserDataRepository extends MongoRepository {

    public UserDataRepository(MongoDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public NBTTagCompound fetch(Profile profile) {

        MongoDatabase database = getDatabaseProvider().provide().getConnection();

        MongoCollection<Document> collection = database.getCollection("skyblock_users_data");

        AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
                Aggregates.match(Filters.eq("profile_id", profile.getId()))
        ));

        Document document = aggregateIterable.first();

        if (document == null || !document.containsKey("compound")) {
            return null;
        }

        return NBTTagCompoundUtils.deserialize(document.getString("compound"));
    }

    public void update(Profile profile, NBTTagCompound compound) {
        MongoDatabase database = getDatabaseProvider().provide().getConnection();

        MongoCollection<Document> collection = database.getCollection("skyblock_users_data");

        collection.updateOne(
                Filters.eq("profile_id", profile.getId()),
                Updates.set("compound", NBTTagCompoundUtils.serialize(compound)),
                new UpdateOptions().upsert(true)
        );
    }
}
