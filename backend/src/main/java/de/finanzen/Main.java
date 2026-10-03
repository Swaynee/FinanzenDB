package de.finanzen;

import java.sql.Connection;
import java.sql.SQLException;

import de.finanzen.database.*;
import de.finanzen.model.*;
import de.finanzen.repository.*;

public class Main 
{
    public static void main(String[] args) 
    {
        try
        {
            testKontoRepository();
        } 
        catch (Exception e) 
        {
            System.err.println("Failed to establish database connection: " + e.getMessage());
        }
    }

    public static void testKontoRepository() 
    {
        KontoRepository kontoRepo = new KontoRepository();
        try 
        {
            Konto konto = kontoRepo.getById(1);
            if (konto != null) 
            {
                System.out.println("Konto retrieved: " + konto.toString());
            } 
            else 
            {
                System.out.println("No Konto found with the given ID.");
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error retrieving Konto: " + e.getMessage());
        }
    }
}
