package com.analysetool.repositories;

import com.analysetool.modells.UserStats;
import com.analysetool.modells.WPUserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WPUserMetaRepository extends JpaRepository<WPUserMeta, Long> {

    boolean existsByUser_id(Long user_id);

    WPUserMeta findByUser_id(Long user_id);

}

