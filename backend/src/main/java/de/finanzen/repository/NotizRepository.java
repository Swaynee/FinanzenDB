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
import de.finanzen.model.Notiz;

public class NotizRepository
{
    private static final Logger LOGGER = Logger.getLogger(NotizRepository.class.getName());

    public Notiz getById(int notiz_id) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return getById(notiz_id, con);
        }
    }

    public Notiz getById(int notiz_id, Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +    "notiz_id, ";
        sql = sql +    "text, ";
        sql = sql +    "betrag, ";
        sql = sql +    "faellig, ";
        sql = sql +    "geloescht ";
        sql = sql + "FROM notiz ";
        sql = sql + "WHERE notiz_id = ?";

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            stmt.setInt(1, notiz_id);
            LOGGER.log(Level.FINE,"getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"getById: ResultSet retrieved successfully.");
                    return mapResultSetToNotiz(rs);
                }
            }
        }
        return null;
    }


    public List<Notiz> getAlle() throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return getAlle(con);
        }
    }

    public List<Notiz> getAlle(Connection con) throws SQLException
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

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            LOGGER.log(Level.FINE,"getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) notizen.add(mapResultSetToNotiz(rs));
            }
        }
        LOGGER.log(Level.FINE,"getAlle: ResultSet retrieved successfully.");
        return notizen;
    }


    public boolean save(Notiz notiz) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return save(notiz, con);
        }
    }

    public boolean save(Notiz notiz, Connection con) throws SQLException
    {
        if (notiz.getId() == 0) return insert(notiz, con);
        return update(notiz, con);
    }

    private boolean insert(Notiz notiz, Connection con) throws SQLException
    {
        String sql = "INSERT INTO notiz ";
        sql = sql +    "( ";
        sql = sql +        "text, ";
        sql = sql +        "betrag, ";
        sql = sql +        "faellig, ";
        sql = sql +        "geloescht ";
        sql = sql +    ") ";
        sql = sql + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement   stmt    = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, notiz.getText());
            stmt.setBigDecimal(2, notiz.getBetrag());
            stmt.setString(3, notiz.getFaellig());
            stmt.setBoolean(4, notiz.istGeloescht());
            LOGGER.log(Level.FINE,"insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"insert: Insert executed successfully.");
                    notiz.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(Notiz notiz, Connection con) throws SQLException
    {
        String sql = "UPDATE notiz ";
        sql = sql +    "SET ";
        sql = sql +        "text = ?, ";
        sql = sql +        "betrag = ?, ";
        sql = sql +        "faellig = ?, ";
        sql = sql +        "geloescht = ? ";
        sql = sql + "WHERE notiz_id = ?";

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            stmt.setString(1, notiz.getText());
            stmt.setBigDecimal(2, notiz.getBetrag());
            stmt.setString(3, notiz.getFaellig());
            stmt.setBoolean(4, notiz.istGeloescht());
            stmt.setInt(5, notiz.getId());
            LOGGER.log(Level.FINE,"update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.log(Level.FINE,"update: Update executed successfully.");
            return true;
        }
    }


    public boolean delete(Notiz notiz) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return delete(notiz, con);
        }
    }

    public boolean delete(Notiz notiz, Connection con) throws SQLException
    {
        notiz.setGeloescht(true);
        return update(notiz, con);           
    }


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