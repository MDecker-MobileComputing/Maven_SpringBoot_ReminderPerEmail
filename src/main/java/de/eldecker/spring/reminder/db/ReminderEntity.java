package de.eldecker.spring.reminder.db;

import static jakarta.persistence.GenerationType.AUTO;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * Entity-Klasse für Tabellen mit den einzelnen Remindern.
 */
@Entity
@Table( name = "REMINDER" )
public class ReminderEntity {

    /** Primärschlüssel, wird von JPA gesetzt/verwaltet. */
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id")
    private Long _id;
    
    @Column(name = "REMINDER_TEXT")
    private String _reminderText;
    
    @Column(name = "ZEITPUNKT_ANGELEGT")
    private LocalDateTime _zeitpunktAngelegt;
    
    /** 
     * Frühester Zeitpunkt, zu dem dieser Reminder versendet werden soll;
     * idealerweise wird der Reminder kurz nach diesem Zeitpunkt versendet.
     */
    @Column(name = "ZEITPUNKT_FAELLIG")
    private LocalDateTime _zeitpunktFaellig;
    
    /**
     * {@code true}, gdw. der Reminder bereits per Email versendet wurde;
     * kann nur {@code true} sein, wenn der Zeitpunkt der Fälligkeit
     * in der Vergangenheit liegt.
     */
    @Column(name = "SCHON_VERSENDET")
    private boolean _schonVersendet;
    
    
    /** Leerer Default-Konstruktor */
    public ReminderEntity() {}
    
    
    /**
     * Konstruktur um ganz neuen Reminder zu ereugen.
     * 
     * @param reminderText Reminder-Text, z.B. "Milch kaufen!"
     * 
     * @param zeitpunktFaelligkeit Zeitpunkt, zu dem dieser Reminder
     *                             versendet werden soll
     */
    public ReminderEntity( String reminderText, 
                           LocalDateTime zeitpunktFaelligkeit
                         ) {
        
        _reminderText     = reminderText;
        _zeitpunktFaellig = zeitpunktFaelligkeit;
        
        _zeitpunktAngelegt = now();
        _schonVersendet    = false;
    }


    public Long getId() {
        
        return _id;
    }
    
    public String getReminderText() {
        
        return _reminderText;
    }


    public void setReminderText(String reminderText) {
        
        _reminderText = reminderText;
    }


    public LocalDateTime getZeitpunktAngelegt() {
        
        return _zeitpunktAngelegt;
    }


    public void setZeitpunktAngelegt(LocalDateTime zeitpunktAngelegt) {
        
        _zeitpunktAngelegt = zeitpunktAngelegt;
    }


    public LocalDateTime getZeitpunktFaelligkeit() {
        
        return _zeitpunktFaellig;
    }


    public void setZeitpunktFaelligkeit(LocalDateTime zeitpunktFaelligkeit) {
        
        this._zeitpunktFaellig = zeitpunktFaelligkeit;
    }


    public boolean isSchonVersendet() {
        
        return _schonVersendet;
    }


    public void setSchonVersendet( boolean schonVersendet ) {
        
        _schonVersendet = schonVersendet;
    }    
    

    @Override
    public int hashCode() {

        return Objects.hash(
                    _reminderText, 
                    _zeitpunktFaellig, 
                    _zeitpunktAngelegt, 
                    _schonVersendet
              );
    }
    
    
    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) {
            
            return true;
        }
        if ( obj == null ) {
            
            return false;
        }

        if ( obj instanceof ReminderEntity other ) {

            return _reminderText.equals(      other._reminderText      ) && 
                   _zeitpunktFaellig.equals(  other._zeitpunktFaellig  ) &&
                   _zeitpunktAngelegt.equals( other._zeitpunktAngelegt ) &&
                   _schonVersendet == other._schonVersendet;
            
        } else {
            
            return false;
        }
    }
    
    
    @Override
    public String toString() {
        
        return String.format( "Reminder fällig am %s: \"%s\"", 
                              _zeitpunktFaellig, _reminderText );
    }

}
