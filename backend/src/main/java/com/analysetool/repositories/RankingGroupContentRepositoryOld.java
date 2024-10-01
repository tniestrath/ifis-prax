package com.analysetool.repositories;

import com.analysetool.modells.RankingGroupContentOld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingGroupContentRepositoryOld extends JpaRepository<RankingGroupContentOld, Long> {

    @Query("SELECT rt.rank FROM RankingGroupContentOld rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);

}
