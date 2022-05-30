package ua.dream.chat.client;

import lombok.Getter;
import lombok.SneakyThrows;
import ru.dargen.fancy.Fancy;
import ru.dargen.fancy.client.FancyClient;
import ru.dargen.fancy.handler.Handlers;
import ru.dargen.fancy.packet.Packet;
import ru.dargen.fancy.packet.callback.Callback;
import ru.dargen.fancy.packet.registry.HandlerPacketRegistry;
import ua.dream.chat.util.References;
import ua.dream.chat.util.logger.Logger;

import java.util.concurrent.TimeUnit;

@Getter
public class DreamChatClient {

    private final HandlerPacketRegistry registry = Fancy.createHandlerPacketRegistry();
    private final Handlers handlers = Fancy.createDefaultHandlers();
    private final FancyClient client = Fancy.createClient(registry, handlers, References.GSON);

    @SneakyThrows
    public DreamChatClient() {
        handlers.onDisconnect(remote -> {
            Logger.LOGGER.warning("Disconnected from server... Shutting down client...");
            System.exit(0);
        });

        registry.registerFromCurrentJar();

        client.connect(References.SERVER_HOST, References.SERVER_PORT).await(15, TimeUnit.SECONDS);
    }

    public <P extends Packet> Callback<P> write(Packet packet) {
        return client.write(packet);
    }

    public <P extends Packet> Callback<P> write(Packet packet, String id) {
        return client.write(packet, id);
    }

}
