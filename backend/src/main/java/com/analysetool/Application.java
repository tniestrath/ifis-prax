package com.analysetool;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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
import java.util.ArrayList;
import java.util.List;


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

		/*HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://test.it-sicherheit.de/wp-login.php");


		String username = "rest_api_test";
		String password = "test_api_rest";
		// Set the login credentials
		try {
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("log", username));
			params.add(new BasicNameValuePair("pwd", password));

			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			Header[] headers = response.getAllHeaders();
			// Get the session cookie from the response headers
			String sessionCookie = response.getFirstHeader("Set-Cookie").getValue();
			Header[] allSetCookie = response.getHeaders("Set-Cookie");

			// Process the response
			String responseBody = EntityUtils.toString(entity);
			System.out.println(responseBody);
		*//*for (int i = 0; i < allSetCookie.length; i++) {
			System.out.println("Name: " + allSetCookie[i].getName() + ", Value: " + allSetCookie[i].getValue());
		}*//*
			for (int i = 0; i < headers.length; i++) {
				System.out.println("Name: " + headers[i].getName() + ", Value: " + headers[i].getValue());
			}

			// Use the sessionCookie for further requests
			//System.out.println("Session Cookie: " + sessionCookie);

		} catch (Exception e) {
			e.printStackTrace();
		}*/



		/*HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://test.it-sicherheit.de/wp-json/server_variables/custom-endpoint");


		String username = "rest_api_test";
		String password = "test_api_rest";
		// Set the login credentials
		try {
			String jsonPayload = "{\"log\":Backend-Admin|1688636807|rl2LROuS4Vs8n1lY32WD3TWP90XXMwGn4H3pmE7J12F|46e814f1ee012aec275d189ba9baefcf51aae1efe494cb6b69e1d9ed4e1c42bd}";
			StringEntity strEntity = new StringEntity(jsonPayload, "UTF-8");
			strEntity.setContentType("application/json");
			httpPost.setEntity(strEntity);

			*//*List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("log", username));
			params.add(new BasicNameValuePair("pwd", password));


			httpPost.setEntity(new UrlEncodedFormEntity(params));*//*

			HttpResponse response2 = httpClient.execute(httpPost);
			HttpEntity entity = response2.getEntity();
			Header[] headers = response2.getAllHeaders();
			// Get the session cookie from the response headers
			String sessionCookie = response2.getFirstHeader("Set-Cookie").getValue();
			Header[] allSetCookie = response2.getHeaders("Set-Cookie");

			// Process the response
			String responseBody = EntityUtils.toString(entity);
			System.out.println(responseBody);
		for (int i = 0; i < allSetCookie.length; i++) {
			System.out.println("Name: " + allSetCookie[i].getName() + ", Value: " + allSetCookie[i].getValue());
		}
			for (int i = 0; i < headers.length; i++) {
				System.out.println("Name: " + headers[i].getName() + ", Value: " + headers[i].getValue());
			}

			// Use the sessionCookie for further requests
			//System.out.println("Session Cookie: " + sessionCookie);

		} catch (Exception e) {
			e.printStackTrace();
		}*/





		System.out.println("lel");

	} catch (Exception e) {
			e.printStackTrace();
		}


	}

}


