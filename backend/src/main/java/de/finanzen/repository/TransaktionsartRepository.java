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
import de.finanzen.model.Transaktionsart;

public class TransaktionsartRepository
{
    private static final Logger LOGGER = Logger.getLogger(TransaktionsartRepository.class.getName());


    public Transaktionsart getById(int transaktionsart_id) throws SQLException 
    {
        try (Connection con = DatabaseConnection.getConnection())  
        {
            return getById(transaktionsart_id, con);
        }
    }

    public Transaktionsart getById(int transaktionsart_id, Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql + "transaktionsart_id, ";
        sql = sql + "name ";
        sql = sql + "FROM transaktionsart ";
        sql = sql + "WHERE transaktionsart_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setInt(1, transaktionsart_id);
            LOGGER.log(Level.FINE,"getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"getById: ResultSet retrieved successfully.");
                    return mapResultSetToTransaktionsart(rs);
                }
            }
        }
        return null;
    }


    public List<Transaktionsart> getAlle() throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection())  
        {
            return getAlle(con);
        }
    }

    public List<Transaktionsart> getAlle(Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "transaktionsart_id, ";
        sql = sql +     "name ";
        sql = sql + "FROM transaktionsart";

        List<Transaktionsart> transaktionsarten = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.log(Level.FINE,"getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) transaktionsarten.add(mapResultSetToTransaktionsart(rs));
            }
        }

        LOGGER.log(Level.FINE,"getAlle: ResultSet retrieved successfully.");
        return transaktionsarten;
    }


    public boolean save(Transaktionsart transaktionsart) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection())  
        {
            return save(transaktionsart, con);
        }
    }

    public boolean save(Transaktionsart transaktionsart, Connection con) throws SQLException
    {
        if (transaktionsart.getId() == 0) return insert(transaktionsart, con);
        return update(transaktionsart, con);
    }

    private boolean insert(Transaktionsart transaktionsart, Connection con) throws SQLException
    {
        String sql = "INSERT INTO transaktionsart ";
        sql = sql +     "(name) ";
        sql = sql + "VALUES (?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, transaktionsart.getName());
            LOGGER.log(Level.FINE,"insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"insert: Insert executed successfully.");
                    transaktionsart.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(Transaktionsart transaktionsart, Connection con) throws SQLException
    {
        String sql = "UPDATE transaktionsart ";
        sql = sql +     "SET ";
        sql = sql +         "name = ? ";
        sql = sql + "WHERE transaktionsart_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setString(1, transaktionsart.getName());
            stmt.setInt(2, transaktionsart.getId());
            LOGGER.log(Level.FINE,"update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.log(Level.FINE,"update: Update executed successfully.");
            return true;
        }
    }


    private Transaktionsart mapResultSetToTransaktionsart(ResultSet rs) throws SQLException
    {
        Transaktionsart transaktionsart = new Transaktionsart();
        transaktionsart.setId(rs.getInt("transaktionsart_id"));
        transaktionsart.setName(rs.getString("name"));
        return transaktionsart;
    }
}