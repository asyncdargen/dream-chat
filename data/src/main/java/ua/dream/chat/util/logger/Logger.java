package ua.dream.chat.util.logger;

import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@Getter
public class Logger extends java.util.logging.Logger {

    public static Logger LOGGER = new Logger();

    private final Thread threadDispatcher = new Thread(this::run, "Log dispatcher thread");
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public Logger() {
        super("DreamChat", null);

        setLevel(Level.ALL);

        addHandler(new LoggerWriter());

        System.setErr(new LoggerPrintStream(this, Level.SEVERE));
        System.setOut(new LoggerPrintStream(this, Level.INFO));

        threadDispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        if (!Thread.currentThread().isInterrupted()) queue.add(record);
    }

    private void doLog(LogRecord record) {
        super.log(record);
    }

    private void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                doLog(queue.take());
            } catch (Throwable __) {}
        }

        queue.forEach(this::doLog);
    }

}
