package de.finanzen;

import java.util.logging.Logger;

import de.finanzen.logging.LoggerConfig;
import de.finanzen.model.Konto;
import de.finanzen.repository.KontoRepository;

public class Main 
{
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) 
    {
        try
        {
            LoggerConfig.setup();

            testKontoRepository();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
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
            /*
            List<Konto> aktiveKonten = kontoRepo.getAlleAktiven();
            if (aktiveKonten.isEmpty()) 
            {
                System.out.println("No active Konten found.");
            } 
            else 
            {
                System.out.println("Active Konten:");
                for (Konto konto : aktiveKonten) 
                {
                    System.out.println(konto.toString());
                }
            } 

            Konto neuesKonto = new Konto();
            neuesKonto.setName("Testkonto");
            neuesKonto.setKontoArt("test");
            neuesKonto.setIban("DE12 3456 7890 1234 5678 90");
            neuesKonto.setBic("TESTBIC");
            boolean insertSuccess = kontoRepo.insert(neuesKonto);
            if (insertSuccess)
            {
                System.out.println("New Konto inserted successfully.");
            } 
            else 
            {
                System.out.println("Failed to insert new Konto.");
            } 

            konto.setName("Updated Konto Name");
            boolean updateSuccess = kontoRepo.update(konto);
            if (updateSuccess) 
            {
                System.out.println("Konto updated successfully.");
            } 
            else 
            {
                System.out.println("Failed to update Konto.");
            }

            boolean deleteSuccess = kontoRepo.delete(konto);
            if (deleteSuccess) 
            {
                System.out.println("Konto deleted successfully.");
            } 
            else 
            {
                System.out.println("Failed to delete Konto.");
            } */

        } 
        catch (Exception e) 
        {
            System.err.println("Error retrieving Konto: " + e.getMessage());
        }
    }
}
