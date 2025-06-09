package de.eldecker.spring.reminder.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                
        LOG.info( "Request erhalten: {}.{}.{}, {}:{} Uhr, Text: {}", 
                  tag, monat, jahr,
                  stunde, minute, reminderText );
                
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
    
}
