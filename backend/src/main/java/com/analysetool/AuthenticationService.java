package com.analysetool;

import com.analysetool.api.LoginController;
import com.analysetool.util.DashConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

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
        if (user_id != null ||  !user_id.contains("INVALID")) {
            for (String id : ADMIN_IDS) {
                if (user_id.contains(id)) {
                    return new ApiKeyAuthentication(cookie, AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN,USER"));
                }
            }
            return new ApiKeyAuthentication(cookie, AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
        } else {
            return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return null;
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }

                @Override
                public boolean isAuthenticated() {
                    return false;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                }

                @Override
                public String getName() {
                    return null;
                }
            };
        }
    }
}
