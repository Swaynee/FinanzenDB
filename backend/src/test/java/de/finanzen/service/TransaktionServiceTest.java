package de.finanzen.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransaktionServiceTest
{
    @Test
    void testAddition()
    {
        int ergebnis = 5 + 5;

        assertEquals(10, ergebnis);
    }

    @Test
    void testNull()
    {
        String text = null;

        assertNull(text);
    }

    @Test
    void testBoolean()
    {
        boolean aktiv = true;

        assertTrue(aktiv);
    }
}