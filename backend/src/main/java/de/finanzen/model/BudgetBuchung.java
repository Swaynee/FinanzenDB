package de.finanzen.model;

import java.math.BigDecimal;

public class BudgetBuchung
{
    // table columns
    private long        budget_buchung_id;
    private Transaktion transaktion;
    private Budget      budget;
    private BigDecimal  betrag;

    // constructors
    public BudgetBuchung() 
    {

    }

    // getters
    public long         getId()             { return this.budget_buchung_id;    }
    public Transaktion  getTransaktion()    { return this.transaktion;          }
    public Budget       getBudget()         { return this.budget;               }
    public BigDecimal   getBetrag()         { return this.betrag;               }

    // setters
    public void setId(long budget_buchung_id)           { this.budget_buchung_id    = budget_buchung_id;    }
    public void setTransaktion(Transaktion transaktion) { this.transaktion          = transaktion;          }
    public void setBudget(Budget budget)                { this.budget               = budget;               }
    public void setBetrag(BigDecimal betrag)            { this.betrag               = betrag;               }
}
