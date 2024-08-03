package com.analysetool.api;

import com.analysetool.modells.LastPing;
import com.analysetool.repositories.LastPingRepository;
import com.analysetool.services.LoginService;
import com.analysetool.services.UserService;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")

public class LoginController {

    @Autowired
    LoginService loginService;

    /**
     * Attempts to log in a user.
     * @param user username.
     * @param pass password.
     * @return String representation of the WordPress-login-cookie value
     */

    @GetMapping(value = {"/login", "/0wB4P2mly-xaRmeeDOj0_g/login"})
    public String login(@RequestParam String user, String pass) {return loginService.login(user, pass);}

    @PostMapping(value = {"/login2", "/0wB4P2mly-xaRmeeDOj0_g/login2"})
    public String login2(@RequestBody LoginService.LoginForm loginForm){return loginService.login2(loginForm);}

    @GetMapping(value = {"/validate", "/0wB4P2mly-xaRmeeDOj0_g/validate"})
    public String validateCookie(HttpServletRequest request){return loginService.validateCookie(request);}

    @GetMapping(value = {"/validateCookie", "/0wB4P2mly-xaRmeeDOj0_g/validateCookie"})
    public String validateCookie(@RequestParam("value") String cookie){return loginService.validateCookie(cookie);}

    @GetMapping(value = {"/ping", "/0wB4P2mly-xaRmeeDOj0_g/ping"})
    public boolean ping() {return loginService.ping();}
}
