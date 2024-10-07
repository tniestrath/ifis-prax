package com.analysetool.repositories;

import com.analysetool.modells.AuthorsRelationships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRelationshipRepository extends JpaRepository<AuthorsRelationships, Long> {

    List<AuthorsRelationships> findByPostId(long postId);

    @Query("SELECT a FROM AuthorsRelationships a JOIN WPTerm t ON t.id=a.authorTerm WHERE t.slug=:slug ORDER BY a.id DESC LIMIT 1")
    Optional<AuthorsRelationships> findByAuthorSlugFirst(String slug);

    @Query("SELECT a.authorTerm FROM AuthorsRelationships a WHERE a.postId=:postId")
    List<Long> findAuthorsTermIdsByPostId(long postId);

}
