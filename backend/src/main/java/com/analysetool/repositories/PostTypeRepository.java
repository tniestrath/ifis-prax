package com.analysetool.repositories;

import com.analysetool.modells.PostTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTypeRepository extends JpaRepository<PostTypes, Long> {


    @Query("SELECT p.type FROM PostTypes p WHERE p.post_id=:id")
    String getType(int id);

    @Query("SELECT p.post_id FROM PostTypes p WHERE p.type =:type")
    List<Integer> getPostsByType(String type);

    @Query("SELECT p.post_id FROM PostTypes p WHERE p.type =:type")
    List<Long> getPostsByTypeLong(String type);

    @Query("SELECT p FROM PostTypes p WHERE p.type='default'")
    List<PostTypes> getDefault();
}