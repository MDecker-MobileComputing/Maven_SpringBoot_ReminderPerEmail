package de.eldecker.spring.reminder.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller für Thymeleaf (Template-Engine).
 */
@Controller
@RequestMapping( "app/v1" )
public class ThymeleafController {

    private static Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );
    
    
    /**
     * Anlegen eines neuen Reminders.
     *
     * @return Immer "ergebnis" für "ergebnis.html"
     */
    @PostMapping( "/reminder-anlegen" )
    public String reminderAnlegen( Model model,
                                   @RequestParam( value = "text"  , required = true ) String reminderText,
                                   @RequestParam( value = "tag"   , required = true ) int tag            ,
                                   @RequestParam( value = "monat" , required = true ) int monat          ,
                                   @RequestParam( value = "jahr"  , required = true ) int jahr           ,
                                   @RequestParam( value = "stunde", required = true ) int stunde         ,
                                   @RequestParam( value = "minute", required = true ) int minute 
                                 ) {
        
        LOG.info( "Request erhalten: {}.{}.{}, {}:{} Uhr, Text: {}", 
                  tag, monat, jahr,
                  stunde, minute, reminderText );
        
        model.addAttribute( "nachricht", "Lorem ipsum" );
        
        return "ergebnis";
    }
    
}
