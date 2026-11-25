## **JUnit und Mockito**

Wir haben uns entschieden, JUnit für automatisierte Tests zu verwenden.

Die Integration ist bereits abgeschlossen:

- JUnit Jupiter: `6.1.3`
- Mockito: `5.24.0`
- Maven Surefire: `3.5.4`
- Tests liegen unter `src/test/java`.

Mockito wird verwendet, um Abhängigkeiten innerhalb von Unit-Tests zu simulieren.

Dadurch können wir beispielsweise einen Service testen, ohne eine echte Datenbankverbindung aufzubauen.
