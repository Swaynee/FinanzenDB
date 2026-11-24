# **Zusammenfassung – Java-Finanzen: Architektur und Testplanung**

Stand: 24.11.2026

## **1. Architekturentscheidungen**

Die grundlegende Schichtenarchitektur wurde festgelegt:

```
Controller / API (später)
        |
        v
    Service
        |
        v
   Repository
        |
        v
 DatabaseConnection
        |
        v
     MariaDB
```

**Verantwortlichkeiten:**

| Schicht          | Aufgabe                                                            |
| ---------------- | ------------------------------------------------------------------ |
| Model            | Repräsentiert die Daten und deren Eigenschaften.                   |
| Repository       | Übernimmt Datenbankzugriffe mittels JDBC.                          |
| Service          | Enthält Geschäftslogik und Validierungen.                          |
| Controller / API | Nimmt später Anfragen vom Frontend entgegen und ruft Services auf. |

Weitere Architekturentscheidungen:

- Java mit JDBC und MariaDB.
- Kein Spring, Hibernate oder JPA. Das Projekt soll zunächst ohne Frameworks auskommen.
- Repository-Abhängigkeiten werden über Konstruktoren injiziert (Constructor Injection).
- Andere Schichten sollen für Geschäftsoperationen die Services verwenden und nicht direkt auf Repositories zugreifen.
- Validierungsmethoden wie `validiere()` bleiben `private` innerhalb der jeweiligen Service-Klasse. Die Validierungslogik wird nicht öffentlich zugänglich gemacht.

## **2. Service-Schicht**

Die Service-Schicht wurde inzwischen implementiert.

Beispielhaft enthält der `TransaktionService` Methoden wie:

- `getAlle()`
- `getById(long transaktion_id)`
- `erstelle(Transaktion transaktion)`
- `aktualisiere(Transaktion transaktion)`
- `loesche(long transaktion_id)`
- `validiere(Transaktion transaktion)` (private)

Die Services übernehmen unter anderem:

- Prüfung auf `null`.
- Prüfung auf gültige IDs.
- Prüfung auf Pflichtfelder.
- Prüfung, ob zu bearbeitende Datensätze tatsächlich existieren.
- Aufruf der Repository-Methoden.
- Logging erfolgreicher Operationen.

Die Repository-Schicht bleibt für die eigentlichen Datenbankoperationen verantwortlich.

**Wichtige Erkenntnis:** Die Service-Schicht soll nicht einfach nur Repository-Methoden durchreichen. Sie ist die zentrale Stelle für Geschäftsregeln und Validierungen.

## **3. JUnit und Mockito**

Wir haben uns entschieden, JUnit für automatisierte Tests zu verwenden.

Die Integration ist bereits abgeschlossen:

- JUnit Jupiter: `6.1.3`
- Mockito: `5.24.0`
- Maven Surefire: `3.5.4`
- Tests liegen unter `src/test/java`.

Mockito wird verwendet, um Abhängigkeiten innerhalb von Unit-Tests zu simulieren.

Dadurch können wir beispielsweise einen Service testen, ohne eine echte Datenbankverbindung aufzubauen.

### **Bisheriger Teststand**

Die ersten drei Tests für `TransaktionService` waren erfolgreich:

| Test                      | Erwartetes Verhalten                                |
| ------------------------- | --------------------------------------------------- |
| `testGetByIdUngueltigeId` | Ungültige IDs lösen `IllegalArgumentException` aus. |
| `testErstelleNull`        | Eine `null`-Transaktion wird abgelehnt.             |
| `testAktualisiereNull`    | Eine `null`-Transaktion wird abgelehnt.             |

Alle drei Tests liefen erfolgreich.

Ein Problem mit der Java-27-Kompatibilität von Byte Buddy wurde durch das Aktualisieren von Mockito auf Version `5.24.0` behoben.

Es gibt noch eine Warnung bezüglich des dynamischen Anhängens des Mockito-Java-Agenten. Sie beeinflusst die Testergebnisse aktuell nicht.

## **4. Aktueller Arbeitsschritt: Erfolgreiche Erstellung testen**

Wir haben uns anschließend den Model-Klassen `Transaktion` und `Transaktionsart` gewidmet.

Beide besitzen:

