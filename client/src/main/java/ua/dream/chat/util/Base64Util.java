package ua.dream.chat.util;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

@UtilityClass
public class Base64Util {

    public Encoder ENCODER = Base64.getEncoder();
    public Decoder DECODER = Base64.getDecoder();

    public String encodeString(String string) {
        val raw = string.getBytes(StandardCharsets.UTF_8);

        val encoded = ENCODER.encodeToString(raw);

        return encoded;
    }

    public String decodeString(String encoded) {
        val raw = DECODER.decode(encoded);

        val decoded = new String(raw, StandardCharsets.UTF_8);

        return decoded;
    }

}
