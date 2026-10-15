___

##### **Budget und Konto sind unabhängig**

*DD-001*

**Problem**

> Eine Ausgabe kann aus mehreren Konten bezahlt werden, soll aber trotzdem demselben Budget zugeordnet werden können.

**Entscheidung**

> Bin Budget ist unabhängig von einem Konto. Die Kontobewegungen und die Budgetzuordnungen werden getrennt abgebildet.

**Begründung**

> Dadurch können Ausgaben auf mehrere Konten verteilt werden, ohne das Budget aufzuteilen oder doppelt zu erfassen.

**Konsequenzen**

- Eine Transaktion kann mehrere Kontobuchungen besitzen.
- Budgetbuchungen werden unabhängig von den Kontobuchungen verwaltet.
- Die Auswertung des tatsächlichen Kontostands unterscheidet sich von der Auswertung des verbleibenden Budgets.

___

DD-002: Warum die Datenbankzugriffe über Repository-Klassen laufen.



DD-003: Warum die Modelle echte Objektverweise statt ausschließlich Fremdschlüssel-IDs verwenden.



DD-004: Warum Transaktion, Buchung und Budgetbuchung getrennte Konzepte sind.



DD-005: Warum Konten deaktiviert statt endgültig gelöscht werden.



DD-006: Wie wiederkehrende Transaktionen und deren Planungsmonat funktionieren.



DD-007: Warum bestimmte Daten nullable sind und wie damit umgegangen wird.



DD-008: Warum Java und MariaDB anstelle von OpenOffice Base verwendet werden.