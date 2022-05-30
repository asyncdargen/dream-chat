package ua.dream.chat.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.model.User;
import ua.dream.chat.packet.user.PacketUserRequestById;
import ua.dream.chat.packet.user.PacketUserRequestByName;
import ua.dream.chat.packet.user.PacketUserResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class UserRepository {

    @Setter
    private long selfId;
    private final Cache<Long, User> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(3)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    {
        App.getClient().getRegistry().registerHandler(PacketUserResponse.class, (packet, __, ___) -> {
            val user = packet.getUser();
            if (user != null) {
                cache.put(user.getId(), user);
                Platform.runLater(() -> {
                    App.MAIN_WINDOW.getController().refreshContacts();
                    App.SETTINGS_WINDOW.getController().handleOpen();
                });
            }
        });
    }

    public User getCachedUser(long id) {
        return cache.getIfPresent(id);
    }

    public User getCachedUser(String name) {
        return cache.asMap()
                .values()
                .stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public User getCachedSelfUser() {
        return getCachedUser(selfId);
    }

    public CompletableFuture<User> getUserLazy(long id) {
        val cached = getCachedUser(id);
        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : App.getClient()
                .<PacketUserResponse>write(new PacketUserRequestById(id))
                .await()
                .thenApply(PacketUserResponse::getUser);

        future.thenAccept(user -> {
            if (user != null) cache.put(user.getId(), user);
        });

        return future;
    }


    public CompletableFuture<User> getUserLazy(String name) {
        val cached = getCachedUser(name);
        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : App.getClient()
                .<PacketUserResponse>write(new PacketUserRequestByName(name))
                .await()
                .thenApply(PacketUserResponse::getUser);

        future.thenAccept(user -> {
            if (user != null) cache.put(user.getId(), user);
        });

        return future;
    }

    public CompletableFuture<User> getSelfUserLazy() {
        return getUserLazy(selfId);
    }

    public User getUser(long id) {
        try {
            return getUserLazy(id).get(7, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public User getUser(String name) {
        try {
            return getUserLazy(name).get(7, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public User getSelfUser() {
        return getUser(selfId);
    }

}
