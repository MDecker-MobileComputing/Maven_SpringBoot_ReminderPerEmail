package de.eldecker.spring.reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Einstiegspunkt der Anwendung. 
 */
@SpringBootApplication
@EnableScheduling
public class EmailReminderApplication {

	public static void main( String[] args ) {
	    
		SpringApplication.run( EmailReminderApplication.class, args );
	}

}
