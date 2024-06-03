package com.analysetool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
@Import(WebConfig.class)
public class Application {



	public static void main(String[] args) {
		try{
		ApplicationContext context = SpringApplication.run(Application.class, args);

		System.out.println("lel");

	} catch (Exception e) {
			e.printStackTrace();
		}


	}

}


