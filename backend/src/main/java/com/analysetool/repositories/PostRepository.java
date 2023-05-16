package com.analysetool.repositories;

import com.analysetool.modells.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

   // @Query("Select * FROM wp_posts ")
    //List<Post> getAllPosts();
   @Query("SELECT p FROM Post p WHERE p.status = 'publish'")
   List<Post> findPublishedPosts();

   @Query("SELECT p FROM Post p where p.authorId = :id AND p.status='publish'")
   List<Post> findByAuthor(int id);

   @Query("SELECT p.id from Post p where p.title =:title")
   List<Long> getIdByTitle(String title);

   @Query("SELECT p.id from Post p where p.slug =:name AND p.status='publish'")
   Long getIdByName(String name);
}

