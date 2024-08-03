package com.analysetool.services;

import com.analysetool.modells.LastPing;
import com.analysetool.repositories.LastPingRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class LoginService {

    @Autowired
    UserService userService;
    @Autowired
    private LastPingRepository lpRepo;

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
    public static class LoginForm {
        public String username;
        public String password;
    }


    public LoginService(DashConfig config) {
        this.config = config;
    }

    /**
     * Attempts to log in a user.
     * @param user username.
     * @param pass password.
     * @return String representation of the WordPress-login-cookie value
     */
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
            String userData = userService.getUserByLogin(user);
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
        //System.out.println("USERNAME: " +  user + " PASSWORT: " + pass);

        return responseCookie;
    }

    /**
     * Attempts to log in a user via request body for security-reasons.
     * @param loginForm the login data, given in the request-body.
     * @return the wordpress_logged_in cookie.
     */
    public String login2(@RequestBody LoginService.LoginForm loginForm){
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
            String userData = userService.getUserByLogin(loginForm.username);
            if (responseCookie.isEmpty()){
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

    /**
     * Validates a cookie for correctness.
     * @param request the request to validate the cookie for.
     * @return ??
     */
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
                    //System.out.println(cookieValue);
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

    /**
     * Validates a cookie.
     * @param cookie the cookie to validate.
     * @return a JSON-String containing the found user_id from the cookie.
     */
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
            //e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * Pings the server to check whether its up and running.
     * @return true.
     */
    public boolean ping() {
        LastPing ping;
        if(lpRepo.findById(1L).isPresent()) {
            ping = lpRepo.findById(1L).get();
        } else {
            ping = new LastPing();
        }
        ping.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        lpRepo.save(ping);
        return true;
    }
}
