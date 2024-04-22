package com.analysetool.repositories;

import com.analysetool.modells.RankingGroupProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingGroupProfileRepository extends JpaRepository<RankingGroupProfile, Long> {

    @Query("SELECT rt.rank FROM RankingGroupProfile rt WHERE rt.userId=:userId")
    Optional<Integer> getRankById(long userId);

}
