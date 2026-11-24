# **Zusammenfassung – Java Finanzen: Designentscheidungen und Planung**

Ich fasse die relevanten technischen Entscheidungen zusammen und trenne zwischen den Entscheidungen dieses Chats und der bereits bestehenden Projektarchitektur.

## **1. Entwicklungsumgebung (VS Code)**

Wir haben deine installierten Erweiterungen überprüft und eine schlankere Konfiguration festgelegt.

**Behalten:**

| Extension                            | Zweck                                           |
| ------------------------------------ | ----------------------------------------------- |
| Extension Pack for Java              | Bündelt die Java-Werkzeuge                      |
| Language Support for Java by Red Hat | Java Language Server, IntelliSense, Refactoring |
| Debugger for Java                    | Debugging                                       |
| Test Runner for Java                 | JUnit-Tests ausführen und debuggen              |
| Maven for Java                       | Maven-Projekte verwalten                        |
| Project Manager for Java             | Java-Projekte verwalten                         |

**Entfernen:**

- Code Runner: Für dein Maven-Projekt unnötig und potenziell problematisch, weil die Ausführung nicht automatisch die Maven-Konfiguration berücksichtigt.
- Gradle for Java: Du verwendest Maven und benötigst Gradle nicht.

### **Untersuchte Fehler**

Wir haben vier unterschiedliche Probleme identifiziert:

1. `sun.misc.Unsafe`: Veraltete interne Java-Funktion in Google Guice 5.1.0.
2. `org.objectweb.asm`: Doppeltes OSGi-Bundle beim Laden von Java Test Runner.
3. `NullPointerException`: Fehler im Java Language Server bei der Generierung von Code Actions.
4. `HttpTimeoutException`: Maven Central antwortet nicht rechtzeitig beim Abruf zusätzlicher Dependency-Informationen.

**Geplante Fehlerbehebung:**

1. Erweiterungen aktualisieren.
2. `Java: Clean Java Language Server Workspace` ausführen.
3. VS Code neu starten.
4. Bei weiterhin bestehendem Bundle-Konflikt Java Test Runner testweise deaktivieren und den Fehler eingrenzen.

Diese Probleme betreffen deine Entwicklungsumgebung und sind nicht automatisch Fehler in deiner Finanzanwendung.

## **2. Bestehende Architektur des Finanzprojekts**

Die grundsätzliche Architektur bleibt:

```
Frontend
   |
Controller / API
   |
Services
   |
Repositories
   |
JDBC
   |
MariaDB
```

Die Anwendung wird mit Java, JDBC und MariaDB entwickelt.

Bewusst verzichten wir vorerst auf:

- Spring / Spring Boot
- Hibernate
- JPA
- unnötige zusätzliche Frameworks

Das Ziel ist eine nachvollziehbare Architektur mit möglichst wenig Abhängigkeiten.

### **Verantwortlichkeiten**

| Schicht    | Verantwortung                                                    |
| ---------- | ---------------------------------------------------------------- |
| Model      | Repräsentiert die Daten und Fachobjekte                          |
| Repository | Datenbankzugriffe und CRUD-Operationen                           |
| Service    | Validierung und Geschäftslogik                                   |
| Controller | Verarbeitung eingehender Anfragen und Kommunikation mit Services |
| Frontend   | Darstellung und Benutzerinteraktion                              |

Eine wichtige Designentscheidung: Andere Schichten sollen nicht direkt auf Repositories zugreifen, sondern die Services verwenden.

Die Services sind damit die zentrale Schnittstelle für die Geschäftslogik.

## **3. Datenbanktransaktionen und Service-Architektur**

Ein besonders wichtiger Punkt aus unserer letzten Architekturentscheidung betrifft Datenbanktransaktionen.

Beispiel: Eine Umbuchung zwischen zwei Konten besteht fachlich aus zwei Buchungen:

- Belastung des Quellkontos
- Gutschrift auf dem Zielkonto

Beide Operationen müssen atomar ausgeführt werden.

Das bedeutet:

- Beide Buchungen erfolgreich → COMMIT.
- Eine Buchung schlägt fehl → ROLLBACK.

