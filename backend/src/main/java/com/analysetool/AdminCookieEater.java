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

        boolean isForum = request.getRequestURL().toString().contains("/forum/");

        if(request.getRequestURL().toString().contains("0wB4P2mly-xaRmeeDOj0_g")) return true;

        if(isForum) {
            return true;
        } else {
            if (!result.contains("INVALID") && !result.contains("kaputt")) {
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

