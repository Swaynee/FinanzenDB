package de.finanzen;

import de.finanzen.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class Main 
{
    public static void main(String[] args) 
    {
        try (Connection connection = DatabaseConnection.getConnection()) 
        {
            System.out.println("Database connection established successfully.");
        } 
        catch (SQLException e) 
        {
            System.err.println("Failed to establish database connection: " + e.getMessage());
        }
    }
}
