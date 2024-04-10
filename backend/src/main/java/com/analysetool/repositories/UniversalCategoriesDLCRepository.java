package com.analysetool.repositories;

import com.analysetool.modells.UniversalCategoriesDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniversalCategoriesDLCRepository extends JpaRepository<UniversalCategoriesDLC, Integer> {

     List<UniversalCategoriesDLC> getByUniStatId(int id);

     @Query("SELECT u.stunde FROM UniversalCategoriesDLC u ORDER BY u.id DESC LIMIT 1")
     int getLastStunde();

     @Query("SELECT u FROM UniversalCategoriesDLC u ORDER BY u.id DESC LIMIT 1")
     UniversalCategoriesDLC getLast();

     @Query("SELECT u FROM UniversalCategoriesDLC  u WHERE u.uniStatId=:uniID AND u.stunde=:hour")
     UniversalCategoriesDLC getByUniStatIdAndStunde(int uniID, int hour);

     @Query("SELECT SUM(u.viewsGlobal) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsGlobalByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsArticle) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsArticleByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsNews) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsNewsByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsBlog) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsBlogByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsPodcast) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsPodcastByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsVideos) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsVideosByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsWhitepaper) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsWhitepaperByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsEvents) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsEventsByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsRatgeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberPost) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsRatgeberPostByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberGlossar) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsRatgeberGlossarByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberBuch) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsRatgeberBuchByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsRatgeberSelf) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsRatgeberSelfByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsMain) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsMainByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsAnbieter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsAnbieterByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsUeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsUeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsImpressum) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsImpressumByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsPreisliste) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsPreislisteByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsPartner) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsPartnerByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsDatenschutz) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsDatenschutzByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsNewsletter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsNewsletterByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsImage) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsImageByUniStatId(int uniID);

     @Query("SELECT SUM(u.viewsAGBS) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumViewsAGBSByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherGlobal) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserGlobalByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherArticle) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserArticleByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherNews) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserNewsByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherBlog) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserBlogByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherPodcast) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserPodcastByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherVideos) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserVideosByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherWhitepaper) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserWhitepaperByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherEvents) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserEventsByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserRatgeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberPost) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserRatgeberPostByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberGlossar) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserRatgeberGlossarByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberBuch) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserRatgeberBuchByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherRatgeberSelf) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserRatgeberSelfByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherMain) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserMainByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherAnbieter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserAnbieterByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherUeber) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserUeberByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherImpressum) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserImpressumByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherPreisliste) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserPreislisteByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherPartner) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserPartnerByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherDatenschutz) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserDatenschutzByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherNewsletter) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserNewsletterByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherImage) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserImageByUniStatId(int uniID);

     @Query("SELECT SUM(u.besucherAGBS) FROM UniversalCategoriesDLC u WHERE u.uniStatId = :uniID")
     int getSumUserAGBSByUniStatId(int uniID);





     @Query("SELECT SUM(u.viewsGlobal) FROM UniversalCategoriesDLC u")
     int getSumViewsGlobalAllTime();

     @Query("SELECT SUM(u.viewsArticle) FROM UniversalCategoriesDLC u")
     int getSumViewsArticleAllTime();

     @Query("SELECT SUM(u.viewsNews) FROM UniversalCategoriesDLC u")
     int getSumViewsNewsAllTime();

     @Query("SELECT SUM(u.viewsBlog) FROM UniversalCategoriesDLC u")
     int getSumViewsBlogAllTime();

     @Query("SELECT SUM(u.viewsPodcast) FROM UniversalCategoriesDLC u")
     int getSumViewsPodcastAllTime();

     @Query("SELECT SUM(u.viewsVideos) FROM UniversalCategoriesDLC u")
     int getSumViewsVideosAllTime();

     @Query("SELECT SUM(u.viewsWhitepaper) FROM UniversalCategoriesDLC u")
     int getSumViewsWhitepaperAllTime();

     @Query("SELECT SUM(u.viewsEvents) FROM UniversalCategoriesDLC u")
     int getSumViewsEventsAllTime();

     @Query("SELECT SUM(u.viewsRatgeber) FROM UniversalCategoriesDLC u")
     int getSumViewsRatgeberAllTime();

     @Query("SELECT SUM(u.viewsRatgeberPost) FROM UniversalCategoriesDLC u")
     int getSumViewsRatgeberPostAllTime();

     @Query("SELECT SUM(u.viewsRatgeberGlossar) FROM UniversalCategoriesDLC u")
     int getSumViewsRatgeberGlossarAllTime();

     @Query("SELECT SUM(u.viewsRatgeberBuch) FROM UniversalCategoriesDLC u")
     int getSumViewsRatgeberBuchAllTime();

     @Query("SELECT SUM(u.viewsRatgeberSelf) FROM UniversalCategoriesDLC u")
     int getSumViewsRatgeberSelfAllTime();

     @Query("SELECT SUM(u.viewsMain) FROM UniversalCategoriesDLC u")
     int getSumViewsMainAllTime();

     @Query("SELECT SUM(u.viewsAnbieter) FROM UniversalCategoriesDLC u")
     int getSumViewsAnbieterAllTime();

     @Query("SELECT SUM(u.viewsUeber) FROM UniversalCategoriesDLC u")
     int getSumViewsUeberAllTime();

     @Query("SELECT SUM(u.viewsImpressum) FROM UniversalCategoriesDLC u")
     int getSumViewsImpressumAllTime();

     @Query("SELECT SUM(u.viewsPreisliste) FROM UniversalCategoriesDLC u")
     int getSumViewsPreislisteAllTime();

     @Query("SELECT SUM(u.viewsPartner) FROM UniversalCategoriesDLC u")
     int getSumViewsPartnerAllTime();

     @Query("SELECT SUM(u.viewsDatenschutz) FROM UniversalCategoriesDLC u")
     int getSumViewsDatenschutzAllTime();

     @Query("SELECT SUM(u.viewsNewsletter) FROM UniversalCategoriesDLC u")
     int getSumViewsNewsletterAllTime();

     @Query("SELECT SUM(u.viewsImage) FROM UniversalCategoriesDLC u")
     int getSumViewsImageAllTime();

     @Query("SELECT SUM(u.viewsAGBS) FROM UniversalCategoriesDLC u")
     int getSumViewsAGBSAllTime();



     @Query("SELECT SUM(u.besucherGlobal) FROM UniversalCategoriesDLC u")
     int getSumUserGlobalAllTime();

     @Query("SELECT SUM(u.besucherArticle) FROM UniversalCategoriesDLC u")
     int getSumUserArticleAllTime();

     @Query("SELECT SUM(u.besucherNews) FROM UniversalCategoriesDLC u")
     int getSumUserNewsAllTime();

     @Query("SELECT SUM(u.besucherBlog) FROM UniversalCategoriesDLC u")
     int getSumUserBlogAllTime();

     @Query("SELECT SUM(u.besucherPodcast) FROM UniversalCategoriesDLC u")
     int getSumUserPodcastAllTime();

     @Query("SELECT SUM(u.besucherVideos) FROM UniversalCategoriesDLC u")
     int getSumUserVideosAllTime();

     @Query("SELECT SUM(u.besucherWhitepaper) FROM UniversalCategoriesDLC u")
     int getSumUserWhitepaperAllTime();

     @Query("SELECT SUM(u.besucherEvents) FROM UniversalCategoriesDLC u")
     int getSumUserEventsAllTime();

     @Query("SELECT SUM(u.besucherRatgeber) FROM UniversalCategoriesDLC u ")
     int getSumUserRatgeberAllTime();

     @Query("SELECT SUM(u.besucherRatgeberPost) FROM UniversalCategoriesDLC u")
     int getSumUserRatgeberPostAllTime();

     @Query("SELECT SUM(u.besucherRatgeberGlossar) FROM UniversalCategoriesDLC u")
     int getSumUserRatgeberGlossarAllTime();

     @Query("SELECT SUM(u.besucherRatgeberBuch) FROM UniversalCategoriesDLC u")
     int getSumUserRatgeberBuchAllTime();

     @Query("SELECT SUM(u.besucherRatgeberSelf) FROM UniversalCategoriesDLC u")
     int getSumUserRatgeberSelfAllTime();

     @Query("SELECT SUM(u.besucherMain) FROM UniversalCategoriesDLC u")
     int getSumUserMainAllTime();

     @Query("SELECT SUM(u.besucherAnbieter) FROM UniversalCategoriesDLC u")
     int getSumUserAnbieterAllTime();

     @Query("SELECT SUM(u.besucherUeber) FROM UniversalCategoriesDLC u")
     int getSumUserUeberAllTime();

     @Query("SELECT SUM(u.besucherImpressum) FROM UniversalCategoriesDLC u")
     int getSumUserImpressumAllTime();

     @Query("SELECT SUM(u.besucherPreisliste) FROM UniversalCategoriesDLC u")
     int getSumUserPreislisteAllTime();

     @Query("SELECT SUM(u.besucherPartner) FROM UniversalCategoriesDLC u")
     int getSumUserPartnerAllTime();

     @Query("SELECT SUM(u.besucherDatenschutz) FROM UniversalCategoriesDLC u")
     int getSumUserDatenschutzAllTime();

     @Query("SELECT SUM(u.besucherNewsletter) FROM UniversalCategoriesDLC u")
     int getSumUserNewsletterAllTime();

     @Query("SELECT SUM(u.besucherImage) FROM UniversalCategoriesDLC u")
     int getSumUserImageAllTime();

     @Query("SELECT SUM(u.besucherAGBS) FROM UniversalCategoriesDLC u")
     int getSumUserAGBSAllTime();

}
