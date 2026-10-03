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
            System.out.println("KontoRepository: Database connection established successfully.");
            stmt.setInt(1, konto_id);
            System.out.println("KontoRepository: Query Statement prepared");
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    System.out.println("KontoRepository: ResultSet retrieved successfully.");
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

    public List<Konto> getAlleAktiven() 
    {
        // Implement logic to retrieve all Konto records from the database
        return null; // Placeholder return statement
    }

    public void save(Konto konto) 
    {
        // Implement logic to save Konto to the database
    }

    public void delete(Konto konto) 
    {
        // Implement logic to delete Konto from the database
    }
    
}
