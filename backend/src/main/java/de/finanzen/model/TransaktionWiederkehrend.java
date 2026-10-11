package de.finanzen.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransaktionWiederkehrend
{
    // table columns
    private int             transaktion_wiederkehrend_id;
    private Transaktionsart transaktionsart;
    private Konto           konto;
    private Konto           konto_ziel;
    private Budget          budget;
    private Budget          budget_ziel;
    private String          beschreibung;
    private BigDecimal      betrag;
    private int             zahlungstag;
    private int             planungsmonat_versatz;
    private LocalDate       gueltig_ab;
    private LocalDate       gueltig_bis;

    // constructors
    public TransaktionWiederkehrend() 
    {

    }

    // getters
    public int getId() { return this.transaktion_wiederkehrend_id; }

    public Transaktionsart  getTransaktionsart()        { return this.transaktionsart;          }
    public Konto            getKonto()                  { return this.konto;                    }
    public Konto            getKontoZiel()              { return this.konto_ziel;               }
    public Budget           getBudget()                 { return this.budget;                   }
    public Budget           getBudgetZiel()             { return this.budget_ziel;              }
    public String           getBeschreibung()           { return this.beschreibung;             }
    public BigDecimal       getBetrag()                 { return this.betrag;                   }
    public int              getZahlungstag()            { return this.zahlungstag;              }
    public int              getPlanungsmonatVersatz()   { return this.planungsmonat_versatz;    }
    public LocalDate        getGueltigAb()              { return this.gueltig_ab;               }
    public LocalDate        getGueltigBis()             { return this.gueltig_bis;              }

    // setters
    public void setId(int transaktion_wiederkehrend_id)             { this.transaktion_wiederkehrend_id = transaktion_wiederkehrend_id; }
    public void setTransaktionsart(Transaktionsart transaktionsart) { this.transaktionsart              = transaktionsart;              }
    public void setKonto(Konto konto)                               { this.konto                        = konto;                        }
    public void setKontoZiel(Konto konto_ziel)                      { this.konto_ziel                   = konto_ziel;                   }
    public void setBudget(Budget budget)                            { this.budget                       = budget;                       }
    public void setBudgetZiel(Budget budget_ziel)                   { this.budget_ziel                  = budget_ziel;                  }
    public void setBeschreibung(String beschreibung)                { this.beschreibung                 = beschreibung;                 }
    public void setBetrag(BigDecimal betrag)                        { this.betrag                       = betrag;                       }
    public void setZahlungstag(int zahlungstag)                     { this.zahlungstag                  = zahlungstag;                  }
    public void setPlanungsmonatVersatz(int planungsmonat_versatz)  { this.planungsmonat_versatz        = planungsmonat_versatz;        }
    public void setGueltigAb(LocalDate gueltig_ab)                  { this.gueltig_ab                   = gueltig_ab;                   }
    public void setGueltigBis(LocalDate gueltig_bis)                { this.gueltig_bis                  = gueltig_bis;                  }

    @Override 
    public String toString()
    {
    return "TransaktionWiederkehrend [ " +
            "transaktion_wiederkehrend_id=" + transaktion_wiederkehrend_id +
            ", transaktionsart=" +  ((transaktionsart != null)  ? transaktionsart.getName() : null) +
            ", konto=" +            ((konto != null)            ? konto.getName()           : null) +
            ", kontoZiel=" +        ((konto_ziel != null)       ? konto_ziel.getName()      : null) +
            ", budget=" +           ((budget != null)           ? budget.getName()          : null) +
            ", budgetZiel=" +       ((budget_ziel != null)      ? budget_ziel.getName()     : null) +
            ", beschreibung='" + beschreibung + '\'' +
            ", betrag=" + betrag.toString() +
            ", zahlungstag=" + zahlungstag +
            ", planungsmonatVersatz=" + planungsmonat_versatz +
            ", gueltigAb=" + gueltig_ab.toString() +
            ", gueltigBis=" + gueltig_bis.toString() +
            " ]";
    }

}