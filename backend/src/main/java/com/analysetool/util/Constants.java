package com.analysetool.util;

public class Constants {
    private static Constants instance;

    // Define your constants here
    private final String blogSlug = "blogeintrag";
    private final String artikelSlug = "artikel";
    private final String whitepaperSlug = "whitepaper";
    private final String podastSlug = "podcast";
    private final String newsSlug = "news";

    // Private constructor to prevent external instantiation
    private Constants() {
    }

    // Lazy initialization of the singleton instance
    public static Constants getInstance() {
        if (instance == null) {
            synchronized (Constants.class) {
                if (instance == null) {
                    instance = new Constants();
                }
            }
        }
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
}
