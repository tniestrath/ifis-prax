package com.analysetool.repositories;

import com.analysetool.modells.ReferrerAgents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferrerAgentsRepository extends JpaRepository<ReferrerAgents, Long> {

    Optional<ReferrerAgents> getReferrerAgentsByTargetIdAndAgent(long targetId, String Agent);

}
