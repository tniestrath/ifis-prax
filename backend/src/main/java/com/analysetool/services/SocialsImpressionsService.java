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
        if(socialsImpressionsRepo.findByUniIdAndAndHourAndAndPostId(uniId,hour,postId).isPresent()){
            impression=socialsImpressionsRepo.findByUniIdAndAndHourAndAndPostId(uniId,hour,postId).get();
        }else{
            impression=new SocialsImpressions();
            impression.setUniId(uniId);
            impression.setHour(hour);
            impression.setPostId(postId);
            impression.setFacebook(0L);
            impression.setTwitter(0L);
            impression.setLinkedIn(0L);
        }
        Long counter;
        switch (whatMatched){
            case "postImpressionFacebook":
                counter= impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
                break;
            case "postImpressionTwitter":
                counter= impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
                break;
            case "postImpressionLinkedIn":
                counter= impression.getLinkedIn();
                counter++;
                impression.setLinkedIn(counter);
                break;
            case "postImpressionFacebookTwitterCombo":
                counter= impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
                counter= impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
                break;
        }
        socialsImpressionsRepo.save(impression);
    }
    public void updateSocialsImpressionsUser(String whatMatched, LocalDateTime dateLog, long userId){
        Integer uniId = uniRepo.getLatestUniStat().getId();
        Integer hour = dateLog.getHour();
        SocialsImpressions impression;
        if(socialsImpressionsRepo.findByUniIdAndAndHourAndAndUserId(uniId,hour,userId).isPresent()){
            impression=socialsImpressionsRepo.findByUniIdAndAndHourAndAndUserId(uniId,hour,userId).get();
        }else{
            impression=new SocialsImpressions();
            impression.setUniId(uniId);
            impression.setHour(hour);
            impression.setPostId(userId);
            impression.setFacebook(0L);
            impression.setTwitter(0L);
            impression.setLinkedIn(0L);
        }
        Long counter;
        switch (whatMatched){
            case "postImpressionFacebook":
                counter= impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
                break;
            case "postImpressionTwitter":
                counter= impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
                break;
            case "postImpressionLinkedIn":
                counter= impression.getLinkedIn();
                counter++;
                impression.setLinkedIn(counter);
                break;
            case "postImpressionFacebookTwitterCombo":
                counter= impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
                counter= impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
                break;
        }        socialsImpressionsRepo.save(impression);
    }
}
