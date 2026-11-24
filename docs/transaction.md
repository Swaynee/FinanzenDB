# **Zusammenfassung – Java Finanzen: Service-Schicht und gemeinsame Datenbanktransaktionen**

Stand: 24.11.2026

Diese Zusammenfassung konzentriert sich auf die Architekturentscheidungen, die wir getroffen haben, und die nächsten geplanten Entwicklungsschritte.

## **1. Bisheriger Projektstand**

Folgende Komponenten sind bereits vorhanden:

| Bereich                           | Status              |
| --------------------------------- | ------------------- |
| MariaDB-Datenbank                 | Implementiert       |
| Model-Klassen                     | Implementiert       |
| Repository-Klassen                | Implementiert       |
| Logging                           | Implementiert       |
| Dokumentation                     | Begonnen            |
| Service-Schicht                   | Implementiert       |
| Gemeinsame Datenbanktransaktionen | In Umsetzung        |
| API-/Controller-Schicht           | Noch nicht begonnen |
| Frontend                          | Noch nicht begonnen |

Die Anwendung ist aktuell als Java-Projekt ohne größeres Framework aufgebaut.

## **2. Architektur und Verantwortlichkeiten**

Wir haben uns für eine mehrschichtige Architektur entschieden:

```
Frontend
   |
   v
Controller / API
   |
   v
Service-Schicht
   |
   v
Repository-Schicht
   |
   v
DatabaseConnection / MariaDB
```

Die Verantwortlichkeiten:

**Model**

- Repräsentiert die Datenbankobjekte.
- Enthält Attribute, Getter und Setter.
- Enthält möglichst keine Geschäftslogik.

**Repository**

- Verantwortlich für SQL-Abfragen und Datenbankzugriffe.
- Mapping zwischen `ResultSet` und Model.
- CRUD-Operationen.
- Keine fachlichen Validierungen.

**Service**

- Enthält die Geschäftslogik.
- Validiert Eingaben und fachliche Zusammenhänge.
- Prüft beispielsweise, ob referenzierte Datensätze existieren.
- Normalisiert Daten vor dem Speichern.
- Ruft die entsprechenden Repository-Methoden auf.

**Controller / API**

- Wird später vom Frontend aufgerufen.
- Nimmt Eingaben entgegen.
- Ruft die entsprechenden Services auf.
- Bereitet Rückgaben und Fehler für das Frontend auf.

## **3. Implementierte Service-Klassen**

Folgende Services wurden erstellt:

- `KontoService`
- `BudgetService`
- `TransaktionsartService`
- `NotizService`
- `TransaktionService`
- `BuchungService`
- `BudgetbuchungService`

Die Services verwenden Constructor Injection für ihre Abhängigkeiten.

Beispiel:

```
private final BuchungRepository buchungRepository;

public BuchungService(BuchungRepository buchungRepository)
{
    this.buchungRepository = buchungRepository;
}
```

### **Validierung**

Alle Services besitzen private `validiere()`-Methoden.

Die Validierung erfolgt innerhalb der jeweiligen Service-Klasse und wird bei schreibenden Operationen aufgerufen.

Wichtige Entscheidungen:

- `validiere()` bleibt grundsätzlich `private`.
- SQL-Ausnahmen werden nicht als `IllegalArgumentException` verschleiert.
- `SQLException` wird bei Bedarf weitergereicht.
- Existenzprüfungen für Fremdreferenzen erfolgen über die entsprechenden Services bzw. Repository-Abfragen.
- Die Validierungslogik soll nicht unnötig zwischen Services dupliziert werden.

### **Buchungsbeträge normalisieren**

Im `BuchungService` erfolgt die Normalisierung der Beträge.

Aktuelle fachliche Regel:

| Transaktionsart | Gespeicherter Betrag |
| --------------- | -------------------- |
| Einnahme        | Positiv              |
| Ausgabe         | Negativ              |
| Sonstige        | Absoluter Wert       |

Beispielsweise wird eine Ausgabe über 50 € unabhängig davon, ob der Benutzer `50` oder `-50` eingibt, als `-50` gespeichert.

