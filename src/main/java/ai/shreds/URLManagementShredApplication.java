package ai.shreds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for URL Management Shred
 * 
 * This Spring Boot application provides URL shortening and management services
 * with MongoDB persistence, Redis caching, and RabbitMQ messaging.
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class URLManagementShredApplication {

    public static void main(String[] args) {
        SpringApplication.run(URLManagementShredApplication.class, args);
    }
}
