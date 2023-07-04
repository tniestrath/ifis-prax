package com.analysetool;


import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling

public class Application {



	public static void main(String[] args) {
		try{
		ApplicationContext context = SpringApplication.run(Application.class, args);

		//Erstellung des WebClients mit meiner accountID und einem erstellten licenseKey
		WebServiceClient client =
				new WebServiceClient.Builder(885598, "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").host("geolite.info").build();

		//Ist halt ne IP, wenn du nicht weißt was das ist, brich das Studium ab
		InetAddress ipAddress = InetAddress.getByName("194.94.127.7");

		//This looks up a country for the IP address above
		CountryResponse response = client.country(ipAddress);
		Country country = response.getCountry();
		System.out.println(country.getName()); //Voller Name des Landes z.B. United States
		System.out.println(country.getIsoCode()); //Nur der Code des Landes z.B. US

		//This should look up country and city for the IP address
		CityResponse cityResponse = client.city(ipAddress);
		Country countryWithCity = cityResponse.getCountry();
		System.out.println(country.getIsoCode());
		Subdivision subdivision = cityResponse.getMostSpecificSubdivision(); //Sammelt die geschätzt nächste Subdivision aus der DB
			//Erklärung Subdivision: Land -> Bundesland -> Stadt, also immer so nah die DB das ganze bestimmen kann halt.
		System.out.println(subdivision.getName()); //Gibt den vollen Namen der Subdivision aus
		System.out.println(subdivision.getIsoCode()); //Gibt den Code der Subdivision aus (Sofern einer vorhanden ist, bei NRW ist es NW -> vlt fraglich im Nutzen)

		System.out.println("lel");

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}


