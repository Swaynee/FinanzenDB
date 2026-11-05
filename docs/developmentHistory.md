# Entwicklungsverlauf

## 1. Ausgangssituation

Bevor ich die Idee für dieses Projekt hatte, habe ich den Überblick über meine Finanzen mithilfe von einfachen (open office calc) spreadsheets behalten. Ich habe jedes Jahr versucht das spreadsheet des Vorjahrs zu verbessern, um eine möglichst genaue und einfache Pflege meiner Daten zu ermöglichen. Nachdem ich meine Ausbildung als Fachinformatiker Anwendungsentwicklung angefangen habe und Erfahrung im Programmieren und mit relationalen Datenbanksystemen gesammelt habe, ist mir klar geworden, dass eine kleine private Datenbank eine genauere und verlässlichere Methode darstellt, meine Daten zu verwalten.

Nachdem meine Anforderungen sehr gering waren, war ich mir sicher dass eine einfache Open Office Base Datenbank ausreichen würde. Ich habe mir ein einfaches RDM (relationales Datenmodell) zusammengestellt basierend auf
meinen bisherigen Bedürfnissen. Ich hatte zu diesem Zeitpunkt schon einige Erfahrung mit SQL in meiner Arbeitsstelle gesammelt und hatte mich darauf gefreut, meine Finanzdaten nach belieben zu selektieren, um
meine Ausgaben und Einnahmen anschaulich zu kategorisieren und zu analysieren. Ich hab das schnell herausgefunden, dass das SQL der integrierten Base Datenbank (HSQLDB) und auch die grafischen Oberflächen, die man in Base zusammenstellen kann, sehr gewöhnungsbedürftig, fehleranfällig und einfach anstrengend sind. Ich hab dann ein einfaches Eingabeformular für Transaktionen zusammengestellt, damit ich es zumindest benutzen kann.
Im Laufe des Jahres, in dem ich diese DB (Datenbank) benutzt habe, ist mir immer mehr aufgefallen, wie benutzerunfreundlich dieses Programm ist. Ich habe auch einige Makros verwendet (Apache OpenOffice Basic), was auch nicht angenehm war (veraltete Sprache).

Zum aktuellen Zeitpunkt befinde ich mich kurz vor dem Ende meiner Ausbildung und habe jetzt schon eine wesentlich besssere Vorstellung von Datenbanken, Java (JDBC) und Webdevelopment (Javascript, HTML, CSS). Aufgrunddessen habe ich mich dazu entschieden, ein größeres Projekt aus meiner nächsten Iteration einer Datenbank zu machen.
Von meiner Arbeit mit Intersystems Objectscript im Backend und Java Servlets/JSPs im Frontend auf meiner Arbeitsstelle habe ich mich inspirieren lassen, ein ähnliches Konzept zu verfolgen. In der Berufsschule haben wir
hauptsächlich mit MariaDB (MySQL) und auch Java gearbeitet. Meine Idee war deshalb beide Welten zu vereinen, indem ich mich bei meinem Backend am Intersystems Objectscript Konzept zu orientieren und Java Klassen zu benutzen, welche ich als Tabellen in MariaDB abbilde, sodass ich in Java programmieren kann und auf die Datenbank zugreifen kann, wie man es aus Intersystems Objectscript kennt.

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
