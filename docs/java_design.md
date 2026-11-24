# **Zusammenfassung – Designentscheidungen und Planungen**

Projekt: **FinanzenDB – Java-Backend mit MariaDB/MySQL**

Diese Zusammenfassung bezieht sich auf die Architektur- und Implementierungsentscheidungen rund um `Transaktion`, `Buchung` und `BudgetBuchung` sowie die zugehörigen Repository-Klassen.

## **1. Architektur und Schichtenmodell**

Das Backend wird in mehrere Verantwortungsbereiche aufgeteilt:

| Schicht            | Verantwortlichkeit                                                                                  |
| ------------------ | --------------------------------------------------------------------------------------------------- |
| `model`            | Repräsentiert die Datenbankobjekte als Java-Klassen.                                                |
| `repository`       | Übernimmt den direkten Datenbankzugriff (SQL, CRUD, Mapping).                                       |
| `service`          | Enthält Geschäftslogik und Validierungen.                                                           |
| `controller` / API | Nimmt Anfragen entgegen und vermittelt zwischen den äußeren Schnittstellen und der Service-Schicht. |

**Wichtige Entscheidung:** Andere Anwendungsteile sollen nicht direkt auf Repositories zugreifen, sondern die Service-Schicht verwenden.

Die Repositories bleiben für den Datenbankzugriff zuständig. Validierungen gehören in die Services und nicht in die Repository-Klassen.

## **2. Objektorientiertes Datenmodell**

Eine zentrale Entscheidung war, Fremdschlüssel in den Java-Modellen nicht einfach als primitive IDs abzubilden, sondern als Referenzen auf andere Modellobjekte.

Beispielsweise:

```
private Transaktion transaktion;
private Konto konto;
private BigDecimal betrag;
```

Dadurch kann später direkt auf Eigenschaften referenzierter Objekte zugegriffen werden:

```
buchung.getTransaktion().getBeschreibung();
buchung.getKonto().getName();
```

Das ist gegenüber einer reinen ID-Verwaltung komfortabler und entspricht der gewünschten objektorientierten Modellierung.

### **Transaktion**

Die `Transaktion` enthält:

- `long transaktion_id`
- `Transaktionsart transaktionsart`
- `LocalDate datum`
- `LocalDate planungsmonat`
- `String beschreibung`

**Bewusste Entscheidung:** Die Transaktion besitzt keine Listen mit `Buchung`- oder `BudgetBuchung`-Objekten.

Obwohl die Datenbank eine 1:n-Beziehung abbildet, wird diese im Java-Modell nur in eine Richtung dargestellt.

### **Buchung**

Die `Buchung` enthält:

- `long buchung_id`
- `Transaktion transaktion`
- `Konto konto`
- `BigDecimal betrag`

Jede Buchung gehört genau einer Transaktion und einem Konto.

### **BudgetBuchung**

Die `BudgetBuchung` enthält:

- `long budget_buchung_id`
- `Transaktion transaktion`
- `Budget budget`
- `BigDecimal betrag`

Jede BudgetBuchung gehört genau einer Transaktion und einem Budget.

### **Warum keine Rückreferenzen?**

Wir haben uns bewusst gegen Listen in `Transaktion` entschieden.

Das verhindert unnötige gegenseitige Abhängigkeiten und mögliche rekursive Ladeprozesse:

```
Transaktion
    └── Buchung
          └── Transaktion
                └── Buchung
                      └── ...
```

Die Beziehung wird stattdessen nur von den Kindobjekten aus abgebildet.

## **3. Datenbankstruktur und Beziehungen**

Die drei Tabellen sind folgendermaßen miteinander verbunden:

```
Transaktionsart
      │
      │ 1:n
      ▼
 Transaktion
      │
      ├───────────────┐
      │               │
      │ 1:n           │ 1:n
      ▼               ▼
   Buchung       Budget_Buchung
      │               │
      │ n:1           │ n:1
      ▼               ▼
    Konto           Budget
```

Die Tabellen verwenden folgende Primärschlüssel:

