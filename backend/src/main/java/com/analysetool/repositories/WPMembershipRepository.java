package com.analysetool.repositories;

import com.analysetool.modells.WPMemberships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPMembershipRepository extends JpaRepository<WPMemberships, Long> {

    @Query("SELECT u FROM WPMemberships u WHERE u.status='active'")
    List<WPMemberships> getAllActiveMembers();

    @Query("SELECT u.user_id FROM WPMemberships u WHERE u.status='active'")
    List<Long> getAllActiveMembersIds();

}

