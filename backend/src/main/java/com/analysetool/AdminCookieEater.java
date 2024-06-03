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
        String cookie = request.getHeader("wordpress_logged_in");
        String result = loginController.validateCookie(request);
        if (cookie != null && !result.contains("INVALID")) {
            return userController.getType(new JSONObject(result).getInt("user_id")).equals("admin");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}

