package com.analysetool;

import com.analysetool.api.LoginController;
import com.analysetool.util.DashConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AuthenticationService {


    private static final LoginController loginController = new LoginController(new DashConfig());

    public static Authentication getAuthentication(HttpServletRequest request) throws Exception {
        var cookie = "";
        for (Cookie c : request.getCookies()){
            if (c.getName().contains("wordpress")){
                cookie = c.getValue();
            }
        }
        String apiKey = loginController.validateCookie(cookie);

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN, USER"));
    }
}
