package de.finanzen.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseTransaction;
import de.finanzen.model.Buchung;
import de.finanzen.model.Budget;
import de.finanzen.model.BudgetBuchung;
import de.finanzen.model.Konto;
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
            boolean transaktionErfolgreich, buchungErfolgreich, budgetBuchungErfolgreich;

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

    public boolean kontenUmbuchen(Transaktion transaktion, BigDecimal betrag, Konto quellKonto, Konto zielKonto) throws SQLException
    {
        try (DatabaseTransaction transaction = new DatabaseTransaction())
        {
            Connection con = transaction.getConnection();
            boolean transaktionErfolgreich, buchungQuelleErfolgreich;

            transaktionErfolgreich = transaktionService.erstelle(transaktion, con);
            if (!transaktionErfolgreich) return false;

            Buchung vonQuellKonto = new Buchung();
            vonQuellKonto.setTransaktion(transaktion);
            vonQuellKonto.setBetrag(betrag.negate());
            vonQuellKonto.setKonto(quellKonto);
            buchungQuelleErfolgreich = buchungService.erstelle(vonQuellKonto);
            if (!buchungQuelleErfolgreich) return false;

            Buchung aufZielKonto = new Buchung();
            aufZielKonto.setTransaktion(transaktion);
            aufZielKonto.setBetrag(betrag);
            aufZielKonto.setKonto(zielKonto);
            return buchungService.erstelle(aufZielKonto);
        }
    }

    public boolean budgetsUmbuchen(Transaktion transaktion, BigDecimal betrag, Budget quellBudget, Budget zielBudget) throws SQLException
    {
        try (DatabaseTransaction transaction = new DatabaseTransaction())
        {
            Connection con = transaction.getConnection();
            boolean transaktionErfolgreich, buchungQuelleErfolgreich;

            transaktionErfolgreich = transaktionService.erstelle(transaktion, con);
            if (!transaktionErfolgreich) return false;

            BudgetBuchung vonQuellBudget = new BudgetBuchung();
            vonQuellBudget.setTransaktion(transaktion);
            vonQuellBudget.setBetrag(betrag.negate());
            vonQuellBudget.setBudget(quellBudget);
            buchungQuelleErfolgreich = budgetBuchungService.erstelle(vonQuellBudget);
            if (!buchungQuelleErfolgreich) return false;

            BudgetBuchung aufZielBudget = new BudgetBuchung();
            aufZielBudget.setTransaktion(transaktion);
            aufZielBudget.setBetrag(betrag);
            aufZielBudget.setBudget(zielBudget);
            return budgetBuchungService.erstelle(aufZielBudget);
        }
    }

    public boolean erzeugeWiederkehrendeTransaktionen() 
    {
        //alle wiederkehrende transaktionen holen
        //für jedes eine transaktion + dazugehörige buchungen/budget buchungen erzeugen
        return false;
    }

}