Die Normalisierung wird vor dem Speichern durchgeführt.

## **4. Dependency Injection zwischen Repositorys**

Wir haben entschieden, Repository-Abhängigkeiten nicht innerhalb anderer Repositorys selbst zu instanziieren.

Ungünstig:

```
private final TransaktionsartRepository transaktionsartRepository =
        new TransaktionsartRepository();
```

Stattdessen werden Abhängigkeiten über den Konstruktor übergeben.

Beispielsweise:

```
public TransaktionRepository(
    TransaktionsartRepository transaktionsartRepository)
{
    this.transaktionsartRepository = transaktionsartRepository;
}
```

Das gilt entsprechend auch für die Abhängigkeiten zwischen:

- `BuchungRepository`
- `TransaktionRepository`
- `KontoRepository`
- `BudgetRepository`
- `BudgetbuchungRepository`

Getter und Setter sind für diese Repository-Abhängigkeiten nicht erforderlich.

Die Abhängigkeiten sind `final` und werden ausschließlich beim Erstellen des Objekts gesetzt.

## **5. Gemeinsame Datenbanktransaktionen**

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

## **6. Architekturentscheidung: Connection-Überladungen auch in den Services**

Hier haben wir mehrere Varianten diskutiert.

Zunächst stand die Idee im Raum, den übergreifenden Service direkt auf die Repositorys zugreifen zu lassen.

Das haben wir verworfen.

**Grund:** Dadurch würde der `FinanzenService` die fachliche Validierung der einzelnen Services umgehen.

Die Service-Schicht soll weiterhin die zentrale Absicherung der Geschäftslogik sein.

Deshalb haben wir uns für folgende Lösung entschieden:

Auch die Services bekommen bei Bedarf überladene Methoden, die eine bestehende Connection entgegennehmen.

Beispiel:

```
public List<Buchung> getAlle() throws SQLException
{
    return buchungRepository.getAlle();
}

public List<Buchung> getAlle(Connection con) throws SQLException
{
    return buchungRepository.getAlle(con);
}
```

Für schreibende Operationen:

```
public boolean erstelle(Buchung buchung) throws SQLException
{
    validiere(buchung);
    normalisiere(buchung);

    return buchungRepository.save(buchung);
}

boolean erstelle(Buchung buchung, Connection con) throws SQLException
{
    validiere(buchung);
    normalisiere(buchung);

    return buchungRepository.save(buchung, con);
}
```

Wichtige Details:

- Beide Methoden verwenden dieselbe interne Validierungslogik.
- Die Normalisierung wird nicht dupliziert.
- Die Connection-Varianten sollen package-private sein.
- Dadurch kann der `FinanzenService` sie im selben Package aufrufen.
- Die Services dürfen die übergebene Connection weder committen noch schließen.

Nicht jede Methode muss überladen werden. Nur diejenigen, die tatsächlich innerhalb einer gemeinsamen Datenbanktransaktion benötigt werden.

**Das ist die zuletzt getroffene Architekturentscheidung.**

## **7. Geplanter FinanzenService**

Wir wollen einen allgemeinen `FinanzenService` erstellen.

Nicht `BuchungserfassungService`, weil er langfristig auch andere übergreifende Geschäftsprozesse übernehmen soll.

Geplante Aufgaben:

| Methode                                   | Funktion                                                      |
| ----------------------------------------- | ------------------------------------------------------------- |
| `erstelleTransaktion(...)`                | Transaktion inklusive Buchungen und Budgetbuchungen erstellen |
| `bucheZwischenKontenUm(...)`              | Umbuchung zwischen zwei Konten                                |
| `bucheZwischenBudgetsUm(...)`             | Umbuchung zwischen zwei Budgets                               |
| `erzeugeWiederkehrendeTransaktionen(...)` | Wiederkehrende Transaktionen automatisch erzeugen             |

Der `FinanzenService` soll:

