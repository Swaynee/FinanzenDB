package de.finanzen.model;

import java.math.BigDecimal;

public class Buchung
{
    // table columns
    private long        buchung_id = 0;
    private Transaktion transaktion;
    private Konto       konto;
    private BigDecimal  betrag;

    // constructors
    public Buchung() 
    {

    }

    // getters
    public long         getId()             { return this.buchung_id;   }
    public Transaktion  getTransaktion()    { return this.transaktion;  }
    public Konto        getKonto()          { return this.konto;        }
    public BigDecimal   getBetrag()         { return this.betrag;       }

    // setters
    public void setId(long buchung_id)                  { this.buchung_id       = buchung_id;   }  
    public void setTransaktion(Transaktion transaktion) { this.transaktion      = transaktion;  }
    public void setKonto(Konto konto)                   { this.konto            = konto;        }
    public void setBetrag(BigDecimal betrag)            { this.betrag           = betrag;       }

}
