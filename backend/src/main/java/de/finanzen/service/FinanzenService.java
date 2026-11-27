package de.finanzen.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.database.DatabaseTransaction;
import de.finanzen.model.Buchung;
import de.finanzen.model.BudgetBuchung;
import de.finanzen.model.Transaktion;
import de.finanzen.repository.BuchungRepository;
import de.finanzen.repository.BudgetBuchungRepository;
import de.finanzen.repository.TransaktionRepository;


public class FinanzenService 
{
    private static final Logger             LOGGER = Logger.getLogger(FinanzenService.class.getName());
    private final TransaktionRepository     transaktionRepository;
    private final BuchungRepository         buchungRepository;
    private final BudgetBuchungRepository   budgetbuchungRepository;
    
    public FinanzenService(TransaktionRepository transaktionRepository, BuchungRepository buchungRepository, BudgetBuchungRepository budgetbuchungRepository)
    {
        this.transaktionRepository      = transaktionRepository;
        this.buchungRepository          = buchungRepository;
        this.budgetbuchungRepository    = budgetbuchungRepository;
    }

    public boolean erstelleTransaktion(Transaktion transaktion, List<Buchung> buchungen, List<BudgetBuchung> budgetBuchungen) 
    {
        try (DatabaseTransaction transaction = new DatabaseTransaction())
        {
            Connection con = transaction.getConnection();
            
            transaktionRepository.save(transaktion, con);

        } 
        catch (SQLException ex) 
        {
            
        }
        
        return false;
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
