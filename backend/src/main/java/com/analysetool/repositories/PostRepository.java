package com.analysetool.repositories;

import com.analysetool.modells.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

   // @Query("Select * FROM wp_posts ")
    //List<Post> getAllPosts();
   @Query("SELECT p FROM Post p WHERE p.status = 'publish'")
   List<Post> findPublishedPosts();

   @Query("SELECT p FROM Post p where p.authorId = :id AND p.status='publish' AND p.type='post'")
   List<Post> findByAuthor(int id);

   @Query("SELECT p.id FROM Post p where p.authorId = :id AND p.status='publish' AND p.type='post'")
   List<Long> findPostIdsByUserId(Long id);

   @Query("SELECT p.id FROM Post p WHERE p.authorId = :userId AND p.status = 'publish' AND p.type = 'post'")
   List<Long> findPostIdsByAuthor(int userId, Pageable pageable);

   List<Post> findByAuthorIdAndStatusAndType(long userId, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN wp_term_relationships wtr ON wtr.objectId = p.id LEFT JOIN WpTermTaxonomy wtt ON wtr.termTaxonomyId = wtt.termTaxonomyId LEFT JOIN WPTerm wpt ON wpt.id = wtt.termId WHERE p.status= 'publish' AND p.type='post' AND p.authorId=:userId AND (p.title LIKE %:search% OR p.content LIKE %:search%) AND wpt.slug =:filter")
   List<Post> findByAuthorPageable(long userId, String search, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.status= 'publish' AND p.type='post' AND p.authorId=:userId AND (p.title LIKE %:search% OR p.content LIKE %:search%)")
   List<Post> findByAuthorPageable(long userId, String search, Pageable pageable);

   @Query("SELECT p.id from Post p where p.title =:title")
   List<Long> getIdByTitle(String title);

   @Query("SELECT p.id from Post p where p.slug =:name AND p.status='publish' AND p.type='post'")
   Long getIdByName(String name);

   @Query("select p.date from Post p where p.id =:Id AND p.status= 'publish' AND p.type='post'")
   LocalDateTime getPostDateById(long Id);

   @Query("SELECT p.content FROM Post p WHERE p.id =:pId AND p.type='post'")
   public String getContentById(long pId);


   @Query("SELECT p FROM Post p WHERE p.status = 'publish' AND p.type = 'post' ORDER BY p.date DESC")
   List<Post> findAllUserPosts();


   @Query("SELECT p.date FROM Post p WHERE p.id =:pId AND p.status = 'publish' AND p.type = 'post'")
   LocalDateTime getDateById(long pId);

   @Query("SELECT p.date FROM Post p WHERE p.status = 'publish' AND p.type = 'post'")
   Map<Integer, LocalDateTime> getAllDates();

   @Query("SELECT e.id FROM Post e WHERE e.status = 'publish' AND e.type = 'post' ORDER BY e.date DESC")
   List<Long> findByTypeOrderByDateDesc(Pageable pageable);

   @Query("SELECT e.id FROM Post e WHERE e.status = 'publish' AND e.type = 'post' ORDER BY e.date DESC")
   List<Long> findByTitle(Pageable pageable);

   @Query("SELECT e.id FROM Post e WHERE e.status = 'publish' AND e.type = 'post' AND e.title LIKE '%'+:title+'%' ORDER BY e.date DESC")
   List<Long> findByTitle(String title);

   List<Post> findByTitleContainingAndStatusIsAndTypeIs(String title, String status, String type, Pageable pageable);

   @Query("SELECT p.id FROM Post p WHERE p.title LIKE %:title%")
   Optional<Long> findByTitleLike(String title);
}

