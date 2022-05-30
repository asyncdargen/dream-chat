package ua.dream.chat.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Getter
@RequiredArgsConstructor
public class EncodedImage {

    private final String encoded;

    public InputStream openInputStream() {
        return new ByteArrayInputStream(Base64.getDecoder().decode(encoded.getBytes()));
    }

}
