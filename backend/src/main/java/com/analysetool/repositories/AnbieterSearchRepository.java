package com.analysetool.repositories;

import com.analysetool.modells.AnbieterSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnbieterSearchRepository extends JpaRepository<AnbieterSearch, Long> {

}
