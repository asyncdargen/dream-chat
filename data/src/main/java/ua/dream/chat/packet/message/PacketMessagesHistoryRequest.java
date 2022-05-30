package ua.dream.chat.packet.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet;

@Packet.Id(9)
@Getter
@RequiredArgsConstructor
public class PacketMessagesHistoryRequest implements DataPacket {

    private final long userId;

}
