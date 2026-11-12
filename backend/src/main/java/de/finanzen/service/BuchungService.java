package de.finanzen.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.finanzen.model.Buchung;
import de.finanzen.model.Transaktionsart;
import de.finanzen.repository.BuchungRepository;

public class BuchungService
{
    private static final Logger             LOGGER = Logger.getLogger(BuchungService.class.getName());
    private final BuchungRepository         buchungRepository;
    private final KontoService              kontoService;
    private final TransaktionService        transaktionService;
    private final TransaktionsartService    transaktionsartService;


    public BuchungService(BuchungRepository buchungRepository, KontoService kontoService, TransaktionService transaktionService,  TransaktionsartService transaktionsartService)
    {
        this.buchungRepository          = buchungRepository;
        this.kontoService               = kontoService;
        this.transaktionService         = transaktionService;
        this.transaktionsartService     = transaktionsartService;
    }

    /*
     * Gibt alle Buchungen zurück.
     */
    public List<Buchung> getAlle() throws SQLException
    {
        return buchungRepository.getAlle();
    }

    /*
     * Sucht eine Buchung anhand ihrer ID.
     * Gibt null zurück, wenn keine Buchung gefunden wurde.
     */
    public Buchung getById(long buchung_id) throws SQLException
    {
        if (buchung_id <= 0) throw new IllegalArgumentException("Die Buchung-ID muss größer als 0 sein.");
        return buchungRepository.getById(buchung_id);
    }

    /*
     * Erstellt eine neue Buchung.
     * Die Buchung darf noch keine ID besitzen.
     */
    public boolean erstelle(Buchung buchung) throws SQLException
    {
        validiere(buchung);
        if (buchung.getId() != 0) throw new IllegalArgumentException("Eine neue Buchung darf noch keine ID besitzen.");

        //normalisiere betrag
        BigDecimal normalisierterBetrag = normalisiereBuchungsbetrag(buchung.getBetrag(),buchung.getTransaktion().getTransaktionsart());
        buchung.setBetrag(normalisierterBetrag);

        boolean erfolgreich = buchungRepository.save(buchung);
        if (erfolgreich) LOGGER.log(Level.INFO, "Buchung erstellt: {0}", buchung.getId());
        return erfolgreich;
    }

    /*
     * Aktualisiert eine bestehende Buchung.
     */
    public boolean aktualisiere(Buchung buchung) throws SQLException
    {
        if (buchung.getId() <= 0) throw new IllegalArgumentException("Die Buchung besitzt keine gültige ID.");
        validiere(buchung);

        //normalisiere betrag
        BigDecimal normalisierterBetrag = normalisiereBuchungsbetrag(buchung.getBetrag(),buchung.getTransaktion().getTransaktionsart());
        buchung.setBetrag(normalisierterBetrag);

        Buchung vorhanden = buchungRepository.getById(buchung.getId());
        if (vorhanden == null) return false;

        boolean erfolgreich = buchungRepository.save(buchung);
        if (erfolgreich) LOGGER.log(Level.INFO, "Buchung aktualisiert: {0}", buchung.getId());
        return erfolgreich;
    }

    /*
     * Löscht eine Buchung aus der Datenbank.
     */
    public boolean loesche(long buchung_id) throws SQLException
    {
        Buchung buchung = getById(buchung_id);
        if (buchung == null) return false;

        boolean erfolgreich = buchungRepository.delete(buchung);
        if (erfolgreich) LOGGER.log(Level.INFO, "Buchung gel\u00f6scht: {0}", buchung_id);
        return erfolgreich;
    }

    /*
     * Prüft die fachlich erforderlichen Buchungdaten.
     */
    private void validiere(Buchung buchung) throws SQLException
    {
        if (buchung == null)                                                                        throw new IllegalArgumentException("Die Buchung darf nicht null sein.");
        if ((buchung.getBetrag() == null) || (buchung.getBetrag().compareTo(BigDecimal.ZERO) == 0)) throw new IllegalArgumentException("Der Buchungsbetrag darf nicht leer sein.");

        if (buchung.getKonto() == null)                                                             throw new IllegalArgumentException("Das Buchungskonto darf nicht null sein");
        if (buchung.getTransaktion() == null)                                                       throw new IllegalArgumentException("Die Transaktion der Buchung darf nicht null sein");
    }


    /*
     * Normalisiert den Betrag
     *
     * -Einnahme: absoluter Wert
     * -Ausgabe: absoluter Wert negiert
     * 
     * Der Rest wird durchgelassen.
     */
    private BigDecimal normalisiereBuchungsbetrag(BigDecimal buchungsBetrag, Transaktionsart transaktionsart) throws SQLException
    {
        BigDecimal  normalisiert    = buchungsBetrag.abs();

        //Ausgabe
        if (transaktionsart.getId() == 3) return normalisiert.negate();

        return normalisiert;
    }
}
