package com.analysetool.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="dash.config")
public class DashConfig {

    @Value("{access}")
    private String access;

    @Value("{wplogin}")
    private String wplogin;

    @Value("{validate}")
    private String validate;

    @Value("{profilephotos}")
    private String profilephotos;


    public String getProfilephotos() {
        return profilephotos;
    }

    public void setProfilephotos(String profilephotos) {
        this.profilephotos = profilephotos;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public String getWplogin() {
        return wplogin;
    }

    public void setWplogin(String wplogin) {
        this.wplogin = wplogin;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
