package com.analysetool.services;

import com.analysetool.modells.SocialsImpressions;
import com.analysetool.repositories.SocialsImpressionsRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        System.out.println(whatMatched);
        Long counter;
        switch (whatMatched) {
            case "postImpressionFacebook" -> {
                counter = impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
            }
            case "postImpressionTwitter" -> {
                counter = impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
            }
            case "postImpressionLinkedIn" -> {
                counter = impression.getLinkedIn();
                counter++;
                impression.setLinkedIn(counter);
            }
            case "postImpressionFacebookTwitterCombo" -> {
                counter = impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
                counter = impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
            }
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
        switch (whatMatched) {
            case "postImpressionFacebook" -> {
                counter = impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
            }
            case "postImpressionTwitter" -> {
                counter = impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
            }
            case "postImpressionLinkedIn" -> {
                counter = impression.getLinkedIn();
                counter++;
                impression.setLinkedIn(counter);
            }
            case "postImpressionFacebookTwitterCombo" -> {
                counter = impression.getFacebook();
                counter++;
                impression.setFacebook(counter);
                counter = impression.getTwitter();
                counter++;
                impression.setTwitter(counter);
            }
        }
        socialsImpressionsRepo.save(impression);
    }

    public List<SocialsImpressions> getSocialsImpressionsByPostId(Long postId){
        List<SocialsImpressions> imp = socialsImpressionsRepo.findByPostId(postId);
        return imp;
    }

    public String getImpressionsAccumulatedAllTimeByPostId(Long postId) throws JSONException {
        List<SocialsImpressions> imp = getSocialsImpressionsByPostId(postId);
        return accumulateImpressionsByListIntoJSON(imp);
    }

    public String getImpressionsAccumulatedAllTimeByUserId(Long userId) throws JSONException {
        List<SocialsImpressions> imp = getSocialsImpressionsByUserId(userId);
        return accumulateImpressionsByListIntoJSON(imp);
    }


    public String accumulateImpressionsByListIntoJSON(List<SocialsImpressions> imp) throws JSONException {
        Long twitter = 0L;
        Long facebook = 0L;
        Long linkedIn=0L;

        for(SocialsImpressions soc:imp){
            twitter = twitter+ soc.getTwitter();
            facebook = facebook+ soc.getFacebook();
            linkedIn = linkedIn+ soc.getLinkedIn();
        }
        JSONObject obj = new JSONObject();
        obj.put("Twitter",twitter);
        obj.put("facebook",facebook);
        obj.put("LinkedIn",linkedIn);

        return obj.toString();
    }

    public List<SocialsImpressions> getSocialsImpressionsByUserId(Long userId){
        List<SocialsImpressions> imp = socialsImpressionsRepo.findByUserId(userId);
        return imp;
    }
}
