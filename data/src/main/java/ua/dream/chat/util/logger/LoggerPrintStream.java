package ua.dream.chat.util.logger;

import java.io.PrintStream;
import java.util.logging.Level;

public class LoggerPrintStream extends PrintStream {

    public LoggerPrintStream(Logger logger, Level level) {
        super(new LoggerOutputStream(logger, level));
    }

}
