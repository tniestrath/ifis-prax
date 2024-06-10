package com.analysetool.repositories;

import com.analysetool.modells.MembershipsBuffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipBufferRepository extends JpaRepository<MembershipsBuffer, Long> {

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId=:userId ORDER BY m.id DESC LIMIT 1")
    MembershipsBuffer getLastByUserId(Long userId);

    @Query("SELECT MAX(m.id) FROM MembershipsBuffer m WHERE m.userId=:userId AND m.id NOT IN (SELECT MAX(m2.id) FROM MembershipsBuffer m2) ORDER BY m.id DESC LIMIT 1")
    Long getSecondLastByUserId(Long userId);

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId NOT IN (SELECT u.id FROM WPUser u)")
    List<MembershipsBuffer> findAllDeleted();
}
