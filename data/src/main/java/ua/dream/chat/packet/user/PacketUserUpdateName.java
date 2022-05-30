package ua.dream.chat.packet.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;

@Id(5)
@Getter
@RequiredArgsConstructor
public class PacketUserUpdateName implements DataPacket {

    private final String newName;

}
