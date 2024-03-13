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

    //Limit funktioniert in Queries so nicht, ChatGPT lügt. Limit lässt sich nicht als Variable angeben.-> pages gleicher schmutz (Lüge Pages gut)
    @Query(value = "SELECT u FROM AnbieterSearch u ORDER BY u.id DESC LIMIT 10")
    List<AnbieterSearch> findLast10();

    @Query("SELECT a FROM AnbieterSearch a WHERE a.count_found=0 ORDER BY a.city_name, a.search")
    List<AnbieterSearch> findAllCount0();

    @Query("SELECT a FROM AnbieterSearch a WHERE (a.search!='' OR a.city_name!='' OR a.plz!=0) AND a.count_found=0 AND a.id NOT IN(SELECT b.id FROM AnbieterSearch b WHERE b.count_found > 0)")
    List<AnbieterSearch> findAllNoneFound();

    @Query("SELECT COUNT(c) FROM AnbieterSearch c WHERE c.search=:search AND c.city_name=:cityName AND c.plz=:plz AND c.umkreis=:umkreis")
    int getCountForData(String search, String cityName, int plz, int umkreis);

    @Query("SELECT c FROM AnbieterSearch c WHERE c.city_name=:city")
    List<AnbieterSearch> findByCity(String city);

    @Query("SELECT c FROM AnbieterSearch c WHERE c.search=:search")
    List<AnbieterSearch> findBySearch(String search);

    @Query("SELECT e FROM AnbieterSearch e WHERE e.search='' AND e.city_name=''")
    List<AnbieterSearch> findAllEmpty();

    Page<AnbieterSearch> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT c FROM AnbieterSearch c WHERE c.search=:search AND c.city_name=:cityName AND c.plz=:plz AND c.umkreis=:umkreis AND c.count_found>0")
    List<AnbieterSearch> findCountNotZeroForData(String search, String cityName, int plz, int umkreis);
}
