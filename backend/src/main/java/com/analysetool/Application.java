package com.analysetool;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
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

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling

public class Application {



	public static void main(String[] args) {
		try{
		ApplicationContext context = SpringApplication.run(Application.class, args);
			//ToDo Toten Code aufräumen
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

		System.out.println("lel");

	} catch (Exception e) {
			e.printStackTrace();
		}


	}

}


