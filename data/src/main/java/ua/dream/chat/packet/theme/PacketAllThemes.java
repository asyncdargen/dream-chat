package ua.dream.chat.packet.theme;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.fancy.packet.DataPacket;
import ru.dargen.fancy.packet.Packet.Id;
import ua.dream.chat.model.Theme;

import java.util.Map;

@Id(12)
@Getter
@RequiredArgsConstructor
public class PacketAllThemes implements DataPacket {

    private final Map<String, Theme> themes;

}
