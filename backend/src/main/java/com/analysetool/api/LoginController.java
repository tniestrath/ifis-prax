package com.analysetool.api;

import com.analysetool.util.DashConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")

public class LoginController {


    private final DashConfig config;

    public LoginController(DashConfig config) {
        this.config = config;
    }

    /**
     *
     * @param user
     * @param pass
     * @return String representation of the WordPress-login-cookie value
     * @throws IOException
     */

    @GetMapping("/login")
    public String login(@RequestParam String user, String pass) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(config.getWplogin());

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
            for (Header h: allSetCookie) {
                if (h.getValue().contains("wordpress_logged_in")){
                    responseCookie = h.getValue();
                }
            }
            if (responseCookie.isEmpty()){
                responseCookie = "LOGIN REJECTED";
            }

            // Process the response
            String responseBody = EntityUtils.toString(entity);
            for (int i = 0; i < allSetCookie.length; i++) {
                System.out.println(responseCookie);
            }
            //ToDo Toten Code aufräumen
            /*for (int i = 0; i < headers.length; i++) {
                System.out.println("Name: " + headers[i].getName() + ", Value: " + headers[i].getValue());
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCookie;
    }
    @GetMapping("/validate")
    public String validateCookie(HttpServletRequest request){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(config.getValidate());


        String responseBody = "INVALID";
        try {
            String cookieValue = "";
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().contains("wordpress_logged_in")) {
                    cookieValue = java.net.URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    System.out.println(cookieValue);
                }
            }

            String jsonPayload = "{\"log\":\""+ cookieValue +"\"}";
            StringEntity strEntity = new StringEntity(jsonPayload, "UTF-8");
            strEntity.setContentType("application/json");
            httpPost.setEntity(strEntity);

            HttpResponse response2 = httpClient.execute(httpPost);
            HttpEntity entity = response2.getEntity();

            responseBody = EntityUtils.toString(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

}
