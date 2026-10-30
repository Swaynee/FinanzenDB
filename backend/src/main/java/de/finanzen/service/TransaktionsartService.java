
package de.finanzen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.model.Transaktionsart;
import de.finanzen.repository.TransaktionsartRepository;

public class TransaktionsartService
{
    private static final Logger                 LOGGER = Logger.getLogger(KontoService.class.getName());
    private final TransaktionsartRepository     transaktionsartRepository;

    public TransaktionsartService(TransaktionsartRepository transaktionsartRepository)
    {
        this.transaktionsartRepository = transaktionsartRepository;
    }

    /*
     * Gibt alle aktiven Konten zurück.
     */
    public List<Transaktionsart> getAlle() throws SQLException
    {
        return transaktionsartRepository.getAlle();
    }

    /*
     * Sucht eine Transaktionsart anhand seiner ID.
     * Gibt null zurück, wenn keine Transaktionsart gefunden wurde.
     */
    public Transaktionsart getById(int transaktionsart_id) throws SQLException
    {
        if (transaktionsart_id <= 0) throw new IllegalArgumentException("Die Transaktionsart-ID muss größer als 0 sein.");
        return transaktionsartRepository.getById(transaktionsart_id);
    }

    /*
     * Erstellt eine neue Transaktionsart.
     * Die Transaktionsart darf noch keine ID besitzen.
     */
    public boolean erstelle(Transaktionsart transaktionsart) throws SQLException
    {
        validiere(transaktionsart);

        if (transaktionsart.getId() != 0) throw new IllegalArgumentException("Eine neue Transaktionsart darf noch keine ID besitzen.");

        boolean erfolgreich = transaktionsartRepository.save(transaktionsart);
        if (erfolgreich) LOGGER.info("Transaktionsart erstellt: " + transaktionsart.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert eine bestehende Transaktionsart.
     */
    public boolean aktualisiere(Transaktionsart transaktionsart) throws SQLException
    {
        validiere(transaktionsart);

        if (transaktionsart.getId() <= 0) throw new IllegalArgumentException("Das Konto besitzt keine gültige ID.");

        Transaktionsart vorhanden = transaktionsartRepository.getById(transaktionsart.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = transaktionsartRepository.save(transaktionsart);
        if (erfolgreich) LOGGER.info("Transaktionsart aktualisiert: " + transaktionsart.getId());
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen Kontodaten.
     */
    private void validiere(Transaktionsart transaktionsart)
    {
        if (transaktionsart == null)                                                          throw new IllegalArgumentException("Die Transaktionsart darf nicht null sein.");
        if (transaktionsart.getName() == null || transaktionsart.getName().trim().isEmpty())  throw new IllegalArgumentException("Der Transaktionsartname darf nicht leer sein.");
    }
}
