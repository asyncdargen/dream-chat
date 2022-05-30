package ua.dream.chat.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Random;

@UtilityClass
public class RandomUtil {

    public final String ALPHABET = "qwertyuiopasdfghjklzxcvbnm";
    public final Random RANDOM = new SecureRandom();

    public String generateHex() {
        return generateAlphaBet(16);
    }

    public String generateAlphaBet(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) builder.append(randomBoolean()
                ? randomInt(10)
                : randomBoolean()
                ? String.valueOf(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length()))).toUpperCase()
                : ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())
        ));

        return builder.toString();
    }

    public boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public int randomInt(int max) {
        return RANDOM.nextInt(max);
    }

    public long randomLong(long max) {
        long bits, val;

        do {
            bits = (RANDOM.nextLong() << 1) >>> 1;
            val = bits % max;
        } while (bits - val + (max - 1) < 0L);

        return val;
    }


}
