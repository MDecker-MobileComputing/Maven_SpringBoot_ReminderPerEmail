# Container mit InfluxDB und Grafana #

<br>

Dieser Ordner enthält eine Datei `docker-compose.yml`, in der zwei Container definiert werden:

* Zeitreihendatanbank "InfluxDB"
* Dashboard-Werkzeug "Grafana"

<br>

**Befehle zur Steuerung:**

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

## Flux-Query für Zeitreihendiagramm ##

<br>

Erzeugen Sie ein Dashboard und fügen Sie eine "Visualization" mit Typ "Time series" hinzu.
Stellen Sie sicher, dass die oben angelegte "Data Source" ausgewählt ist und geben Sie dann die folgende Flux-Query ein:

```
from(bucket: "reminder-bucket")
  |> range(start: -30d) // Adjust time range as needed
  |> filter(fn: (r) => r._measurement == "reminder_anzahl")
  |> filter(fn: (r) => r._field == "schon_versendet" or r._field == "nicht_versendet")
```

Klicken Sie nach Eingabe der Query in ein anderes Feld, z.B. das Feld "Title" in der Leiste auf der rechten Seite, um folgendes einzugeben: "Reminder: Versendet und ausstehend"

In der linken Leiste gibt es auch eine Sektion "Standard Option" (muss evtl. aufgeklappt werden), in der man als "Min"-Wert `0` eingeben kann.

Um die "Visualization" zu speichern ist auf den blauen Button "Apply" rechts oben zu klicken.

<br>

Die folgende Flux-Query zeigt nur eine Zeitreihe an, bei der die Gesamtzahl der Reminder (egal ob schon versendet oder nicht) dargestellt wird:

```
from(bucket: "reminder-bucket")
  |> range(start: -30d)
  |> filter(fn: (r) => r._measurement == "reminder_anzahl")
  |> filter(fn: (r) => r._field == "schon_versendet" or r._field == "nicht_versendet")
  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
  |> map(fn: (r) => ({
      _time: r._time,
      _value: r.schon_versendet + r.nicht_versendet,
      _field: "total_versendet",
      _measurement: r._measurement,
      // Include any other tags you need to preserve
    }))
```

<br>

Noch eine Flux-Query für das Measurement mit der Anzahl der versendeten Emails:
```
from(bucket: "reminder-bucket")
  |> range(start: -24h)
  |> filter(fn: (r) => r._measurement == "emails_versendet")
```

<br>

Weitere Infos zur Abfragesprache "Flux": https://awesome.influxdata.com/docs/part-2/introduction-to-flux/

<br>
