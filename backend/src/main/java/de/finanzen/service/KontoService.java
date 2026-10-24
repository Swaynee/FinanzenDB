
package de.finanzen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import de.finanzen.model.Konto;
import de.finanzen.repository.KontoRepository;

public class KontoService
{
    private static final Logger     LOGGER = Logger.getLogger(KontoService.class.getName());
    private final KontoRepository   kontoRepository;

    public KontoService(KontoRepository kontoRepository)
    {
        this.kontoRepository = kontoRepository;
    }

    /*
     * Gibt alle aktiven Konten zurück.
     */
    public List<Konto> getAlleAktiven() throws SQLException
    {
        return kontoRepository.getAlleAktiven();
    }

    /*
     * Sucht ein Konto anhand seiner ID.
     * Gibt null zurück, wenn kein Konto gefunden wurde.
     */
    public Konto getById(int konto_id) throws SQLException
    {
        if (konto_id <= 0)                                                          throw new IllegalArgumentException("Die Konto-ID muss größer als 0 sein.");
        return kontoRepository.getById(konto_id);
    }

    /*
     * Erstellt ein neues Konto.
     * Das Konto darf noch keine ID besitzen.
     */
    public boolean erstelle(Konto konto) throws SQLException
    {
        validiere(konto);

        if (konto.getId() != 0)                                                     throw new IllegalArgumentException("Ein neues Konto darf noch keine ID besitzen.");

        boolean erfolgreich = kontoRepository.save(konto);
        if (erfolgreich) LOGGER.info("Konto erstellt: " + konto.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert ein bestehendes Konto.
     */
    public boolean aktualisiere(Konto konto) throws SQLException
    {
        validiere(konto);

        if (konto.getId() <= 0)                                                     throw new IllegalArgumentException("Das Konto besitzt keine gültige ID.");

        Konto vorhanden = kontoRepository.getById(konto.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = kontoRepository.save(konto);
        if (erfolgreich) LOGGER.info("Konto aktualisiert: " + konto.getId());
        return erfolgreich;
    }

    /*
     * Deaktiviert ein Konto, ohne es aus der Datenbank zu löschen.
     */
    public boolean deaktiviere(int konto_id) throws SQLException
    {
        Konto konto = getById(konto_id);

        if (konto == null) return false;
        if (!konto.istAktiv()) return false;

        boolean erfolgreich = kontoRepository.delete(konto);
        if (erfolgreich) LOGGER.info("Konto deaktiviert: " + konto_id);
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen Kontodaten.
     */
    private void validiere(Konto konto)
    {
        if (konto == null)                                                          throw new IllegalArgumentException("Das Konto darf nicht null sein.");
        if (konto.getName() == null || konto.getName().trim().isEmpty())            throw new IllegalArgumentException("Der Kontoname darf nicht leer sein.");
        if (konto.getKontoArt() == null || konto.getKontoArt().trim().isEmpty())    throw new IllegalArgumentException("Die Kontoart darf nicht leer sein.");
    }
}
