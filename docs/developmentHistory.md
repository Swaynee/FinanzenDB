# Entwicklungsverlauf

## 1. Ausgangssituation

Die bisherige Finanzverwaltung basierte auf OpenOffice Base. Ziel war es, die bestehende Lösung durch eine eigenständig entwickelte Anwendung mit einer relationalen Datenbank und einem Java-Backend zu ersetzen.

## 2. Konzeption und Datenmodell

Zu Beginn wurden die fachlichen Anforderungen und die Datenstruktur festgelegt. Dabei wurden insbesondere die Trennung von Konten und Budgets sowie die Unterscheidung zwischen Transaktionen und den daraus entstehenden Buchungen berücksichtigt.

## 3. Datenbank und Migration

Die Datenbankstruktur wurde in SQL definiert. Anschließend wurde die Migration der bestehenden Daten vorbereitet und an die neue Struktur angepasst.

Ein besonderes Problem war die Übernahme vorhandener IDs, da die ID `0` im Java-Projekt als Kennzeichnung eines noch nicht gespeicherten Objekts verwendet wird.

## 4. Einrichtung des Java-Backends

Das Backend wurde als Maven-Projekt eingerichtet. Die Java-Pakete wurden strukturiert und die Verbindung zur MariaDB-Datenbank über JDBC implementiert.

Die Konfiguration der Zugangsdaten wurde von den eigentlichen Quellcodedateien getrennt.

## 5. Modell- und Repository-Klassen

Die Datenbankentitäten wurden durch Java-Modellklassen abgebildet. Darauf aufbauend wurden Repository-Klassen für den Datenbankzugriff erstellt.

Dabei wurde eine einheitliche Struktur für CRUD-Operationen angestrebt, soweit sie für die jeweiligen Entitäten fachlich sinnvoll ist.

## 6. Nächste Entwicklungsschritte

- Geschäftslogik für finanzielle Vorgänge implementieren.
- Berechnungen und Auswertungen entwickeln.
- Frontend erstellen und mit dem Backend verbinden.
- Tests und Fehlerbehandlung erweitern.
- Benutzerhandbuch vervollständigen.