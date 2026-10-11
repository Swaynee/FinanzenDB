package de.finanzen.model;

import java.time.LocalDate;

public class Transaktion
{
    // table columns
    private long            transaktion_id = 0;
    private Transaktionsart transaktionsart;
    private LocalDate       datum;
    private LocalDate       planungsmonat;
    private String          beschreibung;

    // constructors
    public Transaktion()
    {

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
