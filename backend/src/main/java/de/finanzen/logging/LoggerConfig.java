package de.finanzen.logging;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig 
{
    public static void setup() throws IOException
    {
        Logger rootLogger = Logger.getLogger("");

        // Vorhandene Handler entfernen
        for (Handler handler : rootLogger.getHandlers()) rootLogger.removeHandler(handler);

        // Globales Log-Level
        rootLogger.setLevel(Level.FINE);

        // -------------------------------------------------
        // Konsole
        // -------------------------------------------------

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new LogFormatter());

        rootLogger.addHandler(consoleHandler);

        // -------------------------------------------------
        // Logdatei
        // -------------------------------------------------

        FileHandler fileHandler = new FileHandler("finanzen.log", true);
        fileHandler.setLevel(Level.FINE);
        fileHandler.setFormatter(new LogFormatter());

        rootLogger.addHandler(fileHandler);
    }
}
