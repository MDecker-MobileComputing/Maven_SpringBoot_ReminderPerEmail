package de.eldecker.spring.reminder.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;


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
                    
}

