
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
import de.finanzen.model.Buchung;

public class BuchungRepository
{
    private static final Logger         LOGGER                  = Logger.getLogger(BuchungRepository.class.getName());   
    private final TransaktionRepository transaktionRepository   = new TransaktionRepository();
    private final KontoRepository       kontoRepository         = new KontoRepository();

    public Buchung getById(long buchung_id) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "buchung_id, ";
        sql = sql +     "transaktion_id, ";
        sql = sql +     "konto_id, ";
        sql = sql +     "betrag ";
        sql = sql + "FROM buchung ";
        sql = sql + "WHERE buchung_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setLong(1, buchung_id);
            LOGGER.fine("getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToBuchung(rs);
                }
            }
        }
        return null;
    }

    public List<Buchung> getAlle() throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "buchung_id, ";
        sql = sql +     "transaktion_id, ";
        sql = sql +     "konto_id, ";
        sql = sql +     "betrag ";
        sql = sql + "FROM buchung";

        List<Buchung> buchungen = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getAlle: Database connection established successfully.");
            LOGGER.fine("getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    buchungen.add(mapResultSetToBuchung(rs));
                }
            }
        }

        LOGGER.fine("getAlle: ResultSet retrieved successfully.");
        return buchungen;
    }

    public boolean save(Buchung buchung) throws SQLException
    {
        if (buchung.getId() == 0) return insert(buchung);
        return update(buchung);
    }

    private boolean insert(Buchung buchung) throws SQLException
    {
        String sql = "INSERT INTO buchung ";
        sql = sql +     "(transaktion_id, konto_id, betrag) ";
        sql = sql + "VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            LOGGER.fine("insert: Database connection established successfully.");

            stmt.setLong(1, buchung.getTransaktion().getId());
            stmt.setInt(2, buchung.getKonto().getId());
            stmt.setBigDecimal(3, buchung.getBetrag());

            LOGGER.fine("insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    buchung.setId(rs.getLong(1));
                    LOGGER.fine("insert: Insert executed successfully.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(Buchung buchung) throws SQLException
    {
        String sql = "UPDATE buchung ";
        sql = sql +     "SET ";
        sql = sql +         "transaktion_id = ?, ";
        sql = sql +         "konto_id = ?, ";
        sql = sql +         "betrag = ? ";
        sql = sql + "WHERE buchung_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("update: Database connection established successfully.");

            stmt.setLong(1, buchung.getTransaktion().getId());
            stmt.setInt(2, buchung.getKonto().getId());
            stmt.setBigDecimal(3, buchung.getBetrag());
            stmt.setLong(4, buchung.getId());

            LOGGER.fine("update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    public boolean delete(Buchung buchung) throws SQLException
    {
        String sql = "DELETE FROM buchung WHERE buchung_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("delete: Database connection established successfully.");
            stmt.setLong(1, buchung.getId());
            LOGGER.fine("delete: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("delete: Delete executed successfully.");
            return true;
        }
    }

    private Buchung mapResultSetToBuchung(ResultSet rs) throws SQLException
    {
        Buchung buchung = new Buchung();

        buchung.setId(rs.getLong("buchung_id"));

        long transaktion_id = rs.getLong("transaktion_id");
        buchung.setTransaktion(transaktionRepository.getById(transaktion_id));

        int konto_id = rs.getInt("konto_id");
        buchung.setKonto(kontoRepository.getById(konto_id));

        buchung.setBetrag(rs.getBigDecimal("betrag"));

        return buchung;
    }
}
