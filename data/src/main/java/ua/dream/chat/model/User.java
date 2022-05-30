package ua.dream.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private final long id;
    private String name;
    private String email;
    private EncodedImage avatar;
    private final Set<Long> chatsIds;

}
