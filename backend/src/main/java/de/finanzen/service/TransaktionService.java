package de.finanzen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.model.Transaktion;
import de.finanzen.repository.TransaktionRepository;

public class TransaktionService
{
    private static final Logger             LOGGER = Logger.getLogger(TransaktionService.class.getName());
    private final TransaktionRepository     transaktionRepository;

    public TransaktionService(TransaktionRepository transaktionRepository)
    {
        this.transaktionRepository = transaktionRepository;
    }

    /*
     * Gibt alle Transaktionen zurück.
     */
    public List<Transaktion> getAlle() throws SQLException
    {
        return transaktionRepository.getAlle();
    }

    /*
     * Sucht eine Transaktion anhand ihrer ID.
     * Gibt null zurück, wenn keine Transaktion gefunden wurde.
     */
    public Transaktion getById(long transaktion_id) throws SQLException
    {
        if (transaktion_id <= 0) throw new IllegalArgumentException("Die Transaktion-ID muss größer als 0 sein.");
        return transaktionRepository.getById(transaktion_id);
    }

    /*
     * Erstellt eine neue Transaktion.
     * Die Transaktion darf noch keine ID besitzen.
     */
    public boolean erstelle(Transaktion transaktion) throws SQLException
    {
        validiere(transaktion);

        if (transaktion.getId() != 0) throw new IllegalArgumentException("Eine neue Transaktion darf noch keine ID besitzen.");

        boolean erfolgreich = transaktionRepository.save(transaktion);
        if (erfolgreich) LOGGER.log(Level.INFO, "Transaktion erstellt: {0}", transaktion.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert eine bestehende Transaktion.
     */
    public boolean aktualisiere(Transaktion transaktion) throws SQLException
    {
        if (transaktion.getId() <= 0) throw new IllegalArgumentException("Die Transaktion besitzt keine gültige ID.");

        validiere(transaktion);

        Transaktion vorhanden = transaktionRepository.getById(transaktion.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = transaktionRepository.save(transaktion);
        if (erfolgreich) LOGGER.log(Level.INFO, "Transaktion aktualisiert: {0}", transaktion.getId());
        return erfolgreich;
    }

    /*
     * Löscht eine Transaktion aus der Datenbank.
     * Zugehörige Buchungen und Budgetbuchungen werden
     * durch ON DELETE CASCADE automatisch mitgelöscht.
     */
    public boolean loesche(long transaktion_id) throws SQLException
    {
        Transaktion transaktion = getById(transaktion_id);
        if (transaktion == null) return false;

        boolean erfolgreich = transaktionRepository.delete(transaktion);
        if (erfolgreich) LOGGER.log(Level.INFO, "Transaktion gel\u00f6scht: {0}", transaktion_id);
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen Transaktiondaten.
     */
    private void validiere(Transaktion transaktion) throws SQLException
    {
        if (transaktion == null)                                                                        throw new IllegalArgumentException("Die Transaktion darf nicht null sein.");
        if (transaktion.getDatum() == null)                                                             throw new IllegalArgumentException("Das Transaktionsdatum darf nicht leer sein.");
        if (transaktion.getPlanungsmonat() == null)                                                     throw new IllegalArgumentException("Der Transaktionsplanungsmonat darf nicht leer sein.");
        if (transaktion.getBeschreibung() == null || transaktion.getBeschreibung().trim().isEmpty())    throw new IllegalArgumentException("Der Transaktionsbeschreibung darf nicht leer sein.");
    
        if (transaktion.getTransaktionsart() == null)                                                   throw new IllegalArgumentException("Die Transaktion benötigt eine Transaktionsart.");
    }
}
