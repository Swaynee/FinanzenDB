package de.finanzen.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseConnection;
import de.finanzen.model.Konto;

public class KontoRepository 
{
    private static final Logger LOGGER = Logger.getLogger(KontoRepository.class.getName());


    public Konto getById(int konto_id) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return getById(konto_id, con);
        }
    }
    
    public Konto getById(int konto_id, Connection con) throws SQLException
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

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setInt(1, konto_id);
            LOGGER.log(Level.FINE,"getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    LOGGER.log(Level.FINE,"getById: ResultSet retrieved successfully.");
                    return mapResultSetToKonto(rs);
                }
            }
        }
        return null;
    }


    public List<Konto> getAlleAktiven() throws SQLException 
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return getAlleAktiven(con);
        }
    }

    public List<Konto> getAlleAktiven(Connection con) throws SQLException
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

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            LOGGER.log(Level.FINE,"getAlleAktiven: Query Statement prepared,");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) konten.add(mapResultSetToKonto(rs));
            }
        }
        LOGGER.log(Level.FINE,"getAlleAktiven: ResultSet retrieved successfully.");
        return konten;
    }


    public boolean save(Konto konto) throws SQLException 
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return save(konto, con);
        }
    }

    public boolean save(Konto konto, Connection con) throws SQLException
    {
        if (konto.getId() == 0) return insert(konto, con);
        return update(konto, con);
    }

    private boolean insert(Konto konto, Connection con) throws SQLException
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

        try (PreparedStatement   stmt    = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, konto.getName());
            stmt.setString(2, konto.getKontoArt());
            stmt.setBoolean(3,konto.istAktiv());
            stmt.setString(4, konto.getIban());
            stmt.setString(5, konto.getBic());
            stmt.setString(6, konto.getBank());
            LOGGER.log(Level.FINE,"insert: Query Statement prepared,");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also kein Konto gespeichert
            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"insert: Insert executed successfully.");
                    konto.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(Konto konto, Connection con) throws SQLException
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

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            stmt.setString(1, konto.getName());
            stmt.setString(2, konto.getKontoArt());
            stmt.setBoolean(3,konto.istAktiv());
            stmt.setString(4, konto.getIban());
            stmt.setString(5, konto.getBic());
            stmt.setString(6, konto.getBank());
            stmt.setInt(7, konto.getId());
            LOGGER.log(Level.FINE,"update: Query Statement prepared,");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also keine Änderungen gespeichert

            LOGGER.log(Level.FINE,"update: Update executed successfully.");
            return true;
        }
    }


    public boolean delete(Konto konto) throws SQLException 
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return delete(konto, con);
        }
    }

    public boolean delete(Konto konto, Connection con) throws SQLException
    {
        konto.setAktiv(false);
        return update(konto, con);           
    }

    /* private boolean delete(Konto konto, Connection con) throws SQLException
    {
        String sql = "DELETE FROM konto ";
        sql =  sql + "WHERE konto_id = ?";

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            stmt.setInt(1, konto.getId());
            LOGGER.log(Level.FINE,"delete: Query Statement prepared,");

            int rc = stmt.executeUpdate();  //enthält die anzahl der betroffenen Zeilen
            if (rc < 1) return false;       //keine Zeilen betroffen, also keine Änderungen gespeichert

            LOGGER.log(Level.FINE,"delete: Delete executed successfully.");
            return true;
        }
    } */
    
        
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
