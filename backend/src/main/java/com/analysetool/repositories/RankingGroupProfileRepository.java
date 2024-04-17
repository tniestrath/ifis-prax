package com.analysetool.repositories;

import com.analysetool.modells.RankingGroupProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingGroupProfileRepository extends JpaRepository<RankingGroupProfile, Long> {

}
