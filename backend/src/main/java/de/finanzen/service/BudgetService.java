
package de.finanzen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.model.Budget;
import de.finanzen.repository.BudgetRepository;

public class BudgetService
{
    private static final Logger     LOGGER = Logger.getLogger(BudgetService.class.getName());
    private final BudgetRepository  budgetRepository;

    public BudgetService(BudgetRepository budgetRepository)
    {
        this.budgetRepository = budgetRepository;
    }

    public List<Budget> getAlleAktiven() throws SQLException
    {
        return budgetRepository.getAlleAktiven();
    }

    public Budget getById(int budget_id) throws SQLException
    {
        if (budget_id <= 0) throw new IllegalArgumentException("Die Budget-ID muss größer als 0 sein.");
        return budgetRepository.getById(budget_id);
    }

    public boolean erstelle(Budget budget) throws SQLException
    {
        validiere(budget);

        if (budget.getId() != 0) throw new IllegalArgumentException("Ein neues Budget darf noch keine ID besitzen.");

        boolean erfolgreich = budgetRepository.save(budget);
        if (erfolgreich) LOGGER.log(Level.INFO,"Budget erstellt: {0}",budget.getId());
        return erfolgreich;
    }

    public boolean aktualisiere(Budget budget) throws SQLException
    {
        validiere(budget);

        if (budget.getId() <= 0) throw new IllegalArgumentException("Das Budget besitzt keine gültige ID.");

        Budget vorhanden = budgetRepository.getById(budget.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = budgetRepository.save(budget);
        if (erfolgreich) LOGGER.log(Level.INFO, "Budget aktualisiert: {0}", budget.getId());
        return erfolgreich;
    }

    public boolean deaktiviere(int budget_id) throws SQLException
    {
        Budget budget = getById(budget_id);

        if (budget == null) return false;
        if (!budget.istAktiv()) return false;

        boolean erfolgreich = budgetRepository.delete(budget);
        if (erfolgreich) LOGGER.log(Level.INFO, "Budget deaktiviert: {0}", budget_id);
        return erfolgreich;
    }

    private void validiere(Budget budget)
    {
        if (budget == null)                                                 throw new IllegalArgumentException("Das Budget darf nicht null sein.");
        if (budget.getName() == null || budget.getName().trim().isEmpty())  throw new IllegalArgumentException("Der Budgetname darf nicht leer sein.");
    }
}
