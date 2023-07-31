package com.analysetool.api;

import com.analysetool.util.DashAuthenticationProvider;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")

public class LoginController {

    public Authentication adminAuthentication = new Authentication() {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return "Admin";
        }
    };

    public Authentication userAuthentication = new Authentication() {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singleton(new SimpleGrantedAuthority("USER"));
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return "User";
        }
    };


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
        HttpPost httpPost = new HttpPost("http://test.it-sicherheit.de/wp-json/server_variables/custom-endpoint");


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

    public String validateCookie(String cookie){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://test.it-sicherheit.de/wp-json/server_variables/custom-endpoint");
        System.out.println("1.1.1");


        String responseBody = "INVALID";
        try {
            String cookieValue = "";
            cookieValue = java.net.URLDecoder.decode(cookie, StandardCharsets.UTF_8);
            System.out.println(cookieValue);

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
