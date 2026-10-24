package de.finanzen.model;

public class Budget 
{
    // table columns
    private int     budget_id = 0;
    private String  name;
    private boolean aktiv = true;

    // constructors
    public Budget() 
    {

    }

    // getters
    public int      getId()     { return this.budget_id;    }
    public String   getName()   { return this.name;         }
    public boolean  istAktiv()  { return this.aktiv;        }

    // setters
    public void setId(int budget_id)    { this.budget_id   = budget_id;  }
    public void setName(String name)    { this.name        = name;       }
    public void setAktiv(boolean aktiv) { this.aktiv       = aktiv;       }

    @Override 
    public String toString() 
    {
        return "Budget [budget_id=" + budget_id + ", name=" + name + "]";
    }
}