package com.analysetool;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthentication implements Authentication {
    private final String apiKey;
    private boolean authenticated;
    public ApiKeyAuthentication(String apiKey, Collection<GrantedAuthority> authorities) {
        super();
        this.apiKey = apiKey;
        setAuthenticated(true);
    }

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
        return apiKey;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }


    @Override
    public String getName() {
        return null;
    }
}
