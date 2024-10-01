package com.analysetool.repositories;

import com.analysetool.modells.RankingTotalProfileOld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingTotalProfileRepositoryOld extends JpaRepository<RankingTotalProfileOld, Long> {

    @Query("SELECT rt.rank FROM RankingTotalProfileOld rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);
}
