package com.analysetool.repositories;

import com.analysetool.modells.RankingTotalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingTotalContentRepository extends JpaRepository<RankingTotalContent, Long> {

}
