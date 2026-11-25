# **Zusammenfassung – Designentscheidungen und Planung des Finanzen-Projekts**

Hier ist der bisherige Stand der konzeptionellen Entscheidungen, insbesondere zum Datenbankmodell, zur Migration und zur Projektstruktur.

## **1. Technische Architektur**

Das Projekt soll bewusst möglichst schlank bleiben.

| Bereich             | Entscheidung             |
| ------------------- | ------------------------ |
| Programmiersprache  | Java                     |
| Build-System        | Maven                    |
| Datenbank           | MariaDB                  |
| Datenbankzugriff    | JDBC                     |
| IDE                 | VS Code                  |
| Datenbankverwaltung | DBeaver                  |
| Versionsverwaltung  | Git / GitHub             |
| Frontend            | HTML, CSS und JavaScript |
| Frameworks          | Möglichst wenige          |

Die geplante Architektur besteht aus mehreren Schichten:

```
Frontend (HTML / CSS / JavaScript)
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
JDBC
              |
              v
MariaDB
```

**Aufgabenverteilung:**

- **Model:** Repräsentiert die Datenbankobjekte, beispielsweise `Konto` oder `Transaktion`.
- **Repository:** Verantwortlich für Datenbankzugriffe, also Lesen, Speichern, Aktualisieren und Löschen.
- **Service:** Enthält Geschäftslogik und Validierungen. Andere Schichten sollen für Geschäftsoperationen die Services verwenden, nicht direkt die Repositories.
- **Controller:** Stellt später die Schnittstelle zwischen Frontend und Backend bereit.

Geldbeträge werden mit `BigDecimal` statt `double` verarbeitet.

## **2. Grundlegende Entscheidungen zum Datenbankmodell**

Das neue Datenbankmodell soll keine 1:1-Kopie der alten OpenOffice-Base-Datenbank sein. Stattdessen wurde das Modell neu strukturiert.

### **Transaktionen und Buchungen**

Eine `Transaktion` beschreibt ein finanzielles Ereignis.

Die tatsächlichen Bewegungen auf Konten und Budgets werden separat über Buchungen abgebildet.

Vereinfacht:

```
Transaktionsart
      |
      v
 Transaktion
      |
      +---- Buchung -------- Konto
      |
      +---- Budget_Buchung - Budget
```

Dadurch können Konto- und Budgetbewegungen unabhängig voneinander abgebildet werden.

### **Konten**

- Konten repräsentieren tatsächliche Geldbestände.
- Ein Konto kann beispielsweise Bargeld, Girokonto oder ein Kreditkonto sein.
- Schulden können vorerst ebenfalls über ein Konto mit negativem Kontostand dargestellt werden.
- Es gibt kein separates Feld `start_saldo` mehr.
- Anfangsbestände werden stattdessen als normale Transaktionen angelegt.

### **Budgets**

Budgets sind von Konten unabhängig.

Beispiel:

- 200 € werden dem Lebensmittelbudget zugewiesen.
- Einkäufe können anschließend sowohl bar als auch mit Karte bezahlt werden.
- Beide Zahlungen reduzieren dasselbe Lebensmittelbudget.

Dafür gibt es eine eigene Tabelle `Budget_Buchung`.

Das ist wichtig, weil eine Ausgabe gleichzeitig eine Kontobewegung und eine Budgetbewegung auslösen kann, diese aber unterschiedliche Dinge darstellen.

### **Transaktionsarten**

Die Tabelle heißt ausdrücklich `Transaktionsart`.

Transaktionsarten dienen der semantischen Unterscheidung verschiedener finanzieller Vorgänge.

Insbesondere sollen auch Umbuchungen und Budgetzuweisungen darüber abgebildet werden.

Eine Umbuchung soll beispielsweise als eine zusammenhängende Transaktion dargestellt werden, obwohl intern zwei Kontobuchungen entstehen:

- Abgang auf dem Quellkonto
- Zugang auf dem Zielkonto

Die Geschäftslogik dafür gehört in die Service-Schicht.

### **Planungsmonat**

Eine wichtige Unterscheidung ist die zwischen tatsächlichem Buchungsdatum und Planungsmonat.

- `datum`: Wann fand die tatsächliche Geldbewegung statt?
- `planungsmonat`: Zu welchem Monat soll die Transaktion finanziell gehören?

Beispiel:

Ein Gehalt wird am 27.09. ausgezahlt, soll aber für Oktober eingeplant werden.

Dann gilt:

```
datum:         27.09.2026
planungsmonat: 01.10.2026
```

Eine separate Tabelle `PLANUNG` wurde verworfen. Der Planungsmonat ist direkt Bestandteil der Transaktion.

### **Wiederkehrende Transaktionen**

Wiederkehrende Transaktionen werden über `Transaktion_Wiederkehrend` verwaltet.

Die Tabelle enthält unter anderem:

- `transaktionsart_id`
- `konto_id`
- `konto_ziel_id`
- `budget_id`
- `budget_ziel_id`
- `beschreibung`
- `betrag`
- `zahlungstag`
- `gueltig_ab`
- `gueltig_bis`
- `planungsmonat_versatz`

Der `planungsmonat_versatz` wurde ergänzt, um zeitliche Verschiebungen abzubilden.

Beispiele:

| Vorgang                     | Zahlungstag | Versatz |
| --------------------------- | ----------- | ------- |
| Gehalt für Folgemonat        | 27     | +5      |
| Stromrechnung für Vormonat | 01      | -3      |
| Normale monatliche Ausgabe  | 15      | 0       |

