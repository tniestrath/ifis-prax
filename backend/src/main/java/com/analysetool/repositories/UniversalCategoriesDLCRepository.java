package com.analysetool.repositories;

import com.analysetool.modells.UniversalCategoriesDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UniversalCategoriesDLCRepository extends JpaRepository<UniversalCategoriesDLC, Integer> {

     public UniversalCategoriesDLC getById(int id);

     @Query("SELECT u.stunde FROM UniversalCategoriesDLC u ORDER BY u.id DESC LIMIT 1")
     public int getLastStunde();

     @Query("SELECT u FROM UniversalCategoriesDLC u ORDER BY u.id DESC LIMIT 1")
     public UniversalCategoriesDLC getLast();
}
