package com.analysetool.repositories;

import com.analysetool.modells.AnbieterSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnbieterSearchRepository extends JpaRepository<AnbieterSearch, Long> {


    List<AnbieterSearch>findTop15ById();

    //Limit funktioniert in Queries so nicht, ChatGPT lügt. Limit lässt sich nicht als Variable angeben.
    @Query(value = "SELECT u FROM AnbieterSearch u ORDER BY u.id DESC LIMIT 10")
    List<AnbieterSearch> findLast10();

}
