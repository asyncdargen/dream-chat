package ua.dream.chat.client;

import lombok.Getter;
import lombok.SneakyThrows;
import ru.dargen.fancy.Fancy;
import ru.dargen.fancy.client.FancyClient;
import ru.dargen.fancy.handler.Handlers;
import ru.dargen.fancy.packet.Packet;
import ru.dargen.fancy.packet.callback.Callback;
import ru.dargen.fancy.packet.registry.HandlerPacketRegistry;
import ua.dream.chat.packet.theme.PacketAllThemes;
import ua.dream.chat.util.LocalSettings;
import ua.dream.chat.util.References;
import ua.dream.chat.util.logger.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class DreamChatClient {

    private final AtomicBoolean restore = new AtomicBoolean(false);

    private final HandlerPacketRegistry registry = Fancy.createHandlerPacketRegistry();
    private final Handlers handlers = Fancy.createDefaultHandlers();
    private final FancyClient client = Fancy.createClient(registry, handlers, References.GSON);

    @SneakyThrows
    public DreamChatClient() {
        handlers.onDisconnect(remote -> {
            if (!restore.get()) {
                Logger.LOGGER.warning("Disconnected from server... Shutting down client...");
                System.exit(0);
            }
            restore.set(!restore.get());
        });

        registry.registerFromCurrentJar();

        registry.registerHandler(PacketAllThemes.class, (packet, __, ___) -> {
            LocalSettings.THEMES.putAll(packet.getThemes());
//            LocalSettings.updateStyles();
        });

        client.connect(References.SERVER_HOST, References.SERVER_PORT).await(15, TimeUnit.SECONDS);
    }

    public <P extends Packet> Callback<P> write(Packet packet) {
        return client.write(packet);
    }

    public <P extends Packet> Callback<P> write(Packet packet, String id) {
        return client.write(packet, id);
    }

    public void reconnect() {
        restore.set(true);
        client.close();
        client.reconnect();
    }

}
