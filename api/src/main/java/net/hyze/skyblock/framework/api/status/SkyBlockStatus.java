package net.hyze.skyblock.framework.api.status;

import java.net.InetSocketAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.apps.ServerStatus;
import net.hyze.core.shared.servers.Server;

@NoArgsConstructor
public class SkyBlockStatus extends ServerStatus {

  @Getter
  @Setter
  private String slaveId;

  public SkyBlockStatus(String slaveId, String appId, AppType type, Server server, InetSocketAddress address, Long onlineSince, long usageMemory, long freeMemory, int online, boolean maintenance) {
    super(appId, type, server, address, onlineSince, usageMemory, freeMemory, online, maintenance);

    this.slaveId = slaveId;
  }

}
