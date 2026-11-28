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
import de.finanzen.model.TransaktionWiederkehrend;


public class FinanzenService 
{
    private static final Logger                     LOGGER = Logger.getLogger(FinanzenService.class.getName());
    private final TransaktionService                transaktionService;
    private final BuchungService                    buchungService;
    private final BudgetBuchungService              budgetBuchungService;
    private final TransaktionWiederkehrendService   transaktionWiederkehrendService;
    
    public FinanzenService(TransaktionService transaktionService, BuchungService buchungService, BudgetBuchungService budgetBuchungService, TransaktionWiederkehrendService transaktionWiederkehrendService)
    {
        this.transaktionService                 = transaktionService;
        this.buchungService                     = buchungService;
        this.budgetBuchungService               = budgetBuchungService;
        this.transaktionWiederkehrendService    = transaktionWiederkehrendService;
    }

    public boolean erstelleTransaktion(Transaktion transaktion, List<Buchung> buchungen, List<BudgetBuchung> budgetBuchungen) throws SQLException
    {
        try (DatabaseTransaction tr = new DatabaseTransaction())
        {
            Connection con = tr.getConnection();
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
            
            tr.commit();
            return true;
        }
    }


    public boolean kontenUmbuchen(Transaktion transaktion, BigDecimal betrag, Konto quellKonto, Konto zielKonto) throws SQLException
    {
        try (DatabaseTransaction tr = new DatabaseTransaction())
        {
            boolean erfolgreich = kontenUmbuchen(transaktion, betrag, quellKonto, zielKonto, tr.getConnection());
            if (!erfolgreich) return false;

            tr.commit();
            return true;
        }
    }

    private boolean kontenUmbuchen(Transaktion transaktion, BigDecimal betrag, Konto quellKonto, Konto zielKonto, Connection con) throws SQLException
    {
        boolean buchungQuelleErfolgreich;

        Buchung vonQuellKonto = erstelleBuchung(transaktion, quellKonto, betrag.negate());
        buchungQuelleErfolgreich = buchungService.erstelle(vonQuellKonto, con);
        if (!buchungQuelleErfolgreich) return false;

        Buchung aufZielKonto = erstelleBuchung(transaktion, zielKonto, betrag);
        return buchungService.erstelle(aufZielKonto, con);
    }


    public boolean budgetsUmbuchen(Transaktion transaktion, BigDecimal betrag, Budget quellBudget, Budget zielBudget) throws SQLException
    {
        try (DatabaseTransaction tr = new DatabaseTransaction())
        {
            boolean erfolgreich = budgetsUmbuchen(transaktion, betrag, quellBudget, zielBudget, tr.getConnection());
            if (!erfolgreich) return false;

            tr.commit();
            return true;
        }
    }

    private boolean budgetsUmbuchen(Transaktion transaktion, BigDecimal betrag, Budget quellBudget, Budget zielBudget, Connection con) throws SQLException
    {
        boolean buchungQuelleErfolgreich;

        BudgetBuchung vonQuellBudget = erstelleBudgetBuchung(transaktion, quellBudget, betrag.negate());
        buchungQuelleErfolgreich = budgetBuchungService.erstelle(vonQuellBudget, con);
        if (!buchungQuelleErfolgreich) return false;

        BudgetBuchung aufZielBudget = erstelleBudgetBuchung(transaktion, zielBudget, betrag);
        return budgetBuchungService.erstelle(aufZielBudget, con);
    }


    public boolean erzeugeWiederkehrendeTransaktionen() throws SQLException
    {
        List<TransaktionWiederkehrend> alleGueltigen = transaktionWiederkehrendService.getAlleGueltige();
        boolean erfolgreich;
        
        try (DatabaseTransaction tr = new DatabaseTransaction()) 
        {
            Connection con = tr.getConnection();

            for (TransaktionWiederkehrend transaktionWiederkehrend : alleGueltigen) 
            {
                Transaktion transaktion = mapAufTransaktion(transaktionWiederkehrend);
                erfolgreich = transaktionService.erstelle(transaktion, con);
                if (!erfolgreich) return false;

                if (transaktionWiederkehrend.getKonto() != null) 
                {
                    if (transaktionWiederkehrend.getKontoZiel() != null) //wenn zielkonto vorhanden, dann ist es eine umbuchung
                    {
                        erfolgreich = kontenUmbuchen(transaktion,transaktionWiederkehrend.getBetrag(),transaktionWiederkehrend.getKonto(),transaktionWiederkehrend.getKontoZiel(), con);
                        if (!erfolgreich) return false;
                    }
                    else //ansonsten einfache buchung
                    {
                        Buchung buchung = erstelleBuchung(transaktion,transaktionWiederkehrend.getKonto(),transaktionWiederkehrend.getBetrag());
                        erfolgreich = buchungService.erstelle(buchung, con);
                        if (!erfolgreich) return false;
                    }
                }
                
                if (transaktionWiederkehrend.getBudget() != null) 
                {
                    if (transaktionWiederkehrend.getBudgetZiel() != null) //wenn zielbudget vorhanden, dann ist es eine budget umbuchung
                    {
                        erfolgreich = budgetsUmbuchen(transaktion,transaktionWiederkehrend.getBetrag(),transaktionWiederkehrend.getBudget(),transaktionWiederkehrend.getBudgetZiel(), con);
                        if (!erfolgreich) return false;
                    }
                    else //ansonsten einfache budget buchung
                    {
                        BudgetBuchung budgetBuchung = erstelleBudgetBuchung(transaktion,transaktionWiederkehrend.getBudget(),transaktionWiederkehrend.getBetrag());
                        erfolgreich = budgetBuchungService.erstelle(budgetBuchung, con);
                        if (!erfolgreich) return false;
                    }
                }
            }

            tr.commit();
        }
        
        return true;
    }

    private Buchung erstelleBuchung(Transaktion transaktion, Konto konto, BigDecimal betrag) 
    {
        Buchung buchung = new Buchung();
        buchung.setTransaktion(transaktion);
        buchung.setKonto(konto);
        buchung.setBetrag(betrag);
        return buchung;
    }

    private BudgetBuchung erstelleBudgetBuchung(Transaktion transaktion, Budget budget, BigDecimal betrag) 
    {
        BudgetBuchung budgetBuchung = new BudgetBuchung();
        budgetBuchung.setTransaktion(transaktion);
        budgetBuchung.setBudget(budget);
        budgetBuchung.setBetrag(betrag);
        return budgetBuchung;
    }

    private Transaktion mapAufTransaktion(TransaktionWiederkehrend wiederkehrend) 
    {
        Transaktion transaktion = new Transaktion();
        transaktion.setBeschreibung(wiederkehrend.getBeschreibung());
        transaktion.setDatum(transaktionWiederkehrendService.mapBuchungstag(wiederkehrend.getZahlungstag()));
        transaktion.setPlanungsmonat(transaktionWiederkehrendService.mapPlanungsmonat(wiederkehrend.getPlanungsmonatVersatz()));
        transaktion.setTransaktionsart(wiederkehrend.getTransaktionsart());
        return transaktion;
    }

}
