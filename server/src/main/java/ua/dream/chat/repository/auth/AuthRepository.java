package ua.dream.chat.repository.auth;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.val;
import lombok.var;
import ua.dream.chat.App;
import ua.dream.chat.packet.auth.PacketAuthorize;
import ua.dream.chat.packet.auth.PacketAuthorizeResponse;
import ua.dream.chat.packet.auth.PacketAuthorizeUpdatePassword;
import ua.dream.chat.util.HashUtil;
import ua.dream.chat.util.RandomUtil;
import ua.dream.chat.util.References;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AuthRepository {

    private final Cache<String, UserAuthData> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(3)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    {
        App.getServer().getRegistry().registerHandler(PacketAuthorize.class, (packet, remote, id) -> {
            var data = getAuthData(packet.getEmail());
            var result = PacketAuthorizeResponse.Result.SUCCESSFUL;

            if (data == null) {
                val user = App.getUserRepository().createUser(packet.getEmail());
                data = createData(user.getId(), packet.getEmail(), HashUtil.hash(packet.getPassword(), RandomUtil.generateHex()));
            } else if (!HashUtil.isEqual(packet.getPassword(), data.getPasswordHash()))
                result = PacketAuthorizeResponse.Result.ERROR_INVALID_PASSWORD;

            if (result == PacketAuthorizeResponse.Result.SUCCESSFUL)
                App.getServer().getRemoteUsers().put(remote, data.getId());

            remote.write(new PacketAuthorizeResponse(data.getId(), result), id);
        });
        App.getServer().getRegistry().registerHandler(PacketAuthorizeUpdatePassword.class, (packet, remote, __) -> {
            val user = App.getServer().getUser(remote);
            val authData = getAuthData(user == null ? -1 : user.getId());
            val oldPassword = packet.getOldPassword();
            val newPassword = packet.getNewPassword();
            if (user != null
                    && authData != null
                    && newPassword.length() >= References.PASSWORD_MIN_LENGTH
                    && newPassword.length() <= References.PASSWORD_MAX_LENGTH
                    && !oldPassword.equals(newPassword)
                    && HashUtil.isEqual(oldPassword, authData.getPasswordHash())
            ) {
                authData.setPasswordHash(HashUtil.hash(newPassword, RandomUtil.generateHex()));
                App.getDatabase().updateAsync(
                        "UPDATE `user_auth` SET `password`=? WHERE `id`=?",
                        authData.getPasswordHash(), authData.getId()
                );
            }
        });
        App.getDatabase().updateAsync(
                "CREATE TABLE IF NOT EXISTS `user_auth` (`id` BIGINT, `password` TEXT, `email` VARCHAR(128));"
        );
    }

    public UserAuthData getCachedAuthData(String email) {
        return cache.getIfPresent(email.toLowerCase());
    }

    public UserAuthData getCachedAuthData(long id) {
        return cache.asMap()
                .values()
                .stream()
                .filter(data -> data.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public CompletableFuture<UserAuthData> getAuthDataLazy(String email) {
        val cached = getCachedAuthData(email);

        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : getAuthDataLazy("email", email);

        future.thenAccept(data -> {
            if (data != null) cache.put(data.getEmail().toLowerCase(), data);
        });

        return future;
    }

    public CompletableFuture<UserAuthData> getAuthDataLazy(long id) {
        val cached = getCachedAuthData(id);

        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : getAuthDataLazy("id", id);

        future.thenAccept(data -> {
            if (data != null) cache.put(data.getEmail().toLowerCase(), data);
        });

        return future;
    }

    private CompletableFuture<UserAuthData> getAuthDataLazy(String column, Object param) {
        return App.getDatabase()
                .queryAsync("SELECT * FROM `user_auth` WHERE `" + column + "`=?;", param)
                .thenApply(resultSet -> {
                    try {
                        if (resultSet.next()) {
                            val id = resultSet.getLong("id");
                            val email = resultSet.getString("email");
                            val passwordHash = resultSet.getString("password");
                            return new UserAuthData(id, email, passwordHash);
                        } else return null;
                    } catch (Throwable t) {
                        t.printStackTrace();
                        return null;
                    }
                });
    }


    public UserAuthData getAuthData(String email) {
        try {
            return getAuthDataLazy(email).get(7, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public UserAuthData getAuthData(long id) {
        try {
            return getAuthDataLazy(id).get(7, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public UserAuthData createData(long id, String email, String passwordHash) {
        email = email.toLowerCase();

        val data = new UserAuthData(id, email, passwordHash);

        cache.put(email, data);

        App.getDatabase().updateAsync("INSERT INTO `user_auth` (`id`, `email`, `password`) VALUES (?, ?, ?);", id, email, passwordHash);

        return data;
    }

}
