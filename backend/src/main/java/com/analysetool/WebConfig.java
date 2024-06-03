package com.analysetool;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminCookieEater adminCookieEater;
    private final ModeratorCookieEater moderatorCookieEater;

    public WebConfig(AdminCookieEater adminCookieEater, ModeratorCookieEater moderatorCookieEater) {
        this.adminCookieEater = adminCookieEater;
        this.moderatorCookieEater = moderatorCookieEater;
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
        registry.addInterceptor(adminCookieEater)
                .addPathPatterns("/api/**").excludePathPatterns("/api/forum/**");

        registry.addInterceptor(moderatorCookieEater).addPathPatterns("/api/forum/**");
    }
}
