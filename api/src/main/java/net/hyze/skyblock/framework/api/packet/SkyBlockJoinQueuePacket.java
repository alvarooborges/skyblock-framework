package net.hyze.skyblock.framework.api.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.echo.api.*;

@NoArgsConstructor
@DebugPacket
@ServerPacket
@ExternalPacket(channel = "skyblock")
public class SkyBlockJoinQueuePacket extends EchoPacket implements Respondable<SkyBlockJoinQueuePacket.RequestCallback> {

    @Getter
    private int userId;

    @Getter
    private String islandId;

    @Setter
    @Getter
    private RequestCallback response;

    /*

     */

    public SkyBlockJoinQueuePacket(int userId, String islandId) {
        this.userId = userId;
        this.islandId = islandId;
    }

    /*

     */

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.userId);
        buffer.writeString(this.islandId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.userId = buffer.readInt();
        this.islandId = buffer.readString();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestCallback extends Response {

        @Getter
        private boolean success;

        @Getter
        private int queuePosition;

        @Override
        public void write(EchoBufferOutput buffer) {
            buffer.writeBoolean(success);
            buffer.writeInt(queuePosition);
        }

        @Override
        public void read(EchoBufferInput buffer) {
            this.success = buffer.readBoolean();
            this.queuePosition = buffer.readInt();
        }
    }
}
