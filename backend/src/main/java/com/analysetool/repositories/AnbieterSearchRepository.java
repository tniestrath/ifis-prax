package com.analysetool.repositories;

import com.analysetool.modells.AnbieterSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnbieterSearchRepository extends JpaRepository<AnbieterSearch, Long> {


    List<AnbieterSearch>findTop15ById(Long id);

    //Limit funktioniert in Queries so nicht, ChatGPT lügt. Limit lässt sich nicht als Variable angeben.-> pages gleicher schmutz
    @Query(value = "SELECT u FROM AnbieterSearch u ORDER BY u.id DESC LIMIT 10")
    List<AnbieterSearch> findLast10();

    @Query("SELECT a FROM AnbieterSearch a WHERE a.count_found=0")
    List<AnbieterSearch> findAllCount0();

    Page<AnbieterSearch> findAllByOrderByIdDesc(Pageable pageable);
}
