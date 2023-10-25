package com.analysetool.repositories;

import com.analysetool.modells.UniqueUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UniqueUserRepository extends JpaRepository<UniqueUser, Long> {

    @Query("SELECT u.category FROM UniqueUser u WHERE u.id = :id")
    public String getCategoryByID(int id);

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.category =:category")
    public int getUserCountByFirstCategory(String category);

    @Query("SELECT count(u.ip) FROM UniqueUser u")
    public int getUserCountGlobal();

    @Query("SELECT u FROM UniqueUser u WHERE u.ip = :ip")
    public UniqueUser findByIP(String ip);

    //Valid values for category are ("article" | "blog" | "news" | "whitepaper" | "podcast" | "ratgeber" | "global" | "main" | "ueber" | "impressum" | "preisliste" | "partner" | "datenschutz" | "newsletter" | "image" | "agb")
    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.article = 1")
    public int getUserCountArticle();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.blog = 1")
    public int getUserCountBlog();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.news = 1")
    public int getUserCountNews();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.whitepaper = 1")
    public int getUserCountWhitepaper();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.podcast = 1")
    public int getUserCountPodcast();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.ratgeber = 1")
    public int getUserCountRatgeber();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.global = 1")
    public int getUserCountGlobal();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.main = 1")
    public int getUserCountMain();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.ueber = 1")
    public int getUserCountUeber();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.impressum = 1")
    public int getUserCountImpressum();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.preisliste = 1")
    public int getUserCountPreisliste();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.partner = 1")
    public int getUserCountPartner();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.datenschutz = 1")
    public int getUserCountDatenschutz();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.newsletter = 1")
    public int getUserCountNewsletter();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.image = 1")
    public int getUserCountImage();

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.agb = 1")
    public int getUserCountAGB();
}
