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

import java.util.List;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
//@ComponentScan(excludeFilters = {
//		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CompanyController.class)
//})
public class Application {

	//public static Company a = new Company("Firmaa",new Company.Kontaktdaten("testa@test.com","012345678","http://www.test.de"),"it-sec","200","DE","Typmedien");
	//public static Company b = new Company("Firmab",new Company.Kontaktdaten("testb@test.com","022345678","http://www.testb.de"),"it-dienst","180","DE","IDK");

	//public static Company c = new Company("Firmac",new Company.Kontaktdaten("testc@test.com","032345678","http://www.testc.de"),"it","1","DE","wassollmedientypeigentlichsein");

	//public static Company d = new Company("Dirma",new Company.Kontaktdaten("testd@test.com","042345678","http://www.testd.de"),"betrueger","777","AU","8===D");

	//@Autowired
	//private CompanyService companyService;

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(Application.class, args);
		System.out.println("lel");
		DataReader.dataindb(context);

		// Get a bean instance from the application context
		//CompanyService companyService = context.getBean(CompanyService.class);
		//companyService.save(a);
		//companyService.save(b);
		//companyService.save(c);
		//companyService.save(d);
	}

}


