package com.analysetool.repositories;

import com.analysetool.modells.Badwords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadWordRepository extends JpaRepository<Badwords, Long> {

    @Query("SELECT b.badWord FROM Badwords b")
    List<String> getAllBadWords();

    @Query("SELECT b FROM Badwords b WHERE b.badWord =:word")
    Optional<Badwords> getByWord(String word);

}
