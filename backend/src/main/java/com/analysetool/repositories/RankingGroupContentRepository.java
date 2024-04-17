package com.analysetool.repositories;

import com.analysetool.modells.RankingGroupContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingGroupContentRepository extends JpaRepository<RankingGroupContent, Long> {

}
