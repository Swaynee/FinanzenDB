package de.finanzen.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseConnection;
import de.finanzen.model.TransaktionWiederkehrend;

public class TransaktionWiederkehrendRepository
{
    private static final Logger             LOGGER = Logger.getLogger(TransaktionWiederkehrendRepository.class.getName());
    private final TransaktionsartRepository transaktionsartRepository;

    public TransaktionWiederkehrendRepository(TransaktionsartRepository transaktionsartRepository)
    {
        this.transaktionsartRepository  = transaktionsartRepository;
    }


    public TransaktionWiederkehrend getById(int transaktion_wiederkehrend_id) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection())
        {
            return getById(transaktion_wiederkehrend_id, con);
        }
    }

    public TransaktionWiederkehrend getById(int transaktion_wiederkehrend_id, Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "transaktion_wiederkehrend_id, ";
        sql = sql +     "transaktionsart_id, ";
        sql = sql +     "konto_id, ";
        sql = sql +     "konto_ziel_id, ";
        sql = sql +     "budget_id, ";
        sql = sql +     "budget_ziel_id, ";
        sql = sql +     "beschreibung, ";
        sql = sql +     "betrag, ";
        sql = sql +     "zahlungstag, ";
        sql = sql +     "planungsmonat_versatz, ";
        sql = sql +     "gueltig_ab, ";
        sql = sql +     "gueltig_bis ";
        sql = sql + "FROM Transaktion_Wiederkehrend ";
        sql = sql + "WHERE transaktion_wiederkehrend_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setInt(1, transaktion_wiederkehrend_id);
            LOGGER.log(Level.FINE,"getById: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"getById: ResultSet retrieved successfully.");
                    return mapResultSetToTransaktionWiederkehrend(rs, con);
                }
            }
        }
        return null;
    }


    public List<TransaktionWiederkehrend> getAlle() throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection())
        {
            return getAlle(con);
        }
    }

    public List<TransaktionWiederkehrend> getAlle(Connection con) throws SQLException
    {
        String sql = "SELECT ";
        sql = sql +     "transaktion_wiederkehrend_id, ";
        sql = sql +     "transaktionsart_id, ";
        sql = sql +     "konto_id, ";
        sql = sql +     "konto_ziel_id, ";
        sql = sql +     "budget_id, ";
        sql = sql +     "budget_ziel_id, ";
        sql = sql +     "beschreibung, ";
        sql = sql +     "betrag, ";
        sql = sql +     "zahlungstag, ";
        sql = sql +     "planungsmonat_versatz, ";
        sql = sql +     "gueltig_ab, ";
        sql = sql +     "gueltig_bis ";
        sql = sql + "FROM Transaktion_Wiederkehrend";

        List<TransaktionWiederkehrend> transaktionen = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            LOGGER.log(Level.FINE,"getAlle: Query Statement prepared.");

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next()) transaktionen.add(mapResultSetToTransaktionWiederkehrend(rs, con));
            }
        }

        LOGGER.log(Level.FINE,"getAlle: ResultSet retrieved successfully.");
        return transaktionen;
    }


    public boolean save(TransaktionWiederkehrend transaktion) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection())
        {
            return save(transaktion, con);
        }
    }

    public boolean save(TransaktionWiederkehrend transaktion, Connection con) throws SQLException
    {
        if (transaktion.getId() == 0) return insert(transaktion, con);
        return update(transaktion, con);
    }

    private boolean insert(TransaktionWiederkehrend transaktion, Connection con) throws SQLException
    {
        String sql = "INSERT INTO Transaktion_Wiederkehrend ";
        sql = sql +     "( ";
        sql = sql +         "transaktionsart_id, ";
        sql = sql +         "konto_id, ";
        sql = sql +         "konto_ziel_id, ";
        sql = sql +         "budget_id, ";
        sql = sql +         "budget_ziel_id, ";
        sql = sql +         "beschreibung, ";
        sql = sql +         "betrag, ";
        sql = sql +         "zahlungstag, ";
        sql = sql +         "planungsmonat_versatz, ";
        sql = sql +         "gueltig_ab, ";
        sql = sql +         "gueltig_bis ";
        sql = sql +     ") ";
        sql = sql + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setInt(1, transaktion.getTransaktionsart().getId());

            if (transaktion.getKonto() != null)     stmt.setInt(2, transaktion.getKonto().getId());
            else                                    stmt.setNull(2, Types.INTEGER);

            if (transaktion.getKontoZiel() != null) stmt.setInt(3, transaktion.getKontoZiel().getId());
            else                                    stmt.setNull(3, Types.INTEGER);

            if (transaktion.getBudget() != null)    stmt.setInt(4, transaktion.getBudget().getId());
            else                                    stmt.setNull(4, Types.INTEGER);

            if (transaktion.getBudgetZiel() != null)stmt.setInt(5, transaktion.getBudgetZiel().getId());
            else                                    stmt.setNull(5, Types.INTEGER);

            stmt.setString(6, transaktion.getBeschreibung());
            stmt.setBigDecimal(7, transaktion.getBetrag());
            stmt.setInt(8, transaktion.getZahlungstag());
            stmt.setInt(9, transaktion.getPlanungsmonatVersatz());
            stmt.setDate(10, java.sql.Date.valueOf(transaktion.getGueltigAb()));

            if (transaktion.getGueltigBis() != null)stmt.setDate(11, java.sql.Date.valueOf(transaktion.getGueltigBis()));
            else                                    stmt.setNull(11, Types.DATE);

            LOGGER.log(Level.FINE,"insert: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    LOGGER.log(Level.FINE,"insert: Insert executed successfully.");
                    transaktion.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean update(TransaktionWiederkehrend transaktion, Connection con) throws SQLException
    {
        String sql = "UPDATE Transaktion_Wiederkehrend ";
        sql = sql +     "SET ";
        sql = sql +         "transaktionsart_id = ?, ";
        sql = sql +         "konto_id = ?, ";
        sql = sql +         "konto_ziel_id = ?, ";
        sql = sql +         "budget_id = ?, ";
        sql = sql +         "budget_ziel_id = ?, ";
        sql = sql +         "beschreibung = ?, ";
        sql = sql +         "betrag = ?, ";
        sql = sql +         "zahlungstag = ?, ";
        sql = sql +         "planungsmonat_versatz = ?, ";
        sql = sql +         "gueltig_ab = ?, ";
        sql = sql +         "gueltig_bis = ? ";
        sql = sql + "WHERE transaktion_wiederkehrend_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql))
        {
            stmt.setInt(1, transaktion.getTransaktionsart().getId());

            if (transaktion.getKonto() != null)     stmt.setInt(2, transaktion.getKonto().getId());
            else                                    stmt.setNull(2, Types.INTEGER);

            if (transaktion.getKontoZiel() != null) stmt.setInt(3, transaktion.getKontoZiel().getId());
            else                                    stmt.setNull(3, Types.INTEGER);

            if (transaktion.getBudget() != null)    stmt.setInt(4, transaktion.getBudget().getId());
            else                                    stmt.setNull(4, Types.INTEGER);

            if (transaktion.getBudgetZiel() != null)stmt.setInt(5, transaktion.getBudgetZiel().getId());
            else                                    stmt.setNull(5, Types.INTEGER);

            stmt.setString(6, transaktion.getBeschreibung());
            stmt.setBigDecimal(7, transaktion.getBetrag());
            stmt.setInt(8, transaktion.getZahlungstag());
            stmt.setInt(9, transaktion.getPlanungsmonatVersatz());
            stmt.setDate(10, java.sql.Date.valueOf(transaktion.getGueltigAb()));

            if (transaktion.getGueltigBis() != null)stmt.setDate(11, java.sql.Date.valueOf(transaktion.getGueltigBis()));
            else                                    stmt.setNull(11, Types.DATE);

            stmt.setInt(12, transaktion.getId());

            LOGGER.log(Level.FINE,"update: Query Statement prepared.");

            int rc = stmt.executeUpdate();
            if (rc < 1) return false;

            LOGGER.log(Level.FINE,"update: Update executed successfully.");
            return true;
        }
    }


    private TransaktionWiederkehrend mapResultSetToTransaktionWiederkehrend(ResultSet rs, Connection con) throws SQLException
    {
        TransaktionWiederkehrend transaktion = new TransaktionWiederkehrend();

        transaktion.setId(rs.getInt("transaktion_wiederkehrend_id"));

        int transaktionsart_id = rs.getInt("transaktionsart_id");
        transaktion.setTransaktionsart(transaktionsartRepository.getById(transaktionsart_id, con));

        // Konto laden, falls vorhanden
        int konto_id = rs.getInt("konto_id");
        if (!rs.wasNull())
        {
            KontoRepository kontoRepository = new KontoRepository();
            transaktion.setKonto(kontoRepository.getById(konto_id, con));
        }

        // Zielkonto laden, falls vorhanden
        int konto_ziel_id = rs.getInt("konto_ziel_id");
        if (!rs.wasNull())
        {
            KontoRepository kontoRepository = new KontoRepository();
            transaktion.setKontoZiel(kontoRepository.getById(konto_ziel_id, con));
        }

        // Budget laden, falls vorhanden
        int budget_id = rs.getInt("budget_id");
        if (!rs.wasNull())
        {
            BudgetRepository budgetRepository = new BudgetRepository();
            transaktion.setBudget(budgetRepository.getById(budget_id, con));
        }

        // Zielbudget laden, falls vorhanden
        int budget_ziel_id = rs.getInt("budget_ziel_id");
        if (!rs.wasNull())
        {
            BudgetRepository budgetRepository = new BudgetRepository();
            transaktion.setBudgetZiel(budgetRepository.getById(budget_ziel_id, con));
        }

        transaktion.setBeschreibung(rs.getString("beschreibung"));
        transaktion.setBetrag(rs.getBigDecimal("betrag"));
        transaktion.setZahlungstag(rs.getInt("zahlungstag"));
        transaktion.setPlanungsmonatVersatz(rs.getInt("planungsmonat_versatz"));

        java.sql.Date gueltigAb = rs.getDate("gueltig_ab");
        if (gueltigAb != null) transaktion.setGueltigAb(gueltigAb.toLocalDate());

        java.sql.Date gueltigBis = rs.getDate("gueltig_bis");
        if (gueltigBis != null) transaktion.setGueltigBis(gueltigBis.toLocalDate());

        return transaktion;
    }
}