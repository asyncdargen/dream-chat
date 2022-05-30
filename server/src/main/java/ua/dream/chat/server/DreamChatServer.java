package ua.dream.chat.server;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import ru.dargen.fancy.Fancy;
import ru.dargen.fancy.handler.Handlers;
import ru.dargen.fancy.packet.registry.HandlerPacketRegistry;
import ru.dargen.fancy.server.FancyRemote;
import ru.dargen.fancy.server.FancyServer;
import ua.dream.chat.App;
import ua.dream.chat.model.User;
import ua.dream.chat.packet.user.PacketUserResponse;
import ua.dream.chat.util.References;
import ua.dream.chat.util.logger.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class DreamChatServer {

    private final HandlerPacketRegistry registry = Fancy.createHandlerPacketRegistry();
    private final Handlers handlers = Fancy.createDefaultHandlers();
    private final FancyServer client = Fancy.createServer(registry, handlers, References.GSON);

    private final Map<FancyRemote, Long> remoteUsers = new ConcurrentHashMap<>();

    @SneakyThrows
    public DreamChatServer() {
        handlers.onDisconnect(remote -> {
            val userId = remoteUsers.remove(remote);
            if (userId == null)
                Logger.LOGGER.warning("Unknown client disconnected.");
            else {
                val user = App.getUserRepository().getUser(userId);
                Logger.LOGGER.info("User " + user.getName() + " (" + userId + ") disconnected.");
            }
        });

        registry.registerFromCurrentJar();

        client.bind(References.SERVER_PORT).await(15, TimeUnit.SECONDS);
    }

    public long getUserId(FancyRemote remote) {
        return remoteUsers.getOrDefault(remote, -1L);
    }

    public User getUser(FancyRemote remote) {
        val id = getUserId(remote);

        return id == -1 ? null : App.getUserRepository().getUser(id);
    }

    public FancyRemote getUserRemote(User user) {
        return getUserIdRemote(user.getId());
    }

    public FancyRemote getUserIdRemote(long id) {
        return remoteUsers.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == id).findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public void broadcastUserUpdate(User user) {
        val packet = new PacketUserResponse(user);
        remoteUsers.keySet()
                .stream()
                .map(this::getUser)
                .filter(target -> target.getId() == user.getId()
                        || user.getChatsIds().contains(target.getId())
                        || target.getChatsIds().contains(user.getId())
                ).map(this::getUserRemote)
                .forEach(target -> target.write(packet));
    }

}
