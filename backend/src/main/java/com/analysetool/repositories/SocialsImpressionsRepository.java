package com.analysetool.repositories;

import com.analysetool.modells.SocialsImpressions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT sum(s.linkedIn) from SocialsImpressions s Where s.postId=:postId")
    Long sumUpLinkedInOfPostId(Long postId);

    @Query("SELECT sum(s.twitter) from SocialsImpressions s Where s.postId=:postId")
    Long sumUpTwitterOfPostId(Long postId);

    @Query("SELECT sum(s.facebook) from SocialsImpressions s Where s.postId=:postId")
    Long sumUpFacebookOfPostId(Long postId);

    @Query("SELECT sum(s.linkedIn) from SocialsImpressions s Where s.userId=:userId")
    Long sumUpLinkedInOfUserId(Long userId);

    @Query("SELECT sum(s.twitter) from SocialsImpressions s Where s.userId=:userId")
    Long sumUpTwitterOfUserId(Long userId);

    @Query("SELECT sum(s.facebook) from SocialsImpressions s Where s.userId=:userId")
    Long sumUpFacebookOfUserId(Long userId);

    @Query("select distinct s.postId from SocialsImpressions s where s.postId>1")
    List<Long> getAllPostIds();

    @Query("select distinct s.userId from SocialsImpressions s where s.userId>1")
    List<Long> getAllUserIds();
}
