package ua.dream.chat.util.logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

@Getter
@RequiredArgsConstructor
public class LoggerOutputStream extends ByteArrayOutputStream {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final Logger logger;
    private final Level level;

    @Override
    public void flush() throws IOException {
        String contents = toString(StandardCharsets.UTF_8.name());
        reset();
        if (contents.length() != 0 && !contents.equals(LINE_SEPARATOR))
            logger.logp(level, "", "", contents);
    }

}
