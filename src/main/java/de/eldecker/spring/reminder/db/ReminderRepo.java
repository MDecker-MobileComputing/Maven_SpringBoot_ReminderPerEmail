package de.eldecker.spring.reminder.db;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repo-Interface für Zugriff auf Tabelle mit Remindern.
 * Dank <i>Spring Data JPA</i> wird zur Laufzeit automatisch
 * eine implementierende Klasse dieses Interfaces erzeugt
 * und instanziert.
 */
public interface ReminderRepo 
                 extends JpaRepository<ReminderEntity, Long> {
}

