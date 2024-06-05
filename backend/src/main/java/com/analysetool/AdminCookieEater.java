package com.analysetool;

import com.analysetool.api.LoginController;
import com.analysetool.api.UserController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminCookieEater implements HandlerInterceptor {

    @Autowired
    LoginController loginController;
    @Autowired
    UserController userController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String result = loginController.validateCookie(request);
        for(Cookie cookie : (new ArrayList<>(List.of(request.getCookies())))) {
            System.out.println(cookie.getDomain());
            if(cookie.getDomain().contains("it-sicherheit.de")) {
                result = loginController.validateCookie(cookie.getValue());
            }
        }

        boolean isForum = request.getRequestURL().toString().contains("/forum/");

        if(isForum) {
            return true;
        } else {
            if (!result.contains("INVALID")) {
                boolean isAdmin = userController.getType(new JSONObject(result).getInt("user_id")).equalsIgnoreCase("admin");
                if (!isAdmin) response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return isAdmin;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
    }
}

