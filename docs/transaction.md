## **Gemeinsame Datenbanktransaktionen**

Das war der Schwerpunkt des letzten Gesprächs.

### **Warum brauchen wir sie?**

Einige fachliche Vorgänge bestehen aus mehreren Datenbankoperationen, die zwingend gemeinsam erfolgreich sein müssen.

Beispiele:

**Transaktion erstellen:**

- Transaktion speichern.
- Eine oder mehrere Buchungen speichern.
- Eine oder mehrere Budgetbuchungen speichern.

**Kontoumbuchung:**

- Eine Buchung für das Quellkonto erstellen.
- Eine Buchung für das Zielkonto erstellen.

**Budgetumbuchung:**

- Eine Budgetbuchung für das Quellbudget erstellen.
- Eine Budgetbuchung für das Zielbudget erstellen.

Wenn eine der Operationen fehlschlägt, müssen alle vorherigen Änderungen zurückgenommen werden.

Dafür verwenden wir JDBC-Transaktionen mit:

- `setAutoCommit(false)`
- `commit()`
- `rollback()`

### **DatabaseTransaction**

Eine entsprechende Klasse wurde bereits erstellt:

```
public class DatabaseTransaction implements AutoCloseable
{
    private static final Logger LOGGER =
        Logger.getLogger(BuchungRepository.class.getName());

    private final Connection connection;
    private boolean committed = false;

    public DatabaseTransaction() throws SQLException
    {
        Connection con = DatabaseConnection.getConnection();

        try
        {
            con.setAutoCommit(false);
            this.connection = con;
        }
        catch (SQLException e)
        {
            con.close();
            throw e;
        }
    }

    public Connection getConnection()
    {
        return this.connection;
    }

    public void commit() throws SQLException
    {
        this.connection.commit();
        this.committed = true;
    }

    @Override
    public void close() throws SQLException
    {
        try
        {
            if (!this.committed)
            {
                this.connection.rollback();
            }
        }
        finally
        {
            this.connection.close();
        }
    }
}
```

Die Klasse übernimmt:

- Öffnen der Connection.
- Deaktivieren von AutoCommit.
- Commit.
- Automatisches Rollback, wenn kein Commit erfolgt.
- Schließen der Connection.

### **Repositorys für Connection-Übergabe erweitern**

Alle Repository-Methoden wurden entsprechend erweitert.

Das Prinzip:

```
public boolean save(Buchung buchung) throws SQLException
{
    try (Connection con = DatabaseConnection.getConnection())
    {
        return save(buchung, con);
    }
}

public boolean save(Buchung buchung, Connection con) throws SQLException
{
    if (buchung.getId() == 0) return insert(buchung, con);
    return update(buchung, con);
}
```

Damit gibt es zwei Möglichkeiten:

1. Eigenständiger Datenbankzugriff mit eigener Connection.
2. Datenbankzugriff innerhalb einer bereits laufenden Transaktion.

Wichtig: Die Connection-übernehmenden Repository-Methoden schließen die übergebene Connection nicht.

Die Connection gehört dem Aufrufer.


- Budgetumbuchung.
- Beide jeweils atomar ausführen.
