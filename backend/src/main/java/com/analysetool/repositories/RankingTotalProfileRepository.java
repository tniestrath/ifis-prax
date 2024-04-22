package com.analysetool.repositories;

import com.analysetool.modells.RankingTotalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingTotalProfileRepository extends JpaRepository<RankingTotalProfile, Long> {

    @Query("SELECT rt.rank FROM RankingTotalProfile rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);
}
