package com.analysetool.repositories;

import com.analysetool.modells.RankingGroupProfileOld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingGroupProfileRepositoryOld extends JpaRepository<RankingGroupProfileOld, Long> {

    @Query("SELECT rt.rank FROM RankingGroupProfileOld rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);

}
