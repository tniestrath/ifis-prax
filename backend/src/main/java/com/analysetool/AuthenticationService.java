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

    private static final String[] ADMIN_IDS = {
            "0",
            "1",
            "20"
    };

    private static final LoginController loginController = new LoginController(new DashConfig());

    public static Authentication getAuthentication(HttpServletRequest request) throws Exception {
        var cookie = "";
        for (Cookie c : request.getCookies()){
            if (c.getName().contains("wordpress")){
                cookie = c.getValue();
            }
        }
        String user_id = loginController.validateCookie(cookie);
        for (String id : ADMIN_IDS) {
            if (user_id.contains(id)){
                return new ApiKeyAuthentication(cookie, AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN, USER"));
            }
        }
        return new ApiKeyAuthentication(cookie, AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
    }
}
