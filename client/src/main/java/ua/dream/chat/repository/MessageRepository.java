package ua.dream.chat.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javafx.application.Platform;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.model.Message;
import ua.dream.chat.packet.message.PacketMessage;
import ua.dream.chat.packet.message.PacketMessagesHistoryRequest;
import ua.dream.chat.packet.message.PacketMessagesHistoryResponse;
import ua.dream.chat.util.EstimatedSizeList;
import ua.dream.chat.util.TrayUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MessageRepository {

    private final Cache<Long, EstimatedSizeList<Message>> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(3)
            .expireAfterAccess(20, TimeUnit.MINUTES)
            .build();

    {
        App.getClient().getRegistry().registerHandler(PacketMessage.class, (packet, __, ___) -> {
            val message = packet.getMessage();
            if (!App.getMainStage().isFocused()
                    && !App.getModalStage().isFocused()
                    && App.getUserRepository().getSelfId() != message.getFromUserId()
            ) TrayUtil.notify(App.getUserRepository().getUser(message.getFromUserId()).getName(), message.getMessage());

            getMessages(message.getFromUserId() == App.getUserRepository().getSelfId() ? message.getToUserId() : message.getFromUserId()).add(message);

            Platform.runLater(() -> {
                App.MAIN_WINDOW.getController().refreshContacts();
            });
        });

        App.getClient().getRegistry().registerHandler(PacketMessagesHistoryResponse.class, (packet, __, ___) -> {
            val messages = new EstimatedSizeList<Message>();

            if (packet.getHistory() != null) messages.addAll(packet.getHistory());

            cache.put(packet.getUserId(), messages);
        });

    }

    public EstimatedSizeList<Message> getCachedMessages(long fromUserId) {
        val messages = cache.getIfPresent(fromUserId);

        return messages;
    }

    public CompletableFuture<EstimatedSizeList<Message>> getMessagesLazy(long fromUserId) {
        val cached = getCachedMessages(fromUserId);

        val future = cached != null
                ? CompletableFuture.completedFuture(cached)
                : App.getClient().<PacketMessagesHistoryResponse>write(new PacketMessagesHistoryRequest(fromUserId))
                .await()
                .thenApply(packet -> {
                    val messages = new EstimatedSizeList<Message>();

                    if (packet.getHistory() != null) messages.addAll(packet.getHistory());

                    return messages;
                });

        future.thenAccept(messages ->
                cache.put(fromUserId, messages)
        );

        return future;
    }

    public EstimatedSizeList<Message> getMessages(long fromUserId) {
        try {
            return getMessagesLazy(fromUserId).get(30, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public Message getLastMessage(long fromUserId) {
        return getMessages(fromUserId)
                .stream()
                .sorted((one, two) -> Long.compare(two.getTimestamp(), one.getTimestamp()))
                .findFirst()
                .orElse(null);
    }

}