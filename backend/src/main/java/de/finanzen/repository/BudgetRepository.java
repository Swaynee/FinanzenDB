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
import de.finanzen.model.Budget;

public class BudgetRepository 
{
    private static final Logger LOGGER = Logger.getLogger(BudgetRepository.class.getName());


    public Budget getById(int budget_id) throws SQLException 
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return getById(budget_id, con);
        }
    }

    public Budget getById(int budget_id, Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql =  sql +    "budget_id, ";
        sql =  sql +    "name ";
        sql =  sql +    "aktiv ";
        sql =  sql + "FROM budget ";
        sql =  sql + "WHERE budget_id = ?";

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            stmt.setInt(1, budget_id);
            LOGGER.log(Level.FINE,"getById: Query Statement prepared.");
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    LOGGER.log(Level.FINE,"getById: ResultSet retrieved successfully.");
                    return mapResultSetToBudget(rs);
                }
            }
        }
        return null;
    }


    public List<Budget> getAlleAktiven() throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return getAlleAktiven(con);
        }
    }

    public List<Budget> getAlleAktiven(Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql =  sql +    "budget_id, ";
        sql =  sql +    "name ";
        sql =  sql +    "aktiv ";
        sql =  sql + "FROM budget";
        sql =  sql + "WHERE aktiv = 1";

        List<Budget> budgets = new ArrayList<>();

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            LOGGER.log(Level.FINE,"getAlle: Query Statement prepared.");
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) budgets.add(mapResultSetToBudget(rs));
            }
        }
        LOGGER.log(Level.FINE,"getAlle: ResultSet retrieved successfully.");
        return budgets;
    }


    public boolean save(Budget budget) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return save(budget, con);
        }
    }

    public boolean save(Budget budget, Connection con) throws SQLException
    {
        if (budget.getId() == 0) return insert(budget, con);
        return update(budget, con);
    }


    private boolean insert(Budget budget, Connection con) throws SQLException
    {
        String sql = "INSERT INTO budget ";
        sql =  sql +    "( ";
        sql =  sql +        "name, ";
        sql =  sql +        "aktiv ";
        sql =  sql +    ") ";
        sql =  sql + "VALUES (?)";

        try (PreparedStatement   stmt    = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, budget.getName());
            LOGGER.log(Level.FINE,"insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();  // enthält die Anzahl der betroffenen Zeilen
            if (rc < 1) return false;       // keine Zeilen betroffen, also kein Budget gespeichert

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"insert: Insert executed successfully.");
                    budget.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(Budget budget, Connection con) throws SQLException
    {
        String sql = "UPDATE budget ";
        sql =  sql +    "SET ";
        sql =  sql +        "name = ? ";
        sql =  sql +        "aktiv = ?, ";
        sql =  sql + "WHERE budget_id = ?";

        try (PreparedStatement   stmt    = con.prepareStatement(sql))
        {
            stmt.setString(1, budget.getName());
            stmt.setInt(2, budget.getId());
            LOGGER.log(Level.FINE,"update: Query Statement prepared.");

            int rc = stmt.executeUpdate();  // enthält die Anzahl der betroffenen Zeilen
            if (rc < 1) return false;       // keine Zeilen betroffen, also keine Änderungen gespeichert

            LOGGER.log(Level.FINE,"update: Update executed successfully.");
            return true;
        }
    }


    public boolean delete(Budget budget) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection()) 
        {
            return delete(budget, con);
        }
    }

    public boolean delete(Budget budget, Connection con) throws SQLException
    {
        budget.setAktiv(false);
        return update(budget, con);
    }


    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException
    {
        Budget budget = new Budget();
        budget.setId(rs.getInt("budget_id"));
        budget.setName(rs.getString("name"));
        budget.setAktiv(rs.getBoolean("aktiv"));
        return budget;
    }
}