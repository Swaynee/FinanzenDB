## **Service-Schicht und Validierungen**

Die Service-Schicht ist inzwischen für folgende Bereiche implementiert:

- `KontoService`
- `BudgetService`
- `TransaktionsartService`
- `TransaktionService`
- `BuchungService`
- `BudgetbuchungService`
- `NotizService`

Die Services übernehmen:

- Fachliche Validierungen
- Prüfung von IDs
- Prüfung der Existenz referenzierter Daten
- Koordination von Repository-Aufrufen
- Logging erfolgreicher Operationen

### **Grundsatz für Validierungen**

Jeder Service besitzt eine eigene private Methode:

```
private void validiere(...)
```

Die Services sollen ihre eigenen Objekte und deren grundlegende fachliche Anforderungen verantworten.

### **Vermeidung doppelter Validierungen**

Ein Beispiel ist die Transaktionsart:

- `TransaktionsartService` prüft die Pflichtfelder einer Transaktionsart.
- `TransaktionService` prüft die Transaktion und die Existenz ihrer Transaktionsart.
- `BuchungService` prüft die Existenz der zugehörigen Transaktion.

Der `BuchungService` muss die Transaktionsart daher nicht noch einmal separat über den `TransaktionsartService` abrufen.

## **4. Fehlerbehandlung und Exceptions**

Wir haben folgende Unterscheidung festgelegt:

| Exception                  | Bedeutung                                |
| -------------------------- | ---------------------------------------- |
| `IllegalArgumentException` | Übergebene Daten sind fachlich ungültig  |
| `SQLException`             | Technischer Fehler beim Datenbankzugriff |

Beispiel:

```
private void validiere(Transaktion transaktion) throws SQLException
{
    if (transaktion == null)
        throw new IllegalArgumentException(
                "Die Transaktion darf nicht null sein.");

    if (transaktionsartRepository.getById(
            transaktion.getTransaktionsart().getId()) == null)
        throw new IllegalArgumentException(
                "Die angegebene Transaktionsart existiert nicht.");
}
```

Eine mögliche `SQLException` wird nicht in eine `IllegalArgumentException` umgewandelt.

Stattdessen:

- Repository wirft die `SQLException`.
- Service reicht sie weiter.
- Die übergeordnete Anwendungsschicht behandelt sie und entscheidet über die Darstellung gegenüber dem Benutzer.

**Logging:**

Exceptions sollen grundsätzlich dort geloggt werden, wo sie tatsächlich behandelt werden. Dadurch wird vermieden, dass dieselbe Exception auf mehreren Ebenen mehrfach protokolliert wird.

## **5. Transaktionen und Buchungen**

Ein wichtiger Teil der fachlichen Logik betrifft die Beziehung zwischen Transaktionen und Buchungen.

Eine Transaktion kann mehrere Buchungen besitzen. Zusätzlich können Budgetbuchungen zu einer Transaktion gehören.

### **Löschen einer Transaktion**

In der Datenbank bestehen `ON DELETE CASCADE`-Beziehungen zwischen `Transaktion` und ihren untergeordneten Buchungen beziehungsweise Budgetbuchungen.

Daraus folgt:

- Der `TransaktionService` ruft das Löschen im `TransaktionRepository` auf.
- Die Datenbank löscht die abhängigen Buchungen automatisch.
- Es sind keine zusätzlichen manuellen DELETE-Abfragen notwendig.

### **Erstellen einer Transaktion mit Buchungen**

Hier ist die Situation anders.

Beispiel:

1. Transaktion erstellen.
2. Buchung für Konto A erstellen.
3. Buchung für Konto B erstellen.
4. Budgetbuchung erstellen.

Diese Schritte gehören fachlich zusammen.

Wenn beispielsweise die dritte Operation fehlschlägt, dürfen die ersten beiden Änderungen nicht dauerhaft bestehen bleiben.

Deshalb benötigen wir eine gemeinsame Datenbanktransaktion mit:

- `Connection`
- `setAutoCommit(false)`
- `commit()`
- `rollback()`

Die fachliche Koordination gehört in die Service-Schicht. Die technische Transaktionssteuerung soll durch eine geeignete Datenbankinfrastruktur erfolgen.
