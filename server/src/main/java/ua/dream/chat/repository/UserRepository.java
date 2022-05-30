package ua.dream.chat.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import lombok.Getter;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.model.EncodedImage;
import ua.dream.chat.model.User;
import ua.dream.chat.packet.user.*;
import ua.dream.chat.util.RandomUtil;
import ua.dream.chat.util.References;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class UserRepository {

    private final TypeAdapter<Set<Long>> chatsIdsAdapter = References.GSON.getAdapter((Class<Set<Long>>) new TypeToken<Set<Long>>() {}.getRawType());

    private final Cache<Long, User> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(3)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    {
        App.getDatabase().updateAsync(
                "CREATE TABLE IF NOT EXISTS `user_data` (`id` BIGINT, `name` VARCHAR("
                        + References.NAME_MAX_LENGTH
                        + "), `avatar` LONGTEXT NULL DEFAULT NULL, `chats_ids` TEXT);"
        );

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> saveAll(true), 5, 5, TimeUnit.MINUTES);

        App.getServer().getRegistry().registerHandler(PacketUserUpdateName.class, (packet, remote, __) -> {
            val user = App.getServer().getUser(remote);
            val newName = packet.getNewName();
            if (user != null
                    && newName.length() >= References.NAME_MIN_LENGTH
                    && newName.length() <= References.NAME_MAX_LENGTH
                    && getUser(newName) == null
            ) {
                user.setName(newName);
                App.getServer().broadcastUserUpdate(user);
            }
        });

        App.getServer().getRegistry().registerHandler(PacketUserUpdateAvatar.class, (packet, remote, __) -> {
            val user = App.getServer().getUser(remote);
            val avatar = packet.getAvatar();

            if (user != null) {
                user.setAvatar(avatar);
                App.getServer().broadcastUserUpdate(user);
            }
        });

        App.getServer().getRegistry().registerHandler(PacketUserRequestById.class, (packet, remote, id) -> {
            val user = getUser(packet.getId());
            remote.write(new PacketUserResponse(user), id);
        });

        App.getServer().getRegistry().registerHandler(PacketUserRequestByName.class, (packet, remote, id) -> {
            val user = getUser(packet.getName());
            remote.write(new PacketUserResponse(user), id);
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

    public CompletableFuture<User> getUserLazy(long id) {
        val cached = getCachedUser(id);
        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : getUserLazy(id, "id");

        future.thenAccept(user -> {
            if (user != null) cache.put(user.getId(), user);
        });

        return future;
    }

    public CompletableFuture<User> getUserLazy(String name) {
        val cached = getCachedUser(name);
        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : getUserLazy(name, "name");

        future.thenAccept(user -> {
            if (user != null) cache.put(user.getId(), user);
        });

        return future;
    }

    private CompletableFuture<User> getUserLazy(Object param, String column) {
        return App.getDatabase()
                .queryAsync("SELECT * FROM `user_data` WHERE `" + column + "`=?;", param)
                .thenApply(resultSet -> {
                    try {
                        if (resultSet.next()) {
                            val id = resultSet.getLong("id");
                            val name = resultSet.getString("name");
                            val avatarRaw = resultSet.getString("avatar");
                            val chatsIds = chatsIdsAdapter.fromJson(resultSet.getString("chats_ids"));
                            val email = App.getAuthRepository().getAuthData(id).getEmail();
                            return new User(id, name, email, avatarRaw == null ? null : new EncodedImage(avatarRaw), chatsIds);
                        } else return null;
                    } catch (Throwable t) {
                        t.printStackTrace();
                        return null;
                    }
                });
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

    public User createUser(String email) {
        val id = References.ID_MIN_LENGTH + RandomUtil.randomLong(References.ID_MAX_LENGTH - References.ID_MIN_LENGTH + 1);
        val name = "user@" + id / 111_111_111;

        val user = new User(id, name, email,  null, new HashSet<>());

        cache.put(id, user);

        App.getDatabase().updateAsync("INSERT INTO `user_data` (`id`, `name`, `chats_ids`) VALUES (?, ?, '[]')", id, name);

        return user;
    }

    public void saveAll(boolean sync) {
        cache.asMap().values().forEach(user -> {
            val query = "UPDATE `user_data` SET `name`=?, `avatar`=?, `chats_ids`=? WHERE `id`=?;";

            if (sync) App.getDatabase().update(query, user.getName(), user.getAvatar().getEncoded(), chatsIdsAdapter.toJson(user.getChatsIds()), user.getId());
            else App.getDatabase().updateAsync(query, user.getName(), user.getAvatar().getEncoded(), chatsIdsAdapter.toJson(user.getChatsIds()), user.getId());
        });
    }

}
