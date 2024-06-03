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

        System.out.println(result);

        boolean isForum = request.getRequestURL().toString().contains("/forum/");
        boolean isAdmin = userController.getType(new JSONObject(result).getInt("user_id")).equalsIgnoreCase("admin");

        if(isForum) {
            return true;
        } else {
            if (!result.contains("INVALID")) {
                if (!isAdmin) response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return isAdmin;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
    }
}

