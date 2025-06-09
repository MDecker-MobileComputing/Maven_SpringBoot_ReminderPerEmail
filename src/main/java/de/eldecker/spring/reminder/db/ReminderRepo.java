package de.eldecker.spring.reminder.db;

import static java.time.LocalDateTime.now;

import java.util.List; // Added import

import org.springframework.data.domain.Sort; // Added import
import org.springframework.data.jpa.domain.Specification; // Added import
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repo-Interface für Zugriff auf Tabelle mit Remindern.
 * Dank <i>Spring Data JPA</i> wird zur Laufzeit automatisch
 * eine implementierende Klasse dieses Interfaces erzeugt
 * und instanziert.
 */
public interface ReminderRepo 
                 extends JpaRepository<ReminderEntity, Long> {
    
    /**
     * Findet alle Reminder und sortiert sie gemäß dem übergebenen Sort-Objekt.
     *
     * @param sort Objekt für Spezifikation der Sortier-Reihenfolge
     * 
     * @return Eine Liste aller Reminder-Entitäten, sortiert Argument {@code sort}
     */
    List<ReminderEntity> findAll( Sort sort );

    
    /**
     * Findet alle Reminder, die dem Filterkriterium entsprechen, 
     * und sortiert sie gemäß dem übergebenen Sort-Objekt.
     *
     * @param spec Objekt für Spezifikation der Filter-Kriterien; kann z.B. 
     *             mit Methode {@link #buildSpecFuerReminderZumVersenden()}
     *             erzeugt werden.
     * 
     * @param sort Objekt für Spezifikation der Sortier-Reihenfolge
     * 
     * @return Eine Liste der gefilterten und sortierten Reminder-Entitäten
     */
    List<ReminderEntity> findAll( Specification<ReminderEntity> spec, Sort sort );
    
    
    /**
     * Erzeugt eine Spezifikation, um Reminder zu finden, die noch nicht versendet wurden
     * und deren Fälligkeitsdatum in der Vergangenheit liegt.
     * 
     * @return Eine {@code Specification<ReminderEntity>} für die Filterung.
     */
    static Specification<ReminderEntity> buildSpecFuerReminderZumVersenden() {
        
        return ( root, query, criteriaBuilder ) -> {
            return criteriaBuilder.and(
                criteriaBuilder.isFalse(  root.get("_schonVersendet"  )        ),
                criteriaBuilder.lessThan( root.get("_zeitpunktFaellig"), now() )
            );
        };
    }

}