### **Notizen**

Notizen sind vollständig unabhängig von Transaktionen.

Sie dienen beispielsweise dazu, zukünftige finanzielle Verpflichtungen oder Erinnerungen festzuhalten.

Es gibt deshalb keine Fremdschlüsselverbindung zwischen `Notiz` und `Transaktion`.

### **Kategorien**

Kategorien wurden vorerst bewusst aus dem neuen Modell entfernt.

Es soll zunächst keine zusätzliche Kategorisierung geben.

## **3. Migration der alten Datenbank**

Die alte OpenOffice-Base-Datenbank wurde untersucht.

Folgende Datenbestände wurden dabei identifiziert:

| Alte Tabelle                     | Datensätze |
| -------------------------------- | ---------- |
| Transaktionen                    | 383        |
| Buchungen                        | 427        |
| Budgetzuweisungen                | 213        |
| Konten                           | 5          |
| Budgets                          | 13         |
| Transaktionsarten                | 3          |
| Wiederkehrende Budgetzuweisungen | 14         |
| Wiederkehrende Transaktionen     | 13         |
| Notizen                          | 10         |

Kategorien werden bei der Migration nicht übernommen.

### **Umgang mit alten Buchungen**

Die alte Datenbank hatte eine gemeinsame Buchungstabelle mit Konto- und Budgetzuordnung.

Im neuen Modell werden diese Verantwortlichkeiten getrennt:

- Alte Konto-Buchungen werden zu neuen `Buchung`-Datensätzen.
- Alte Budgetzuordnungen werden zusätzlich zu `Budget_Buchung`-Datensätzen.

Wenn mehrere alte Buchungen derselben Transaktion dasselbe Budget betreffen, sollen diese für die neue Budgetbuchung zusammengefasst werden.

### **Alte Budgetzuweisungen**

Die alten Budgetzuweisungen waren unabhängig von Transaktionen.

Im neuen Modell ist eine Budgetbuchung aber einer Transaktion zugeordnet.

Deshalb wurde folgende Lösung vorgesehen:

1. Für jede alte Budgetzuweisung wird eine künstliche Transaktion erzeugt.
2. Diese erhält die Transaktionsart `Budgetzuweisung`.
3. Dazu wird eine passende `Budget_Buchung` angelegt.

So bleiben die alten Budgetzuweisungen erhalten, ohne das neue Datenmodell aufzubrechen.

### **Alte Planungsmonate**

Die alte Datenbank hatte noch kein Feld `planungsmonat`.

Für die initiale Migration war vorgesehen, den ersten Tag des Monats von `datum` als Planungsmonat zu verwenden.

Sonderfälle können anschließend korrigiert werden.

## **4. Git und Umgang mit persönlichen Finanzdaten**

Eine wichtige Entscheidung war, dass echte Finanzdaten niemals in GitHub landen sollen.

Die Projektstruktur ist grundsätzlich:

```
finanzen/
├── backend/
│   ├── database/
│   │   ├── schema.sql
│   │   └── migrate.sql
│   ├── src/
│   └── pom.xml
│
├── frontend/
│
├── .gitignore
└── README.md
```

Dabei gilt:

| Datei                                  | GitHub |
| -------------------------------------- | ------ |
| `schema.sql`                           | Ja     |
| Java-Quellcode                         | Ja     |
| Frontend-Code                          | Ja     |
| Persönliche Migration mit echten Daten | Nein   |
| Datenbankzugangsdaten                  | Nein   |

Die private Migration wird über `.gitignore` ausgeschlossen:

```
/backend/database/migration.sql
```


## **5. Aktuelles Thema: IDs mit dem Wert 0**

Bei der Migration ist aufgefallen, dass die alte Datenbank IDs ab `0` verwendet.

MariaDB behandelt bei `AUTO_INCREMENT` eine explizit angegebene `0` normalerweise wie einen nicht angegebenen Wert und vergibt stattdessen eine neue ID.

Die alten Datensätze wurden entsprechend angepasst, dass sie mit 1 anfangen, anstatt mit 0.

Es wird in den Repositories beziehungsweise Models teilweise `id == 0` benutzt, um zu erkennen, dass ein Objekt noch nicht gespeichert wurde und neu eingefügt werden soll.

## **6. Zusammenfassung des aktuellen Projektstands**

| Bereich                           | Status                                                              |
| --------------------------------- | ------------------------------------------------------------------- |
| Grundlegende Datenbankarchitektur | Entschieden                                                         |
| Datenmodell                       | Entschieden                                                         |
| Konten und Budgets                | Entschieden                                                         |
| Transaktionen und Buchungen       | Entschieden                                                         |
| Planungsmonat                     | Entschieden                                                         |
| Wiederkehrende Transaktionen      | Entschieden                                                         |
| Unabhängige Notizen               | Entschieden                                                         |
| Kategorien                        | Vorerst verworfen                                                   |
| Java-Schichtenarchitektur         | Umgesetzt / in Arbeit                                               |
| Model-Klassen                     | Umgesetzt                                                           |
| Repository-Klassen                | Umgesetzt                                                           |
| Service-Schicht                   | Inzwischen fertiggestellt                                           |
| Git-Struktur und `.gitignore`     | Eingerichtet                                                        |
| Migration der alten Datenbank     | Erstellt, muss an das aktuelle Schema angepasst und getestet werden |           |
