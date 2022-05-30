package ua.dream.chat.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.model.Message;
import ua.dream.chat.packet.message.PacketMessage;
import ua.dream.chat.packet.message.PacketMessageCreate;
import ua.dream.chat.packet.message.PacketMessagesHistoryRequest;
import ua.dream.chat.packet.message.PacketMessagesHistoryResponse;
import ua.dream.chat.util.EstimatedSizeList;
import ua.dream.chat.util.RandomUtil;

import java.util.Map;
import java.util.concurrent.*;

public class MessageRepository {

    private final Cache<Long, Map<Long, EstimatedSizeList<Message>>> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(3)
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();

    private final BlockingQueue<Message> databaseLogQueue = new LinkedBlockingQueue<>();
    private final Thread logDispatcher = new Thread(this::run, "Message database log thread");


    {
        logDispatcher.start();

        App.getDatabase().updateAsync("CREATE TABLE IF NOT EXISTS `user_messages` (`id` BIGINT, `fromUserId` BIGINT, `toUserId` BIGINT, `timestamp` BIGINT, `message` LONGTEXT);");

        App.getServer().getRegistry().registerHandler(PacketMessageCreate.class, (packet, remote, __) -> {
            val user = App.getServer().getUser(remote);

            if (user != null && packet.getMessage().length() != 0 && packet.getUserId() != user.getId()) {
                createMessage(user.getId(), packet.getUserId(), packet.getTimestamp(), packet.getMessage());
            }
        });

        App.getServer().getRegistry().registerHandler(PacketMessagesHistoryRequest.class, (packet, remote, id) -> {
            val user = App.getServer().getUser(remote);

            if (user != null) {
                remote.write(new PacketMessagesHistoryResponse(packet.getUserId(), getMessages(user.getId(), packet.getUserId())), id);
            }
        });

    }

    public EstimatedSizeList<Message> getCachedMessages(long toUserId, long fromUserId) {
        val messages = cache.getIfPresent(toUserId);

        return messages == null ? null : messages.get(fromUserId);
    }

    public CompletableFuture<EstimatedSizeList<Message>> getMessagesLazy(long toUserId, long fromUserId) {
        val cached = getCachedMessages(toUserId, fromUserId);

        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : App.getDatabase().queryAsync(
                            "SELECT * FROM `user_messages` WHERE (`fromUserId`=? AND `toUserId`=?) OR (`toUserId`=? AND `fromUserId`=?) ORDER BY `timestamp` ASC LIMIT 100",
                            toUserId, fromUserId, toUserId, fromUserId
                    ).thenApply(resultSet -> {
                        val messages = new EstimatedSizeList<Message>();
                        try {
                            while (resultSet.next())
                                messages.add(new Message(
                                        resultSet.getLong("id"),
                                        resultSet.getLong("fromUserId"),
                                        resultSet.getLong("toUserId"),
                                        resultSet.getLong("timestamp"),
                                        resultSet.getString("message")
                                ));
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }

                        return messages;
                    });

        future.thenAccept(messages ->
                cache.asMap().computeIfAbsent(toUserId, __ -> new ConcurrentHashMap<>()).put(fromUserId, messages)
        );

        return future;
    }

    public EstimatedSizeList<Message> getMessages(long toUserId, long fromUserId) {
        try {
            return getMessagesLazy(toUserId, fromUserId).get(30, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public Message createMessage(long fromUserId, long toUserId, long timestamp, String text) {
        val message = new Message(
                RandomUtil.randomLong(9_999_999_999L) + 10_000_000_000L,
                fromUserId, toUserId, timestamp, text
        );

        databaseLogQueue.add(message);

        getMessages(fromUserId, toUserId).add(message);
        getMessages(toUserId, fromUserId).add(message);

        val user = App.getUserRepository().getUser(fromUserId);
        val target = App.getUserRepository().getUser(toUserId);

        if (!user.getChatsIds().contains(toUserId)) {
            user.getChatsIds().add(toUserId);
            App.getServer().broadcastUserUpdate(user);
        }
        if (!target.getChatsIds().contains(fromUserId)) {
            target.getChatsIds().add(fromUserId);
            App.getServer().broadcastUserUpdate(target);
        }

        val userRemote = App.getServer().getUserIdRemote(fromUserId);
        val targetRemote = App.getServer().getUserIdRemote(toUserId);
        val packet = new PacketMessage(message);

        if (userRemote != null) userRemote.write(packet);
        if (targetRemote != null) targetRemote.write(packet);

        return message;
    }

    private void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                val message = databaseLogQueue.take();
                App.getDatabase().update(
                        "INSERT INTO `user_messages` (`id`, `fromUserId`, `toUserId`, `timestamp`, `message`) VALUES (?, ?, ?, ?, ?);",
                        message.getId(), message.getFromUserId(), message.getToUserId(), message.getTimestamp(), message.getMessage()
                );
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        databaseLogQueue.forEach(message -> App.getDatabase().update(
                "INSERT INTO `user_messages` (`id`, `fromUserId`, `toUserId`, `timestamp`, `message`) VALUES (?, ?, ?, ?, ?);",
                message.getId(), message.getFromUserId(), message.getToUserId(), message.getTimestamp(), message.getMessage()
        ));

    }

}