1. Eine `DatabaseTransaction` öffnen.
2. Die entsprechenden Service-Methoden mit derselben Connection aufrufen.
3. Bei erfolgreicher Durchführung committen.
4. Bei Fehlern automatisch rollbacken lassen.

Konzeptionelles Beispiel:

```
try (DatabaseTransaction tx = new DatabaseTransaction())
{
    Connection con = tx.getConnection();

    transaktionService.erstelle(transaktion, con);

    buchungService.erstelle(buchung1, con);
    buchungService.erstelle(buchung2, con);

    budgetbuchungService.erstelle(budgetbuchung, con);

    tx.commit();
}
```

Damit bleibt die fachliche Logik in den jeweiligen Services und die übergreifende Koordination im `FinanzenService`.

## **8. Nächste Entwicklungsschritte**

Ich würde in dieser Reihenfolge weitermachen:

**Schritt 1 – Service-Klassen erweitern**

- `Connection` importieren.
- Benötigte Methoden überladen.
- Connection-Varianten package-private halten.
- Validierung und Normalisierung nicht duplizieren.
- Keine Connections in Service-Attributen speichern.

**Schritt 2 – FinanzenService erstellen**

- Konstruktor mit den benötigten Services.
- Gemeinsame Datenbanktransaktion verwalten.
- Erste Methode `erstelleTransaktion(...)` implementieren.

**Schritt 3 – Umbuchungen implementieren**

- Kontoumbuchung.
- Budgetumbuchung.
- Beide jeweils atomar ausführen.

**Schritt 4 – Wiederkehrende Transaktionen**

- Wiederholungstabelle auslesen.
- Fällige Wiederholungen ermitteln.
- Transaktionen und zugehörige Buchungen erzeugen.
- Doppelte Erzeugung verhindern.

**Schritt 5 – Tests**

Mit JUnit insbesondere testen:

- Erfolgreiche Transaktion.
- Fehlgeschlagene Buchung mit anschließendem Rollback.
- Fehlgeschlagene Budgetbuchung mit anschließendem Rollback.
- Ungültige Eingabedaten.
- Korrekte Betragsnormalisierung.

**Schritt 6 – Controller/API**

Erst wenn die Service-Schicht einschließlich der zusammengesetzten Geschäftsprozesse funktioniert, beginnen wir mit der API- bzw. Controller-Schicht.

## **9. Zusammengefasste Architekturentscheidungen**

| Thema                            | Entscheidung                                                                          |
| -------------------------------- | ------------------------------------------------------------------------------------- |
| Architektur                      | Model – Repository – Service – Controller                                             |
| Dependency Injection             | Constructor Injection                                                                 |
| Repository-Abhängigkeiten        | Keine eigenständige Instanziierung                                                    |
| Validierung                      | Private Methoden in den jeweiligen Services                                           |
| SQL-Fehler                       | `SQLException` weiterreichen                                                          |
| Betragsnormalisierung            | Im `BuchungService`                                                                   |
| Connection-Verwaltung            | Repository bei Einzeloperationen, `DatabaseTransaction` bei gemeinsamen Transaktionen |
| Connection-Übergabe              | Überladene Repository- und Service-Methoden                                           |
| Connection-Attribute in Services | Nein                                                                                  |
| Connection-Varianten in Services | Package-private                                                                       |
| Commit/Rollback                  | Ausschließlich `DatabaseTransaction` bzw. übergeordneter Vorgang                      |
| Übergreifende Geschäftslogik     | `FinanzenService`                                                                     |

**Aktueller nächster Schritt:** Die Connection-Überladungen in den benötigten Service-Klassen implementieren. Danach den `FinanzenService` mit `erstelleTransaktion(...)` aufbauen.

Wichtig für die weitere Umsetzung: Bei der gemeinsamen Transaktion müssen wir noch sicherstellen, dass ein fehlgeschlagener Speichervorgang (`false`) nicht versehentlich zu einem Commit führt. Außerdem sollten wir die Normalisierung so gestalten, dass sie bei wiederholten Aufrufen nicht unerwartet Seiteneffekte erzeugt.
