package com.analysetool.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam String user, String pass) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://test.it-sicherheit.de/wp-login.php");

        String responseCookie = "";

        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("log", user));
            params.add(new BasicNameValuePair("pwd", pass));

            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            //Header[] headers = response.getAllHeaders();

            Header[] allSetCookie = response.getHeaders("Set-Cookie");
            responseCookie = allSetCookie[4].getValue();
            // Process the response
            String responseBody = EntityUtils.toString(entity);
            System.out.println(responseBody);
            for (int i = 0; i < allSetCookie.length; i++) {
                System.out.println("Name: " + allSetCookie[i].getName() + ", Value: " + allSetCookie[i].getValue());
            }
            /*for (int i = 0; i < headers.length; i++) {
                System.out.println("Name: " + headers[i].getName() + ", Value: " + headers[i].getValue());
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCookie;
    }

}
