package net.hyze.skyblock.framework.api.user.profile;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.types.ObjectId;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileData {

    @Getter
    private List<Profile> profiles = Lists.newLinkedList();

    @Setter
    @Getter
    private Profile selectedProfile;

    /*

     */

    public ProfileData(Document profilesDocument, List<Document> joinedProfilesDocument) {
        ObjectId selectedProfile = profilesDocument.getObjectId("selected");

        for (Document value : joinedProfilesDocument) {
            Profile profile = new Profile(value);
            this.profiles.add(profile);

            if (profile.getId().equals(selectedProfile)) {
                this.selectedProfile = profile;
            }
        }

        if (this.selectedProfile == null) {
            this.selectedProfile = this.profiles.get(0);
        }
    }

    public static ProfileData empty(String islandId) {
        List<Profile> profiles = Lists.newArrayList();
        Profile profile = Profile.empty(islandId);
        profiles.add(profile);

        ProfileData profileData = new ProfileData(profiles, profile);
        return profileData;
    }

}
