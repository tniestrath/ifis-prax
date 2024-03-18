package com.analysetool.repositories;

import com.analysetool.modells.BlockedSearches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedSearchesRepository extends JpaRepository<BlockedSearches, Long> {

    @Query("SELECT b FROM BlockedSearches b WHERE b.search=:search")
    Optional<BlockedSearches> getBySearch(String search);

}
