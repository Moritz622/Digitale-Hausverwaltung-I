<h1>Digitale Hausverwaltung</h1>

<h2>Projektbeschreibung</h2>
<p>
Dieses Projekt dient der Entwicklung einer digitalen Hausverwaltung. Es umfasst die Speicherung und Verwaltung
von Kunden- und Ablesungsdaten mithilfe einer Datenbank sowie die Bereitstellung einer REST-Schnittstelle für
den Zugriff auf diese Daten.
</p>

<h2>Inhalt</h2>
<ol>
<li><p>Projektstruktur</p></li>
<li><p>Technologien</p></li>
<li><p>Funktionen</p></li>
<li><p>Setup</p></li>
<li><p>API-Endpunkte</p></li>
<li><p>Teammitglieder</p></li>
</ol>

<h2 id="projektstruktur">Projektstruktur</h2>
<p>
Das Projekt gliedert sich in zwei Sprints:
</p>
<ul>
<li><strong>Sprint 1: Datenbankanbindung</strong>
<ul>
<li>Aufbau einer <strong>MariaDB-Datenbank</strong>.</li>
<li>Implementierung von CRUD-Funktionalitäten mit JDBC.</li>
</ul>
</li>
<li><strong>Sprint 2: REST-Schnittstelle</strong>
<ul>
<li>Entwicklung einer REST-API mit <strong>Jersey</strong>, basierend auf den CRUD-Operationen.</li>
</ul>
<li><strong>Sprint 3: Frontend fur die digitale Hausverwaltung und Abschluss
des Projekts</strong>
</li>
</ul>

<h2 id="technologien">Technologien</h2>
<ul>
<li><strong>Java</strong> (Version 17 oder höher)</li>
<li><strong>MariaDB</strong> (Version 11.3 oder höher)</li>
<li><strong>Jersey-Framework</strong> (REST-Schnittstelle)</li>
<li><strong>Maven</strong> (Build- und Dependency-Management)</li>
<li><strong>JUnit</strong> &amp; <strong>REST-assured</strong> (Test-Frameworks)</li>
</ul>

<h2 id="funktionen">Funktionen</h2>
<ul>
<li><strong>Kundenverwaltung</strong>:
<ul>
<li>Kunden können erstellt, aktualisiert, abgerufen und gelöscht werden.</li>
</ul>
</li>
<li><strong>Ablesungsmanagement</strong>:
<ul>
<li>Ablesungsdaten können erfasst, aktualisiert, abgerufen und gelöscht werden.</li>
</ul>
</li>
<li><strong>REST-API</strong>:
<ul>
<li>JSON-basierte Endpunkte für CRUD-Operationen.</li>
</ul>
</li>
<li><strong>Datenbankmanagement</strong>:
<ul>
<li>Dynamische Verwaltung der Tabellen mit Testzwecken (z. B. Reset).</li>
</ul>
</li>
</ul>

<li>Datenbank einrichten:
<p>Datenbank-Verbindungsinformationen in der Datei <code>config.properties</code> eintragen.</p>
</li>
<li>Projekt kompilieren:
<pre><code>mvn clean install</code></pre>
</li>
<li>REST-API starten:
<pre><code>mvn exec:java@run-server</code></pre>
</li>
</ol>
</li>
</ol>

![Unbenanntes Diagramm drawio](https://github.com/user-attachments/assets/f383803b-c271-4185-ad73-0d15b48e2bb8)

<h2 id="api-endpunkte">API-Endpunkte</h2>
<table>
<thead>
<tr>
<th>Pfad</th>
<th>Methode</th>
<th>Beschreibung</th>
</tr>
</thead>
<tbody>
<tr>
<td><code>/customers</code></td>
<td>GET</td>
<td>Liste aller Kunden abrufen</td>
</tr>
<tr>
<td><code>/customers/{uuid}</code></td>
<td>GET</td>
<td>Details eines Kunden abrufen</td>
</tr>
<tr>
<td><code>/customers</code></td>
<td>POST</td>
<td>Neuen Kunden erstellen</td>
</tr>
<tr>
<td><code>/customers/{uuid}</code></td>
<td>PUT</td>
<td>Kunden aktualisieren</td>
</tr>
<tr>
<td><code>/customers/{uuid}</code></td>
<td>DELETE</td>
<td>Kunden löschen</td>
</tr>
<tr>
<td><code>/readings</code></td>
<td>GET</td>
<td>Liste aller Ablesungen abrufen</td>
</tr>
<tr>
<td><code>/readings</code></td>
<td>POST</td>
<td>Neue Ablesung erstellen</td>
</tr>
<tr>
<td><code>/readings/{uuid}</code></td>
<td>PUT</td>
<td>Ablesung aktualisieren</td>
</tr>
<tr>
<td><code>/readings/{uuid}</code></td>
<td>DELETE</td>
<td>Ablesung löschen</td>
</tr>
<tr>
<td><code>/setupDB</code></td>
<td>DELETE</td>
<td>Datenbanktabellen zurücksetzen</td>
</tr>
</tbody>
</table>

<h2 id="teammitglieder">Teammitglieder</h2>
<ul>
<li>Marco</li>
<li>Moritz</li>
<li>Nian</li>

