package de.finanzen.model;

import java.math.BigDecimal;

public class Notiz
{
    // table columns
    private int         notiz_id = 0;
    private String      text;
    private BigDecimal  betrag;
    private String      faellig;
    private boolean     geloescht = false;

    // constructors
    public Notiz() 
    {

    }

    // getters
    public int          getId()         { return this.notiz_id;     }
    public String       getText()       { return this.text;         }
    public BigDecimal   getBetrag()     { return this.betrag;       }
    public String       getFaellig()    { return this.faellig;      }
    public boolean      istGeloescht()  { return this.geloescht;    }

    // setters
    public void setId(int notiz_id)             { this.notiz_id = notiz_id;     }
    public void setText(String text)            { this.text = text;             }
    public void setBetrag(BigDecimal betrag)    { this.betrag = betrag;         }
    public void setFaellig(String faellig)      { this.faellig = faellig;       }
    public void setGeloescht(boolean geloescht) { this.geloescht = geloescht;   }

    public String toString() 
    {
        return "Konto [notiz_id=" + notiz_id + ", text=" + text + ", betrag=" + betrag.toString() + ", faellig=" + faellig + ", geloescht=" + geloescht + "]";
    }
}