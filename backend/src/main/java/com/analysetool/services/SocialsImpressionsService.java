package com.analysetool.services;

import com.analysetool.modells.SocialsImpressions;
import com.analysetool.repositories.SocialsImpressionsRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SocialsImpressionsService {
    @Autowired
    private SocialsImpressionsRepository socialsImpressionsRepo;
    @Autowired
    private universalStatsRepository uniRepo;

    public void updateSocialsImpressionsPost(String whatMatched, LocalDateTime dateLog, long postId){
        Integer uniId = uniRepo.getLatestUniStat().getId();
        Integer hour = dateLog.getHour();
        SocialsImpressions impression;

        switch (whatMatched){
            case "postImpressionFacebook":
                break;
            case "postImpressionTwitter":
                break;
            case "postImpressionLinkedIn":
                break;
            case "postImpressionFacebookTwitterCombo":
                break;
        }
       // socialsImpressionsRepo.save(impression);
    }
    public void updateSocialsImpressionsUser(String whatMatched, LocalDateTime dateLog, long userId){
        Integer uniId = uniRepo.getLatestUniStat().getId();
        Integer hour = dateLog.getHour();
        SocialsImpressions impression;

        switch (whatMatched){
            case "userImpressionFacebook":
                break;
            case "userImpressionTwitter":
                break;
            case "userImpressionLinkedIn":
                break;
            case "userImpressionFacebookTwitterCombo":
                break;
        }
        // socialsImpressionsRepo.save(impression);
    }
}
