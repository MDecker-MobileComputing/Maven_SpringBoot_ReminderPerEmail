package de.eldecker.spring.reminder.logik;

import static de.eldecker.spring.reminder.db.ReminderRepo.buildSpecFuerReminderZumVersenden;
import static java.time.LocalDateTime.now;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled; // Added import
import org.springframework.stereotype.Service;

import de.eldecker.spring.reminder.EmailReminderApplication;
import de.eldecker.spring.reminder.db.ReminderEntity;
import de.eldecker.spring.reminder.db.ReminderRepo;
import de.eldecker.spring.reminder.email.EmailSender;
import de.eldecker.spring.reminder.model.ReminderException;


/**
 * Bean-Klasse mit Logik für Verwaltung von Remindern. 
 */
@Service
public class ReminderService {
    
    private static Logger LOG = LoggerFactory.getLogger( ReminderService.class );
    
    /** Sortierobjekt, um Reminder-Liste nach Fälligkeit aufsteigend zu sortieren. */
    private final Sort _sortByZeitpunktFaelligAsc = Sort.by( ASC, "_zeitpunktFaellig" );    

    /** Repo-Bean für Zugriff auf Tabelle mit Remindern. */
    private ReminderRepo _reminderRepo;
    
    /** Bean, um Emails zu versenden. */
    private EmailSender _emailSender;
    
    
    /**
     * Konstruktor für Dependency Injection.
     */    
    @Autowired
    public ReminderService( ReminderRepo reminderRepo,
                            EmailSender emailSender ) {
       
        _reminderRepo = reminderRepo;
        _emailSender  = emailSender;
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
        
        LocalDateTime faelligkeitsZeitpunkt = null;
        
        try {
            
            faelligkeitsZeitpunkt = LocalDateTime.of( jahr, monat, tag,                                     
                                                     stunde, minute );                                                          
            final LocalDateTime now = now();
            if ( faelligkeitsZeitpunkt.isBefore( now ) ) {
                
                throw new ReminderException( "Der angegebene Zeitpunkt liegt in der Vergangenheit." );
            }
            
        } catch ( DateTimeException ex ) {
            
            final String zeitpunkt = String.format( "%d.%d.%d, %d:%d Uhr",                      
                                                    tag, monat, jahr, 
                                                    stunde, minute );
            
            throw new ReminderException( "Ungültiger Fälligkeitszeitpunkt: " + zeitpunkt );
        }
        
        final ReminderEntity reminderEntity = new ReminderEntity( reminderText, faelligkeitsZeitpunkt );
        _reminderRepo.save( reminderEntity );
        
        LOG.info( "Neuer Reminder mit Faelligkeitszeitpunkt {} angelegt.", faelligkeitsZeitpunkt );
        
        return reminderEntity.getId();
    }
    
    
    /**
     * Gibt alle Reminder (auch solche, für die schon die Email versendet wurde), zurück.
     * 
     * @return Aller Reminder, aufsteigend sortiert nach Fälligkeitszeitpunkt
     */
    public List<ReminderEntity> getAlleReminderSortiert() {
        
        return _reminderRepo.findAll( _sortByZeitpunktFaelligAsc );
    }
    
    
    /**
     * Gibt Liste aller Reminder, für die noch keine Emails versendet wurde, zurück.
     * 
     * @return Aller Reminder, aufsteigend sortiert nach Fälligkeitszeitpunkt
     */
    public List<ReminderEntity> getAlleReminderNochNichtVersendet() {
        
        return _reminderRepo.findAll( _sortByZeitpunktFaelligAsc );
    }


    /**
     * Methode zum Versenden fälliger Reminder, wird jede Minute ausgeführt.
     * Für "Einschalten" der Annotation "Scheduled" wurde die Klasse 
     * {@link EmailReminderApplication} mit Annotation {@code EnableScheduling}
     * versehen.
     * <br><br>
     * 
     * Werte der Attribute der Annotation {@code Scheduled} sind jeweils
     * Zeitspannen in Millisekunden. 
     */
    @Scheduled( initialDelay = 30_000, fixedRate = 60_000 )
    public void versendeEmails() {
        
        final Specification<ReminderEntity> spec = buildSpecFuerReminderZumVersenden();
        List<ReminderEntity> faelligeReminderList = 
                        _reminderRepo.findAll( spec, _sortByZeitpunktFaelligAsc );

        LOG.info( "Anzahl fälliger Reminder gefunden : {}", faelligeReminderList.size() );
        
        for ( ReminderEntity r : faelligeReminderList ) {
            
            final String betreff = "[Reminder] " + r.getReminderText();
            final String body    = "siehe Betreff\n\nFälligkeit: " + r.getZeitpunktFaelligkeitFormatiert();
            
            _emailSender.sendeEmail( betreff, body );
        }
    }
    
}
