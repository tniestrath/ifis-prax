package com.analysetool.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam String t, HttpServletResponse response) throws IOException {
        Cookie loginCookie = new Cookie("login_token", t);
        response.addCookie(loginCookie);
        response.sendRedirect("http://localhost:4200");
        return "Success";
    }

}
