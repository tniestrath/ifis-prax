package com.analysetool.repositories;

import com.analysetool.modells.Bounce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BounceRepository extends JpaRepository<Bounce, Long> {

    @Query("SELECT b FROM Bounce b WHERE b.uniId=:uniId")
    Optional<Bounce> findByUniId(long uniId);

}
