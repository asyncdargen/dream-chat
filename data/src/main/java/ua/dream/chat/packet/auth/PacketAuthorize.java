package ua.dream.chat.packet.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(0)
@Getter
@RequiredArgsConstructor
public class PacketAuthorize implements DataPacket {

    private final String email;
    private final String password;

}
