package net.hyze.skyblock.framework.api.user.profile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

@EqualsAndHashCode
@RequiredArgsConstructor
public class ProfileUser {

    @Getter
    private final int userId;

    @Getter
    private final ObjectId profileId;

    public ProfileUser(Document document) {
        this.userId = document.getInteger("user_id");
        this.profileId = document.getObjectId("profile_id");
    }

    public void serialize(Document document) {
        document.put("user_id", this.userId);
        document.put("profile_id", this.profileId);
    }

}
