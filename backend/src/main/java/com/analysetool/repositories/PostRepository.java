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

   @Query("SELECT p FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm  t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND (p.type='post' OR p.type='video') " +
           "ORDER BY p.date DESC")
   List<Post> findByAuthor(long userId);

   @Query("SELECT p.id FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm  t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND (p.type='post' OR p.type='video') " +
           "ORDER BY p.date DESC")
   List<Long> findPostIdsByUserId(long userId);

   @Query("SELECT p FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND (p.type='post' OR p.type='video') " +
           "AND (p.title LIKE %:search% OR p.content LIKE %:search%) " +
           "AND t.slug=:filter " +
           "ORDER BY p.date DESC")
   List<Post> findByAuthorPageable(long userId, String search, String filter, Pageable pageable);

   @Query("SELECT p FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm  t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND (p.type='post' OR p.type='video') " +
           "AND (p.title LIKE %:search% OR p.content LIKE %:search%)" +
           "ORDER BY p.date DESC")
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

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND (pt.type='blog' OR pt.type='news' OR pt.type='artikel' OR pt.type LIKE '%podcast%' OR pt.type='whitepaper') ORDER BY p.id ASC")
   List<Post> pageByTitleWithTypeQueryByIdASC(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND (pt.type='blog' OR pt.type='news' OR pt.type='artikel' OR pt.type LIKE '%podcast%' OR pt.type='whitepaper') ORDER BY p.id DESC")
   List<Post> pageByTitleWithTypeQueryByIdDESC(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter%")
   List<Post> pageByTitleWithTypeQueryWithFilter(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY p.id ASC")
   List<Post> pageByTitleWithTypeQueryWithFilterIdASC(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY p.id DESC")
   List<Post> pageByTitleWithTypeQueryWithFilterIdDESC(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id JOIN PostStats ps ON ps.artId=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY ps.clicks ASC")
   List<Post> postPageByClicksASC(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id JOIN PostStats ps ON ps.artId=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY ps.clicks DESC")
   List<Post> postPageByClicksDESC(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY p.id ASC")
   List<Post> postPageByCreationByIdASC(String title, String status, String type, String filter, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN PostTypes pt ON pt.post_id=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type AND pt.type LIKE %:filter% ORDER BY p.id DESC")
   List<Post> postPageByCreationByIdDESC(String title, String status, String type, String filter, Pageable pageable);


   @Query("SELECT p FROM Post p JOIN PostStats ps ON ps.artId=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type ORDER BY ps.clicks ASC")
   List<Post> postPageByClicksASC(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p JOIN PostStats ps ON ps.artId=p.id WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type ORDER BY ps.clicks DESC")
   List<Post> postPageByClicksDESC(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type ORDER BY p.id ASC")
   List<Post> postPageByCreationByIdASC(String title, String status, String type, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% AND p.status=:status AND p.type=:type ORDER BY p.id DESC")
   List<Post> postPageByCreationByIdDESC(String title, String status, String type, Pageable pageable);


   @Query("SELECT p FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm  t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND (p.type='post' OR p.type='video') " +
           "AND DATE(p.date) = DATE(:date)" +
           "ORDER BY p.date DESC")
   List<Post> getPostsByAuthorAndDate(long userId, LocalDate date);

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

   @Query("SELECT p FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm  t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND p.type='event' " +
           "AND p.title LIKE %:search% " +
           "AND t.id=:typeId " +
           "ORDER BY p.date DESC")
   List<Post> getAllEventsWithTypeAndSearchAndAuthor(long typeId, String search, long userId, Pageable pageable);

   @Query("SELECT p FROM AuthorsRelationships a " +
           "JOIN Post p ON a.postId=p.id " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm  t ON t.id=wtt.termId " +
           "WHERE t.slug IN (SELECT u.nicename FROM WPUser u WHERE u.id=:userId) " +
           "AND p.status= 'publish' AND p.type='event' " +
           "AND p.title LIKE %:search% " +
           "ORDER BY p.date DESC")
   List<Post> getAllEventsWithSearchAndAuthor(String search, long userId, Pageable pageable);

   @Query("Select p FROM Post p WHERE p.slug =:postName AND p.type = 'page' ")
   Optional<Post> findPageByPostName(String postName);

   @Query("SELECT p.title FROM Post p " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm t ON t.id=wtt.termId JOIN PostTypes pt ON p.id = pt.post_id WHERE " +
           "p.status= 'publish' AND p.type='post' " +
           "AND p.title LIKE %:search% AND pt.type=:filter " +
           "ORDER BY p.date DESC")
   List<String> getSuggestions(String search, String filter);

   @Query("SELECT p.title FROM Post p " +
           "JOIN wp_term_relationships wtr ON wtr.objectId=p.id " +
           "JOIN WpTermTaxonomy wtt ON wtt.termTaxonomyId=wtr.termTaxonomyId " +
           "JOIN WPTerm t ON t.id=wtt.termId JOIN PostTypes pt ON p.id = pt.post_id WHERE " +
           "p.status = 'publish' AND p.type='post' " +
           "AND p.title LIKE %:search%" +
           "ORDER BY p.date DESC")
   List<String> getSuggestions(String search);

}