| Tabelle          | Primärschlüssel     | Java-Datentyp |
| ---------------- | ------------------- | ------------- |
| `Transaktion`    | `transaktion_id`    | `long`        |
| `Buchung`        | `buchung_id`        | `long`        |
| `Budget_Buchung` | `budget_buchung_id` | `long`        |

Die Fremdschlüssel zu `Konto`, `Budget` und `Transaktionsart` verwenden `INT UNSIGNED` und werden in Java als `int` behandelt.

Beträge werden in der Datenbank als `DECIMAL(12,2)` und in Java als `BigDecimal` gespeichert.

Datumswerte werden als `DATE` gespeichert und in Java mit `LocalDate` repräsentiert.

### **Löschen von Transaktionen**

Für die Fremdschlüssel von `Buchung` und `Budget_Buchung` auf `Transaktion` wurde `ON DELETE CASCADE` vorgesehen.

Das bedeutet:

- Wird eine Transaktion gelöscht, werden ihre Buchungen automatisch mitgelöscht.
- Dasselbe gilt für ihre Budgetbuchungen.

Das Löschen einer Transaktion ist deshalb eine bewusste Operation mit Auswirkungen auf die zugehörigen Datensätze.

## **4. Repository-Design**

Alle Repository-Klassen orientieren sich am bereits bestehenden `KontoRepository`.

Verwendetes Muster:

| Methode                  | Aufgabe                                               |
| ------------------------ | ----------------------------------------------------- |
| `getById(...)`           | Einzelnen Datensatz anhand seiner ID laden.           |
| `getAlle()`              | Alle Datensätze laden.                                |
| `save(...)`              | Entscheidet anhand der ID zwischen Insert und Update. |
| `insert(...)`            | Neuen Datensatz einfügen.                             |
| `update(...)`            | Bestehenden Datensatz aktualisieren.                  |
| `delete(...)`            | Datensatz löschen.                                    |
| `mapResultSetTo...(...)` | Datenbankergebnis in ein Java-Modell umwandeln.       |

`insert()` und `update()` sind private Methoden. Der Aufrufer verwendet ausschließlich `save()`.

### **ID-Konvention**

Es gilt weiterhin:

- ID `0`: Objekt wurde noch nicht gespeichert.
- ID größer `0`: Objekt existiert bereits in der Datenbank.

Bei einem erfolgreichen Insert wird die generierte ID direkt in das übergebene Modellobjekt geschrieben.

### **Datenbankzugriff**

Die Repositories verwenden:

- `DatabaseConnection.getConnection()`
- `PreparedStatement`
- `ResultSet`
- Try-with-resources
- `Statement.RETURN_GENERATED_KEYS`
- `java.util.logging.Logger`

SQL-Parameter werden grundsätzlich über PreparedStatements übergeben.

### **Objekt-Mapping**

Beim Laden werden Fremdschlüssel in vollständige Modellreferenzen umgewandelt.

Beispiel `BuchungRepository`:

```
long transaktion_id = rs.getLong("transaktion_id");
buchung.setTransaktion(transaktionRepository.getById(transaktion_id));

int konto_id = rs.getInt("konto_id");
buchung.setKonto(kontoRepository.getById(konto_id));
```

Dadurch enthält eine geladene Buchung nicht nur die IDs, sondern die entsprechenden Java-Objekte.

## **5. Aktuelle Repository-Klassen**

Wir haben folgende Repository-Klassen vorgesehen:

| Repository                | Abhängigkeiten                              |
| ------------------------- | ------------------------------------------- |
| `TransaktionRepository`   | `TransaktionsartRepository`                 |
| `BuchungRepository`       | `TransaktionRepository`, `KontoRepository`  |
| `BudgetBuchungRepository` | `TransaktionRepository`, `BudgetRepository` |

Die Abhängigkeiten werden aktuell direkt über Instanziierung hergestellt:

```
private final TransaktionRepository transaktionRepository = new TransaktionRepository();
```

Das ist für den aktuellen Projektumfang eine einfache Lösung. Später könnte man die Abhängigkeiten über Konstruktoren injizieren, wie es bereits bei den Services vorgesehen ist.

## **6. Noch offene technische Überlegungen**

