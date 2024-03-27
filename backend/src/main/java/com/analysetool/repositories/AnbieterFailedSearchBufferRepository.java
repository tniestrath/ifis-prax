package com.analysetool.repositories;

import com.analysetool.modells.AnbieterFailedSearchBuffer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnbieterFailedSearchBufferRepository extends JpaRepository<AnbieterFailedSearchBuffer, Long> {

    @Query("SELECT a FROM AnbieterFailedSearchBuffer a WHERE a.search=:search AND a.city=:cityName AND a.plz=:plz AND a.umkreis=:umkreis")
    Optional<AnbieterFailedSearchBuffer> getByData(String search, String cityName, int plz, int umkreis);

    @Query("SELECT a FROM AnbieterFailedSearchBuffer a ORDER BY a.count DESC")
    List<AnbieterFailedSearchBuffer> getPageable(Pageable pageable);

    @Query("SELECT c FROM AnbieterFailedSearchBuffer c WHERE c.city=:city AND c.search=:search")
    Optional<AnbieterFailedSearchBuffer> findByCityAndSearch(String city, String search);
}
