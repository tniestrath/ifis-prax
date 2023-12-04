package com.analysetool.repositories;

import com.analysetool.modells.PostTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTypeRepository extends JpaRepository<PostTypes, Long> {

}