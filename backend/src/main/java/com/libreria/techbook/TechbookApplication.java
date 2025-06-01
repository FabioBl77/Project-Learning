package com.libreria.techbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point dell'applicazione Spring Boot.
 * Avvia il contesto Spring e il server embedded (es. Tomcat).
 */

@SpringBootApplication
public class TechbookApplication {

	private static final Logger logger = LoggerFactory.getLogger(TechbookApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(TechbookApplication.class, args);
		logger.info("Techbook application started successfully!");
	}

}
