package de.eldecker.spring.reminder.db_influx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;


@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url}")
    private String _influxDBUrl;

    @Value("${influxdb.token}")
    private String _token;

    @Value("${influxdb.org}")
    private String _org;

    @Value("${influxdb.bucket}")
    private String _bucket;

    
    @Bean
    public InfluxDBClient influxDBClient() {
        
        return InfluxDBClientFactory.create( _influxDBUrl, 
                                             _token.toCharArray(),
                                             _org,
                                             _bucket
                                           );                                               
    }
    
}
