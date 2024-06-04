package com.analysetool.repositories;

import com.analysetool.modells.ExternalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalServiceRepository extends JpaRepository<ExternalService, Long> {

    @Query("SELECT e FROM ExternalService e WHERE e.link=:link OR e.name=:name")
    Optional<ExternalService> findByLinkOrName(String link, String name);

}
