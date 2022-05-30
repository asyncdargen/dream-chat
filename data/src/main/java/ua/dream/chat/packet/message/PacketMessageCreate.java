package ua.dream.chat.packet.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(8)
@Getter
@RequiredArgsConstructor
public class PacketMessageCreate implements DataPacket {

    private final long userId;
    private final long timestamp;
    private final String message;

}
