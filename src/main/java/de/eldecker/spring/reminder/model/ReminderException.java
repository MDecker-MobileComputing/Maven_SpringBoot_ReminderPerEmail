package de.eldecker.spring.reminder.model;


/**
 * Applikations-spezifische Exception-Klasse. 
 */
@SuppressWarnings("serial")
public class ReminderException extends Exception {
    
    public ReminderException( String fehlerbeschreibung ) {
        
        super( fehlerbeschreibung );
    }
    
}