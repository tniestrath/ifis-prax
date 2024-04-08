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

@Service
public class SocialsImpressionsService {
    @Autowired
    private SocialsImpressionsRepository socialsImpressionsRepo;
    @Autowired
    private universalStatsRepository uniRepo;


    /**
     * Updates social media impression table for a Social Impression of a post.
     * @param whatMatched a simple String representation of what kind of impression happened.
     * @param dateLog the date of impression, according to the log.
     * @param postId the postId to update for.
     */
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
        incrementImpression(whatMatched, impression);
    }

    /**
     * Updates social media impression table for a Social Impression of a user.
     * @param whatMatched a simple String representation of what kind of impression happened.
     * @param dateLog the date of impression, according to the log.
     * @param userId the userId to update for.
     */
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
        incrementImpression(whatMatched, impression);
    }

    /**
     * Increments the matched category of a given SocialsImpressions row.
     * @param whatMatched a simple String representation of what kind of impression happened.
     * @param impression the SocialsImpressions object to update for.
     */
    private void incrementImpression(String whatMatched, SocialsImpressions impression) {
        switch (whatMatched) {
            case "postImpressionFacebook" -> {
                impression.setFacebook(impression.getFacebook() + 1);
            }
            case "postImpressionTwitter" -> {
                impression.setTwitter(impression.getTwitter() + 1);
            }
            case "postImpressionLinkedIn" -> {
                impression.setLinkedIn(impression.getLinkedIn() + 1);
            }
            case "postImpressionFacebookTwitterCombo" -> {
                impression.setFacebook(impression.getFacebook() + 1);
                impression.setTwitter(impression.getTwitter() + 1);
            }
        }
        socialsImpressionsRepo.save(impression);
    }

    /**
     * Fetches a JSON representation for the all-time Impressions of a post.
     * @param postId the postId to fetch for.
     * @return a JSON-String containing the total number of impressions per Medium.
     */
    public String getImpressionsAccumulatedAllTimeByPostId(Long postId)  {
        try{
        List<SocialsImpressions> imp = getSocialsImpressionsByPostId(postId);
            return accumulateImpressionsByListIntoJSON(imp);
        }
        catch (Exception e){e.printStackTrace();
            return "";
        }

    }

    /**
     * Fetches a JSON representation for the all-time Impressions of a user.
     * @param userId the postId to fetch for.
     * @return a JSON-String containing the total number of impressions per Medium.
     */
    public String getImpressionsAccumulatedAllTimeByUserId(Long userId) {
        try{
        List<SocialsImpressions> imp = getSocialsImpressionsByUserId(userId);
        return accumulateImpressionsByListIntoJSON(imp);}
        catch (Exception e){e.printStackTrace();
        return "";
        }
    }

    /**
     * Calculates totals for each Medium from a given SocialsImpressions-List.
     * @param imp the list to extract data from.
     * @return a JSON-String containing the total number of impressions per Medium.
     * @throws JSONException .
     */
    public String accumulateImpressionsByListIntoJSON(List<SocialsImpressions> imp) throws JSONException {
        Long twitter = 0L;
        Long facebook = 0L;
        Long linkedIn=0L;

        for(SocialsImpressions soc:imp){
            twitter += soc.getTwitter();
            facebook += soc.getFacebook();
            linkedIn += soc.getLinkedIn();
        }
        JSONObject obj = new JSONObject();
        obj.put("Twitter",twitter);
        obj.put("facebook",facebook);
        obj.put("LinkedIn",linkedIn);

        return obj.toString();
    }

    /**
     * Builds a detailed representation of an Impressions object.
     * @param imp the SocialsImpressions to fetch and build data for.
     * @return a JSON-String containing all data from the table row.
     */
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

    /**
     * Filters all User Impressions from a list.
     * @param unfilteredImps the list to filter from.
     * @return a list containing all Impressions from unfilteredImps but without userImpressions.
     */
    public List<SocialsImpressions> filterOutUserImpressions(List<SocialsImpressions> unfilteredImps){
        List<SocialsImpressions> filteredImps=new ArrayList<>();

        for(SocialsImpressions imp: unfilteredImps){
            if(imp.getPostId()!=null){
                filteredImps.add(imp);
            }
        }

        return filteredImps;
    }

    /**
     * Filters all Post Impressions from a list.
     * @param unfilteredImps the list to filter from.
     * @return a list containing all Impressions from unfilteredImps but without postImpressions.
     */
    public List<SocialsImpressions> filterOutPostImpressions(List<SocialsImpressions> unfilteredImps){
        List<SocialsImpressions> filteredImps=new ArrayList<>();

        for(SocialsImpressions imp: unfilteredImps){
            if(imp.getUserId()!=null){
                filteredImps.add(imp);
            }
        }

        return filteredImps;
    }

    /**
     * Passes a query along to SocialsImpressionsRepository, fetching all SocialsImpressions for a given user.
     * @param userId the userId to fetch for.
     * @return a list of SocialsImpressions.
     */
    public List<SocialsImpressions> getSocialsImpressionsByUserId(Long userId){
        List<SocialsImpressions> imp = socialsImpressionsRepo.findByUserId(userId);
        return imp;
    }

    /**
     * Passes a query along to SocialsImpressionsRepository, fetching all SocialsImpressions for a given post.
     * @param postId the postId to fetch for.
     * @return a list of SocialsImpressions.
     */
    public List<SocialsImpressions> getSocialsImpressionsByPostId(Long postId){
        List<SocialsImpressions> imp = socialsImpressionsRepo.findByPostId(postId);
        return imp;
    }

    /**
     * Gets the greatest SocialImpressionsRow by total Impressions from a list.
     * @param allImps the list to fetch from.
     * @return a SocialsImpressions-Object representing the table-row.
     */
    public SocialsImpressions getMostImpressionsFromList(List<SocialsImpressions> allImps){
        SocialsImpressions topImp=new SocialsImpressions();
        Long bestImpCount=0L;
        for(SocialsImpressions imp:allImps){
            Long impCount= imp.getFacebook()+imp.getLinkedIn()+imp.getTwitter();
            if(impCount>bestImpCount){
                topImp = imp;
                bestImpCount = impCount;
            }
        }
        return topImp;
    }

    /**
     * Fetches all Socials Impressions.
     * @return a list of all SocialsImpressions.
     */
    public List<SocialsImpressions> findAll(){
        return socialsImpressionsRepo.findAll();
    }

    public SocialsImpressions getMostTwitterImpressionsFromList(List<SocialsImpressions> allImps){

        SocialsImpressions topImp=new SocialsImpressions();
        Long bestImpCount=0L;
        for(SocialsImpressions imp:allImps){
            if(imp.getTwitter()>bestImpCount){
                topImp = imp;
                bestImpCount = imp.getTwitter();
            }
        }
        return topImp;

    }

    public SocialsImpressions getMostLinkedInImpressionsFromList(List<SocialsImpressions> allImps){

        SocialsImpressions topImp=new SocialsImpressions();
        Long bestImpCount=0L;
        for(SocialsImpressions imp:allImps){
            if(imp.getLinkedIn()>bestImpCount){
                topImp = imp;
                bestImpCount = imp.getLinkedIn();
            }
        }
        return topImp;

    }

    public SocialsImpressions getMostFacebookImpressionsFromList(List<SocialsImpressions> allImps){

        SocialsImpressions topImp=new SocialsImpressions();
        Long bestImpCount=0L;
        for(SocialsImpressions imp:allImps){
            if(imp.getFacebook()>bestImpCount){
                topImp = imp;
                bestImpCount =imp.getFacebook();
            }
        }
        return topImp;

    }

}
