package de.finanzen.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.model.BudgetBuchung;
import de.finanzen.repository.BudgetBuchungRepository;

public class BudgetBuchungService
{
    private static final Logger             LOGGER = Logger.getLogger(BudgetBuchungService.class.getName());
    private final BudgetBuchungRepository   budgetBuchungRepository;

    public BudgetBuchungService(BudgetBuchungRepository budgetBuchungRepository)
    {
        this.budgetBuchungRepository    = budgetBuchungRepository;
    }

    /*
     * Gibt alle BudgetBuchungen zurück.
     */
    public List<BudgetBuchung> getAlle() throws SQLException
    {
        return budgetBuchungRepository.getAlle();
    }

    /*
     * Sucht eine BudgetBuchung anhand ihrer ID.
     * Gibt null zurück, wenn keine BudgetBuchung gefunden wurde.
     */
    public BudgetBuchung getById(long budgetBuchung_id) throws SQLException
    {
        if (budgetBuchung_id <= 0) throw new IllegalArgumentException("Die BudgetBuchung-ID muss größer als 0 sein.");
        return budgetBuchungRepository.getById(budgetBuchung_id);
    }

    /*
     * Erstellt eine neue BudgetBuchung.
     * Die BudgetBuchung darf noch keine ID besitzen.
     */
    public boolean erstelle(BudgetBuchung budgetBuchung) throws SQLException
    {
        validiere(budgetBuchung);
        if (budgetBuchung.getId() != 0) throw new IllegalArgumentException("Eine neue BudgetBuchung darf noch keine ID besitzen.");

        boolean erfolgreich = budgetBuchungRepository.save(budgetBuchung);
        if (erfolgreich) LOGGER.log(Level.INFO, "BudgetBuchung erstellt: {0}", budgetBuchung.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert eine bestehende BudgetBuchung.
     */
    public boolean aktualisiere(BudgetBuchung budgetBuchung) throws SQLException
    {
        if (budgetBuchung.getId() <= 0) throw new IllegalArgumentException("Die BudgetBuchung besitzt keine gültige ID.");
        validiere(budgetBuchung);

        BudgetBuchung vorhanden = budgetBuchungRepository.getById(budgetBuchung.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = budgetBuchungRepository.save(budgetBuchung);
        if (erfolgreich) LOGGER.log(Level.INFO, "BudgetBuchung aktualisiert: {0}", budgetBuchung.getId());
        return erfolgreich;
    }

    /*
     * Löscht eine BudgetBuchung aus der Datenbank.
     */
    public boolean loesche(long budgetBuchung_id) throws SQLException
    {
        BudgetBuchung budgetBuchung = getById(budgetBuchung_id);
        if (budgetBuchung == null) return false;

        boolean erfolgreich = budgetBuchungRepository.delete(budgetBuchung);
        if (erfolgreich) LOGGER.log(Level.INFO, "BudgetBuchung gel\u00f6scht: {0}", budgetBuchung_id);
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen BudgetBuchungdaten.
     */
    private void validiere(BudgetBuchung budgetBuchung) throws SQLException
    {
        if (budgetBuchung == null)                                                                              throw new IllegalArgumentException("Die BudgetBuchung darf nicht null sein.");
        if ((budgetBuchung.getBetrag() == null) || (budgetBuchung.getBetrag().compareTo(BigDecimal.ZERO) == 0)) throw new IllegalArgumentException("Der BudgetBuchungsbetrag darf nicht leer sein.");

        if (budgetBuchung.getBudget() == null)                                                                  throw new IllegalArgumentException("Das BudgetBuchungsbudget darf nicht null sein");
        if (budgetBuchung.getTransaktion() == null)                                                             throw new IllegalArgumentException("Die Transaktion der BudgetBuchung darf nicht null sein");
    }
}
