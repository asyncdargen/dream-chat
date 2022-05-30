package ua.dream.chat.packet.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;
import ua.dream.chat.model.User;

@Id(4)
@Getter
@RequiredArgsConstructor
public class PacketUserResponse implements DataPacket {

    private final User user;

}
