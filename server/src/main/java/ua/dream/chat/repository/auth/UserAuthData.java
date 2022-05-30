package ua.dream.chat.repository.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserAuthData {

    private final long id;
    private final String email;
    private String passwordHash;

}
