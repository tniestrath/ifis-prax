package com.analysetool.repositories;

import com.analysetool.modells.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

    @Query("SELECT s FROM Subscriptions s WHERE s.tag=:tagId AND s.type IS NULL AND s.author IS NULL AND s.word IS NULL")
    Optional<Subscriptions> findByTag(long tagId);

    @Query("SELECT s FROM Subscriptions s WHERE s.type=:type AND s.tag IS NULL AND s.author IS NULL AND s.word IS NULL")
    Optional<Subscriptions> findByType(String type);

    @Query("SELECT s FROM Subscriptions s WHERE s.type IS NULL AND s.tag IS NULL AND s.author IS NULL AND s.word=:word")
    Optional<Subscriptions> findByWord(String word);

    @Query("SELECT s FROM Subscriptions s WHERE s.type IS NULL AND s.tag IS NULL AND s.author=:author AND s.word IS NULL")
    Optional<Subscriptions> findByAuthor(long author);

    @Query("SELECT s FROM Subscriptions s WHERE s.type=:type AND s.tag=:tagId AND s.author=:author AND s.word=:word")
    Optional<Subscriptions> findByAll(String type, Integer tagId, Long author, String word);

    @Query("SELECT s FROM Subscriptions s WHERE s.tag=:tagId")
    List<Subscriptions> findAllByTagFull(long tagId);

    @Query("SELECT s FROM Subscriptions s WHERE s.type=:type")
    List<Subscriptions> findAllByTypeFull(String type);

    @Query("SELECT s FROM Subscriptions s WHERE s.word=:word")
    List<Subscriptions> findAllByWordFull(String word);

    @Query("SELECT s FROM Subscriptions s WHERE s.author=:author")
    List<Subscriptions> findAllByAuthorFull(long author);

    @Query("SELECT s.id FROM Subscriptions s WHERE s.tag=:tagId")
    List<Long> findAllByTag(long tagId);

    @Query("SELECT s.id FROM Subscriptions s WHERE s.type=:type")
    List<Long> findAllByType(String type);

    @Query("SELECT s.id FROM Subscriptions s WHERE s.word=:word")
    List<Long> findAllByWord(String word);

    @Query("SELECT s.id FROM Subscriptions s WHERE s.author=:author")
    List<Long> findAllByAuthor(long author);


    @Query("SELECT DISTINCT s.tag FROM Subscriptions s")
    List<Integer> findAllTags();

    @Query("SELECT DISTINCT s.type FROM Subscriptions s")
    List<String> findAllTypes();

    @Query("SELECT DISTINCT s.word FROM Subscriptions s")
    List<String> findAllWords();

    @Query("SELECT DISTINCT s.author FROM Subscriptions s")
    List<Long> findAllAuthors();

}
