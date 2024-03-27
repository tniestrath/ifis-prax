package com.analysetool.util;

public class Constants {

    // Define your constants here
    private final String blogSlug = "blogeintrag";
    private final String artikelSlug = "artikel";
    private final String whitepaperSlug = "whitepaper";
    private final String podastSlug = "podcast";
    private final String newsSlug = "news";


    private final String basisAnbieter = "um_basis";
    private final String basisPlusAnbieter = "um_basis-plus";
    private final String plusAnbieter = "um_plus";
    private final String premiumAnbieter = "um_premium";

    private final String thumbnailLocationStart = "https://it-sicherheit.de/wp-content/uploads/";
    private final String profilePhotoStart = "https://it-sicherheit.de/wp-content/uploads/ultimatemember/";

    // Private constructor to prevent external instantiation
    private Constants() {
    }

    private static final class InstanceHolder {
        private static final Constants instance = new Constants();
    }

    // Lazy initialization of the singleton instance
    public static Constants getInstance() {
        return InstanceHolder.instance;
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
}
