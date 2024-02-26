package com.analysetool.repositories;

import com.analysetool.modells.FinalSearchStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalSearchStatRepository extends JpaRepository<FinalSearchStat, Long> {
    // Hier können Sie bei Bedarf benutzerdefinierte Abfragemethoden hinzufügen
    List<FinalSearchStat> findAllByCity(String city);

    List<FinalSearchStat> findAllByState(String state);

    List<FinalSearchStat> findAllByCountry(String country);

    @Query("SELECT f.uniId, COUNT(f.uniId) FROM FinalSearchStat f GROUP BY f.uniId")
    List<Object[]> findUniIdCounts();


    @Query("SELECT s FROM FinalSearchStat s ORDER BY (s.foundAnbieterCount + s.foundArtikelCount + s.foundBlogCount + s.foundEventsCount + s.foundNewsCount + s.foundPodcastCount + s.foundRatgeberCount + s.foundWhitepaperCount) ASC")
    List<FinalSearchStat> getAllSearchesOrderedByFoundAscending();

    @Query("SELECT s FROM FinalSearchStat s WHERE s.searchQuery=:search AND ((s.foundAnbieterCount + s.foundArtikelCount + s.foundBlogCount + s.foundEventsCount + s.foundNewsCount + s.foundPodcastCount + s.foundRatgeberCount + s.foundWhitepaperCount) > 0) ORDER BY s.id DESC LIMIT 1")
    FinalSearchStat hasFoundForSearch(String search);

    @Query("SELECT s FROM FinalSearchStat s ORDER BY COUNT(s.searchQuery) DESC")
    List<FinalSearchStat> getAllSearchesOrderedByCount(Pageable pageable);

    @Query("SELECT COUNT(s.searchQuery) FROM FinalSearchStat s")
    Integer getCountForSearch(String search);

    @Query("SELECT s.id FROM FinalSearchStat s WHERE s.searchQuery=:search")
    List<Integer> getIdsBySearch(String search);
}
