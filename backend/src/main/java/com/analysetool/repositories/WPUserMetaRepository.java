package com.analysetool.repositories;

import com.analysetool.modells.WPUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WPUserMetaRepository extends JpaRepository<WPUserMeta, Long> {

    Optional<WPUserMeta> findByUserID(int id);

    Optional<WPUserMeta> findByMetaValue(String metaValue);

    Optional<WPUserMeta> findByAccountType(String accType);

    // benutzerdefinierte Methoden, falls benötigt

}

