package com.analysetool.services;

import com.analysetool.modells.WPUser;
import com.analysetool.repositories.WPUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class DashUserDetailsService implements UserDetailsService {

    private WPUserRepository userRepository;

    @Autowired
    public DashUserDetailsService(WPUserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        @SuppressWarnings("OptionalGetWithoutIsPresent") WPUser user= userRepository.findByLogin(username).get();
        return new User(user.getLogin(), user.getPassword(), mapIdsToAuthorities(user.getId()));
    }

    private Collection<GrantedAuthority> mapIdsToAuthorities(Long id){
        if (id == 0){
            return Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
        } else {
            return Collections.singleton(new SimpleGrantedAuthority("USER"));
        }
    }
}
