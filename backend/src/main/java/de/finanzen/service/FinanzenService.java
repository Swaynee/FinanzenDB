package de.finanzen.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseTransaction;
import de.finanzen.model.Buchung;
import de.finanzen.model.BudgetBuchung;
import de.finanzen.model.Transaktion;


public class FinanzenService 
{
    private static final Logger         LOGGER = Logger.getLogger(FinanzenService.class.getName());
    private final TransaktionService    transaktionService;
    private final BuchungService        buchungService;
    private final BudgetBuchungService  budgetBuchungService;
    
    public FinanzenService(TransaktionService transaktionService, BuchungService buchungService, BudgetBuchungService budgetBuchungService)
    {
        this.transaktionService      = transaktionService;
        this.buchungService          = buchungService;
        this.budgetBuchungService    = budgetBuchungService;
    }

    public boolean erstelleTransaktion(Transaktion transaktion, List<Buchung> buchungen, List<BudgetBuchung> budgetBuchungen) throws SQLException
    {
        try (DatabaseTransaction transaction = new DatabaseTransaction())
        {
            Connection con = transaction.getConnection();
            boolean buchungErfolgreich, transaktionErfolgreich, budgetBuchungErfolgreich;

            transaktionErfolgreich = transaktionService.erstelle(transaktion, con);
            if (!transaktionErfolgreich) return false;

            for (Buchung buchung : buchungen) 
            {
                buchungErfolgreich = buchungService.erstelle(buchung, con);
                if (!buchungErfolgreich) return false;
            }

            for (BudgetBuchung budgetBuchung : budgetBuchungen) 
            {
                budgetBuchungErfolgreich = budgetBuchungService.erstelle(budgetBuchung, con);
                if (!budgetBuchungErfolgreich) return false;
            }
            
            return true;
        }
    }

    public boolean kontenUmbuchen() 
    {
        return false;
    }

    public boolean budgetsUmbuchen() 
    {
        return false;
    }

    public boolean erzeugeWiederkehrendeTransaktionen() 
    {
        return false;
    }

}
