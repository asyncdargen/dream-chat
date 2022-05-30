package ua.dream.chat.packet.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;
import ua.dream.chat.model.Message;

import java.util.LinkedList;

@Id(10)
@Getter
@RequiredArgsConstructor
public class PacketMessagesHistoryResponse implements DataPacket {

    private final long userId;
    private final LinkedList<Message> history;

}
