package de.eldecker.spring.reminder.logik;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import de.eldecker.spring.reminder.db_jpa.ReminderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReminderMetricsService {

    private final InfluxDBClient influxDBClient;
    private final ReminderRepo reminderRepo;

    @Autowired
    public ReminderMetricsService(InfluxDBClient influxDBClient, ReminderRepo reminderRepo) {
        this.influxDBClient = influxDBClient;
        this.reminderRepo = reminderRepo;
    }

    @Scheduled(fixedRate = 60000) // Every minute, adjust as needed
    public void recordReminderCounts() {
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

            long pendingReminders = reminderRepo.countBy_schonVersendet(false);
            long dispatchedReminders = reminderRepo.countBy_schonVersendet(true);

            Point pendingPoint = Point.measurement("reminder_stats")
                                      .addTag("status", "pending")
                                      .addField("count", pendingReminders)
                                      .time(Instant.now(), WritePrecision.S); // Changed to S for seconds

            Point dispatchedPoint = Point.measurement("reminder_stats")
                                         .addTag("status", "dispatched")
                                         .addField("count", dispatchedReminders)
                                         .time(Instant.now(), WritePrecision.S); // Changed to S for seconds

            writeApi.writePoint(pendingPoint);
            writeApi.writePoint(dispatchedPoint);

            System.out.println("Successfully wrote reminder counts to InfluxDB. Pending: " + pendingReminders + ", Dispatched: " + dispatchedReminders);

        } catch (Exception e) {
            System.err.println("Error writing reminder counts to InfluxDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
