package de.eldecker.spring.reminder.db;

import static de.eldecker.spring.reminder.helferlein.ZeitpunktFormatierer.formatiere;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


/**
 * Entity-Klasse für Tabellen mit den einzelnen Remindern.
 * <br><br>
 * 
 * Mit der Annotation {@code SequenceGenerator} wird der Sequenz-Generator für
 * die Erzeugung der Primärschlüsselwerte so konfiguriert, dass immer noch
 * ein Wert im Voraus erzeugt wird, so dass durch Neustarten der Anwendung 
 */
@Entity
@Table( name = "REMINDER" )
@SequenceGenerator( name = "reminder_seq", sequenceName = "reminder_id_seq", allocationSize = 1 )
public class ReminderEntity {

    /** Primärschlüssel, wird von JPA gesetzt/verwaltet. */
    @Id
    @GeneratedValue( strategy = SEQUENCE, generator = "reminder_seq" )
    @Column( name = "ID" )
    private Long _id;
    
    @Column( name = "REMINDER_TEXT" )
    private String _reminderText;
    
    @Column( name = "ZEITPUNKT_ANGELEGT" )
    private LocalDateTime _zeitpunktAngelegt;
    
    /** 
     * Frühester Zeitpunkt, zu dem dieser Reminder versendet werden soll;
     * idealerweise wird der Reminder kurz nach diesem Zeitpunkt versendet.
     */
    @Column( name = "ZEITPUNKT_FAELLIG" )
    private LocalDateTime _zeitpunktFaellig;
    
    /**
     * {@code true}, gdw. der Reminder bereits per Email versendet wurde;
     * kann nur {@code true} sein, wenn der Zeitpunkt der Fälligkeit
     * in der Vergangenheit liegt.
     */
    @Column( name = "SCHON_VERSENDET" )
    private boolean _schonVersendet;
    
    
    /** 
     * Leerer Default-Konstruktor.   
     */
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


    public void setReminderText( String reminderText ) {
        
        _reminderText = reminderText;
    }


    public LocalDateTime getZeitpunktAngelegt() {
        
        return _zeitpunktAngelegt;
    }


    public void setZeitpunktAngelegt( LocalDateTime zeitpunktAngelegt ) {
        
        _zeitpunktAngelegt = zeitpunktAngelegt;
    }


    public LocalDateTime getZeitpunktFaelligkeit() {
        
        return _zeitpunktFaellig;
    }


    public void setZeitpunktFaelligkeit( LocalDateTime zeitpunktFaelligkeit ) {
        
        this._zeitpunktFaellig = zeitpunktFaelligkeit;
    }


    public boolean isSchonVersendet() {
        
        return _schonVersendet;
    }


    public void setSchonVersendet( boolean schonVersendet ) {
        
        _schonVersendet = schonVersendet;
    }    
    
    
    public String getZeitpunktFaelligkeitFormatiert() {
        
        return formatiere( _zeitpunktFaellig );
    }
    

    /**
     * Hash-Wert für aufrufendes Objekt.
     * 
     * @return Hash-Wert, in dessen Berechnung alle Attributwerte eingangen sind,
     *         bis auf die ID (weil diese u.U. erst noch von JPA gesetzt wird).
     */
    @Override
    public int hashCode() {

        return Objects.hash(
                    _reminderText, 
                    _zeitpunktFaellig, 
                    _zeitpunktAngelegt, 
                    _schonVersendet
              );
    }
    
    
    /**
     * Vergleich aufrufendes Objekt auf Gleichmit mit dem als Argument übergebenen
     * Objekt.
     * 
     * @return {@code true} gdw. alle Attribute (bis auf die ID) gleich sind.
     */
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
    
    
    /**
     * String-Repräsentation des aufrufenden Objekts.
     * 
     * @return Text mit Fälligkeitszeitpunkt und Reminder-Text;
     *         Beispiel:
     *         <pre>
     *         Fällig am 23.12.2026 (Mi.), 07:10 Uhr: "Schon alle Weihnachtsgeschenke besorgt?" (ID=123)
     *         </pre>
     */
    @Override
    public String toString() {
        
        return String.format( 
                    "Fällig am %s Uhr: \"%s\" (ID=%d)", 
                    getZeitpunktFaelligkeitFormatiert(), 
                    _reminderText,
                    _id
               );
    }

}
