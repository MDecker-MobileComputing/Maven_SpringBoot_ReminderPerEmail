package de.eldecker.spring.reminder.web;

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

import de.eldecker.spring.reminder.db_jpa.ReminderEntity;
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
        
    
    /**
     * Konstruktor für Dependency Injection.
     */
    @Autowired
    public ThymeleafController( ReminderService reminderService ) {
    
        _reminderService = reminderService;
    }
    
    
    /**
     * Anlegen eines neuen Reminders.
     * 
     * @param model Objekt mit Platzhalterwerten für Template-Datei
     *
     * @param text für Betreffzeile der Reminder-Email 
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
                                   @RequestParam( value = "text"  , required = true ) String text  ,
                                   @RequestParam( value = "tag"   , required = true ) int    tag   ,
                                   @RequestParam( value = "monat" , required = true ) int    monat ,
                                   @RequestParam( value = "jahr"  , required = true ) int    jahr  ,
                                   @RequestParam( value = "stunde", required = true ) int    stunde,
                                   @RequestParam( value = "minute", required = true ) int    minute 
                                 ) throws ReminderException { 
                
        LOG.info( "Request erhalten fuer neuen Reminder: {}.{}.{}, {}:{} Uhr, Text: {}", 
                  tag, monat, jahr, stunde, minute, text );                                     
                
        try {
        
            long reminderId = _reminderService.reminderAnlegen( tag, monat, jahr, 
                                                                stunde, minute, 
                                                                text );
            
            final String erfolgText = String.format( "ERFOLG: Reminder unter ID %d angelegt.", reminderId );
            LOG.info( erfolgText );
            model.addAttribute( "nachricht", erfolgText );            
        }
        catch ( ReminderException ex ) {
            
            final String fehlerText = "FEHLER – Reminder konnte nicht angelegt werden: " + ex.getMessage();             
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
     * @return Template-Datei "liste" (also "liste.html")
     */
    @GetMapping( "/liste" )
    public String reminderListe( Model model ) {
                
        final List<ReminderEntity> liste = _reminderService.getAlleReminderSortiert();
        model.addAttribute( "reminderListe", liste );
        
        return "liste";
    }
    
}
