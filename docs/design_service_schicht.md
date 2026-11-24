# **Zusammenfassung – Designentscheidungen und weitere Planung**

Projekt: **Java Finanzen**

Diese Zusammenfassung dokumentiert die Architekturentscheidungen, die wir in diesem Chat besprochen haben, sowie die daraus abgeleiteten nächsten Entwicklungsschritte.

## **1. Architektur und Schichtenmodell**

Wir haben uns für eine mehrschichtige Architektur entschieden, bei der jede Schicht eine klar abgegrenzte Verantwortung besitzt.

Geplante Architektur:

```
Frontend
    |
    v
Controller / REST API
    |
    v
Service
    |
    v
Repository
    |
    v
Datenbank (MariaDB)
```

**Verantwortlichkeiten:**

| Schicht          | Aufgabe                                                                      |
| ---------------- | ---------------------------------------------------------------------------- |
| Frontend         | Darstellung und Benutzerinteraktion                                          |
| Controller / API | HTTP-Anfragen entgegennehmen, Eingaben verarbeiten und Antworten zurückgeben |
| Service          | Geschäftslogik, Validierungen und Koordination von Geschäftsoperationen      |
| Repository       | Datenbankzugriffe und SQL-Abfragen                                           |
| Model            | Repräsentation der Daten und Datenbankobjekte                                |
| Database         | Verbindungsverwaltung und Datenbankinfrastruktur                             |

Die einzelnen Schichten sollen nicht übersprungen werden. Insbesondere soll das Frontend nicht direkt auf Services oder Repositories zugreifen.

## **2. Dependency Injection**

Wir haben entschieden, Abhängigkeiten zwischen Klassen konsequent über Konstruktoren bereitzustellen.

Bisherige Variante:

```
private final TransaktionsartRepository transaktionsartRepository =
        new TransaktionsartRepository();
```

Bevorzugte Variante:

```
private final TransaktionsartRepository transaktionsartRepository;

public TransaktionRepository(
        TransaktionsartRepository transaktionsartRepository)
{
    this.transaktionsartRepository = transaktionsartRepository;
}
```

**Begründung:**

- Abhängigkeiten werden explizit sichtbar.
- Klassen erzeugen ihre benötigten Abhängigkeiten nicht selbst.
- Austauschbarkeit und Testbarkeit werden verbessert.
- Die Abhängigkeiten können zentral verwaltet werden.

Weitere Festlegungen:

- Abhängigkeiten werden als `private final` deklariert.
- Getter und Setter für diese Abhängigkeiten sind nicht notwendig.
- Die Objekte werden später zentral initialisiert und miteinander verbunden.
- Auch Repository-Abhängigkeiten untereinander sollen diesem Prinzip folgen.

Allerdings sollen Abhängigkeiten nicht unnötig aufgebaut werden. Jede Abhängigkeit muss einen konkreten Zweck erfüllen.

## **3. Service-Schicht und Validierungen**

Die Service-Schicht ist inzwischen für folgende Bereiche implementiert:

- `KontoService`
- `BudgetService`
- `TransaktionsartService`
- `TransaktionService`
- `BuchungService`
- `BudgetbuchungService`
- `NotizService`

Die Services übernehmen:

- Fachliche Validierungen
- Prüfung von IDs
- Prüfung der Existenz referenzierter Daten
- Koordination von Repository-Aufrufen
- Logging erfolgreicher Operationen

### **Grundsatz für Validierungen**

Jeder Service besitzt eine eigene private Methode:

```
private void validiere(...)
```

Wir haben ausdrücklich entschieden, diese Methoden nicht öffentlich zu machen.

**Begründung:**

Die Validierung ist eine interne Implementierungsdetails des jeweiligen Services. Andere Services sollen nicht direkt auf die Validierungsmethoden zugreifen.

Stattdessen verwenden sie die öffentlichen Fachmethoden des jeweils anderen Services, beispielsweise `getById()`.

Beispiel:

```
BuchungService
    |
    +--> KontoService.getById()
    |
    +--> TransaktionService.getById()
```

Die Services sollen ihre eigenen Objekte und deren grundlegende fachliche Anforderungen verantworten.

### **Vermeidung doppelter Validierungen**

Ein Beispiel ist die Transaktionsart:

- `TransaktionsartService` prüft die Pflichtfelder einer Transaktionsart.
- `TransaktionService` prüft die Transaktion und die Existenz ihrer Transaktionsart.
- `BuchungService` prüft die Existenz der zugehörigen Transaktion.

Der `BuchungService` muss die Transaktionsart daher nicht noch einmal separat über den `TransaktionsartService` abrufen.

## **4. Fehlerbehandlung und Exceptions**

Wir haben folgende Unterscheidung festgelegt:

| Exception                  | Bedeutung                                |
| -------------------------- | ---------------------------------------- |
| `IllegalArgumentException` | Übergebene Daten sind fachlich ungültig  |
| `SQLException`             | Technischer Fehler beim Datenbankzugriff |

