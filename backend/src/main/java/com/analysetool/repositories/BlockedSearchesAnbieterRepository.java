package com.analysetool.repositories;

import com.analysetool.modells.BlockedSearchesAnbieter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedSearchesAnbieterRepository extends JpaRepository<BlockedSearchesAnbieter, Long> {

    @Query("SELECT b FROM BlockedSearchesAnbieter b WHERE b.search=:search")
    Optional<BlockedSearchesAnbieter> getBySearch(String search);

}
