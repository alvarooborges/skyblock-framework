package net.hyze.skyblock.framework.api.user.storage;

import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import java.util.Collection;
import java.util.List;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.providers.MongoDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MongoRepository;
import net.hyze.skyblock.framework.api.user.attributes.AttributeData;
import net.hyze.skyblock.framework.api.user.attributes.api.BasicAttribute;
import net.hyze.skyblock.framework.api.user.coop.CoopData;
import net.hyze.skyblock.framework.api.user.profile.Profile;
import net.hyze.skyblock.framework.api.user.profile.ProfileData;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class UserRepository extends MongoRepository {

    public UserRepository(MongoDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    /*

     */
    private final Profiles profiles = new Profiles();

    public Profiles profiles() {
        return this.profiles;
    }

    public void create(int userId, ProfileData profileData) {
        MongoDatabase database = getDatabaseProvider().provide().getConnection();
        MongoCollection<Document> collection = database.getCollection("skyblock_profiles");

        List<Document> profilesList = Lists.newArrayList();
        List<ObjectId> idsList = Lists.newArrayList();

        for (Profile profile : profileData.getProfiles()) {
            Document outDocument = new Document();
            outDocument.put("_id", profile.getId());
            outDocument.put("user_id", userId);
            outDocument.put("name", profile.getName());
            outDocument.put("icon", String.format("%s:%d", profile.getIcon().getMaterialRaw(), profile.getIcon().getData()));
            outDocument.put("island_id", profile.getIslandId());
            outDocument.put("purse", profile.getPurse());
            outDocument.put("last_app", profile.getLastApp().toString());

            idsList.add(profile.getId());
            profilesList.add(outDocument);
        }

        collection.insertMany(profilesList);
        collection = database.getCollection("skyblock_users");

        Document profilesDocument = new Document();

        profilesDocument.put("collection", idsList);
        profilesDocument.put("selected", profileData.getSelectedProfile().getId());

        collection.updateOne(Filters.eq("user_id", userId), Updates.set("profiles", profilesDocument), new UpdateOptions().upsert(true));
    }

    public class Profiles {

        private final Attributes attributes = new Attributes();

        public Attributes attributes() {
            return this.attributes;
        }

        /*

         */

        public void migrate() {
        }

        public void insert(int userId, Profile profile) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_profiles");

            Document document = new Document();
            document.put("_id", profile.getId());
            document.put("user_id", userId);
            document.put("name", profile.getName());
            document.put("icon", String.format("%s:%d", profile.getIcon().getMaterialRaw(), profile.getIcon().getData()));
            document.put("island_id", profile.getIslandId());
            document.put("purse", profile.getPurse());
            document.put("last_app", profile.getLastApp().toString());

            collection.insertOne(document);

            collection = database.getCollection("skyblock_users");
            collection.updateOne(Filters.eq("user_id", userId), Updates.push("profiles.collection", profile.getId()));
        }

        public void remove(int userId, Profile profile) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();

            MongoCollection<Document> collection = database.getCollection("skyblock_profiles");
            collection.deleteOne(Filters.eq("_id", profile.getId()));

            collection = database.getCollection("skyblock_users");
            collection.updateOne(Filters.eq("user_id", userId), Updates.pull("profiles.collection", profile.getId()));
        }

        public void select(int userId, Profile profile) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_users");
            collection.updateOne(Filters.eq("user_id", userId), Updates.set("profiles.selected", profile.getId()));
        }

        public void updateLastApp(int userId, Profile profile, AppType lastApp) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_profiles");
            collection.updateOne(
                Filters.eq("_id", profile.getId()),
                Updates.set("last_app", lastApp.toString())
            );
        }

        public void incrementPurse(int userId, Profile profile, double purse) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_profiles");
            collection.updateOne(
                Filters.eq("_id", profile.getId()),
                Updates.inc("purse", purse)
            );
        }

        public ProfileData fetch(int userId) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_users");

            AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
                Aggregates.match(Filters.eq("user_id", userId)),
                Aggregates.lookup("skyblock_profiles", "profiles.collection", "_id", "joined_profiles"),
                Aggregates.project(Projections.include("profiles", "joined_profiles"))
            ));

            Document first = aggregateIterable.first();

            if (first != null && first.containsKey("profiles") && first.containsKey("joined_profiles")) {
                Document profileDocument = first.get("profiles", Document.class);
                List<Document> joinedProfilesDocument = first.getList("joined_profiles", Document.class);
                return new ProfileData(profileDocument, joinedProfilesDocument);
            }

            return null;
        }

        public class Attributes {

            public void updateBase(Profile profile, AttributeData data, Collection<BasicAttribute> attributes) {
                MongoDatabase database = getDatabaseProvider().provide().getConnection();
                MongoCollection<Document> collection = database.getCollection("skyblock_profiles");

                List<Bson> updates = Lists.newLinkedList();
                for(BasicAttribute attribute : attributes) {
                    updates.add(Updates.set("attributes." + attribute.getId() + ".base", data.getValue(attribute)));
                }

                collection.updateOne(Filters.eq("_id", profile.getId()), Updates.combine(updates));
            }

            public AttributeData fetch(Profile profile) {
                MongoDatabase database = getDatabaseProvider().provide().getConnection();
                MongoCollection<Document> collection = database.getCollection("skyblock_profiles");

                AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
                    Aggregates.match(Filters.eq("_id", profile.getId())),
                    Aggregates.project(Projections.include("attributes"))
                ));

                Document first = aggregateIterable.first();
                if(first != null && first.containsKey("attributes")) {
                    return new AttributeData(first.get("attributes", Document.class));
                }

                return null;
            }

        }

    }

    /*

     */
    private final Coop coop = new Coop();

    public Coop coop() {
        return this.coop;
    }

    public class Coop {

        public CoopData fetch(int userId) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_users");

            AggregateIterable<Document> aggregateIterable = collection.aggregate(Lists.newArrayList(
                Aggregates.match(Filters.eq("user_id", userId)),
                Aggregates.project(Projections.include("coop"))
            ));

            Document first = aggregateIterable.first();
            if (first != null && first.containsKey("coop")) {
                return new CoopData((Document) first.get("coop"));
            }

            return CoopData.empty();
        }

        public void invite(int userId, int targetId) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_users");

            List<UpdateOneModel<Document>> bulkWrite = Lists.newArrayList();

            bulkWrite.add(new UpdateOneModel<>(Filters.eq("user_id", userId), Updates.addToSet("coop.sent_invitations", targetId)));
            bulkWrite.add(new UpdateOneModel<>(Filters.eq("user_id", targetId), Updates.addToSet("coop.received_invitations", userId)));

            collection.bulkWrite(bulkWrite);
        }

        public void remove(int userId, int targetId) {
            MongoDatabase database = getDatabaseProvider().provide().getConnection();
            MongoCollection<Document> collection = database.getCollection("skyblock_users");

            List<UpdateOneModel<Document>> bulkWrite = Lists.newArrayList();

            bulkWrite.add(new UpdateOneModel<>(Filters.eq("user_id", userId), Updates.pull("coop.sent_invitations", targetId)));
            bulkWrite.add(new UpdateOneModel<>(Filters.eq("user_id", targetId), Updates.pull("coop.received_invitations", userId)));

            collection.bulkWrite(bulkWrite);
        }
    }

    /*

     */
}
