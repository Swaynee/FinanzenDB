# Architektur

## 1. Überblick

FinanzenDB wird in getrennte Bereiche für Datenhaltung, Backend und Frontend aufgeteilt.

## 2. Datenhaltung

Die Daten werden in einer MariaDB-Datenbank gespeichert. Die Tabellenstruktur wird durch SQL-Dateien definiert. Für die Übernahme der bestehenden Daten existiert eine separate Migrationsdatei.

## 3. Java-Backend

Das Backend basiert auf Java und Maven. Die Java-Klassen sind im Package `de.finanzen` organisiert.

### Model

Das Package `de.finanzen.model` enthält die Klassen, welche die fachlichen Daten und ihre Beziehungen in Java abbilden.

### Repository

Das Package `de.finanzen.repository` enthält die Klassen für den Datenbankzugriff. Dazu gehören das Laden, Speichern, Aktualisieren und gegebenenfalls Deaktivieren von Datensätzen.

### Database

Die Datenbankverbindung wird über `DatabaseConnection` bereitgestellt. Der Zugriff erfolgt über JDBC.

### Logging

Das Logging wird zentral konfiguriert. Die Konfiguration legt unter anderem fest, welche Meldungen in der Konsole und in der Logdatei ausgegeben werden.

## 4. Konfiguration

Die Datenbankverbindung wird über Umgebungsvariablen konfiguriert. Die lokale `.env`-Datei enthält die Zugangsdaten und wird nicht in das Git-Repository aufgenommen.

## 5. Fehlerbehandlung

Datenbankoperationen können SQL-Fehler verursachen. Die Repository-Klassen behandeln diese Fehler entsprechend ihrer Implementierung und geben sie gegebenenfalls an aufrufende Komponenten weiter.

## 6. Geplante Erweiterungen

Das Frontend und die Verbindung zwischen Benutzeroberfläche und Backend werden im weiteren Entwicklungsverlauf ergänzt. Die konkrete Schnittstelle wird dokumentiert, sobald sie festgelegt ist.