package de.eldecker.spring.reminder.db_influx;

import static java.time.Instant.now;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;


/**
 * Bean-Klasse für das Schreiben von Werten in die Zeitreihendatenbank "InfluxDB".
 */
public class InfluxDB {

    private static Logger LOG = LoggerFactory.getLogger( InfluxDB.class );
    
    
    /** Bean für Zugriff auf InfluxDB-Instanz. */
    private final InfluxDBClient _influxDBClient;
    
    /**
     * Name einer Measurement.
     * Eine Measurement kann mehrere Zeitreihen enthalten, wobei die einzelnen
     * Zeitreihen dann durch Tag-Werte unterschieden werden.
     */
    private static final String MEASUREMENT_REMINDER_ANZAHL = "reminder_anzahl";
    
    /** Feld für {@link #MEASUREMENT_REMINDER_ANZAHL} für Anzahl bereits versendeter Reminder. */ 
    private static final String FELD_SCHON_VERSENDET = "schon_versendet";
    
    /** Feld für {@link #MEASUREMENT_REMINDER_ANZAHL} für Anzahl noch nicht versendeter Reminder. */
    private static final String FELD_NICHT_VERSENDET = "nicht_versendet";
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public InfluxDB( InfluxDBClient influxDbClient ) {
        
        _influxDBClient = influxDbClient;
    }
    
    
    public void verbuche( int anzahlSchonVersendet, int anzahlNochNichtVersendet ) {
        
        try {
            
            final WriteApiBlocking influxSchreiber = _influxDBClient.getWriteApiBlocking();
            
            
            final Point datenpunkt = Point.measurement( MEASUREMENT_REMINDER_ANZAHL )
                                          .addField( FELD_SCHON_VERSENDET , anzahlSchonVersendet    )
                                          .addField( FELD_NICHT_VERSENDET , anzahlNochNichtVersendet)
                                          .time( now(), WritePrecision.S );
            
            influxSchreiber.writePoint( datenpunkt );
            
        }
        catch ( Exception ex ) {
            
            LOG.error( 
                    "Fehler beim Versuch Zeitreihenwerte fuer (nicht)versendete Reminder zu schreiben.", 
                    ex );                     
        }
    }
    
}
