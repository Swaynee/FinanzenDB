package de.finanzen.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseTransaction implements AutoCloseable
{
    private static final Logger LOGGER = Logger.getLogger(DatabaseTransaction.class.getName());  
    private final Connection    connection;
    private boolean             committed = false;

    public DatabaseTransaction() throws SQLException
    {
        Connection con = DatabaseConnection.getConnection();
        try
        {
            con.setAutoCommit(false);
            this.connection = con;

            LOGGER.log(Level.FINE,"databaseTransaction: connection established successfully");
        }
        catch (SQLException e)
        {
            con.close();
            throw e;
        }
    }

    public Connection getConnection() { return this.connection; }

    public void commit() throws SQLException
    {
        this.connection.commit();
        this.committed = true;

        LOGGER.log(Level.FINE,"commit: transaction successful");
    }

    @Override
    public void close() throws SQLException
    {
        try (this.connection) 
        {
            if (!this.committed) 
            {
                this.connection.rollback();

                LOGGER.log(Level.FINE,"close: transaction failed");
            } 
        }
        finally
        {
            LOGGER.log(Level.FINE,"close: transaction connection closed");
        }
    }
}
