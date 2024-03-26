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

     @Query("SELECT u FROM UniversalCategoriesDLC  u WHERE u.uniStatId=:uniID AND u.stunde=:hour")
     public UniversalCategoriesDLC getByUniStatIdAndStunde(int uniID, int hour);

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

     @Query("SELECT SUM(u.viewsVideos) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsVideosByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsWhitepaper) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsWhitepaperByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsEvents) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsEventsByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsRatgeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberPost) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsRatgeberPostByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberGlossar) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsRatgeberGlossarByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberBuch) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsRatgeberBuchByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberSelf) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsRatgeberSelfByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsMain) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsMainByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsAnbieter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsAnbieterByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsUeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsUeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsImpressum) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsImpressumByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsPreisliste) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsPreislisteByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsPartner) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsPartnerByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsDatenschutz) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsDatenschutzByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsNewsletter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsNewsletterByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsImage) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsImageByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsAGBS) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumViewsAGBSByUniStatId(int uniID);

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

     @Query("SELECT SUM(u.besucherVideos) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserVideosByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherWhitepaper) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserWhitepaperByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherEvents) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserEventsByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserRatgeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberPost) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserRatgeberPostByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberGlossar) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserRatgeberGlossarByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberBuch) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserRatgeberBuchByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberSelf) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserRatgeberSelfByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherMain) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserMainByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherAnbieter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserAnbieterByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherUeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserUeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherImpressum) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserImpressumByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherPreisliste) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserPreislisteByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherPartner) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserPartnerByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherDatenschutz) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserDatenschutzByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherNewsletter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserNewsletterByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherImage) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserImageByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherAGBS) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     public int getSumUserAGBSByUniStatId(int uniID);





     @Query("SELECT SUM(u.viewsGlobal) FROM UniversalCategoriesDLC u")
     public int getSumViewsGlobalAllTime();

     @Query("SELECT SUM(u.viewsArticle) FROM UniversalCategoriesDLC u")
     public int getSumViewsArticleAllTime();

     @Query("SELECT SUM(u.viewsNews) FROM UniversalCategoriesDLC u")
     public int getSumViewsNewsAllTime();

     @Query("SELECT SUM(u.viewsBlog) FROM UniversalCategoriesDLC u")
     public int getSumViewsBlogAllTime();

     @Query("SELECT SUM(u.viewsPodcast) FROM UniversalCategoriesDLC u")
     public int getSumViewsPodcastAllTime();

     @Query("SELECT SUM(u.viewsVideos) FROM UniversalCategoriesDLC u")
     public int getSumViewsVideosAllTime();

     @Query("SELECT SUM(u.viewsWhitepaper) FROM UniversalCategoriesDLC u")
     public int getSumViewsWhitepaperAllTime();

     @Query("SELECT SUM(u.viewsEvents) FROM UniversalCategoriesDLC u")
     public int getSumViewsEventsAllTime();

     @Query("SELECT SUM(u.viewsRatgeber) FROM UniversalCategoriesDLC u")
     public int getSumViewsRatgeberAllTime();

     @Query("SELECT SUM(u.viewsRatgeberPost) FROM UniversalCategoriesDLC u")
     public int getSumViewsRatgeberPostAllTime();

     @Query("SELECT SUM(u.viewsRatgeberGlossar) FROM UniversalCategoriesDLC u")
     public int getSumViewsRatgeberGlossarAllTime();

     @Query("SELECT SUM(u.viewsRatgeberBuch) FROM UniversalCategoriesDLC u")
     public int getSumViewsRatgeberBuchAllTime();

     @Query("SELECT SUM(u.viewsRatgeberSelf) FROM UniversalCategoriesDLC u")
     public int getSumViewsRatgeberSelfAllTime();

     @Query("SELECT SUM(u.viewsMain) FROM UniversalCategoriesDLC u")
     public int getSumViewsMainAllTime();

     @Query("SELECT SUM(u.viewsAnbieter) FROM UniversalCategoriesDLC u")
     public int getSumViewsAnbieterAllTime();

     @Query("SELECT SUM(u.viewsUeber) FROM UniversalCategoriesDLC u")
     public int getSumViewsUeberAllTime();

     @Query("SELECT SUM(u.viewsImpressum) FROM UniversalCategoriesDLC u")
     public int getSumViewsImpressumAllTime();

     @Query("SELECT SUM(u.viewsPreisliste) FROM UniversalCategoriesDLC u")
     public int getSumViewsPreislisteAllTime();

     @Query("SELECT SUM(u.viewsPartner) FROM UniversalCategoriesDLC u")
     public int getSumViewsPartnerAllTime();

     @Query("SELECT SUM(u.viewsDatenschutz) FROM UniversalCategoriesDLC u")
     public int getSumViewsDatenschutzAllTime();

     @Query("SELECT SUM(u.viewsNewsletter) FROM UniversalCategoriesDLC u")
     public int getSumViewsNewsletterAllTime();

     @Query("SELECT SUM(u.viewsImage) FROM UniversalCategoriesDLC u")
     public int getSumViewsImageAllTime();

     @Query("SELECT SUM(u.viewsAGBS) FROM UniversalCategoriesDLC u")
     public int getSumViewsAGBSAllTime();



     @Query("SELECT SUM(u.besucherGlobal) FROM UniversalCategoriesDLC u")
     public int getSumUserGlobalAllTime();

     @Query("SELECT SUM(u.besucherArticle) FROM UniversalCategoriesDLC u")
     public int getSumUserArticleAllTime();

     @Query("SELECT SUM(u.besucherNews) FROM UniversalCategoriesDLC u")
     public int getSumUserNewsAllTime();

     @Query("SELECT SUM(u.besucherBlog) FROM UniversalCategoriesDLC u")
     public int getSumUserBlogAllTime();

     @Query("SELECT SUM(u.besucherPodcast) FROM UniversalCategoriesDLC u")
     public int getSumUserPodcastAllTime();

     @Query("SELECT SUM(u.besucherVideos) FROM UniversalCategoriesDLC u")
     public int getSumUserVideosAllTime();

     @Query("SELECT SUM(u.besucherWhitepaper) FROM UniversalCategoriesDLC u")
     public int getSumUserWhitepaperAllTime();

     @Query("SELECT SUM(u.besucherEvents) FROM UniversalCategoriesDLC u")
     public int getSumUserEventsAllTime();

     @Query("SELECT SUM(u.besucherRatgeber) FROM UniversalCategoriesDLC u ")
     public int getSumUserRatgeberAllTime();

     @Query("SELECT SUM(u.besucherRatgeberPost) FROM UniversalCategoriesDLC u")
     public int getSumUserRatgeberPostAllTime();

     @Query("SELECT SUM(u.besucherRatgeberGlossar) FROM UniversalCategoriesDLC u")
     public int getSumUserRatgeberGlossarAllTime();

     @Query("SELECT SUM(u.besucherRatgeberBuch) FROM UniversalCategoriesDLC u")
     public int getSumUserRatgeberBuchAllTime();

     @Query("SELECT SUM(u.besucherRatgeberSelf) FROM UniversalCategoriesDLC u")
     public int getSumUserRatgeberSelfAllTime();

     @Query("SELECT SUM(u.besucherMain) FROM UniversalCategoriesDLC u")
     public int getSumUserMainAllTime();

     @Query("SELECT SUM(u.besucherAnbieter) FROM UniversalCategoriesDLC u")
     public int getSumUserAnbieterAllTime();

     @Query("SELECT SUM(u.besucherUeber) FROM UniversalCategoriesDLC u")
     public int getSumUserUeberAllTime();

     @Query("SELECT SUM(u.besucherImpressum) FROM UniversalCategoriesDLC u")
     public int getSumUserImpressumAllTime();

     @Query("SELECT SUM(u.besucherPreisliste) FROM UniversalCategoriesDLC u")
     public int getSumUserPreislisteAllTime();

     @Query("SELECT SUM(u.besucherPartner) FROM UniversalCategoriesDLC u")
     public int getSumUserPartnerAllTime();

     @Query("SELECT SUM(u.besucherDatenschutz) FROM UniversalCategoriesDLC u")
     public int getSumUserDatenschutzAllTime();

     @Query("SELECT SUM(u.besucherNewsletter) FROM UniversalCategoriesDLC u")
     public int getSumUserNewsletterAllTime();

     @Query("SELECT SUM(u.besucherImage) FROM UniversalCategoriesDLC u")
     public int getSumUserImageAllTime();

     @Query("SELECT SUM(u.besucherAGBS) FROM UniversalCategoriesDLC u")
     public int getSumUserAGBSAllTime();

}
