package com.analysetool.repositories;

import com.analysetool.modells.MembershipsBuffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipBufferRepository extends JpaRepository<MembershipsBuffer, Long> {

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId=:userId ORDER BY m.id DESC LIMIT 1")
    MembershipsBuffer getLastByUserId(Long userId);

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId=:userId ORDER BY m.id DESC")
    List<MembershipsBuffer> getPageableSingle(Long userId, Pageable pageable);

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId NOT IN (SELECT u.id FROM WPUser u)")
    List<MembershipsBuffer> findAllDeleted();

    @Query("SELECT m FROM MembershipsBuffer m WHERE m.userId=:userId ORDER BY m.id DESC")
    Page<MembershipsBuffer> findAllForUser(long userId, Pageable pageable);

    @Query("SELECT m.membership FROM MembershipsBuffer m WHERE m.id < :id AND m.userId =: userId ORDER BY m.id DESC LIMIT 1")
    String getPreviousMembership(long userId, long id);
}
