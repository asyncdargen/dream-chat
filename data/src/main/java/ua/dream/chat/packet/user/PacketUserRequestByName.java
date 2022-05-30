package ua.dream.chat.packet.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(2)
@Getter
@RequiredArgsConstructor
public class PacketUserRequestByName implements DataPacket {

    private final String name;

}
