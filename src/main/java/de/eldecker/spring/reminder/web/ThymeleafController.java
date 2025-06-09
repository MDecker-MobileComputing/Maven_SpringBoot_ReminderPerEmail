package de.eldecker.spring.reminder.web;

import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Sort;

import de.eldecker.spring.reminder.db.ReminderEntity;
import de.eldecker.spring.reminder.db.ReminderRepo;
import de.eldecker.spring.reminder.logik.ReminderService;
import de.eldecker.spring.reminder.model.ReminderException;


/**
 * Controller für Thymeleaf (Template-Engine).
 */
@Controller
@RequestMapping( "app/v1" )
public class ThymeleafController {

    private static Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );
    
    /** Bean mit Geschäftslogik. */
    private ReminderService _reminderService;
    
    /** Bean für direkten Zugriff auf DB. */
    private ReminderRepo _reminderRepo;
    
    /** Sortierobjekt für Reminder-Liste nach Fälligkeit aufsteigend. */
    private final Sort _sortByZeitpunktFaelligAsc = Sort.by( ASC, "_zeitpunktFaellig" );
    
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public ThymeleafController( ReminderService reminderService, ReminderRepo reminderRepo ) {
    
        _reminderService = reminderService;
        _reminderRepo    = reminderRepo;
    }
    
    
    /**
     * Anlegen eines neuen Reminders.
     * 
     * @param model Objekt mit Platzhalterwerten für Template-Datei
     *
     * @param reminderText für Betreffzeile der Reminder-Email 
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
     * @return Immer "ergebnis" für "ergebnis.html" (zeigt ggf. Fehlernachricht an)
     */
    @PostMapping( "/reminder-anlegen" )
    public String reminderAnlegen( Model model,
                                   @RequestParam( value = "text"  , required = true ) String reminderText,
                                   @RequestParam( value = "tag"   , required = true ) int tag            ,
                                   @RequestParam( value = "monat" , required = true ) int monat          ,
                                   @RequestParam( value = "jahr"  , required = true ) int jahr           ,
                                   @RequestParam( value = "stunde", required = true ) int stunde         ,
                                   @RequestParam( value = "minute", required = true ) int minute 
                                 ) throws ReminderException { 
                
        LOG.info( "Request erhalten fuer neuen Reminder: {}.{}.{}, {}:{} Uhr, Text: {}", 
                  tag, monat, jahr, stunde, minute, reminderText );                                     
                
        try {
        
            long reminderId = _reminderService.reminderAnlegen( tag, monat, jahr, 
                                                                stunde, minute, 
                                                                reminderText );
            
            final String erfolgText = String.format( "Reminder unter ID=%d angelegt.", reminderId );
            LOG.info( erfolgText );
            model.addAttribute( "nachricht", erfolgText );            
        }
        catch ( ReminderException ex ) {
            
            final String fehlerText = "Fehler beim Anlegen von Reminder aufgetreten: " + ex.getMessage();             
            LOG.error( fehlerText, ex );            
            model.addAttribute( "nachricht", fehlerText );
        }
        
        return "ergebnis";
    }
    
    
    /**
     * Methode bringt Seite mit allen Remindern zur Anzeige.
     * 
     * @param model Objekt mit Platzhalterwerten für Template-Datei 
     * 
     * @return liste
     */
    @GetMapping( "/liste" )
    public String reminderListe( Model model ) {
                
        final List<ReminderEntity> liste = _reminderRepo.findAll( _sortByZeitpunktFaelligAsc );
        
        model.addAttribute( "reminderListe", liste );
        
        return "liste";
    }
    
}
