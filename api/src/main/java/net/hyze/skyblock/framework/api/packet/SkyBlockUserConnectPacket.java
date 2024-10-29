package net.hyze.skyblock.framework.api.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.echo.api.DebugPacket;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.ExternalPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.Respondable;
import net.hyze.core.shared.echo.api.Response;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.skyblock.framework.api.packet.SkyBlockUserConnectPacket.RequestCallback;

@NoArgsConstructor
@DebugPacket
@ExternalPacket(channel = "skyblock")
public class SkyBlockUserConnectPacket extends EchoPacket implements Respondable<RequestCallback> {


    @Getter
    private User user;

    @Getter
    private Server server;

    @Setter
    @Getter
    private RequestCallback response;

    public SkyBlockUserConnectPacket(User user, Server server) {
        this.user = user;
        this.server = server;
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, user);
        PacketSerializeUtil.writeServer(buffer, server);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        user = PacketSerializeUtil.readUser(buffer);
        server = PacketSerializeUtil.readServer(buffer);
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
