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
        System.out.println(request.getCookies()[1].getValue());
        String apiKey = loginController.validateCookie("philipAlbers|1690968265|uwJqLGDLv1CN5W85vRCgvxfv45khDRALNU9clRpevPN|1024b26d08a52b4b9d5ed58be2064c096efcd0ae944d116b2846918b757e67b6");
        System.out.println(apiKey);

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN, USER"));
    }
}
