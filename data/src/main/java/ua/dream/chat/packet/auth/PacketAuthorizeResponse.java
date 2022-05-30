package ua.dream.chat.packet.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(1)
@Getter
@RequiredArgsConstructor
public class PacketAuthorizeResponse implements DataPacket {

    private final long id;
    private final Result result;

    @Getter
    @RequiredArgsConstructor
    public enum Result {

        SUCCESSFUL(""),
        ERROR_INVALID_PASSWORD("Invalid password."),
        ;

        private final String message;
        private final boolean error = name().startsWith("ERROR");

    }

}
