# Datenmodell

## 1. Überblick

Die Datenbank bildet die Verwaltung persönlicher Finanzen ab. Dabei werden Konten, Budgets, Transaktionen und deren Buchungen getrennt voneinander verwaltet.

Die Datenbankstruktur ist relational aufgebaut. Die Java-Modellklassen bilden die fachlichen Entitäten objektorientiert ab.

## 2. Entitäten

Für jede Tabelle wird eine kurze Beschreibung ergänzt.

| Entität | Beschreibung |
|---|---|
| Konto | Repräsentiert ein Konto, beispielsweise ein Girokonto oder Bargeld. |
| Budget | Beschreibt einen finanziellen Rahmen für einen bestimmten Zweck. |
| Transaktion | Repräsentiert einen zusammengehörigen finanziellen Vorgang. |
| Buchung | Bildet die Auswirkung einer Transaktion auf ein Konto ab. |
| Budgetbuchung | Bildet die Auswirkung einer Transaktion auf ein Budget ab. |
| Transaktionsart | Klassifiziert die Art eines finanziellen Vorgangs. |
| Notiz | Speichert ergänzende Informationen zu finanziellen Vorgängen. |
| Wiederkehrende_Transaktion | Beschreibt die Regeln für regelmäßig auftretende finanzielle Vorgänge. |

Die endgültigen Tabellen- und Feldbeschreibungen werden mit `schema.sql` abgeglichen.

## 3. Beziehungen

Dieses Kapitel beschreibt die Primär- und Fremdschlüssel sowie die Kardinalitäten zwischen den Entitäten.

Besonders zu dokumentieren sind:

- Die Beziehungen zwischen Transaktion und Buchungen.
- Die Beziehungen zwischen Transaktion und Budgetbuchungen.
- Die Zuordnung von Buchungen zu Konten.
- Die Zuordnung von Budgetbuchungen zu Budgets.
- Die Beziehungen wiederkehrender Transaktionen zu ihren zugehörigen Konten und Budgets.

## 4. Fachliche Regeln

### Konto und Budget

Konten und Budgets sind voneinander unabhängig. Eine finanzielle Aktivität kann deshalb Auswirkungen auf Konten und Budgets haben, ohne dass diese Zuordnungen identisch sein müssen.

### Anfangsbestände

Anfangsbestände werden über Transaktionen mit der Transaktionsart `initialbestand` erfasst.

### Umbuchungen

Umbuchungen verbinden ein Quellkonto mit einem Zielkonto und müssen beide Kontobewegungen berücksichtigen.

### Wiederkehrende Transaktionen

Wiederkehrende Vorgänge besitzen Angaben zur Gültigkeit, zum Zahlungstag und zum Planungsmonat. Die genaue Berechnung des Planungsmonats wird gesondert erläutert.

## 5. Datenintegrität

Dieses Kapitel dokumentiert:

- Primärschlüssel und Fremdschlüssel.
- Pflichtfelder und optionale Felder.
- Eindeutigkeitsbedingungen und weitere Constraints.
- Regeln zur Behandlung historischer Daten.
- Besonderheiten bei der Migration.

## 6. Diagramm

Hier wird ein ER-Diagramm der Datenbank ergänzt.