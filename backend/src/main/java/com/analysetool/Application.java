package com.analysetool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.analysetool.modells.Company;
import com.analysetool.services.CompanyService;
import com.analysetool.api.CompanyController;
import com.analysetool.services.LogService;
import java.util.List;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class Application {



	public static void main(String[] args) {
		try{
		ApplicationContext context = SpringApplication.run(Application.class, args);
		System.out.println("lel");
		//DataReader.dataindb(context);}
			LogService logService = context.getBean(LogService.class);
			logService.run(true,Application.class.getClassLoader().getResource("access.log").getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}


