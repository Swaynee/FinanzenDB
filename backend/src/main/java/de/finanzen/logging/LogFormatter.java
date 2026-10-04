package de.finanzen.logging;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.logging.*;

public class LogFormatter extends Formatter
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogRecord record)
    {
        String timestamp    = LocalDateTime.ofInstant(record.getInstant(),java.time.ZoneId.systemDefault()).format(DATE_FORMAT);

        String loggerName   = record.getLoggerName();
        String level        = record.getLevel().getName();
        String message      = formatMessage(record);

        StringBuilder output= new StringBuilder();

        output.append(String.format("%s [%s] %s - %s%n",timestamp,level,loggerName,message));

        if (record.getThrown() != null)
        {
            StringWriter    sw = new StringWriter();
            PrintWriter     pw = new PrintWriter(sw);

            record.getThrown().printStackTrace(pw);

            output.append(sw);
            output.append(System.lineSeparator());
        }

        return output.toString();
    }
}
