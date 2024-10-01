package com.analysetool.repositories;

import com.analysetool.modells.RankingTotalContentOld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingTotalContentRepositoryOld extends JpaRepository<RankingTotalContentOld, Long> {

    @Query("SELECT rt.rank FROM RankingTotalContentOld rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);

}
