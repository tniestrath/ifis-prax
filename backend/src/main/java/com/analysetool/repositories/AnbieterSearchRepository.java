package com.analysetool.repositories;

import com.analysetool.modells.AnbieterSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnbieterSearchRepository extends JpaRepository<AnbieterSearch, Long> {


    List<AnbieterSearch>findTop15ById();
    @Query(value = "SELECT * FROM AnbieterSearch ORDER BY id DESC LIMIT :x", nativeQuery = true)
    List<AnbieterSearch> findLastX(int x);

}
