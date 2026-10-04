package de.finanzen.repository;

import java.util.*;
import java.sql.*;

import de.finanzen.model.*;
import de.finanzen.database.DatabaseConnection;

public class KontoRepository 
{
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
            System.out.println("KontoRepository: getById: Database connection established successfully.");
            stmt.setInt(1, konto_id);
            System.out.println("KontoRepository: getById: Query Statement prepared");
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    System.out.println("KontoRepository: getById: ResultSet retrieved successfully.");
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
            System.out.println("KontoRepository: getAlleAktiven: Database connection established successfully.");
            System.out.println("KontoRepository: getAlleAktiven: Query Statement prepared");
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) 
                {
                    Konto konto = new Konto();
                    konto.setId(rs.getInt("konto_id"));
                    konto.setName(rs.getString("name"));
                    konto.setKontoArt(rs.getString("konto_art"));
                    konto.setAktiv(rs.getBoolean("aktiv"));
                    konto.setIban(rs.getString("iban"));
                    konto.setBic(rs.getString("bic"));
                    konto.setBank(rs.getString("bank"));
                    konten.add(konto);
                }
            }
        }
        System.out.println("KontoRepository: getAlleAktiven: ResultSet retrieved successfully.");
        return konten;
    }

    public boolean save(Konto konto) throws SQLException
    {
        //If the konto_id is 0, it means it's a new Konto and should be inserted. Otherwise, it should be updated.
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
            System.out.println("KontoRepository: insert: Database connection established successfully.");
            stmt.setString(1, konto.getName());
            stmt.setString(2, konto.getKontoArt());
            stmt.setBoolean(3,konto.istAktiv());
            stmt.setString(4, konto.getIban());
            stmt.setString(5, konto.getBic());
            stmt.setString(6, konto.getBank());
            System.out.println("KontoRepository: insert: Query Statement prepared");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also kein Konto gespeichert
            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    System.out.println("KontoRepository: insert: executed successfully.");
                    konto.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }


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
            System.out.println("KontoRepository: update: Database connection established successfully.");
            stmt.setString(1, konto.getName());
            stmt.setString(2, konto.getKontoArt());
            stmt.setBoolean(3,konto.istAktiv());
            stmt.setString(4, konto.getIban());
            stmt.setString(5, konto.getBic());
            stmt.setString(6, konto.getBank());
            stmt.setInt(7, konto.getId());
            System.out.println("KontoRepository: update: Query Statement prepared");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also keine Änderungen gespeichert

            System.out.println("KontoRepository: update: executed successfully.");
            return true;
        }
    }

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
            System.out.println("KontoRepository: delete: Database connection established successfully.");
            stmt.setInt(1, konto.getId());
            System.out.println("KontoRepository: delete: Query Statement prepared");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also keine Änderungen gespeichert

            System.out.println("KontoRepository: delete: executed successfully.");
            return true;
        }
    } */
    
}
