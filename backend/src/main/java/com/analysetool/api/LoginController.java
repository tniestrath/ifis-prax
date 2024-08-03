package com.analysetool.api;


import com.analysetool.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    /**
     * Attempts to log in a user via request body for security-reasons.
     * @param loginForm the login data, given in the request-body.
     * @return the wordpress_logged_in cookie.
     */
    @PostMapping(value = {"/login2", "/0wB4P2mly-xaRmeeDOj0_g/login2"})
    public String login2(@RequestBody LoginService.LoginForm loginForm){return loginService.login2(loginForm);}

    /**
     * Validates a cookie for correctness.
     * @param request the request to validate the cookie for.
     * @return ??
     */
    @GetMapping(value = {"/validate", "/0wB4P2mly-xaRmeeDOj0_g/validate"})
    public String validateCookie(HttpServletRequest request){return loginService.validateCookie(request);}

    /**
     * Validates a cookie.
     * @param cookie the cookie to validate.
     * @return a JSON-String containing the found user_id from the cookie.
     */
    @GetMapping(value = {"/validateCookie", "/0wB4P2mly-xaRmeeDOj0_g/validateCookie"})
    public String validateCookie(@RequestParam("value") String cookie){return loginService.validateCookie(cookie);}

    /**
     * Pings the server to check whether its up and running.
     * @return true.
     */
    @GetMapping(value = {"/ping", "/0wB4P2mly-xaRmeeDOj0_g/ping"})
    public boolean ping() {return loginService.ping();}
}
