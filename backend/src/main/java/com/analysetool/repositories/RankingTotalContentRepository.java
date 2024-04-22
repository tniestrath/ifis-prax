package com.analysetool.repositories;

import com.analysetool.modells.RankingTotalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingTotalContentRepository extends JpaRepository<RankingTotalContent, Long> {

    @Query("SELECT rt.rank FROM RankingTotalContent rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);

}
