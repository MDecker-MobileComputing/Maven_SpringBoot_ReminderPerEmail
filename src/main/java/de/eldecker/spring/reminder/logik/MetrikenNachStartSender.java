package de.eldecker.spring.reminder.logik;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * Diese Bean-Klasse enthält Logik, die unmittelbar nach Initialisierung
 * der Spring-Boot-Anwendung ausgeführt wird.
 */
@Component
public class MetrikenNachStartSender implements ApplicationRunner {

	/** Bean mit Geschäftslogik für Verwaltung von Reminder. */
    private ReminderService _reminderService;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public MetrikenNachStartSender( ReminderService reminderService ) {
        
        _reminderService = reminderService;
    }
    
    
    /**
     * Sobald die Anwendung gestartet wurde, wird diese Methode aufgerufen.
     * Hier werden die Metriken für die Anzahl der Reminder in die InfluxDB geschrieben.
     */
    @Override
    public void run( ApplicationArguments args ) throws Exception {
        
        _reminderService.erfasseAnzahlReminderInInfluxDB();
    }
    
}
