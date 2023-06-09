package com.analysetool;

import com.analysetool.modells.SysVar;
import com.analysetool.services.SysVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.analysetool.modells.Company;
import com.analysetool.services.CompanyService;
import com.analysetool.api.CompanyController;
import com.analysetool.services.LogService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling

public class Application {



	public static void main(String[] args) {
		try{
		ApplicationContext context = SpringApplication.run(Application.class, args);
		System.out.println("lel");
		//DataReader.dataindb(context);}
			/*SysVarService sysVarService = context.getBean(SysVarService.class);
			SysVar SystemVariabeln = new SysVar();
			if(sysVarService.getAllSysVars().isEmpty()){


			SystemVariabeln.setDate(LocalDateTime.now());
			SystemVariabeln.setDayInYear(LocalDateTime.now().getDayOfYear());
			SystemVariabeln.setDayInWeek(LocalDateTime.now().getDayOfWeek().getValue());
			SystemVariabeln.setDayInMonth(LocalDateTime.now().getDayOfMonth());
			SystemVariabeln.setLastLine("");
			SystemVariabeln.setLastLineCount(0);

			}else {SystemVariabeln = sysVarService.getAllSysVars().get(sysVarService.getAllSysVars().size()-1);

				if(SystemVariabeln.getDate().getDayOfYear()!=(LocalDateTime.now().getDayOfYear())){
					SystemVariabeln.setDate(LocalDateTime.now());
					SystemVariabeln.setDayInYear(LocalDateTime.now().getDayOfYear());
					SystemVariabeln.setDayInWeek(LocalDateTime.now().getDayOfWeek().getValue());
					SystemVariabeln.setDayInMonth(LocalDateTime.now().getDayOfMonth());
					SystemVariabeln.setLastLine("");
					SystemVariabeln.setLastLineCount(0);
				}

			}

			LogService logService = context.getBean(LogService.class);
			logService.run(true,Application.class.getClassLoader().getResource("access.log").getPath(), SystemVariabeln);*/
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}


