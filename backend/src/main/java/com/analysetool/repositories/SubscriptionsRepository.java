package com.analysetool.repositories;

import com.analysetool.modells.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
