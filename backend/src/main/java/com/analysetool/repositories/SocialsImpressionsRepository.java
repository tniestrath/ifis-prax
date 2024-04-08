package com.analysetool.repositories;

import com.analysetool.modells.SocialsImpressions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SocialsImpressionsRepository  extends JpaRepository<SocialsImpressions, Long> {

    Optional<SocialsImpressions> findByUniIdAndAndHourAndAndPostId(Integer uniId, Integer hour,Long postId);
    Optional<SocialsImpressions> findByUniIdAndAndHourAndAndUserId(Integer uniId, Integer hour,Long userId);

    List<SocialsImpressions> findByPostId(Long postId);
    List<SocialsImpressions> findByUserId(Long userId);
}
