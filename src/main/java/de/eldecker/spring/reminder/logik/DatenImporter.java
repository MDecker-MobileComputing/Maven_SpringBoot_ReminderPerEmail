package de.eldecker.spring.reminder.logik;

import static java.time.Year.now;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * Diese Bean-Klasse enthält Logik, die unmittelbar nach Initialisierung der
 * Spring-Boot-Anwendung ausgeführt wird.
 */
@Component
public class DatenImporter implements ApplicationRunner {

	private static Logger LOG = LoggerFactory.getLogger( DatenImporter.class );
	
	
	/** Bean mit Geschäftslogik für Verwaltung von Reminder. */
	private ReminderService _reminderService;

	
	/**
	 * Konstruktor für Dependency Injection.
	 */
	@Autowired
	public DatenImporter(ReminderService reminderService) {

		_reminderService = reminderService;
	}

	
	/**
	 * Sobald die Anwendung gestartet wurde, wird diese Methode aufgerufen. Hier
	 * werden die Metriken für die Anzahl der Reminder in die InfluxDB geschrieben.
	 */
	@Override
	public void run( ApplicationArguments args ) throws Exception {
		
		final int anzahlReminder = _reminderService.erfasseAnzahlReminderInInfluxDB();
		
		if ( anzahlReminder > 0 ) {
									
			LOG.info( 
				"Es sind schon {} Reminder in der DB, lade deshalb keine Demo-Daten.",
				anzahlReminder );			
			
		} else {

			final int naechstesJahr = now().plusYears( 1 ).getValue();
			
			_reminderService.reminderAnlegen( 1, 1, naechstesJahr, 8, 30, 					 
                                              "Heute Sauerkraut essen" );				
			
			_reminderService.reminderAnlegen( 23, 12, naechstesJahr, 12, 0, 					 
					                          "Schon Geschenke für morgen gekauft?" );			
			
			_reminderService.reminderAnlegen( 30, 4, naechstesJahr, 10, 15, 
				    					      "Bier für morgige Maifeiertag besorgen" );

			_reminderService.reminderAnlegen( 31, 10, naechstesJahr, 15, 0, 
				                              "Halloween – Süßes oder Saures vorbereiten!" );

			_reminderService.reminderAnlegen( 5, 12, naechstesJahr, 7, 30, 
				                              "Stiefel für Nikolaus rausstellen!");			
		}
	}

}
