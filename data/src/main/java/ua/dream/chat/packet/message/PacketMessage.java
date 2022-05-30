package ua.dream.chat.packet.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;
import ua.dream.chat.model.Message;

@Id(11)
@Getter
@RequiredArgsConstructor
public class PacketMessage implements DataPacket {

    private final Message message;

}
