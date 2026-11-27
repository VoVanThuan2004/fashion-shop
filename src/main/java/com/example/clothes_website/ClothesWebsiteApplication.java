package com.example.clothes_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClothesWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClothesWebsiteApplication.class, args);
	}

}
