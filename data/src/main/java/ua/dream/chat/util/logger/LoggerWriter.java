package ua.dream.chat.util.logger;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggerWriter extends Handler {

    private final PrintStream output = System.out;

    {
        setFormatter(new LoggerFormatter());
    }

    private void print(String log) {
        output.print(log);
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) print(getFormatter().format(record));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}
