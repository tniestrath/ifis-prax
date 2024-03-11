package com.analysetool.repositories;

import com.analysetool.modells.SocialsImpressions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialsImpressionsRepository  extends JpaRepository<SocialsImpressions, Long> {

}
