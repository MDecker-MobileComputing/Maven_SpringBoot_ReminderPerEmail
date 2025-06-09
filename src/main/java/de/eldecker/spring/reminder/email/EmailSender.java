package de.eldecker.spring.reminder.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;


/**
 * Diese Klasse definiert eine Bean, mit der Emails versendet werden können.
 * <br><br>
 * 
 * Für Versenden von Email mit <i>Spring Boot</i> siehe auch
 * <a href="https://docs.spring.io/spring-framework/reference/integration/email.html">diese Seite</a>.
 */
@Component
public class EmailSender {

    private static Logger LOG = LoggerFactory.getLogger( EmailSender.class );
    
    /** Email-Adresse des Absenders, es wird die Email-Adresse des Email-Kontos verwendet. */
    @Value( "${spring.mail.username}" )
    private String _absenderAdresse;
    
    /** Empfänger-Adresse für die Reminder. */
    @Value( "${de.eldecker.reminder.email_empfaenger}" )
    private String _empfaengerAdresse;
    
    /** Bean für eigentlichen Versand der E-Mails. */
    private MailSender _mailSender;
    
    
    /**
     * Konstruktur für Dependency Injection.
     */
    @Autowired
    public EmailSender( MailSender mailSender) {
        
        _mailSender = mailSender;
    }
    

    /**
     * Email an konfigurierten Empfänger versenden.
     * 
     * @param betreff Betreffzeile der Email
     * 
     * @param emailInhalt Text für Body der Email
     * 
     * @throws MailException Fehler beim VErsenden der Email aufgetreten
     */
    public void sendeEmail( String betreff, String emailInhalt ) throws MailException {

        final SimpleMailMessage message = new SimpleMailMessage();
                
        message.setSubject( betreff     );
        message.setText(    emailInhalt );
        
        message.setTo(   _empfaengerAdresse );
        message.setFrom( _absenderAdresse   );
        
        _mailSender.send( message );
        LOG.info( "Email versendet mit folgendem Betreff: \"{}\"", betreff );
    }
    
}