Einige Punkte sollten wir bei der weiteren Entwicklung im Hinterkopf behalten.

### **A. Performance beim Laden**

Aktuell lädt `getAlle()` zunächst alle Datensätze und ruft anschließend für jede Zeile weitere Repository-Methoden auf.

Beispiel:

```
getAlle() mit 100 Buchungen

1 Abfrage: Alle Buchungen
100 Abfragen: Zugehörige Transaktionen
100 Abfragen: Zugehörige Konten
```

Das ist das sogenannte N+1-Query-Problem.

Für den Anfang haben wir uns bewusst für die verständlichere Implementierung entschieden. Eine spätere Optimierung könnte über JOIN-Abfragen erfolgen.

### **B. Datenbanktransaktionen**

Ein wichtiger nächster Punkt ist die Speicherung einer vollständigen Transaktion mit mehreren Buchungen und Budgetbuchungen.

Beispielsweise:

```
Transaktion speichern
    │
    ├── Buchung 1 speichern
    ├── Buchung 2 speichern
    └── BudgetBuchung 1 speichern
```

Diese Vorgänge müssen zusammengehören.

Wenn beispielsweise die zweite Buchung fehlschlägt, sollten die erste Buchung und die Transaktion nicht bereits dauerhaft gespeichert sein.

Dafür brauchen wir eine gemeinsame JDBC-`Connection` und eine Datenbanktransaktion:

```
con.setAutoCommit(false);

try
{
    // Transaktion und zugehörige Buchungen speichern

    con.commit();
}
catch (SQLException e)
{
    con.rollback();
    throw e;
}
```

**Wichtig:** Die aktuell erstellten Repository-Methoden öffnen jeweils selbst eine Connection. Für diesen Anwendungsfall müssen wir sie erweitern, damit sie auch eine bereits bestehende Connection entgegennehmen können.

Das ist insbesondere für die Service-Schicht relevant.

### **C. Validierungen**

Die Repository-Klassen prüfen nicht, ob eine Buchung fachlich gültig ist.

Beispielsweise:

- Ist die Transaktion vorhanden?
- Ist das Konto vorhanden?
- Ist der Betrag zulässig?
- Ist das Budget vorhanden?

Solche Prüfungen gehören in die Service-Schicht.

Die Datenbank-Fremdschlüssel bleiben zusätzlich als Absicherung bestehen.

## **7. Geplante nächste Schritte**

Ausgehend vom aktuellen Stand ergibt sich folgende sinnvolle Reihenfolge:

1. **Repository-Klassen fertigstellen und kompilieren**
   - `TransaktionRepository`
   - `BuchungRepository`
   - `BudgetBuchungRepository`
2. **Repository-Methoden für gemeinsame Connections ergänzen**
   - `getById(id, con)`
   - `getAlle(con)`
   - `save(obj, con)`
   - Weitere Methoden nur bei tatsächlichem Bedarf.
3. **TransaktionService erweitern**
   - Eine vollständige Transaktion inklusive Buchungen und Budgetbuchungen speichern.
   - Alle Schreiboperationen über eine gemeinsame Connection ausführen.
   - Bei Fehlern Rollback durchführen.
4. **Services testen**
   - Validierungen testen.
   - Erfolgreiches Speichern testen.
   - Fehlerfälle und Rollback testen.
5. **JUnit-Tests für die Repository- und Service-Schicht ergänzen.**
6. **Anschließend die API beziehungsweise die Anbindung an das Frontend planen.**

## **Fazit**

Die zentrale Architekturentscheidung lautet:

**Die Datenbankstruktur bildet die Beziehungen ab, die Java-Modelle verwenden Objekt-Referenzen, die Repositories kümmern sich ausschließlich um den Datenbankzugriff und die Services koordinieren Geschäftslogik und zusammenhängende Datenbankoperationen.**

Damit bleibt die Verantwortlichkeit der einzelnen Schichten klar getrennt. Die wichtigste noch ausstehende Erweiterung ist die Unterstützung gemeinsamer JDBC-Connections, damit zusammengehörige Buchungen atomar gespeichert werden können.
