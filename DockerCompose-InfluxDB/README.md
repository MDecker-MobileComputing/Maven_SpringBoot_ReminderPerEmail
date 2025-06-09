# Container mit InfluxDB und Grafana #

<br>

Dieser Ordner enthält eine Datei `docker-compose.yml`, in der zwei Container definiert werden:

* Zeitreihendatanbank "InfluxDB"
* Dashboard-Werkzeug "Grafana"

<br>

Befehle zur Steuerung: 

* Start und Erzeugung: `docker-compose up`
* Pausieren: `docker-compose stop`
* Neustart: `docker-compose start`
* Herunterfahren und Löschen: `docker-compose down`

<br>

Beim Herunterfahren mit `docker-compose down` gehen insb. die in InfluxDB gespeicherten
Messwerte verloren, sowie die in Grafana konfigurierten Dashboards.

<br>

----

## InfluxDB als Datenquelle in Grafana hinzufügen ##

<br>

* In Grafana, Menüpunkt "Connections | Add new connection" auswählen

* In der Liste der unterstützen Datentypen "InfluxDB" auswählen

* Auf Button "Add new data source" klicken

* Als Query-Sprache "Flux" auswählen

* Als URL folgendes eingeben: http://influxdb:8086 (nicht `localhost` verwenden, weil die Container in Docker ein gemeinsames Netzwerk verwenden)

* Unter "InfluxDB Details" folgende Eingaben machen:
  * Organization: `reminder-org`
  * Token: `mein-super-geheimes-auth-token`
  * Default Buckert: `reminder-bucket`

* Danach auf den Button "Save & test" klicken

* Es sollte danach ein Kasten mit dem Text "datasource is working. ... buckets found" angezeigt werden.

<br>

---

## Flux Query für Zeitreihendiagramm ##

<br>


<br>

```
from(bucket: "reminder-bucket")
  |> range(start: -30d) // Adjust time range as needed
  |> filter(fn: (r) => r._measurement == "reminder_anzahl")
  |> filter(fn: (r) => r._field == "schon_versendet" or r._field == "nicht_versendet")
  ``` 