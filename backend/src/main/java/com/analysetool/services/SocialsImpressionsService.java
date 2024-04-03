package com.analysetool.services;

import com.analysetool.modells.SocialsImpressions;
import com.analysetool.repositories.SocialsImpressionsRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public String getImpressionsAccumulatedAllTimeByPostId(Long postId)  {
        try{
        List<SocialsImpressions> imp = getSocialsImpressionsByPostId(postId);
            return accumulateImpressionsByListIntoJSON(imp);
        }
        catch (Exception e){e.printStackTrace();
            return "";
        }

    }

    public String getImpressionsAccumulatedAllTimeByUserId(Long userId) {
        try{
        List<SocialsImpressions> imp = getSocialsImpressionsByUserId(userId);
        return accumulateImpressionsByListIntoJSON(imp);}
        catch (Exception e){e.printStackTrace();
        return "";
        }
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

    public String impToJSON(SocialsImpressions imp){

        JSONObject obj = new JSONObject();
        try{
        obj.put("Twitter",imp.getTwitter());
        obj.put("facebook",imp.getFacebook());
        obj.put("LinkedIn",imp.getLinkedIn());
        obj.put("Datum",uniRepo.getDateByUniId(imp.getUniId()));
        obj.put("Hour",imp.getHour());
        if(imp.getPostId()!=null){
            obj.put("postId",imp.getPostId());
        } else if (imp.getUserId()!=null) {
            obj.put("userId",imp.getUserId());
        }

            return obj.toString();
        }catch(Exception e){return "Weird JSON Error";}

    }
    public List<SocialsImpressions> filterOutUserImpressions(List<SocialsImpressions> unfilteredImps){
        List<SocialsImpressions> filteredImps=new ArrayList<>();

        for(SocialsImpressions imp: unfilteredImps){
            if(imp.getPostId()!=null){
                filteredImps.add(imp);
            }
        }

        return filteredImps;
    }

    public List<SocialsImpressions> filterOutPostImpressions(List<SocialsImpressions> unfilteredImps){
        List<SocialsImpressions> filteredImps=new ArrayList<>();

        for(SocialsImpressions imp: unfilteredImps){
            if(imp.getUserId()!=null){
                filteredImps.add(imp);
            }
        }

        return filteredImps;
    }

    public List<SocialsImpressions> getSocialsImpressionsByUserId(Long userId){
        List<SocialsImpressions> imp = socialsImpressionsRepo.findByUserId(userId);
        return imp;
    }

    public SocialsImpressions getMostImpressionsFromList(List<SocialsImpressions> allImps){
        SocialsImpressions topImp=new SocialsImpressions();
        Long bestImpCount=0L;
        for(SocialsImpressions imp:allImps){
            Long impCount= imp.getFacebook()+imp.getLinkedIn()+imp.getTwitter();
            if(impCount>bestImpCount){
                topImp = imp;
            }
        }
        return topImp;
    }

    public List<SocialsImpressions> findAll(){
        return socialsImpressionsRepo.findAll();
    }

}
