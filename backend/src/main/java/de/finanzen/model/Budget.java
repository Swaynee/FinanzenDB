package de.finanzen.model;

public class Budget 
{
    // table columns
    private int budget_id;
    private String name;

    // constructors
    public Budget() 
    {

    }

    // getters
    public int      getId()     { return this.budget_id;    }
    public String   getName()   { return this.name;         }

    // setters
    public void setId(int budget_id) { this.budget_id = budget_id;  }
    public void setName(String name) { this.name = name;            }

    public String toString() 
    {
        return "Budget [budget_id=" + budget_id + ", name=" + name + "]";
    }
}