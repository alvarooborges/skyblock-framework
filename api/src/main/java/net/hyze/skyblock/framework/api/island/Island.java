package net.hyze.skyblock.framework.api.island;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.skyblock.framework.api.user.profile.ProfileUser;

@ToString
public class Island {

  @Getter
  private final String id;

  @Getter
  private boolean coop;

  @Getter
  @Setter
  private long timeMillis;

  @Getter
  private Collection<ProfileUser> members = Sets.newHashSet();

  @Setter
  @Getter
  private SerializedLocation spawnLocation = new SerializedLocation("world", -1.5, 66.5, 0.5, 90, 20);

  /*

   */

  public Island(String id, boolean coop, ProfileUser... members) {
    this.id = id;
    this.coop = coop;

    Collections.addAll(this.members, members);
  }
}
