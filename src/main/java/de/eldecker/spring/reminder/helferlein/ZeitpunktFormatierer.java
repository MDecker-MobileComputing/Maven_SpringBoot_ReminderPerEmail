package de.eldecker.spring.reminder.helferlein;

import static java.util.Locale.GERMAN;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ZeitpunktFormatierer {

    private final static DateTimeFormatter DATE_TIME_FORMATIERER = 
                                                  DateTimeFormatter.ofPattern( 
                                                          "dd.MM.yyyy (EE), HH:mm", 
                                                          GERMAN 
                                                  );    
    
    /**
     * Methode um Datum mit Zeitpunkt im deutschen Format für Anzeige auf
     * Nutzeroberfläche zurückzugeben. 
     * 
     * @param dateTime Objekt mit Zeitpunkt, der formatiert werden soll 
     * 
     * @return Formatiertes Datum inkl. Stunde+Minuten.
     *         Beispiel: <pre>30.04.2026 (Do.), 12:10 Uhr</pre>         
     */
    public static String formatiere( LocalDateTime dateTime ) {
        
        return DATE_TIME_FORMATIERER.format( dateTime );
    }
    
}
