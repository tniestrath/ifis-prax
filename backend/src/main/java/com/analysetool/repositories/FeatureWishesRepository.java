package com.analysetool.repositories;

import com.analysetool.modells.FeatureWishes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureWishesRepository extends JpaRepository<FeatureWishes, Long> {

    @Query("SELECT f FROM FeatureWishes f WHERE f.isFixed = true")
    List<FeatureWishes> findAllFixed();

    @Query("SELECT f FROM FeatureWishes f WHERE f.isFixed = false")
    List<FeatureWishes> findAllNotFixed();

}
