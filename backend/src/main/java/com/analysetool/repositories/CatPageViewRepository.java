package com.analysetool.repositories;

import com.analysetool.modells.CatPageViews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatPageViewRepository extends JpaRepository<CatPageViews, Long> {

    @Query("SELECT c FROM CatPageViews c WHERE c.uniId=:uniId AND c.cat=:cat AND c.page=:page")
    Optional<CatPageViews> findByUniAndCatAndPage(int uniId, String cat, int page);

}
