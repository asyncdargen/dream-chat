package ua.dream.chat.packet.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(6)
@Getter
@RequiredArgsConstructor
public class PacketAuthorizeUpdatePassword implements DataPacket {

    private final String oldPassword;
    private final String newPassword;

}
