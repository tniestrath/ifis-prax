package com.analysetool.repositories;

import com.analysetool.modells.UniversalCategoriesDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UniversalCategoriesDLCRepository extends JpaRepository<UniversalCategoriesDLC, Integer> {

     public UniversalCategoriesDLC getById(int id);

}