- Einen parameterlosen Konstruktor.
- Getter und Setter.
- Eine ID, die standardmäßig `0` beträgt.

Für eine neue Transaktion bedeutet das:

```
transaktion.getId() == 0
```

Das entspricht der bestehenden Repository-Logik:

```
public boolean save(Transaktion transaktion) throws SQLException
{
    if (transaktion.getId() == 0) return insert(transaktion);
    return update(transaktion);
}
```

Wir haben darauf aufbauend einen positiven Testfall für `erstelle()` vorbereitet:

```
@Test
void testErstelleErfolgreich() throws SQLException
{
    Transaktionsart art = new Transaktionsart();
    art.setId(1);
    art.setName("Einnahme");

    Transaktion transaktion = new Transaktion();
    transaktion.setTransaktionsart(art);
    transaktion.setDatum(LocalDate.of(2026, 11, 12));
    transaktion.setPlanungsmonat(LocalDate.of(2026, 11, 1));
    transaktion.setBeschreibung("Testtransaktion");

    when(transaktionRepository.save(transaktion)).thenReturn(true);

    boolean erfolgreich = transaktionService.erstelle(transaktion);

    assertTrue(erfolgreich);
    verify(transaktionRepository).save(transaktion);
}
```

Dabei testen wir zwei Dinge:

1. Der Service gibt das erfolgreiche Ergebnis des Repositories zurück.
2. Der Service ruft tatsächlich `save()` mit der übergebenen Transaktion auf.

Das Repository wird dabei vollständig simuliert. Es findet kein tatsächlicher Datenbankzugriff statt.

**Dieser Test ist vorbereitet, aber sein tatsächliches Ausführen und Überprüfen steht noch aus.**

## **5. Weitere geplante Schritte**

Die bisherige Reihenfolge und Planung sieht folgendermaßen aus:

| Reihenfolge | Aufgabe                                           | Status        |
| ----------- | ------------------------------------------------- | ------------- |
| 1           | Datenbankstruktur und Migrationen                 | Abgeschlossen |
| 2           | Model-Klassen                                     | Abgeschlossen |
| 3           | Repository-Schicht                                | Abgeschlossen |
| 4           | Service-Schicht                                   | Abgeschlossen |
| 5           | JUnit und Mockito einrichten                      | Abgeschlossen |
| 6           | Service-Unit-Tests vervollständigen               | In Arbeit     |
| 7           | Repository-Tests erstellen                        | Offen         |
| 8           | JDBC-Transaktionen implementieren                 | Offen         |
| 9           | Zentrale Initialisierung und Dependency Injection | Offen         |
| 10          | Controller/API implementieren                     | Offen         |
| 11          | Frontend entwickeln                               | Offen         |

### **Besonders wichtige offene Architekturfrage: JDBC-Transaktionen**

Wir hatten bereits besprochen, dass zusammengehörige Datenbankoperationen atomar ausgeführt werden müssen.

Beispiel: Eine Umbuchung zwischen zwei Konten besteht aus einer Abbuchung und einer Gutschrift.

Beide Operationen müssen gemeinsam erfolgreich sein oder gemeinsam zurückgerollt werden.

Dafür brauchen wir später eine Lösung, bei der mehrere Repository-Aufrufe dieselbe JDBC-`Connection` verwenden können.

Die konkrete Umsetzung über einen TransactionManager beziehungsweise eine zentrale Datenbanktransaktionsverwaltung ist noch offen.

## **6. Aktuelle Entwicklungsrichtung**

Die nächste sinnvolle Etappe ist, die Service-Schicht systematisch mit JUnit und Mockito abzusichern.

Dabei sollten wir nicht nur Fehlerfälle testen, sondern auch erfolgreiche Operationen und das Verhalten bei nicht vorhandenen Datensätzen.

Anschließend folgen die Repository-Tests. Diese sollten echte SQL-Operationen gegen eine geeignete Testdatenbank prüfen, da Mockito allein nicht sicherstellen kann, dass die SQL-Abfragen korrekt funktionieren.

Erst danach würde ich die gemeinsame JDBC-Transaktionsverwaltung angehen. Sie ist eine wichtige Grundlage für Geschäftsoperationen wie Umbuchungen.
