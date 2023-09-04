package com.analysetool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new AuthenticationFilter(), BasicAuthenticationFilter.class);

        http.authorizeHttpRequests((auth) -> {
                auth.requestMatchers("/login", "/validate", "/users/profilePic", "/tags/getPostTagsIdName", "/posts/bestPost").permitAll();
                auth.anyRequest().authenticated();
            });

        return http.build();
    }*/

}
