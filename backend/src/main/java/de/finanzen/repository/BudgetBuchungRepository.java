
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
import de.finanzen.model.BudgetBuchung;

public class BudgetBuchungRepository
{
    private static final Logger         LOGGER                  = Logger.getLogger(BudgetBuchungRepository.class.getName());
    private final TransaktionRepository transaktionRepository   = new TransaktionRepository();
    private final BudgetRepository      budgetRepository        = new BudgetRepository();

    public BudgetBuchung getById(long budget_buchung_id) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "budget_buchung_id, ";
        sql = sql +     "transaktion_id, ";
        sql = sql +     "budget_id, ";
        sql = sql +     "betrag ";
        sql = sql + "FROM budget_buchung ";
        sql = sql + "WHERE budget_buchung_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getById: Database connection established successfully.");
            stmt.setLong(1, budget_buchung_id);
            LOGGER.fine("getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.fine("getById: ResultSet retrieved successfully.");
                    return mapResultSetToBudgetBuchung(rs);
                }
            }
        }
        return null;
    }

    public List<BudgetBuchung> getAlle() throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "budget_buchung_id, ";
        sql = sql +     "transaktion_id, ";
        sql = sql +     "budget_id, ";
        sql = sql +     "betrag ";
        sql = sql + "FROM budget_buchung";

        List<BudgetBuchung> budgetBuchungen = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("getAlle: Database connection established successfully.");
            LOGGER.fine("getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    budgetBuchungen.add(mapResultSetToBudgetBuchung(rs));
                }
            }
        }

        LOGGER.fine("getAlle: ResultSet retrieved successfully.");
        return budgetBuchungen;
    }

    public boolean save(BudgetBuchung budgetBuchung) throws SQLException
    {
        if (budgetBuchung.getId() == 0) return insert(budgetBuchung);
        return update(budgetBuchung);
    }

    private boolean insert(BudgetBuchung budgetBuchung) throws SQLException
    {
        String sql = "INSERT INTO budget_buchung ";
        sql = sql +     "(transaktion_id, budget_id, betrag) ";
        sql = sql + "VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            LOGGER.fine("insert: Database connection established successfully.");

            stmt.setLong(1, budgetBuchung.getTransaktion().getId());
            stmt.setInt(2, budgetBuchung.getBudget().getId());
            stmt.setBigDecimal(3, budgetBuchung.getBetrag());

            LOGGER.fine("insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    budgetBuchung.setId(rs.getLong(1));
                    LOGGER.fine("insert: Insert executed successfully.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(BudgetBuchung budgetBuchung) throws SQLException
    {
        String sql = "UPDATE budget_buchung ";
        sql = sql +     "SET ";
        sql = sql +         "transaktion_id = ?, ";
        sql = sql +         "budget_id = ?, ";
        sql = sql +         "betrag = ? ";
        sql = sql + "WHERE budget_buchung_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("update: Database connection established successfully.");

            stmt.setLong(1, budgetBuchung.getTransaktion().getId());
            stmt.setInt(2, budgetBuchung.getBudget().getId());
            stmt.setBigDecimal(3, budgetBuchung.getBetrag());
            stmt.setLong(4, budgetBuchung.getId());

            LOGGER.fine("update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("update: Update executed successfully.");
            return true;
        }
    }

    public boolean delete(BudgetBuchung budgetBuchung) throws SQLException
    {
        String sql = "DELETE FROM budget_buchung WHERE budget_buchung_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.fine("delete: Database connection established successfully.");
            stmt.setLong(1, budgetBuchung.getId());
            LOGGER.fine("delete: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.fine("delete: Delete executed successfully.");
            return true;
        }
    }

    private BudgetBuchung mapResultSetToBudgetBuchung(ResultSet rs) throws SQLException
    {
        BudgetBuchung budgetBuchung = new BudgetBuchung();

        budgetBuchung.setId(rs.getLong("budget_buchung_id"));

        long transaktion_id = rs.getLong("transaktion_id");
        budgetBuchung.setTransaktion(transaktionRepository.getById(transaktion_id));

        int budget_id = rs.getInt("budget_id");
        budgetBuchung.setBudget(budgetRepository.getById(budget_id));

        budgetBuchung.setBetrag(rs.getBigDecimal("betrag"));

        return budgetBuchung;
    }
}
