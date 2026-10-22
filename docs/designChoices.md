# Designentscheidungen

Dieses Dokument beschreibt wesentliche fachliche und technische Entscheidungen des Projekts FinanzenDB. Es hält fest, welche Probleme gelöst werden sollen, welche Lösung gewählt wurde und welche Auswirkungen sich daraus ergeben.

## DD-001: Trennung von Konto und Budget

**Ausgangsproblem**

Ein Budget beschreibt, wie viel Geld für einen bestimmten Zweck zur Verfügung steht. Ein Konto beschreibt dagegen, wo sich Geld tatsächlich befindet. Beide Konzepte sind nicht identisch.

**Entscheidung**

Konten und Budgets werden unabhängig voneinander modelliert. Die Zuordnung von Geldbewegungen zu Konten und Budgets wird getrennt abgebildet.

**Begründung**

Eine Ausgabe kann beispielsweise teilweise bar und teilweise per Girokonto bezahlt werden, aber trotzdem vollständig demselben Budget zugeordnet sein.

**Auswirkungen**

- Kontoauswertungen und Budgetauswertungen können unabhängig voneinander erfolgen.
- Eine Ausgabe muss nicht künstlich auf mehrere Budgets verteilt werden, nur weil mehrere Konten beteiligt sind.
- Der verbleibende Kontostand und das verbleibende Budget müssen getrennt berechnet werden.

## DD-002: Trennung von Transaktion und Buchungen

**Ausgangsproblem**

Eine einzelne finanzielle Aktivität kann mehrere Kontobewegungen und mehrere Budgetzuordnungen umfassen.

**Entscheidung**

Transaktion, Buchung und Budgetbuchung werden als getrennte Konzepte modelliert.

**Begründung**

Die Transaktion beschreibt den zusammengehörigen finanziellen Vorgang. Die Buchungen bilden die Auswirkungen auf Konten ab. Budgetbuchungen bilden die Auswirkungen auf Budgets ab.

**Auswirkungen**

Die einzelnen Buchungen können einem gemeinsamen Vorgang zugeordnet werden. Dadurch lassen sich Konto- und Budgetauswertungen getrennt durchführen, ohne denselben Vorgang fachlich mehrfach erfassen zu müssen.

## DD-003: Umbuchungen zwischen Konten

**Entscheidung**

Eine Umbuchung wird über ein Quellkonto und ein Zielkonto abgebildet. Die zugehörigen Kontobuchungen werden gemeinsam erzeugt.

**Begründung**

Bei einer Umbuchung sinkt der Kontostand des Quellkontos, während der Kontostand des Zielkontos steigt. Die Umbuchung soll als zusammengehöriger Vorgang erkennbar bleiben.

## DD-004: Anfangsbestände als Transaktionen

**Entscheidung**

Anfangsbestände werden als Transaktionen mit der Transaktionsart `initialbestand` erfasst, anstatt einen separaten Anfangssaldo direkt im Konto zu speichern.

**Begründung**

Der Kontostand kann dadurch aus den erfassten Buchungen hergeleitet werden. Der Anfangsbestand wird wie ein eigener nachvollziehbarer Vorgang behandelt.

## DD-005: Deaktivieren statt endgültigem Löschen von Konten

**Entscheidung**

Konten werden bei der Löschoperation im Repository deaktiviert, statt physisch aus der Datenbank entfernt zu werden.

**Begründung**

Bestehende Buchungen und historische Zusammenhänge sollen erhalten bleiben.

## DD-006: Objektorientierte Modellierung

**Entscheidung**

Die Java-Modelle sollen fachliche Objektverweise verwenden, wenn Beziehungen zu anderen Modellen bestehen, statt diese Beziehungen ausschließlich über primitive ID-Felder abzubilden.

**Begründung**

Die Objektstruktur soll die fachlichen Beziehungen ausdrücken und die spätere Arbeit mit zusammengehörigen Daten vereinfachen.

**Technische Umsetzung**

Die Repository-Klassen sind dafür verantwortlich, die relationalen Datenbankdaten in Java-Objekte zu überführen.

## DD-007: Trennung von Modell und Datenbankzugriff

**Entscheidung**

Datenmodelle und Repository-Klassen werden in getrennten Paketen organisiert.

**Begründung**

Modelle beschreiben die fachlichen Daten. Repository-Klassen übernehmen das Laden, Speichern, Aktualisieren und gegebenenfalls Deaktivieren dieser Daten in der Datenbank.

Dadurch bleibt der Datenbankzugriff von der eigentlichen Darstellung der Daten getrennt.

## DD-008: Konfiguration außerhalb des Quellcodes

**Entscheidung**

Die Zugangsdaten für die Datenbank werden über Umgebungsvariablen beziehungsweise eine lokale `.env`-Datei bereitgestellt.

**Begründung**

Zugangsdaten sollen nicht fest im Quellcode stehen und nicht versehentlich in das Git-Repository gelangen.

## DD-009: Migration bestehender Daten

**Ausgangsproblem**

Die bisherigen Daten stammen aus einer bestehenden OpenOffice-Base-Datenbank und müssen in die neue Datenbankstruktur übernommen werden.

**Entscheidung**

Die Migration wird in einer separaten SQL-Datei dokumentiert und ausgeführt.

**Besonderheit**

Die bisherigen Konto-IDs wurden bei der Migration angepasst, einschließlich der zugehörigen Fremdschlüsselverweise. Dadurch wird die ID `0` als Kennzeichnung eines noch nicht gespeicherten Java-Objekts freigehalten.

**Auswirkung**

Die ursprünglichen IDs können nicht unverändert übernommen werden. Die Anpassung muss bei allen betroffenen Beziehungen konsistent erfolgen.