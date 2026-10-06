package de.finanzen.model;

public class Konto 
{
    //table columns
    private int     konto_id = 0;

    private String  name;
    private String  konto_art;
    private boolean aktiv = true;
    private String  iban;
    private String  bic;
    private String  bank;

    //constructors
    public Konto() 
    {
        
    }

    //getters
    public int      getId()         { return this.konto_id;     }
    public String   getName()       { return this.name;         }
    public String   getKontoArt()   { return this.konto_art;    }
    public boolean  istAktiv()      { return this.aktiv;        }
    public String   getIban()       { return this.iban;         }
    public String   getBic()        { return this.bic;          }
    public String   getBank()       { return this.bank;         }

    //setters
    public void setId(int konto_id)             { this.konto_id     = konto_id;    }
    public void setName(String name)            { this.name         = name;        }
    public void setKontoArt(String konto_art)   { this.konto_art    = konto_art;   }
    public void setAktiv(boolean aktiv)         { this.aktiv        = aktiv;       }
    public void setIban(String iban)            { this.iban         = iban;        }
    public void setBic(String bic)              { this.bic          = bic;         }
    public void setBank(String bank)            { this.bank         = bank;        }

    public String toString() 
    {
        return "Konto [konto_id=" + konto_id + ", name=" + name + ", konto_art=" + konto_art + ", aktiv=" + aktiv
                + ", iban=" + iban + ", bic=" + bic + ", bank=" + bank + "]";
    }
    
}
