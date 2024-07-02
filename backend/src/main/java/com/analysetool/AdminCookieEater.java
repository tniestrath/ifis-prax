package com.analysetool;

import com.analysetool.api.LoginController;
import com.analysetool.api.UserController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminCookieEater implements HandlerInterceptor {

    @Autowired
    LoginController loginController;
    @Autowired
    UserController userController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String result = loginController.validateCookie(request);


        if(request.getRequestURL().toString().contains("0wB4P2mly-xaRmeeDOj0_g")) return true;
        if(request.getRequestURL().toString().contains("api/users/getByLogin")) return true;
        if(request.getRequestURL().toString().contains("/login")) return true;
        if(request.getRequestURL().toString().contains("/ping")) return true;


        if (!result.contains("INVALID") && !result.contains("kaputt")) {
            String accessLevel = userController.getAccessLevel(new JSONObject(result).getInt("user_id"));
            if (accessLevel.equals("none")) response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            if(accessLevel.equals("admin")) return true;

            if(accessLevel.equals("user") && (request.getRequestURL().toString().contains("/api/users/") || (request.getRequestURL().toString().contains("/posts/bestPost")) || (request.getRequestURL().toString().contains("/geo/getUserGeo")))) {
                return request.getRequestURL().toString().endsWith("=" + new JSONObject(result).getInt("user_id")) || request.getRequestURL().toString().contains("=" + new JSONObject(result).getInt("user_id") + "&");
            }

            return accessLevel.equals("mod") && request.getRequestURL().toString().contains("forum");


        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return false;

    }
}

