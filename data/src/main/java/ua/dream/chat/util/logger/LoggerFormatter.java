package ua.dream.chat.util.logger;

import lombok.val;
import ua.dream.chat.util.References;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(String.format(
                "[%s %s] %s\n",
                References.TIME_FORMAT.format(record.getMillis()),
                record.getLevel().getLocalizedName(),
                formatMessage(record)
        ));

        if (record.getThrown() != null) {
            val writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            builder.append(writer);
        }

        return builder.toString();
    }

}
