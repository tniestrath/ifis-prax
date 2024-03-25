package com.analysetool.repositories;

import com.analysetool.modells.FeatureWishes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureWishesRepository extends JpaRepository<FeatureWishes, Long> {

}
