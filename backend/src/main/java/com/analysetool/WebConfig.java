package com.analysetool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminCookieEater adminCookieEater;
    @Autowired
    private ModeratorCookieEater moderatorCookieEater;

    private String[] apiPaths = new String[]{"/api/**", ""};
    private String[] forumPaths = new String[]{"/api/forum/**", "/login", "/validate"};
    private String[] excludedPaths = new String[]{"/login", "/validate"};



    @Bean
    public MappedInterceptor adminCookieBeater() {
        return new MappedInterceptor(apiPaths, forumPaths, adminCookieEater);
    }

    @Bean
    public MappedInterceptor moderatorCookieBeater() {
        return new MappedInterceptor(forumPaths, excludedPaths, moderatorCookieEater);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminCookieBeater());

        registry.addInterceptor(moderatorCookieBeater());

        System.out.println("CookieEaters-Added");
    }
}
