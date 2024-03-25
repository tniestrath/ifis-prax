package com.analysetool.repositories;

import com.analysetool.modells.FeatureWishes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureWishesRepository extends JpaRepository<FeatureWishes, Long> {

    @Query("SELECT f FROM FeatureWishes f WHERE f.email=:email AND f.feature=:feature AND f.isFixed=:isFixed AND f.team=:team AND f.isNew=:isNew")
    Optional<FeatureWishes> getByAllExceptId(String email, String feature, String team, boolean isNew);
}
