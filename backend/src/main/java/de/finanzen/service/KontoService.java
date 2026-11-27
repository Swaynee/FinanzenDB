
package de.finanzen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
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

    public List<Konto> getAlleAktiven() throws SQLException
    {
        return kontoRepository.getAlleAktiven();
    }

    public Konto getById(int konto_id) throws SQLException
    {
        if (konto_id <= 0) throw new IllegalArgumentException("Die Konto-ID muss größer als 0 sein.");
        return kontoRepository.getById(konto_id);
    }

    public boolean erstelle(Konto konto) throws SQLException
    {
        validiere(konto);

        if (konto.getId() != 0) throw new IllegalArgumentException("Ein neues Konto darf noch keine ID besitzen.");

        boolean erfolgreich = kontoRepository.save(konto);
        if (erfolgreich) LOGGER.log(Level.INFO, "Konto erstellt: {0}", konto.getId());
        return erfolgreich;
    }

    public boolean aktualisiere(Konto konto) throws SQLException
    {
        validiere(konto);

        if (konto.getId() <= 0) throw new IllegalArgumentException("Das Konto besitzt keine gültige ID.");

        Konto vorhanden = kontoRepository.getById(konto.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = kontoRepository.save(konto);
        if (erfolgreich) LOGGER.log(Level.INFO, "Konto aktualisiert: {0}", konto.getId());
        return erfolgreich;
    }

    public boolean deaktiviere(int konto_id) throws SQLException
    {
        Konto konto = getById(konto_id);

        if (konto == null) return false;
        if (!konto.istAktiv()) return false;

        boolean erfolgreich = kontoRepository.delete(konto);
        if (erfolgreich) LOGGER.log(Level.INFO, "Konto deaktiviert: {0}", konto_id);
        return erfolgreich;
    }

    private void validiere(Konto konto)
    {
        if (konto == null)                                                          throw new IllegalArgumentException("Das Konto darf nicht null sein.");
        if (konto.getName() == null || konto.getName().trim().isEmpty())            throw new IllegalArgumentException("Der Kontoname darf nicht leer sein.");
        if (konto.getKontoArt() == null || konto.getKontoArt().trim().isEmpty())    throw new IllegalArgumentException("Die Kontoart darf nicht leer sein.");
    }
}
