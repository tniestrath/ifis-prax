package com.analysetool.repositories;

import com.analysetool.modells.UniversalCategoriesDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniversalCategoriesDLCRepository extends JpaRepository<UniversalCategoriesDLC, Integer> {

     public List<UniversalCategoriesDLC> getByUniStatId(int id);

     @Query("SELECT u.stunde FROM UniversalCategoriesDLC u ORDER BY u.id DESC LIMIT 1")
     public int getLastStunde();

     @Query("SELECT u FROM UniversalCategoriesDLC u ORDER BY u.id DESC LIMIT 1")
     public UniversalCategoriesDLC getLast();

     public UniversalCategoriesDLC getByUniStatIdAndStunde(int uniId, int hour);

     @Query("SELECT SUM(u.viewsGlobal) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsGlobalByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsArticle) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsArticleByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsNews) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsNewsByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsBlog) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsBlogByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsPodcast) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsPodcastByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsWhitepaper) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsWhitepaperByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsRatgeberByUniStatId(int uniID);



     @Query("SELECT SUM(u.besucherGlobal) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserGlobalByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherArticle) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserArticleByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherNews) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserNewsByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherBlog) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserBlogByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherPodcast) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserPodcastByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherWhitepaper) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserWhitepaperByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserRatgeberByUniStatId(int uniID);

}
