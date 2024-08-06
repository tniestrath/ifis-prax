package com.analysetool.repositories;

import com.analysetool.modells.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

   // @Query("Select * FROM wp_posts ")
    //List<Post> getAllPosts();
   @Query("SELECT p FROM Post p WHERE p.status = 'publish'")
   List<Post> findPublishedPosts();

   @Query("SELECT p FROM Post p where :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug) AND p.status='publish' AND (p.type='post' OR p.type='video')")
   List<Post> findByAuthor(int id);

   @Query("SELECT p.id FROM Post p where :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug) AND p.status='publish' AND (p.type='post' OR p.type='video')")
   List<Long> findPostIdsByUserId(Long id);

   @Query("SELECT p.id FROM Post p WHERE :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug) AND p.status = 'publish' AND (p.type='post' OR p.type='video')")
   List<Long> findPostIdsByAuthor(int userId, Pageable pageable);

   List<Post> findByAuthorIdAndStatusAndType(long userId, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN wp_term_relationships wtr ON wtr.objectId = p.id LEFT JOIN WpTermTaxonomy wtt ON wtr.termTaxonomyId = wtt.termTaxonomyId LEFT JOIN WPTerm wpt ON wpt.id = wtt.termId WHERE p.status= 'publish' AND (p.type='post' OR p.type='video') AND :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug) AND (p.title LIKE %:search% OR p.content LIKE %:search%) AND wpt.slug =:filter ORDER BY p.date DESC")
   List<Post> findByAuthorPageable(long userId, String search, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.status= 'publish' AND (p.type='post' OR p.type='video') AND :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug) AND (p.title LIKE %:search% OR p.content LIKE %:search%) ORDER BY p.date DESC")
   List<Post> findByAuthorPageable(long userId, String search, Pageable pageable);

   @Query("SELECT p.id from Post p where p.title =:title")
   List<Long> getIdByTitle(String title);

   @Query("SELECT p.id from Post p where p.slug =:name AND p.status='publish' AND ((p.type='post' OR p.type='video') OR p.type='page' OR p.type='event')")
   Long getIdByName(String name);

   @Query("select p.date from Post p where p.id =:Id AND p.status= 'publish' AND (p.type='post' OR p.type='page' OR p.type='event')")
   LocalDateTime getPostDateById(long Id);

   @Query("SELECT p.content FROM Post p WHERE p.id =:pId AND (p.type='post' OR p.type='video')")
   String getContentById(long pId);


   @Query("SELECT p FROM Post p WHERE p.status = 'publish' AND (p.type='post' OR p.type='video') ORDER BY p.date DESC")
   List<Post> findAllUserPosts();


   @Query("SELECT p.date FROM Post p WHERE p.id =:pId AND p.status = 'publish' AND (p.type='post' OR p.type='video')")
   LocalDateTime getDateById(long pId);

   @Query("SELECT e.id FROM Post e WHERE e.status = 'publish' AND e.type = 'post' ORDER BY e.date DESC")
   List<Long> findByTypeOrderByDateDesc(Pageable pageable);

   @Query("SELECT e.id FROM Post e WHERE e.status = 'publish' AND e.type = 'post' ORDER BY e.date DESC")
   List<Long> findByTitle(Pageable pageable);

   @Query("SELECT e.id FROM Post e WHERE e.status = 'publish' AND e.type = 'post' AND e.title LIKE '%'+:title+'%' ORDER BY e.date DESC")
   List<Long> findByTitle(String title);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND (pt.type='blog' OR pt.type='news' OR pt.type='artikel' OR pt.type LIKE '%podcast%' OR pt.type='whitepaper')")
   List<Post> pageByTitleWithTypeQuery(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter%")
   List<Post> pageByTitleWithTypeQueryWithFilter(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id JOIN PostStats ps ON ps.artId=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY ps.clicks")
   List<Post> postPageByClicks(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY p.date")
   List<Post> postPageByCreation(String title, String status, String type, String filter, Pageable pageable);


   @Query("SELECT p FROM Post p JOIN PostStats ps ON ps.artId=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type ORDER BY ps.clicks")
   List<Post> postPageByClicks(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type ORDER BY p.date")
   List<Post> postPageByCreation(String title, String status, String type, Pageable pageable);

   List<Post> findByAuthorIdAndStatusIsAndTypeIsOrderByModifiedDesc(long authorId, String status, String type, Pageable pageable);

   List<Post> findByTitleContainingAndAuthorIdAndStatusIsAndTypeIsOrderByModifiedDesc(String title, long authorId, String status, String type, Pageable pageable);

   @Query("SELECT p.id FROM Post p WHERE p.title LIKE %:title% AND p.status='inherit' AND p.type='attachment'")
   Optional<Long> findByTitleLike(String title);

   @Query("SELECT p FROM Post p WHERE :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug) AND DATE(p.date) = DATE(:date) AND p.status='publish' AND p.type='post'")
   List<Post> getPostsByAuthorAndDate(long authorId, LocalDate date);

   @Query("SELECT p.id FROM Post p WHERE p.id NOT IN (SELECT u.post_id FROM PostTypes u) AND p.status='publish' AND p.type='post'")
   List<Integer> getIdsOfUntyped();

   @Query("SELECT p.parentId FROM Post p WHERE p.type='attachment' AND p.status='inherit' AND p.id IN :postIds AND p.guid LIKE %:filename")
   Optional<Long> getParentFromListAnd(List<Long> postIds, String filename);

   @Query("SELECT p FROM Post p WHERE p.type='post' AND p.status='publish' ORDER BY p.date DESC LIMIT 1")
   Post getNewestPost();

   @Query("SELECT p FROM Post p JOIN wp_term_relationships wtr ON p.id= wtr.objectId JOIN WpTermTaxonomy wpt ON wtr.termTaxonomyId=wpt.termTaxonomyId WHERE p.type='event' AND p.status='publish' AND wpt.termId=:typeId AND p.title LIKE %:search%")
   List<Post> getAllEventsWithTypeAndSearch(long typeId, String search, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.type='event' AND p.status='publish' AND p.title LIKE %:search%")
   List<Post> getAllEventsWithSearch(String search, Pageable pageable);

   @Query("SELECT p FROM Post p JOIN wp_term_relationships wtr ON p.id= wtr.objectId JOIN WpTermTaxonomy wpt ON wtr.termTaxonomyId=wpt.termTaxonomyId WHERE p.type='event' AND p.status='publish' AND wpt.termId=:typeId AND p.title LIKE %:search% AND p.authorId=:authorId")
   List<Post> getAllEventsWithTypeAndSearchAndAuthor(long typeId, String search, long authorId, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.type='event' AND p.status='publish' AND p.title LIKE %:search% AND :userId IN (SELECT a FROM AuthorsRelationships a JOIN WPTerm term ON a.authorTerm = term.id JOIN Post post ON p.slug = term.slug)")
   List<Post> getAllEventsWithSearchAndAuthor(String search, long authorId, Pageable pageable);

   @Query("Select p FROM Post p WHERE p.slug =:postName AND p.type = 'page' ")
   Optional<Post> findPageByPostName(String postName);
}

