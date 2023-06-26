package com.analysetool.repositories;

import com.analysetool.modells.UserStats;
import com.analysetool.modells.WPUserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WPUserMetaRepository extends JpaRepository<WPUserMeta, Long> {

    boolean existsByUserId(Long user_id);

    WPUserMeta findByUserId(Long user_id);

}

