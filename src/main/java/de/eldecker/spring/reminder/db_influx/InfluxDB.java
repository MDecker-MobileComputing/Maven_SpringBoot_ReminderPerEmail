package de.eldecker.spring.reminder.db_influx;

import static java.time.Instant.now;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;


/**
 * Bean-Klasse für das Schreiben von Werten in die Zeitreihendatenbank "InfluxDB".
 * <br><br>
 *
 * Zum Begriff "Measurement":
 * Eine Measurement kann mehrere Zeitreihen enthalten, wobei die einzelnen
 * Zeitreihen dann durch Tags unterschieden werden.
 */
@Component
public class InfluxDB {

    private static Logger LOG = LoggerFactory.getLogger( InfluxDB.class );


    /** Bean für Zugriff auf InfluxDB-Instanz. */
    private final InfluxDBClient _influxDBClient;

    /** Measurement für Anzahl versendeter und ausstehender Reminder. */
    private static final String MEASUREMENT_REMINDER_ANZAHL = "reminder_anzahl";

    /** Measurement für Anzahl versendeter Emails. */
    private static final String MEASUREMENT_REMINDER_EMAILS = "emails_versendet";

    /** Feld für {@link #MEASUREMENT_REMINDER_ANZAHL} für Anzahl bereits versendeter Reminder. */
    private static final String FELD_SCHON_VERSENDET = "schon_versendet";

    /** Feld für {@link #MEASUREMENT_REMINDER_ANZAHL} für Anzahl noch nicht versendeter Reminder. */
    private static final String FELD_NICHT_VERSENDET = "nicht_versendet";

    /** Feld für {@link #MEASUREMENT_REMINDER_EMAILS} . */
    private static final String FELD_ANZAHL = "anzahl";
    
    @Value( "${influxdb.bucket}" )
    private String _bucket;


    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public InfluxDB( InfluxDBClient influxDbClient ) {

        _influxDBClient = influxDbClient;
    }


    /**
     * Methode, um Metrikwerte für die Anzahl der bereits versendeten und noch nicht
     * versendeten Reminder in die InfluxDB zu schreiben.
     *
     * @param anzahlSchonVersendet Anzahl der bereits versendeten Reminder
     *
     * @param anzahlNochNichtVersendet Anzahl der Reminder, die noch nicht versendet wurden
     */
    public void schreibeAnzahlReminder( int anzahlSchonVersendet, int anzahlNochNichtVersendet ) {

        try {

            final WriteApiBlocking influxSchreiber = _influxDBClient.getWriteApiBlocking();

            final Point datenpunkt = Point.measurement( MEASUREMENT_REMINDER_ANZAHL )
                                          .addField( FELD_SCHON_VERSENDET , anzahlSchonVersendet     )
                                          .addField( FELD_NICHT_VERSENDET , anzahlNochNichtVersendet )
                                          .time( now(), WritePrecision.S );

            influxSchreiber.writePoint( datenpunkt );

            LOG.info( "Metrikwerte in InfluxDB geschrieben: schon versendet={}, nicht versendet={}",
                      anzahlSchonVersendet, anzahlNochNichtVersendet );
        }
        catch ( InfluxException ex ) {

            LOG.error(
                    "Fehler beim Versuch Zeitreihenwerte fuer (nicht)versendete Reminder zu schreiben.",
                    ex );
        }
    }


    /**
     * Anzahl der Emails, die bei einem Lauf des "Versende-Jobs" versendet wurden, in
     * InfluxDB schreiben.
     *
     * @param anzahlReminderVersendet Anzahl Emails, die versendet wurden, kann auch 0 sein
     *                                (wird in den meisten Fällen 0 sein?)
     */        
    public void schreibeAnzahlEmails( int anzahlReminderVersendet ) {

        try {

            final WriteApiBlocking influxSchreiber = _influxDBClient.getWriteApiBlocking();

            final Point datenpunkt = Point.measurement( MEASUREMENT_REMINDER_EMAILS )
                                          .addField( FELD_ANZAHL, anzahlReminderVersendet )
                                          .time( now(), WritePrecision.S );

            influxSchreiber.writePoint( datenpunkt );
        }
        catch ( InfluxException ex ) {

            LOG.error(
                    "Fehler beim Versuch Anzahl versendeter Emails zu schreiben.", ex );                    
        }
    }
    
    
    /**
     * Methode liefert Anzahl der insgesamt versendeten Emails zurück.
     *  
     * @return Anzahl insgesamt versendeter Emails, {@code -1} wenn hierbei ein
     *         Fehler aufgetreten ist.
     */
    
    public int getGesamtzahlEmails() {

    	final String fluxQuery = 
	    	"""
	        from(bucket: "%s")
	          |> range(start: 0)
	          |> filter(fn: (r) => r._measurement == "%s")
	          |> filter(fn: (r) => r._field       == "%s")
	          |> sum()
	        """.formatted( _bucket, MEASUREMENT_REMINDER_EMAILS, FELD_ANZAHL );

        try {
        	
            final QueryApi queryApi = _influxDBClient.getQueryApi();
            
            final List<FluxTable> tables = queryApi.query( fluxQuery );
            
            for ( FluxTable table : tables ) {
            	
                for ( FluxRecord record : table.getRecords() ) {
                	
                    final Object value = record.getValue();
                    if ( value instanceof Number ) {
                    	
                        return ((Number) value).intValue();
                    }
                }
            }
            
        } catch ( Exception ex ) {
        	
            LOG.error( "Fehler beim Lesen der Gesamtanzahl versendeter Emails aus InfluxDB.", ex );
        }        
        
        return -1;
    }    

}
