package com.analysetool.repositories;

import com.analysetool.modells.RankingTotalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingTotalProfileRepository extends JpaRepository<RankingTotalProfile, Long> {

}
