package ua.dream.chat.packet.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(3)
@Getter
@RequiredArgsConstructor
public class PacketUserRequestById implements DataPacket {

    private final long id;

}
