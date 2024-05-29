package com.analysetool.api;

import com.analysetool.util.DashConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")

public class LoginController {

    @Autowired UserController userController;

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
    private static class LoginForm {
        public String username;
        public String password;
    }


    public LoginController(DashConfig config) {
        this.config = config;
    }

    /**
     * Attempts to log in a user.
     * @param user username.
     * @param pass password.
     * @return String representation of the WordPress-login-cookie value
     */

    @GetMapping("/login")
    public String login(@RequestParam String user, String pass) {
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
            String userData = userController.getUserByLogin(user);
            if (responseCookie.isEmpty() || !(new JSONObject(userData).get("accountType").equals("admin"))){
                responseCookie = "LOGIN REJECTED";
            }

            // Process the response
            String responseBody = EntityUtils.toString(entity);
            for (int i = 0; i < allSetCookie.length; i++) {
                //System.out.println(responseCookie);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("USERNAME: " +  user + " PASSWORT: " + pass);

        return responseCookie;
    }
    @PostMapping("/login2")
    public String login2(@RequestBody LoginForm loginForm){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(config.getWplogin());

        String responseCookie = "";

        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("log", loginForm.username));
            params.add(new BasicNameValuePair("pwd", loginForm.password));

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
            String userData = userController.getUserByLogin(loginForm.username);
            if (responseCookie.isEmpty() || !(new JSONObject(userData).get("accountType").equals("admin"))){
                responseCookie = "LOGIN REJECTED";
            }

            // Process the response
            String responseBody = EntityUtils.toString(entity);
            for (int i = 0; i < allSetCookie.length; i++) {
                //System.out.println(responseCookie);
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return responseCookie;
    }

    @GetMapping("/validate")
    public String validateCookie(HttpServletRequest request){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(config.getValidate());
        //System.out.println("Cookie Validation in progress");

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
            //e.printStackTrace();
        }
        return responseBody;
    }

    @GetMapping(value = "/validateCookie")
    public String validateCookie(@RequestParam("value") String cookie){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(config.getValidate());
        //System.out.println("Manual Cookie Validation in progress");


        String responseBody = "INVALID";
        try {
            String cookieValue;
            cookieValue = java.net.URLDecoder.decode(cookie, StandardCharsets.UTF_8);
            //System.out.println(cookieValue);

            String jsonPayload = "{\"log\":\""+ cookieValue +"\"}";
            StringEntity strEntity = new StringEntity(jsonPayload, "UTF-8");
            strEntity.setContentType("application/json");
            httpPost.setEntity(strEntity);

            HttpResponse response2 = httpClient.execute(httpPost);
            HttpEntity entity = response2.getEntity();

            responseBody = new JSONObject(EntityUtils.toString(entity)).getString("user_id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    @GetMapping("/ping")
    public boolean ping() {
        return true;
    }
}
