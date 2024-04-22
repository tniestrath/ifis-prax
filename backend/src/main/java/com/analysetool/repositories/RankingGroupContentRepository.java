package com.analysetool.repositories;

import com.analysetool.modells.RankingGroupContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingGroupContentRepository extends JpaRepository<RankingGroupContent, Long> {

    @Query("SELECT rt.rank FROM RankingGroupContent rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);

}
