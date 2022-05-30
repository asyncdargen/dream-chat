package ua.dream.chat.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Message {

    private final long id;
    private final long fromUserId;
    private final long toUserId;
    private final long timestamp;
    private final String message;

}
