package net.hyze.skyblock.framework.api.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;

@AllArgsConstructor
@NoArgsConstructor
public class SkyBlockIslandRequestPacket extends EchoPacket {

    @Getter
    private int userId;

    @Getter
    private String islandId;

    @Getter
    private String serverId;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.userId);
        buffer.writeString(this.islandId);
        buffer.writeString(this.serverId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.userId = buffer.readInt();
        this.islandId = buffer.readString();
        this.serverId = buffer.readString();
    }
}
