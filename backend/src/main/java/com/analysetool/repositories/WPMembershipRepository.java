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

    @Query("SELECT u.user_id FROM WPMemberships u WHERE u.status='changed'")
    List<Long> getAllChangedMemberIds();

    @Query("SELECT u.user_id FROM WPMemberships u WHERE u.status='cancelled'")
    List<Long> getAllCancelledMemberIds();

    @Query("SELECT u.membership_id FROM WPMemberships u WHERE u.status='active' AND u.user_id=:id")
    Integer getUserMembership(int id);

}

