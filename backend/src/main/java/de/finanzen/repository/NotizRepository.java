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
import de.finanzen.model.Notiz;

public class NotizRepository
{
    private static final Logger LOGGER = Logger.getLogger(NotizRepository.class.getName());

    public Notiz getById(int notiz_id) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +    "notiz_id, ";
        sql = sql +    "text, ";
        sql = sql +    "betrag, ";
        sql = sql +    "faellig, ";
        sql = sql +    "geloescht ";
        sql = sql + "FROM notiz ";
        sql = sql + "WHERE notiz_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setInt(1, notiz_id);
            LOGGER.fine("getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToNotiz(rs);
                }
            }
        }
        return null;
    }

    /*
     * Retrieves all Notiz records that have not been deleted.
     * Returns an empty list if no records are found.
     */
    public List<Notiz> getAlle() throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +    "notiz_id, ";
        sql = sql +    "text, ";
        sql = sql +    "betrag, ";
        sql = sql +    "faellig, ";
        sql = sql +    "geloescht ";
        sql = sql + "FROM notiz ";
        sql = sql + "WHERE geloescht = 0";

        List<Notiz> notizen = new ArrayList<>();

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("getAlle: Database connection established successfully.");
            LOGGER.fine("getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) notizen.add(mapResultSetToNotiz(rs));
            }
        }
        LOGGER.fine("getAlle: ResultSet retrieved successfully.");
        return notizen;
    }

    /*
     * Saves a Notiz object. If the ID is 0, it will be inserted as a new record.
     * If the ID is non-zero, it will be updated.
     */
    public boolean save(Notiz notiz) throws SQLException
    {
        if (notiz.getId() == 0) return insert(notiz);
        return update(notiz);
    }

    /*
     * Inserts a new Notiz and sets its generated notiz_id.
     */
    private boolean insert(Notiz notiz) throws SQLException
    {
        String sql = "INSERT INTO notiz ";
        sql = sql +    "( ";
        sql = sql +        "text, ";
        sql = sql +        "betrag, ";
        sql = sql +        "faellig, ";
        sql = sql +        "geloescht ";
        sql = sql +    ") ";
        sql = sql + "VALUES (?, ?, ?, ?)";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            )
        {
            LOGGER.fine("insert: Database connection established successfully.");
            stmt.setString(1, notiz.getText());
            stmt.setBigDecimal(2, notiz.getBetrag());
            stmt.setString(3, notiz.getFaellig());
            stmt.setBoolean(4, notiz.istGeloescht());
            LOGGER.fine("insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.fine("insert: Insert executed successfully.");
                    notiz.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Updates an existing Notiz based on its notiz_id.
     */
    private boolean update(Notiz notiz) throws SQLException
    {
        String sql = "UPDATE notiz ";
        sql = sql +    "SET ";
        sql = sql +        "text = ?, ";
        sql = sql +        "betrag = ?, ";
        sql = sql +        "faellig = ?, ";
        sql = sql +        "geloescht = ? ";
        sql = sql + "WHERE notiz_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("update: Database connection established successfully.");
            stmt.setString(1, notiz.getText());
            stmt.setBigDecimal(2, notiz.getBetrag());
            stmt.setString(3, notiz.getFaellig());
            stmt.setBoolean(4, notiz.istGeloescht());
            stmt.setInt(5, notiz.getId());
            LOGGER.fine("update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    /*
     * Maps the current ResultSet row to a Notiz object.
     */
    private Notiz mapResultSetToNotiz(ResultSet rs) throws SQLException
    {
        Notiz notiz = new Notiz();
        notiz.setId(rs.getInt("notiz_id"));
        notiz.setText(rs.getString("text"));
        notiz.setBetrag(rs.getBigDecimal("betrag"));
        notiz.setFaellig(rs.getString("faellig"));
        notiz.setGeloescht(rs.getBoolean("geloescht"));
        return notiz;
    }
}