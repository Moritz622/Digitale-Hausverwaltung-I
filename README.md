Digitale Hausverwaltung
Projektbeschreibung
Dieses Projekt dient der Entwicklung einer digitalen Hausverwaltung. Es umfasst die Speicherung und Verwaltung von Kunden- und Ablesungsdaten mithilfe einer Datenbank sowie die Bereitstellung einer REST-Schnittstelle für den Zugriff auf diese Daten.

Inhalt
Projektstruktur
Technologien
Funktionen
Setup
API-Endpunkte
Teammitglieder
Projektstruktur
Das Projekt gliedert sich in zwei Sprints:

Sprint 1: Datenbankanbindung
Aufbau einer MariaDB-Datenbank.
Implementierung von CRUD-Funktionalitäten mit JDBC.
Sprint 2: REST-Schnittstelle
Entwicklung einer REST-API mit Jersey, basierend auf den CRUD-Operationen.
Technologien
Java (Version 17 oder höher)
MariaDB (Version 11.3 oder höher)
Jersey-Framework (REST-Schnittstelle)
Maven (Build- und Dependency-Management)
JUnit & REST-assured (Test-Frameworks)
Funktionen
Kundenverwaltung:
Kunden können erstellt, aktualisiert, abgerufen und gelöscht werden.
Ablesungsmanagement:
Ablesungsdaten können erfasst, aktualisiert, abgerufen und gelöscht werden.
REST-API:
JSON-basierte Endpunkte für CRUD-Operationen.
Datenbankmanagement:
Dynamische Verwaltung der Tabellen mit Testzwecken (z. B. Reset).
Setup
Voraussetzungen:

Java (JDK 17+)
MariaDB-Server
Maven (mind. Version 3.6)
Installation:

Repository klonen:
bash
Kopieren
Bearbeiten
git clone <repository-url>
Datenbank einrichten:
Datenbank-Verbindungsinformationen in der Datei config.properties eintragen.
Projekt kompilieren:
bash
Kopieren
Bearbeiten
mvn clean install
REST-API starten:
bash
Kopieren
Bearbeiten
mvn exec:java@run-server
API-Endpunkte
Hier sind die Hauptendpunkte der REST-API:

Pfad	Methode	Beschreibung
/customers	GET	Liste aller Kunden abrufen
/customers/{uuid}	GET	Details eines Kunden abrufen
/customers	POST	Neuen Kunden erstellen
/customers/{uuid}	PUT	Kunden aktualisieren
/customers/{uuid}	DELETE	Kunden löschen
/readings	GET	Liste aller Ablesungen abrufen
/readings	POST	Neue Ablesung erstellen
/readings/{uuid}	PUT	Ablesung aktualisieren
/readings/{uuid}	DELETE	Ablesung löschen
/setupDB	DELETE	Datenbanktabellen zurücksetzen
Weitere Details zu den Endpunkten findest du im Kommunikationsprotokoll.

Teammitglieder
[Name 1] – Entwickler
[Name 2] – Datenbank-Spezialist
[Name 3] – API-Entwickler
