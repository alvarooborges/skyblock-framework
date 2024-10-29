package net.hyze.skyblock.framework.api.user.coop;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.Document;

@NoArgsConstructor(staticName = "empty")
public class CoopData {

    @Getter
    private final List<Integer> sentInvitations = Lists.newArrayList();

    @Getter
    private final List<Integer> receivedInvitations = Lists.newArrayList();

    /*

     */

    public CoopData(Document document) {
        if(document.containsKey("sent_invitations")) {
            this.sentInvitations.addAll(document.getList("sent_invitations", Integer.class));
        }

        if(document.containsKey("received_invitations")) {
            this.receivedInvitations.addAll(document.getList("received_invitations", Integer.class));
        }
    }

}
