package com.analysetool.repositories;

import com.analysetool.modells.ReferrerTargets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferrerTargetsRepository extends JpaRepository<ReferrerTargets, Long> {

    Optional<ReferrerTargets> getReferrerTargetsByLink(String link);

}
