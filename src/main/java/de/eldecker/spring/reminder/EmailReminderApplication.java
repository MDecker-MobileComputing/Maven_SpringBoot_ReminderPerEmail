package de.eldecker.spring.reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Einstiegspunkt der Anwendung. 
 */
@SpringBootApplication
public class EmailReminderApplication {

	public static void main( String[] args ) {
	    
		SpringApplication.run( EmailReminderApplication.class, args );
	}

}