Beispiel:

```
private void validiere(Transaktion transaktion) throws SQLException
{
    if (transaktion == null)
        throw new IllegalArgumentException(
                "Die Transaktion darf nicht null sein.");

    if (transaktionsartRepository.getById(
            transaktion.getTransaktionsart().getId()) == null)
        throw new IllegalArgumentException(
                "Die angegebene Transaktionsart existiert nicht.");
}
```

Eine mögliche `SQLException` wird nicht in eine `IllegalArgumentException` umgewandelt.

Stattdessen:

- Repository wirft die `SQLException`.
- Service reicht sie weiter.
- Die übergeordnete Anwendungsschicht behandelt sie und entscheidet über die Darstellung gegenüber dem Benutzer.

**Logging:**

Exceptions sollen grundsätzlich dort geloggt werden, wo sie tatsächlich behandelt werden. Dadurch wird vermieden, dass dieselbe Exception auf mehreren Ebenen mehrfach protokolliert wird.

## **5. Transaktionen und Buchungen**

Ein wichtiger Teil der fachlichen Logik betrifft die Beziehung zwischen Transaktionen und Buchungen.

Eine Transaktion kann mehrere Buchungen besitzen. Zusätzlich können Budgetbuchungen zu einer Transaktion gehören.

### **Löschen einer Transaktion**

In der Datenbank bestehen `ON DELETE CASCADE`-Beziehungen zwischen `Transaktion` und ihren untergeordneten Buchungen beziehungsweise Budgetbuchungen.

Daraus folgt:

- Der `TransaktionService` ruft das Löschen im `TransaktionRepository` auf.
- Die Datenbank löscht die abhängigen Buchungen automatisch.
- Es sind keine zusätzlichen manuellen DELETE-Abfragen notwendig.

### **Erstellen einer Transaktion mit Buchungen**

Hier ist die Situation anders.

Beispiel:

1. Transaktion erstellen.
2. Buchung für Konto A erstellen.
3. Buchung für Konto B erstellen.
4. Budgetbuchung erstellen.

Diese Schritte gehören fachlich zusammen.

Wenn beispielsweise die dritte Operation fehlschlägt, dürfen die ersten beiden Änderungen nicht dauerhaft bestehen bleiben.

Deshalb benötigen wir eine gemeinsame Datenbanktransaktion mit:

- `Connection`
- `setAutoCommit(false)`
- `commit()`
- `rollback()`

Die fachliche Koordination gehört in die Service-Schicht. Die technische Transaktionssteuerung soll durch eine geeignete Datenbankinfrastruktur erfolgen.

## **6. Repository-Schicht und gemeinsame Connections**

Aktuell öffnen die Repository-Methoden ihre Datenbankverbindungen selbst:

```
try (Connection con = DatabaseConnection.getConnection())
{
    // SQL-Abfrage
}
```

Das funktioniert für einzelne CRUD-Operationen.

Für mehrere zusammenhängende Operationen ist es allerdings problematisch, weil jede Repository-Methode ihre eigene Connection verwendet.

**Geplante Änderung:**

Die Repositorys sollen später auch eine bereits bestehende `Connection` entgegennehmen können.

Beispielsweise:

```
public boolean save(Transaktion transaktion, Connection con)
        throws SQLException
```

Damit können mehrere Repository-Aufrufe dieselbe Connection verwenden.

Wir haben außerdem besprochen, dafür eine zentrale Klasse wie `DatabaseTransaction` oder `TransactionManager` einzuführen.

Die konkrete Implementierung ist noch offen.

## **7. Betragsnormalisierung in Buchungen**

Wir haben entschieden, das Vorzeichen der Beträge weiterhin in der Datenbank zu speichern.

Beispiel:

| Buchungsart            | Gespeicherter Betrag |
| ---------------------- | -------------------- |
| Einnahme               | +50,00 €             |
| Ausgabe                | -50,00 €             |
| Umbuchung – Quellkonto | -50,00 €             |
| Umbuchung – Zielkonto  | +50,00 €             |

Die Eingabe soll möglichst unkompliziert sein.

### **Geplante Eingaberegeln**

| Transaktionsart | Benutzereingabe | Verarbeitung                  |
| --------------- | --------------- | ----------------------------- |
| Einnahme        | +50             | +50                           |
| Einnahme        | -50             | +50                           |
| Ausgabe         | +50             | -50                           |
| Ausgabe         | -50             | -50                           |
| Umbuchung       | +50             | Quellkonto -50, Zielkonto +50 |

Bei Einnahmen und Ausgaben soll der Service das Vorzeichen automatisch normalisieren.

Dafür ist eine Methode im `BuchungService` vorgesehen:

```
private BigDecimal normalisiereBuchungsbetrag(
        BigDecimal buchungsBetrag,
        Transaktionsart transaktionsart)
```

