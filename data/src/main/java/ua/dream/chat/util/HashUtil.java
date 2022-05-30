package ua.dream.chat.util;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class HashUtil {

    public final MessageDigest ALGORITHM;

    static {
        try {
            ALGORITHM = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String hash0(String text) {
        ALGORITHM.reset();
        ALGORITHM.update(text.getBytes(StandardCharsets.UTF_8));

        val digest = ALGORITHM.digest();
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
    }

    public String hash(String text, String salt) {
        return "$SHA$" + salt + "$" + hash0(hash0(text) + salt);
    }

    public boolean isEqual(String text, String hash) {
        val lines = hash.split("\\$");
        return lines.length == 4
                && MessageDigest.isEqual(hash.getBytes(StandardCharsets.UTF_8), hash(text, lines[2]).getBytes(StandardCharsets.UTF_8));
    }

}
