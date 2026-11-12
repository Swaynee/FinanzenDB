package de.finanzen.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.finanzen.model.Transaktion;
import de.finanzen.model.Transaktionsart;
import de.finanzen.repository.TransaktionRepository;

public class TransaktionServiceTest
{
    private TransaktionRepository transaktionRepository;
    private TransaktionService transaktionService;

    @BeforeEach
    void setUp()
    {
        transaktionRepository = mock(TransaktionRepository.class);
        transaktionService = new TransaktionService(transaktionRepository);
    }

    @Test
    void testGetByIdUngueltigeId()
    {
        assertThrows(IllegalArgumentException.class, () ->
        {
            transaktionService.getById(0);
        });

        assertThrows(IllegalArgumentException.class, () ->
        {
            transaktionService.getById(-1);
        });
    }

    @Test
    void testErstelleNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
        {
            transaktionService.erstelle(null);
        });
    }

    @Test
    void testAktualisiereNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
        {
            transaktionService.aktualisiere(null);
        });
    }

    @Test
    void testErstelleErfolgreich() throws SQLException
    {
        // Transaktionsart erstellen
        Transaktionsart art = new Transaktionsart();
        art.setId(1);
        art.setName("Einnahme");

        // Transaktion erstellen
        Transaktion transaktion = new Transaktion();
        transaktion.setTransaktionsart(art);
        transaktion.setDatum(LocalDate.of(2026, 11, 12));
        transaktion.setPlanungsmonat(LocalDate.of(2026, 11, 1));
        transaktion.setBeschreibung("Testtransaktion");

        // Repository-Verhalten simulieren
        when(transaktionRepository.save(transaktion)).thenReturn(true);

        // Service-Methode aufrufen
        boolean erfolgreich = transaktionService.erstelle(transaktion);

        // Ergebnis überprüfen
        assertTrue(erfolgreich);
        verify(transaktionRepository).save(transaktion);
    }
}