Wichtig: `BigDecimal` ist unveränderlich. Methoden wie `negate()` liefern einen neuen Wert zurück.

Deshalb muss das Ergebnis zugewiesen werden:

```
normalisiert = normalisiert.negate();
```

Die Normalisierung wird in `erstelle()` und `aktualisiere()` nach der Validierung und vor dem Speichern aufgerufen.

### **Sonderfall Umbuchung**

Eine Umbuchung wird als zusammengehörige Operation behandelt:

- Benutzer gibt einen Betrag ein.
- Quellkonto erhält eine negative Buchung.
- Zielkonto erhält eine positive Buchung.
- Beide Buchungen gehören zur selben Transaktion.

Die Erzeugung und Speicherung dieser beiden Buchungen soll später gemeinsam und atomar erfolgen.

Außerdem wurde festgehalten, dass die Transaktionsart nicht anhand ihres veränderlichen Namens identifiziert werden sollte. Eine stabile Typkennung oder ein Enum wäre langfristig besser als fest codierte Datenbank-IDs.

## **8. Logging**

Die Services verwenden `java.util.logging`.

Beispielsweise:

```
private static final Logger LOGGER =
        Logger.getLogger(BuchungService.class.getName());
```

Erfolgreiche Operationen werden protokolliert, beispielsweise:

```
if (erfolgreich)
    LOGGER.log(Level.INFO, "Buchung erstellt: {0}", buchung.getId());
```

Fehler werden nicht unnötig mehrfach auf verschiedenen Architekturebenen geloggt.

# **9. Geplante nächste Entwicklungsschritte**

Die Service-Schicht ist inzwischen fertiggestellt. Wir haben folgende Reihenfolge für die weitere Entwicklung festgelegt:

| Priorität | Aufgabe                                           | Status   |
| --------- | ------------------------------------------------- | -------- |
| 1         | Model-Schicht                                     | Erledigt |
| 2         | Repository-Schicht                                | Erledigt |
| 3         | Service-Schicht                                   | Erledigt |
| 4         | Repository- und Service-Tests mit JUnit           | Offen    |
| 5         | Gemeinsame Datenbanktransaktionen                 | Offen    |
| 6         | Zentrale Initialisierung und Dependency Injection | Offen    |
| 7         | Controller-/REST-API-Schicht                      | Offen    |
| 8         | Frontend                                          | Offen    |
| 9         | Weitere Fehlerbehandlung und Dokumentation        | Offen    |

### **Schritt 1: Tests**

Die bestehenden Services und Repositorys sollen getestet werden.

Insbesondere:

- CRUD-Operationen
- Ungültige IDs
- Nicht existierende Referenzen
- `null`-Werte
- Ungültige Beträge
- Inaktive Konten und Budgets
- Vorzeichennormalisierung

### **Schritt 2: Datenbanktransaktionen**

Anschließend soll die gemeinsame Verwendung einer JDBC-Connection implementiert werden.

Das ist die wichtigste noch offene technische Grundlage für die zusammenhängenden Buchungsoperationen.

### **Schritt 3: Zentrale Initialisierung**

Eine zentrale Stelle soll die Repository- und Service-Objekte erstellen und ihre Abhängigkeiten verbinden.

Beispielsweise in einer `ApplicationConfig` oder einer entsprechenden Initialisierungsklasse.

### **Schritt 4: Controller/API**

Danach folgt die API-Schicht.

Als mögliche Technologien wurden besprochen:

- Java Servlets
- Javalin
- Spring Boot

Da das Projekt bewusst ohne unnötige Frameworks entwickelt wird, ist Spring Boot nicht automatisch die bevorzugte Lösung. Die Entscheidung ist noch offen.

### **Schritt 5: Frontend**

Erst wenn die API funktioniert, soll das Frontend angebunden werden.

Geplant ist eine Trennung zwischen Java-Backend und HTML/CSS/JavaScript-Frontend.

## **10. Noch offene Designentscheidungen**

Folgende Punkte wurden noch nicht abschließend entschieden:

1. Wie genau wird die gemeinsame JDBC-Transaktionssteuerung implementiert?
2. Werden Repository-Methoden grundsätzlich um `Connection`-Overloads erweitert oder wird eine andere Lösung verwendet?
3. Wird die Transaktionsart langfristig über ein Enum oder eine stabile Typkennung identifiziert?
4. Welches Framework wird für die REST-API verwendet?
5. Wie genau wird die Umbuchungsoperation im Service modelliert?
6. Wie werden automatisierte Tests organisiert und welche Datenbank wird dafür verwendet?

**Aktueller Entwicklungsstand:** Die Model-, Repository- und Service-Schicht sind fertiggestellt. Der nächste geplante größere Entwicklungsschritt ist die Absicherung der bestehenden Implementierung durch Tests und anschließend die Einführung einer gemeinsamen Datenbanktransaktionssteuerung.
