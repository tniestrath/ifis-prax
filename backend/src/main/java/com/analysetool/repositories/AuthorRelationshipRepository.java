package com.analysetool.repositories;

import com.analysetool.modells.AuthorsRelationships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRelationshipRepository extends JpaRepository<AuthorsRelationships, Long> {

    List<AuthorsRelationships> findByPostId(long postId);

}