Wir haben dafür folgende Verantwortlichkeiten vorgesehen:

**FinanzenService**

- Koordiniert übergreifende Geschäftsprozesse.
- Steuert COMMIT und ROLLBACK.
- Stellt sicher, dass zusammengehörige Änderungen atomar ausgeführt werden.

**Einzelne Services**

- Validieren ihre jeweiligen Fachobjekte.
- Übernehmen ihre spezifische Geschäftslogik.
- Können innerhalb einer bestehenden Datenbanktransaktion arbeiten.

**Repositories**

- Führen SQL-Operationen aus.
- Verwenden bei Bedarf eine gemeinsam übergebene JDBC-Connection.
- Übernehmen nicht selbst die übergreifende Transaktionssteuerung.

Damit vermeiden wir, dass Geschäftslogik oder Transaktionssteuerung in den Repositories landet.

## **4. Teststrategie**

JUnit ist bereits integriert.

Die vorgesehene Reihenfolge:

1. Unit-Tests für die Service-Schicht.
2. Mockito verwenden, um Repository-Abhängigkeiten bei Bedarf zu simulieren.
3. Repository-Integrationstests mit einer Testdatenbank.
4. Später Integrationstests für zusammenhängende Geschäftsprozesse und Datenbanktransaktionen.

Insbesondere die Validierungsmethoden der Services sollen getestet werden.

## **5. Aktueller Projektstand und nächste Schritte**

Nach unserem bisherigen Entwicklungsstand:

| Bereich                | Status                                         |
| ---------------------- | ---------------------------------------------- |
| MariaDB-Datenbank      | Implementiert                                  |
| Model-Klassen          | Implementiert                                  |
| Repository-Schicht     | Implementiert                                  |
| Logging                | Integriert                                     |
| Dokumentation          | Begonnen                                       |
| Service-Schicht        | Implementiert                                  |
| JUnit                  | Integriert                                     |
| Datenbanktransaktionen | Architektur geplant, Umsetzung noch ausstehend |
| Controller/API         | Noch ausstehend                                |
| Frontend               | Noch ausstehend                                |

### **Empfohlene weitere Reihenfolge**

1. **Service-Schicht testen:** Validierungen und Geschäftslogik systematisch mit JUnit überprüfen.
2. **Transaktionsmanagement implementieren:** Gemeinsame JDBC-Connection und COMMIT/ROLLBACK sauber integrieren.
3. **Repository-Integrationstests:** Datenbankoperationen mit einer Testdatenbank überprüfen.
4. **Controller/API konzipieren:** Festlegen, wie das spätere Frontend mit dem Java-Backend kommuniziert.
5. **Ersten End-to-End-Prozess implementieren:** Einen vollständigen Ablauf vom Frontend bis zur Datenbank umsetzen.
6. **Frontend entwickeln.**

## **6. Grundsätzliche Architekturprinzipien**

Folgende Prinzipien haben wir für das Projekt festgelegt:

- **KISS:** Keine unnötige Komplexität.
- **YAGNI:** Keine Funktionen oder Abstraktionen implementieren, die aktuell nicht benötigt werden.
- **Dependency Injection:** Abhängigkeiten über Konstruktoren übergeben.
- **Single Responsibility:** Jede Schicht hat klar abgegrenzte Aufgaben.
- **Geringe Kopplung:** Services sollen nicht von konkreten Implementierungsdetails anderer Schichten abhängig sein, soweit sinnvoll.
- **Testbarkeit:** Geschäftslogik möglichst unabhängig von der Datenbank testen.
- **Transaktionssicherheit:** Zusammengehörige Datenbankänderungen müssen atomar sein.
- **Dokumentation:** Architekturentscheidungen und Verantwortlichkeiten nachvollziehbar festhalten.

**Wichtigster aktueller Schwerpunkt:** Die Service-Schicht ist fertiggestellt. Als nächstes sollten wir deren Verhalten mit JUnit absichern und anschließend das gemeinsame Transaktionsmanagement sauber implementieren, bevor wir mit der API beginnen.&#x20;
