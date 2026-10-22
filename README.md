# FinanzenDB

## Projektbeschreibung

FinanzenDB ist eine Anwendung zur Verwaltung persönlicher Finanzen. Sie soll die bisherige Finanzverwaltung mit OpenOffice Base durch eine eigene Anwendung mit einer MariaDB-Datenbank, einem Java-Backend und einem Webfrontend ersetzen.

Ziel ist es, Einnahmen und Ausgaben, Konten, Budgets und wiederkehrende Transaktionen strukturiert zu verwalten und die finanzielle Situation sowohl auf Konto- als auch auf Budgetebene nachvollziehbar darzustellen.

## Technologien

- **Datenbank:** MariaDB
- **Backend:** Java
- **Build-System:** Maven
- **Datenbankzugriff:** JDBC
- **Konfiguration:** Umgebungsvariablen über eine `.env`-Datei
- **Versionsverwaltung:** Git
- **Entwicklungsumgebung:** Visual Studio Code

## Projektstruktur

- `backend/` – Java-Backend und Datenbankdateien
- `frontend/` – vorgesehenes Webfrontend
- `backend/database/schema.sql` – Definition der Datenbankstruktur
- `backend/database/migrate.sql` – Migration der bestehenden Daten

## Aktueller Entwicklungsstand

Die Datenbankstruktur wurde erstellt und die Übernahme der bisherigen Daten vorbereitet beziehungsweise angepasst. Die Datenbankverbindung des Java-Backends ist eingerichtet. Die Modell- und Repository-Klassen bilden die Datenstruktur objektorientiert in Java ab und stellen den Datenbankzugriff bereit.

Die Benutzeroberfläche und die weitere Anwendungslogik werden im weiteren Entwicklungsverlauf ergänzt.

## Einrichtung

Die genauen Installations- und Startanweisungen werden in der technischen Dokumentation ergänzt.

Datenbankzugangsdaten werden lokal konfiguriert und nicht in das Git-Repository eingecheckt.