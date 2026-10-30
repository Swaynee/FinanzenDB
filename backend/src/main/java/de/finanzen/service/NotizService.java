package de.finanzen.service;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.model.Notiz;
import de.finanzen.repository.NotizRepository;

public class NotizService
{
    private static final Logger     LOGGER = Logger.getLogger(NotizService.class.getName());
    private final NotizRepository   notizRepository;

    public NotizService(NotizRepository notizRepository)
    {
        this.notizRepository = notizRepository;
    }

    /*
     * Gibt alle Notizen zurück.
     */
    public List<Notiz> getAlle() throws SQLException
    {
        return notizRepository.getAlle();
    }

    /*
     * Sucht eine Notiz anhand ihrer ID.
     * Gibt null zurück, wenn keine Notiz gefunden wurde.
     */
    public Notiz getById(int notiz_id) throws SQLException
    {
        if (notiz_id <= 0) throw new IllegalArgumentException("Die Notiz-ID muss größer als 0 sein.");
        return notizRepository.getById(notiz_id);
    }

    /*
     * Erstellt eine neue Notiz.
     * Die Notiz darf noch keine ID besitzen.
     */
    @SuppressWarnings("LoggerStringConcat")
    public boolean erstelle(Notiz notiz) throws SQLException
    {
        validiere(notiz);

        if (notiz.getId() != 0) throw new IllegalArgumentException("Eine neue Notiz darf noch keine ID besitzen.");

        boolean erfolgreich = notizRepository.save(notiz);
        if (erfolgreich) LOGGER.info("Notiz erstellt: " + notiz.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert eine bestehende Notiz.
     */
    public boolean aktualisiere(Notiz notiz) throws SQLException
    {
        validiere(notiz);

        if (notiz.getId() <= 0) throw new IllegalArgumentException("Die Notiz besitzt keine gültige ID.");

        Notiz vorhanden = notizRepository.getById(notiz.getId());

        if (vorhanden == null) return false;

        boolean erfolgreich = notizRepository.save(notiz);
        if (erfolgreich) LOGGER.log(Level.INFO, "Notiz aktualisiert: {0}", notiz.getId());
        return erfolgreich;
    }

    /*
     * Markiert eine Notiz als gelöscht.
     */
    public boolean loesche(Notiz notiz) throws SQLException
    {
        if (notiz == null)      throw new IllegalArgumentException("Die Notiz darf nicht null sein.");
        if (notiz.getId() <= 0) throw new IllegalArgumentException("Die Notiz besitzt keine gültige ID.");
        if (notiz.istGeloescht()) return false;

        boolean erfolgreich = notizRepository.delete(notiz);
        if (erfolgreich) LOGGER.log(Level.INFO, "Notiz geloescht: {0}", notiz.getId());
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen Notizdaten.
     */
    private void validiere(Notiz notiz)
    {
        if (notiz == null) throw new IllegalArgumentException("Die Notiz darf nicht null sein.");
        if (notiz.getText() == null || notiz.getText().trim().isEmpty()) throw new IllegalArgumentException("Der Notiztext darf nicht leer sein.");
    }
}
