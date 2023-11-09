package com.analysetool.repositories;

import com.analysetool.modells.AnbieterSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnbieterSearchRepository extends JpaRepository<AnbieterSearch, Long> {

/*    @Query("SELECT a from AnbieterSearch a order by a.id desc LIMIT =:x")
    List<AnbieterSearch> findLastX(int x);*/
    List<AnbieterSearch>findTop15ById();
}
