package ua.dream.chat.packet.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;
import ua.dream.chat.model.EncodedImage;

@Id(7)
@Getter
@RequiredArgsConstructor
public class PacketUserUpdateAvatar implements DataPacket {

    private final EncodedImage avatar;

}
