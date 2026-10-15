package de.finanzen.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseConnection;
import de.finanzen.model.Transaktion;

public class TransaktionRepository
{
    private static final Logger LOGGER = Logger.getLogger(TransaktionRepository.class.getName());

    private final TransaktionsartRepository transaktionsartRepository = new TransaktionsartRepository();

    public Transaktion getById(long transaktion_id) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "transaktion_id, ";
        sql = sql +     "transaktionsart_id, ";
        sql = sql +     "datum, ";
        sql = sql +     "planungsmonat, ";
        sql = sql +     "beschreibung ";
        sql = sql + "FROM transaktion ";
        sql = sql + "WHERE transaktion_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setLong(1, transaktion_id);
            LOGGER.fine("getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToTransaktion(rs);
                }
            }
        }
        return null;
    }

    public List<Transaktion> getAlle() throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "transaktion_id, ";
        sql = sql +     "transaktionsart_id, ";
        sql = sql +     "datum, ";
        sql = sql +     "planungsmonat, ";
        sql = sql +     "beschreibung ";
        sql = sql + "FROM transaktion";

        List<Transaktion> transaktionen = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getAlle: Database connection established successfully.");
            LOGGER.fine("getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    transaktionen.add(mapResultSetToTransaktion(rs));
                }
            }
        }

        LOGGER.fine("getAlle: ResultSet retrieved successfully.");
        return transaktionen;
    }

    public boolean save(Transaktion transaktion) throws SQLException
    {
        if (transaktion.getId() == 0) return insert(transaktion);
        return update(transaktion);
    }

    private boolean insert(Transaktion transaktion) throws SQLException
    {
        String sql = "INSERT INTO transaktion ";
        sql = sql +     "(transaktionsart_id, datum, planungsmonat, beschreibung) ";
        sql = sql + "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            LOGGER.fine("insert: Database connection established successfully.");

            stmt.setInt(1, transaktion.getTransaktionsart().getId());
            stmt.setDate(2, Date.valueOf(transaktion.getDatum()));
            stmt.setDate(3, Date.valueOf(transaktion.getPlanungsmonat()));
            stmt.setString(4, transaktion.getBeschreibung());

            LOGGER.fine("insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    transaktion.setId(rs.getLong(1));
                    LOGGER.fine("insert: Insert executed successfully.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(Transaktion transaktion) throws SQLException
    {
        String sql = "UPDATE transaktion ";
        sql = sql + "SET ";
        sql = sql + "transaktionsart_id = ?, ";
        sql = sql + "datum = ?, ";
        sql = sql + "planungsmonat = ?, ";
        sql = sql + "beschreibung = ? ";
        sql = sql + "WHERE transaktion_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("update: Database connection established successfully.");

            stmt.setInt(1, transaktion.getTransaktionsart().getId());
            stmt.setDate(2, Date.valueOf(transaktion.getDatum()));
            stmt.setDate(3, Date.valueOf(transaktion.getPlanungsmonat()));
            stmt.setString(4, transaktion.getBeschreibung());
            stmt.setLong(5, transaktion.getId());

            LOGGER.fine("update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    public boolean delete(Transaktion transaktion) throws SQLException
    {
        String sql = "DELETE FROM transaktion WHERE transaktion_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("delete: Database connection established successfully.");
            stmt.setLong(1, transaktion.getId());
            LOGGER.fine("delete: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("delete: Delete executed successfully.");
            return true;
        }
    }

    private Transaktion mapResultSetToTransaktion(ResultSet rs) throws SQLException
    {
        Transaktion transaktion = new Transaktion();

        transaktion.setId(rs.getLong("transaktion_id"));

        int transaktionsart_id = rs.getInt("transaktionsart_id");
        transaktion.setTransaktionsart(transaktionsartRepository.getById(transaktionsart_id));

        transaktion.setDatum(rs.getDate("datum").toLocalDate());
        transaktion.setPlanungsmonat(rs.getDate("planungsmonat").toLocalDate());
        transaktion.setBeschreibung(rs.getString("beschreibung"));

        return transaktion;
    }
}
