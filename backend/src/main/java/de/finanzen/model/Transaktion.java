package de.finanzen.model;

import java.time.LocalDate;

public class Transaktion
{
    // table columns
    private long            transaktion_id = 0;
    private Transaktionsart transaktionsart;

    /// tatsächlicher buchungstag
    private LocalDate       datum;

    /// planungsmonat, um differenzen des monats in dem abgebucht wird auszugleichen
    /// z.B. Strom für September wird erst am 3.Oktober abgebucht -> planungsmonat september, buchungstag 03.10. 
    private LocalDate       planungsmonat;
    private String          beschreibung;

    // constructors
    public Transaktion()
    {

    }

    public Transaktion(TransaktionWiederkehrend wiederkehrend) 
    {
        LocalDate heute = LocalDate.now();
        LocalDate planungsMonat;
        LocalDate buchungsTag;

        //letzter tag des monats, falls zahlungstag größer ist (z.B. 30 im Februar -> 28/29)
        int zahlungstagAbgesichert = Math.min(wiederkehrend.getZahlungstag(),heute.lengthOfMonth());
        
        //planungsmonat anhand von heute und versatz berechnen (siehe transaktion/transaktionwiederkehrend felder)
        planungsMonat = heute.plusDays(wiederkehrend.getPlanungsmonatVersatz());

        //buchungstag anhand vom aktuellen monat mit zahlungstag als tag festlegen
        buchungsTag = LocalDate.of(heute.getYear(),heute.getMonth(),zahlungstagAbgesichert);

        this.beschreibung       = wiederkehrend.getBeschreibung();
        this.datum              = buchungsTag;
        this.planungsmonat      = planungsMonat;
        this.transaktionsart    = wiederkehrend.getTransaktionsart();
    }

    // getters
    public long             getId()                 { return this.transaktion_id;   }
    public Transaktionsart  getTransaktionsart()    { return this.transaktionsart;  }
    public LocalDate        getDatum()              { return this.datum;            }
    public LocalDate        getPlanungsmonat()      { return this.planungsmonat;    }
    public String           getBeschreibung()       { return this.beschreibung;     }

    // setters
    public void setId(long transaktion_id)                          { this.transaktion_id   = transaktion_id;   }
    public void setTransaktionsart(Transaktionsart transaktionsart) { this.transaktionsart  = transaktionsart;  }
    public void setDatum(LocalDate datum)                           { this.datum            = datum;            }
    public void setPlanungsmonat(LocalDate planungsmonat)           { this.planungsmonat    = planungsmonat;    }
    public void setBeschreibung(String beschreibung)                { this.beschreibung     = beschreibung;     }
}
