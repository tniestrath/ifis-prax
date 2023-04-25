package com.analysetool.repositories;

import com.analysetool.modells.WPUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WPUserRepository extends JpaRepository<WPUser, Long> {

    Optional<WPUser> findByLogin(String login);
    Optional<WPUser> findByEmail(String email);

    // benutzerdefinierte Methoden, falls benötigt

}

