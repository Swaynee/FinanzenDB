package de.finanzen.model;

public class Transaktionsart
{
    // table columns
    private int     transaktionsart_id = 0;
    private String  name;

    // constructors
    public Transaktionsart() 
    {

    }

    // getters
    public int      getId()     { return this.transaktionsart_id;   }
    public String   getName()   { return this.name;                 }

    // setters
    public void setId(int transaktionsart_id)   { this.transaktionsart_id   = transaktionsart_id;   }
    public void setName(String name)            { this.name                 = name;                 }

    @Override 
    public String toString()
    {
        return "Transaktionsart [transaktionsart_id=" + transaktionsart_id + ", name=" + name + "]";
    }
}