package de.eldecker.spring.reminder.logik;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.time.DateTimeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.eldecker.spring.reminder.db.ReminderEntity;
import de.eldecker.spring.reminder.db.ReminderRepo;
import de.eldecker.spring.reminder.model.ReminderException;


/**
 * Bean-Klasse mit Logik für Verwaltung von Remindern. 
 */
@Service
public class ReminderService {

    /** Repo-Bean für Zugriff auf Tabelle mit Remindern. */
    private ReminderRepo _reminderRepo;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */    
    @Autowired
    public ReminderService( ReminderRepo reminderRepo ) {
       
        _reminderRepo = reminderRepo;
    }
    
    
    /**
     * Neuen Reminder einplanen.
     * 
     * @param tag Tag von Fälligkeitsdatum (1..31)
     * 
     * @param monat Montag von Fälligkeitsdatum (1..12)
     * 
     * @param jahr Vierstelliges Jahr von Fälligkeitsdatum, 
     *             darf nicht in Vergangenheit liegen (2025..)
     * 
     * @param stunde Stunde von Fälligkeitszeitpunkt (0..23)
     * 
     * @param minute Minute von Fälligkeitszeitpunkt (0..59)
     * 
     * @param Text für Betreffzeile der Reminder-Email
     * 
     * @return ID von neu angelegtem Reminder
     * 
     * @throws ReminderException Ungültiger Fälligkeitzeitpunkt spezifiziert
     *                           (inkl. Zeitpunktei in Vergangenheit) oder
     *                           leeren {@code reminderText} 
     */
    public long reminderAnlegen( int tag, int monat, int jahr,
                                 int stunde, int minute,
                                 String reminderText )
                    throws ReminderException {

        reminderText = reminderText.trim();        
        if ( reminderText.isEmpty() ) {
            
            throw new ReminderException( "Leerer Reminder-Text" );
        }
        
        LocalDateTime faelligkeitsZeitraum = null;
        
        try {
            
            faelligkeitsZeitraum = LocalDateTime.of( jahr, monat, tag,                                     
                                                     stunde, minute );                                                          
            final LocalDateTime now = now();
            if ( faelligkeitsZeitraum.isBefore( now ) ) {
                
                throw new ReminderException( "Der angegebene Zeitpunkt liegt in der Vergangenheit." );
            }
            
        } catch ( DateTimeException ex ) {
            
            final String zeitpunkt = String.format( "%d.%d.%d, %d:%d Uhr",                      
                                                    tag, monat, jahr, 
                                                    stunde, minute );
            
            throw new ReminderException( "Ungueltiger Faelligkeitszeitpunkt: " + zeitpunkt );
        }
        
        final ReminderEntity reminderEntity = new ReminderEntity( reminderText,  faelligkeitsZeitraum );
        _reminderRepo.save( reminderEntity );
        
        return reminderEntity.getId();
    }
    
}
