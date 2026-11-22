
package de.finanzen.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.model.TransaktionWiederkehrend;
import de.finanzen.repository.TransaktionWiederkehrendRepository;

public class TransaktionWiederkehrendService
{
    private static final Logger                         LOGGER = Logger.getLogger(TransaktionWiederkehrendService.class.getName());
    private final TransaktionWiederkehrendRepository    transaktionWiederkehrendRepository;

    public TransaktionWiederkehrendService(TransaktionWiederkehrendRepository transaktionWiederkehrendRepository)
    {
        this.transaktionWiederkehrendRepository = transaktionWiederkehrendRepository;
    }

    
    public List<TransaktionWiederkehrend> getAlle() throws SQLException
    {
        return transaktionWiederkehrendRepository.getAlle();
    }

    public TransaktionWiederkehrend getById(int transaktionWiederkehrend_id) throws SQLException
    {
        if (transaktionWiederkehrend_id <= 0) throw new IllegalArgumentException("Die TransaktionWiederkehrend-ID muss größer als 0 sein.");
        return transaktionWiederkehrendRepository.getById(transaktionWiederkehrend_id);
    }

    public boolean erstelle(TransaktionWiederkehrend transaktionWiederkehrend) throws SQLException
    {
        validiere(transaktionWiederkehrend);

        if (transaktionWiederkehrend.getId() != 0) throw new IllegalArgumentException("Eine neue TransaktionWiederkehrend darf noch keine ID besitzen.");

        boolean erfolgreich = transaktionWiederkehrendRepository.save(transaktionWiederkehrend);
        if (erfolgreich) LOGGER.log(Level.INFO, "TransaktionWiederkehrend erstellt: {0}", transaktionWiederkehrend.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert eine bestehende TransaktionWiederkehrend.
     */
    public boolean aktualisiere(TransaktionWiederkehrend transaktionWiederkehrend) throws SQLException
    {
        validiere(transaktionWiederkehrend);

        if (transaktionWiederkehrend.getId() <= 0) throw new IllegalArgumentException("Die TransaktionWiederkehrend besitzt keine gültige ID.");

        TransaktionWiederkehrend vorhanden = transaktionWiederkehrendRepository.getById(transaktionWiederkehrend.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = transaktionWiederkehrendRepository.save(transaktionWiederkehrend);
        if (erfolgreich) LOGGER.log(Level.INFO, "TransaktionWiederkehrend aktualisiert: {0}", transaktionWiederkehrend.getId());
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen TransaktionWiederkehrenddaten.
     */
    private void validiere(TransaktionWiederkehrend transaktionWiederkehrend)
    {
        if (transaktionWiederkehrend == null)                                                                                           throw new IllegalArgumentException("Die TransaktionWiederkehrend darf nicht null sein.");
        if (transaktionWiederkehrend.getBeschreibung() == null || transaktionWiederkehrend.getBeschreibung().trim().isEmpty())          throw new IllegalArgumentException("Der TransaktionWiederkehrendsbeschreibung darf nicht leer sein.");
        if ((transaktionWiederkehrend.getBetrag() == null) || (transaktionWiederkehrend.getBetrag().compareTo(BigDecimal.ZERO) == 0))   throw new IllegalArgumentException("Der TransaktionWiederkehrendsbetrag darf nicht leer sein.");
        if (transaktionWiederkehrend.getZahlungstag() == 0)                                                                             throw new IllegalArgumentException("Der TransaktionWiederkehrendzahlungstag darf nicht leer sein.");
        if (transaktionWiederkehrend.getGueltigAb() == null)                                                                            throw new IllegalArgumentException("Das TransaktionWiederkehrend Gueltig ab Datum darf nicht leer sein.");
        if (transaktionWiederkehrend.getGueltigBis() == null)                                                                           throw new IllegalArgumentException("Das TransaktionWiederkehrend Gueltig bis Datum darf nicht leer sein.");

        if (transaktionWiederkehrend.getTransaktionsart() == null)                                                                      throw new IllegalArgumentException("Die TransaktionWiederkehrend benötigt eine Transaktionsart.");
    }
}
