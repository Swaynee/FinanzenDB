package de.finanzen.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseConnection;
import de.finanzen.model.Transaktionsart;

public class TransaktionsartRepository
{
    private static final Logger LOGGER = Logger.getLogger(TransaktionsartRepository.class.getName());

    public Transaktionsart getById(int transaktionsart_id) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql + "transaktionsart_id, ";
        sql = sql + "name ";
        sql = sql + "FROM transaktionsart ";
        sql = sql + "WHERE transaktionsart_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setInt(1, transaktionsart_id);
            LOGGER.fine("getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToTransaktionsart(rs);
                }
            }
        }
        return null;
    }

    /*
     * Retrieves all Transaktionsart records from the database.
     * Returns an empty list if no records are found.
     */
    public List<Transaktionsart> getAlle() throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "transaktionsart_id, ";
        sql = sql +     "name ";
        sql = sql + "FROM transaktionsart";

        List<Transaktionsart> transaktionsarten = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getAlle: Database connection established successfully.");
            LOGGER.fine("getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                    transaktionsarten.add(mapResultSetToTransaktionsart(rs));
            }
        }

        LOGGER.fine("getAlle: ResultSet retrieved successfully.");
        return transaktionsarten;
    }

    /*
     * Saves a Transaktionsart object.
     * ID 0 means insert; a non-zero ID means update.
     */
    public boolean save(Transaktionsart transaktionsart) throws SQLException
    {
        if (transaktionsart.getId() == 0) return insert(transaktionsart);
        return update(transaktionsart);
    }

    /*
     * Inserts a new Transaktionsart and sets its generated ID.
     */
    private boolean insert(Transaktionsart transaktionsart) throws SQLException
    {
        String sql = "INSERT INTO transaktionsart ";
        sql = sql +     "(name) ";
        sql = sql + "VALUES (?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            LOGGER.fine("insert: Database connection established successfully.");
            stmt.setString(1, transaktionsart.getName());
            LOGGER.fine("insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.fine("insert: Insert executed successfully.");
                    transaktionsart.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Updates an existing Transaktionsart based on its ID.
     */
    private boolean update(Transaktionsart transaktionsart) throws SQLException
    {
        String sql = "UPDATE transaktionsart ";
        sql = sql +     "SET ";
        sql = sql +         "name = ? ";
        sql = sql + "WHERE transaktionsart_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("update: Database connection established successfully.");
            stmt.setString(1, transaktionsart.getName());
            stmt.setInt(2, transaktionsart.getId());
            LOGGER.fine("update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    /*
     * Maps the current ResultSet row to a Transaktionsart object.
     */
    private Transaktionsart mapResultSetToTransaktionsart(ResultSet rs) throws SQLException
    {
        Transaktionsart transaktionsart = new Transaktionsart();
        transaktionsart.setId(rs.getInt("transaktionsart_id"));
        transaktionsart.setName(rs.getString("name"));
        return transaktionsart;
    }
}