package de.finanzen.repository;

import java.util.*;
import java.util.logging.*;
import java.sql.*;

import de.finanzen.model.*;
import de.finanzen.database.DatabaseConnection;

public class KontoRepository 
{
    private static final Logger LOGGER = Logger.getLogger(KontoRepository.class.getName());

    public Konto getById(int konto_id) throws SQLException
    {
        String sql = "SELECT ";
        sql =  sql +    "konto_id, ";
        sql =  sql +    "name, ";
        sql =  sql +    "konto_art, ";
        sql =  sql +    "aktiv, ";
        sql =  sql +    "iban, ";
        sql =  sql +    "bic, ";
        sql =  sql +    "bank ";
        sql =  sql + "FROM konto ";
        sql =  sql + "WHERE konto_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setInt(1, konto_id);
            LOGGER.fine("getById: Query Statement prepared.");
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToKonto(rs);
                }
            }
        }
        return null;
    }

    /*
     * Retrieves all active Konto records from the database.
     * Returns a list of Konto objects. Empty list if no active Konto records are found.
     */
    public List<Konto> getAlleAktiven() throws SQLException
    {
        String sql = "SELECT ";
        sql =  sql +    "konto_id, ";
        sql =  sql +    "name, ";
        sql =  sql +    "konto_art, ";
        sql =  sql +    "aktiv, ";
        sql =  sql +    "iban, ";
        sql =  sql +    "bic, ";
        sql =  sql +    "bank ";
        sql =  sql + "FROM konto ";
        sql =  sql + "WHERE aktiv = 1";

        List<Konto> konten = new ArrayList<>();

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("getAlleAktiven: Database connection established successfully.");
            LOGGER.fine("getAlleAktiven: Query Statement prepared,");
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) konten.add(mapResultSetToKonto(rs));
            }
        }
        LOGGER.fine("getAlleAktiven: ResultSet retrieved successfully.");
        return konten;
    }

    /*
     * Saves a Konto object to the database. If the Konto has an ID of 0, it will be inserted as a new record.
     * If the Konto has a non-zero ID, it will be updated in the database.
     * Returns true if the operation was successful, false otherwise.
     */
    public boolean save(Konto konto) throws SQLException
    {
        if (konto.getId() == 0) return insert(konto);
        return update(konto);
    }

    /*
     * Inserts a new Konto into the database. 
     * And sets the generated konto_id in the provided Konto object.
     * Returns true if the insertion was successful, false otherwise.
     */
    private boolean insert(Konto konto) throws SQLException
    {
        String sql = "INSERT INTO konto ";
        sql =  sql +    "( ";
        sql =  sql +        "name, ";
        sql =  sql +        "konto_art, ";
        sql =  sql +        "aktiv, ";
        sql =  sql +        "iban, ";
        sql =  sql +        "bic, ";
        sql =  sql +        "bank ";
        sql =  sql +    ") ";
        sql =  sql + "VALUES (?, ?, ?, ?, ?, ?)";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            )
        {
            LOGGER.fine("insert: Database connection established successfully.");
            stmt.setString(1, konto.getName());
            stmt.setString(2, konto.getKontoArt());
            stmt.setBoolean(3,konto.istAktiv());
            stmt.setString(4, konto.getIban());
            stmt.setString(5, konto.getBic());
            stmt.setString(6, konto.getBank());
            LOGGER.fine("insert: Query Statement prepared,");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also kein Konto gespeichert
            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.fine("insert: Insert executed successfully.");
                    konto.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /* 
     * Updates an existing Konto in the database based on its konto_id.
     * Returns true if the update was successful, false otherwise.
     */
    private boolean update(Konto konto) throws SQLException
    {
        String sql = "UPDATE konto ";
        sql =  sql +    "SET ";
        sql =  sql +        "name       = ?, ";
        sql =  sql +        "konto_art  = ?, ";
        sql =  sql +        "aktiv      = ?, ";
        sql =  sql +        "iban       = ?, ";
        sql =  sql +        "bic        = ?, ";
        sql =  sql +        "bank       = ? ";
        sql =  sql + "WHERE konto_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("update: Database connection established successfully.");
            stmt.setString(1, konto.getName());
            stmt.setString(2, konto.getKontoArt());
            stmt.setBoolean(3,konto.istAktiv());
            stmt.setString(4, konto.getIban());
            stmt.setString(5, konto.getBic());
            stmt.setString(6, konto.getBank());
            stmt.setInt(7, konto.getId());
            LOGGER.fine("update: Query Statement prepared,");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also keine Änderungen gespeichert

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    /*
     * Deactivates a Konto in the database by setting its aktiv field to false.
     * Returns true if the operation was successful, false otherwise.
     */
    public boolean delete(Konto konto) throws SQLException
    {
        konto.setAktiv(false);
        return update(konto);           
    }

    /*
     * Deletes a Konto from the database based on its konto_id.
     * IMPORTANT: Don't use this method to deactivate a Konto. Use the update method to set the aktiv field to false instead.
     * Returns true if the deletion was successful, false otherwise.
     */
    /* private boolean delete(Konto konto) throws SQLException
    {
        String sql = "DELETE FROM konto ";
        sql =  sql + "WHERE konto_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("delete: Database connection established successfully.");
            stmt.setInt(1, konto.getId());
            LOGGER.fine("delete: Query Statement prepared,");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also keine Änderungen gespeichert

            LOGGER.fine("delete: Delete executed successfully.");
            return true;
        }
    } */
    
    /*
     * Maps the current row of the provided ResultSet to a Konto object.
     * Assumes that the ResultSet is positioned at a valid row.
     */
    private Konto mapResultSetToKonto(ResultSet rs) throws SQLException
    {
        Konto konto = new Konto();
        konto.setId(rs.getInt("konto_id"));
        konto.setName(rs.getString("name"));
        konto.setKontoArt(rs.getString("konto_art"));
        konto.setAktiv(rs.getBoolean("aktiv"));
        konto.setIban(rs.getString("iban"));
        konto.setBic(rs.getString("bic"));
        konto.setBank(rs.getString("bank"));
        return konto;
    }
}
