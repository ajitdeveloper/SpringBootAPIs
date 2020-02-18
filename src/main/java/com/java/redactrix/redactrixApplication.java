package com.java.redactrix;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.java.redactrix.storage.StorageProperties;
import com.java.redactrix.storage.StorageService;

//@EnableJpaRepositories(basePackages = { "repository"})
@EnableConfigurationProperties(StorageProperties.class)
//@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = { "model" })
@SpringBootApplication
public class redactrixApplication {


	
	public static void main(String[] args) {
		SpringApplication.run(redactrixApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};

	}
}
