package de.finanzen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection 
{
    private static final Dotenv dotenv       = Dotenv.load();

    private static final String DB_URL       = dotenv.get("DB_URL");
    private static final String DB_USER      = dotenv.get("DB_USER");    
    private static final String DB_PASSWORD  = dotenv.get("DB_PASSWORD");
    private static final Logger LOGGER       = Logger.getLogger(DatabaseConnection.class.getName());  

    public static Connection getConnection() throws SQLException
    {
        LOGGER.log(Level.FINE,"databaseConnection: connection established successfully");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
}
