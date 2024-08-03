package com.analysetool;

import com.analysetool.services.LoginService;
import com.analysetool.util.DashConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private static final String[] ADMIN_IDS = {
            "0",
            "1",
            "20",
            "27"
    };

    private static final LoginService loginService = new LoginService(new DashConfig());

    public static Authentication getAuthentication(HttpServletRequest request) {
        var cookie = "";
        for (Cookie c : request.getCookies()){
            if (c.getName().contains("wordpress")){
                cookie = c.getValue();
            }
        }
        String user_id;
        if(!cookie.equals("")) {
            user_id = loginService.validateCookie(cookie);
        } else {
            user_id = "";
        }
        for (String id : ADMIN_IDS) {
            if(user_id.contains("{user_id: ")) {
                if (user_id.contains(id)) {
                    return new ApiKeyAuthentication(cookie, AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN, USER"));
                }
            }
        }
        return new ApiKeyAuthentication(cookie, AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
    }
}
