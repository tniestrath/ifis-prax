package com.analysetool.repositories;

import com.analysetool.modells.MembershipsBuffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipBufferRepository extends JpaRepository<MembershipsBuffer, Long> {

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId=:userId ORDER BY m.id DESC LIMIT 1")
    MembershipsBuffer getLastByUserId(Long userId);

}
