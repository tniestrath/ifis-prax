package com.analysetool.util;

import com.analysetool.repositories.PostTypeRepository;
import com.analysetool.repositories.WPTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class Constants {

    @Autowired
    private PostTypeRepository ptRepo;

    @Autowired
    private WPTermRepository termRepo;

    // Define your constants here
    private final String blogSlug = "blogeintrag";
    private final String artikelSlug = "artikel";
    private final String whitepaperSlug = "whitepaper";
    private final String podastSlug = "podcast";
    private final String newsSlug = "news";

    private final String videoSlug = "videos";


    private final String basisAnbieter = "um_basis";
    private final String basisPlusAnbieter = "um_basis-plus";
    private final String plusAnbieter = "um_plus";
    private final String premiumAnbieter = "um_premium";

    private final String thumbnailLocationStart = "https://it-sicherheit.de/wp-content/uploads/";
    private final String profilePhotoStart = "https://it-sicherheit.de/wp-content/uploads/ultimatemember/";


    private final long sonstigeEventsTermId = 312L;
    private final long messenTermId = 313L;
    private final long kongressTermId = 314L;
    private final long schulungTermId = 315L;
    private final long workshopTermId = 316L;

    private final long podcastTermId = 386L;

    private final String ratgeberSlug = "ratgeber";

    private final String cyberRiskSlug = "cyber-risk-check";

    private static Constants instance;

    @Autowired
    public Constants(PostTypeRepository ptRepo, WPTermRepository termRepo) {
        this.ptRepo = ptRepo;
        this.termRepo = termRepo;
        instance = this;
    }


    // Lazy initialization of the singleton instance
    public static Constants getInstance() {
        return instance;
    }

    public String getBlogSlug() {
        return blogSlug;
    }

    public String getArtikelSlug() {
        return artikelSlug;
    }

    public String getWhitepaperSlug() {
        return whitepaperSlug;
    }

    public String getPodastSlug() {
        return podastSlug;
    }

    public String getNewsSlug() {
        return newsSlug;
    }

    public String getBasisAnbieter() {
        return basisAnbieter;
    }

    public String getBasisPlusAnbieter() {
        return basisPlusAnbieter;
    }

    public String getPlusAnbieter() {
        return plusAnbieter;
    }

    public String getPremiumAnbieter() {
        return premiumAnbieter;
    }

    public String getThumbnailLocationStart() {
        return thumbnailLocationStart;
    }

    public String getProfilePhotoStart() {
        return profilePhotoStart;
    }

    public long getSonstigeEventsTermId() {
        return sonstigeEventsTermId;
    }

    public long getMessenTermId() {
        return messenTermId;
    }

    public long getKongressTermId() {
        return kongressTermId;
    }

    public long getSchulungTermId() {
        return schulungTermId;
    }

    public long getWorkshopTermId() {
        return workshopTermId;
    }

    public long getPodcastTermId() {
        return podcastTermId;
    }

    public String getVideoSlug() {
        return videoSlug;
    }

    public List<String> getListOfPostTypesSlug() {
        return ptRepo.getDistinctTypes();
    }

    public PostTypeRepository getPtRepo() {
        return ptRepo;
    }

    public WPTermRepository getTermRepo() {
        return termRepo;
    }

    public String getRatgeberSlug() {
        return ratgeberSlug;
    }

    public String getCyberRiskSlug() {
        return cyberRiskSlug;
    }

    public List<Integer> getListOfPostTypesInteger() {
        List<Integer> ids = new ArrayList<>();
        for(String type : getListOfPostTypesSlug()) {
            if(!type.equals("podcast") && !type.startsWith("Event") && !type.equals("blog") && !type.equals("ratgeber")) {
                if(termRepo.findBySlug(type) == null) continue;
                ids.add(termRepo.findBySlug(type).getId().intValue());
            } else if (type.equals("podcast")){
                ids.add((int) podcastTermId);
            } else if(type.startsWith("Event")){
                if(type.contains("Messe")) ids.add((int) messenTermId);
                if(type.contains("Kongress")) ids.add((int) kongressTermId);
                if(type.contains("Schulung")) ids.add((int) schulungTermId);
                if(type.contains("Workshop")) ids.add((int) workshopTermId);
                if(type.contains("Sonstige")) ids.add((int) sonstigeEventsTermId);
            } else if(type.equals("blog")){
                ids.add(termRepo.findBySlug(blogSlug).getId().intValue());
            } else {
                ids.add(termRepo.findBySlug(cyberRiskSlug).getId().intValue());
            }
        }
        return ids;
    }
}
