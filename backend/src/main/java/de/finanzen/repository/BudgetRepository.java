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
import de.finanzen.model.Budget;

public class BudgetRepository 
{
    private static final Logger LOGGER = Logger.getLogger(BudgetRepository.class.getName());

    public Budget getById(int budget_id) throws SQLException
    {
        String sql = "SELECT ";
        sql =  sql +    "budget_id, ";
        sql =  sql +    "name ";
        sql =  sql + "FROM budget ";
        sql =  sql + "WHERE budget_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setInt(1, budget_id);
            LOGGER.fine("getById: Query Statement prepared.");
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) 
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToBudget(rs);
                }
            }
        }
        return null;
    }

    /*
     * Retrieves all Budget records from the database.
     * Returns a list of Budget objects. Empty list if no Budget records are found.
     */
    public List<Budget> getAlle() throws SQLException
    {
        String sql = "SELECT ";
        sql =  sql +    "budget_id, ";
        sql =  sql +    "name ";
        sql =  sql + "FROM budget";

        List<Budget> budgets = new ArrayList<>();

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("getAlle: Database connection established successfully.");
            LOGGER.fine("getAlle: Query Statement prepared.");
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) budgets.add(mapResultSetToBudget(rs));
            }
        }
        LOGGER.fine("getAlle: ResultSet retrieved successfully.");
        return budgets;
    }

    /*
     * Saves a Budget object to the database. If the Budget has an ID of 0, it will be inserted as a new record.
     * If the Budget has a non-zero ID, it will be updated in the database.
     * Returns true if the operation was successful, false otherwise.
     */
    public boolean save(Budget budget) throws SQLException
    {
        if (budget.getId() == 0) return insert(budget);
        return update(budget);
    }

    /*
     * Inserts a new Budget into the database.
     * And sets the generated budget_id in the provided Budget object.
     * Returns true if the insertion was successful, false otherwise.
     */
    private boolean insert(Budget budget) throws SQLException
    {
        String sql = "INSERT INTO budget ";
        sql =  sql +    "( ";
        sql =  sql +        "name ";
        sql =  sql +    ") ";
        sql =  sql + "VALUES (?)";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            )
        {
            LOGGER.fine("insert: Database connection established successfully.");
            stmt.setString(1, budget.getName());
            LOGGER.fine("insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();  // enthält die Anzahl der betroffenen Zeilen
            if (rc < 1) return false;       // keine Zeilen betroffen, also kein Budget gespeichert

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.fine("insert: Insert executed successfully.");
                    budget.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Updates an existing Budget in the database based on its budget_id.
     * Returns true if the update was successful, false otherwise.
     */
    private boolean update(Budget budget) throws SQLException
    {
        String sql = "UPDATE budget ";
        sql =  sql +    "SET ";
        sql =  sql +        "name = ? ";
        sql =  sql + "WHERE budget_id = ?";

        try (   Connection          con     = DatabaseConnection.getConnection();
                PreparedStatement   stmt    = con.prepareStatement(sql);
            )
        {
            LOGGER.fine("update: Database connection established successfully.");
            stmt.setString(1, budget.getName());
            stmt.setInt(2, budget.getId());
            LOGGER.fine("update: Query Statement prepared.");

            int rc = stmt.executeUpdate();  // enthält die Anzahl der betroffenen Zeilen
            if (rc < 1) return false;       // keine Zeilen betroffen, also keine Änderungen gespeichert

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    /*
     * Maps the current row of the provided ResultSet to a Budget object.
     * Assumes that the ResultSet is positioned at a valid row.
     */
    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException
    {
        Budget budget = new Budget();
        budget.setId(rs.getInt("budget_id"));
        budget.setName(rs.getString("name"));
        return budget;
    }
}