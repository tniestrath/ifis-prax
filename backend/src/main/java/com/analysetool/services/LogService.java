package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Duration;
@Service
public class LogService {

    private PostRepository postRepository;
    private statsRepository statsRepo;
    private TagStatRepository tagStatRepo;
    private WpTermRelationshipsRepository termRelRepo;
    private WPTermRepository termRepo;
    private WpTermTaxonomyRepository termTaxRepo;
    private WPUserRepository wpUserRepo;
    private UserStatsRepository userStatsRepo;

    private BufferedReader br;
    private String path = "";
    private String BlogSSPattern = ".*GET /blog/(\\S+).*s="; //search +1, view +1,(bei match) vor blog view pattern
    private String ArtikelSSPattern = ".*GET /artikel/(\\S+).*s=";//search +1, view +1,(bei match) vor artikel view pattern
    //private String BlogViewPattern = "^.*GET \/blog\/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
    private String BlogViewPattern = ".*GET /blog/(\\S+)";
    private String RedirectPattern = "/.*GET .*goto=.*\"(https?:/.*/(artikel|blog)/(\\S*)/)";
    private String UserViewPattern=".*GET /user/(\\S+)/";

    //Blog view +1 bei match
    private String ArtikelViewPattern = ".*GET /artikel/(\\S+)";//Artikel view +1 bei match
    Pattern pattern1_1 = Pattern.compile(ArtikelViewPattern);
    Pattern pattern1_2 = Pattern.compile(ArtikelSSPattern);
    Pattern pattern2_1 = Pattern.compile(BlogViewPattern);
    Pattern pattern2_2 = Pattern.compile(BlogSSPattern);
    Pattern pattern3=Pattern.compile(RedirectPattern);
    Pattern pattern4=Pattern.compile(UserViewPattern);
    private String lastLine = "";
    private int lineCounter = 0;
    private int lastLineCounter = 0;
    private boolean liveScanning = false;

    @Autowired
    public LogService(PostRepository postRepository, statsRepository StatsRepository,TagStatRepository tagStatRepo,WpTermRelationshipsRepository termRelRepo,WPTermRepository termRepo,WpTermTaxonomyRepository termTaxRepo,WPUserRepository wpUserRepo,UserStatsRepository userStatsRepo) {
        this.postRepository = postRepository;
        this.statsRepo = StatsRepository;
        this.tagStatRepo=tagStatRepo;
        this.termRelRepo=termRelRepo;
        this.termRepo=termRepo;
        this.termTaxRepo=termTaxRepo;
        this.wpUserRepo=wpUserRepo;
        this.userStatsRepo=userStatsRepo;
    }

    public void run(boolean liveScanning, String path)  {
        this.liveScanning = liveScanning;
        this.path = path;
        try  {
            br = new BufferedReader(new FileReader(path));
            findAMatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findAMatch() throws IOException {
        String line;

        boolean foundPattern = false;

        while ((line = br.readLine()) != null) {

            while(lineCounter!=lastLineCounter){
                br.readLine();
                lineCounter++;
                System.out.println("Counting up");
            }

           // if (foundPattern) {
                Matcher matcher1_1 = pattern1_1.matcher(line);

                if (matcher1_1.find()) {
                    Matcher matcher1_2 = pattern1_2.matcher(line);

                    foundPattern = true;
                    if (matcher1_2.find()) {
                        // Do something with the matched 1.2 patterns
                        //System.out.println(line+"SEARCH FOUND");
                        processLine(line,2,matcher1_2);
                        foundPattern = true;
                    } else {//1.1 matched
                        //System.out.println(line+"NO SEARCH");
                        processLine(line,1,matcher1_1);
                        foundPattern = true;
                    }
                }
           // }
            else {
                    Matcher matcher2_1 = pattern2_1.matcher(line);

                    if (matcher2_1.find()) {
                        Matcher matcher2_2 = pattern2_2.matcher(line);

                        if (matcher2_2.find()) {
                            // Do something with the matched 2.2 patterns
                            processLine(line, 4, matcher2_2);
                            foundPattern = false;
                           // System.out.println(line+" SEARCH FOUND");
                        } else {
                            //2.1 match
                            processLine(line, 3, matcher2_1);
                            foundPattern = false;
                           // System.out.println(line+" NO SEARCH");
                        }
                    }
                    //}
                }

            Matcher matcher3=pattern3.matcher(line);
            if(matcher3.find()){
                processLine(line,5,matcher3);
                }
            Matcher matcher4=pattern4.matcher(line);
            if(matcher4.find()){
                processLine(line,6,matcher4);
            }

            lineCounter++;
            lastLineCounter++;
            //System.out.println(lineCounter+" "+lastLine);
            //br.readLine();

        }
        //updateuseraktivität
        System.out.println("UPDATING USER ACTIVITY");
        updateUserActivity();
        System.out.println("END OF LOG");
    }


    public void processLine(String line,int patternNumber,Matcher matcher){
        lastLine=line;
        if (patternNumber==1){

            System.out.println(postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1))+matcher.group(1).substring(0,matcher.group(1).length()-1)+" PROCESSING 1.1");
            try{

                long id =postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1));
                //hier nach TagSuchen WIP

                checkTheTag(id,false);
                if (statsRepo.existsByArtId(id)){
                long views = statsRepo.getClicksByArtId(id);
                views ++;

                LocalDateTime PostTimestamp = postRepository.getPostDateById(id);
                LocalDateTime Now =  LocalDateTime.now();
                Duration duration = Duration.between(PostTimestamp, Now);
                long diffInDays = duration.toDays();
                float Performance = views;
                if (diffInDays>0&&views > 0){
                    Performance = (float)views/diffInDays;
                }

                statsRepo.updateClicksAndPerformanceByArtId(views,id,Performance);
                    updateDailyClicks(id);
            }else{  statsRepo.save(new stats(id,(float) 0,(float) 0,1,0,0,(float) 1)); updateDailyClicks(id);}
               }
            catch(Exception e){
                System.out.println("IGNORE "+matcher.group(1).substring(0,matcher.group(1).length()-1)+" BECAUSE: "+e.getMessage());
            }
        }
        if (patternNumber==2){

            System.out.println(postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1))+matcher.group(1).substring(0,matcher.group(1).length()-1)+" PROCESSING 1.2");
            try{
                long id =postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1));
                checkTheTag(id,true);
            if (statsRepo.existsByArtId(id)){
                long views = statsRepo.getClicksByArtId(id);
                views ++;
                long searchSuccess= statsRepo.getSearchSuccesByArtId(id);
                searchSuccess ++;
                LocalDateTime PostTimestamp = postRepository.getPostDateById(id);
                LocalDateTime Now =  LocalDateTime.now();
                Duration duration = Duration.between(PostTimestamp, Now);
                long diffInDays = duration.toDays();
                float Performance = views;
                if (diffInDays>0&&views > 0){
                    Performance = (float)views/diffInDays;
                }
                statsRepo.updateClicksSearchSuccessRateAndPerformance(id,views,searchSuccess,Performance);
                updateDailyClicks(id);
            }else{  statsRepo.save(new stats(id,(float) 0,(float) 0,1,1,0,(float) 1)); updateDailyClicks(id);}
               }
            catch(Exception e){
                System.out.println("IGNORE "+matcher.group(1).substring(0,matcher.group(1).length()-1)+" BECAUSE: "+e.getMessage());

        }}
        if (patternNumber==3){

            System.out.println(postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1))+matcher.group(1).substring(0,matcher.group(1).length()-1)+" PROCESSING 2.1");
            try{
                long id =postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1));
                checkTheTag(id,false);
                if (statsRepo.existsByArtId(id)){
                long views = statsRepo.getClicksByArtId(id);
                views ++;
                    LocalDateTime PostTimestamp = postRepository.getPostDateById(id);
                    LocalDateTime Now =  LocalDateTime.now();
                    Duration duration = Duration.between(PostTimestamp, Now);
                    long diffInDays = duration.toDays();
                    float Performance = views;
                    if (diffInDays>0&&views > 0){
                        Performance = (float)views/diffInDays;
                    }

                    statsRepo.updateClicksAndPerformanceByArtId(views,id,Performance);
                    updateDailyClicks(id);
            }else{  statsRepo.save(new stats(id,(float) 0,(float) 0,1,0,0,(float) 1));updateDailyClicks(id); }
                }
        catch(Exception e){
                System.out.println("IGNORE "+matcher.group(1).substring(0,matcher.group(1).length()-1)+" BECAUSE: "+e.getMessage());
           // e.printStackTrace();
        }}
        if (patternNumber==4){

            System.out.println(postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1))+matcher.group(1).substring(0,matcher.group(1).length()-1)+" PROCESSING 2.2");
            try{
                long id =postRepository.getIdByName(matcher.group(1).substring(0,matcher.group(1).length()-1));
                checkTheTag(id,true);
            if (statsRepo.existsByArtId(id)){
                long views = statsRepo.getClicksByArtId(id);
                views ++;
                long searchSuccess= statsRepo.getSearchSuccesByArtId(id);
                searchSuccess ++;
                LocalDateTime PostTimestamp = postRepository.getPostDateById(id);
                LocalDateTime Now =  LocalDateTime.now();
                Duration duration = Duration.between(PostTimestamp, Now);
                long diffInDays = duration.toDays();
                float Performance = views;
                if (diffInDays>0&&views > 0){
                    Performance = (float)views/diffInDays;
                }
                statsRepo.updateClicksSearchSuccessRateAndPerformance(id,views,searchSuccess,Performance);
                updateDailyClicks(id);
            }else{  statsRepo.save(new stats(id,(float) 0,(float) 0,1,1,0,(float) 1));
                updateDailyClicks(id);
            }
                }
        catch(Exception e){
                
                System.out.println("IGNORE "+matcher.group(1).substring(0,matcher.group(1).length()-1)+" BECAUSE: "+e.getMessage());
        }}

        if(patternNumber==5){
            System.out.println(matcher.group(3)+" PROCESSING 3");
            //gibts das stats objekt? -nein = neues -ja = updaten
            long id =postRepository.getIdByName(matcher.group(3));
            if (statsRepo.existsByArtId(id)){
                long views = statsRepo.getClicksByArtId(id);
                long refferings =statsRepo.getReferringsByArtId(id);
                refferings++;
                float article_reffering_rate= ((float)refferings/views)*100;
                System.out.println("RefRate :"+article_reffering_rate);
                statsRepo.updateRefferingsAndRateByArtId(article_reffering_rate,refferings,id);

            }else{  statsRepo.save(new stats(id,(float) 0,(float) 0,0,0,1,(float) 1));
            }

        }

        if(patternNumber==6){
            System.out.println(matcher.group(1).replace("+","-")+" PROCESSING 4");
            if(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).isPresent()){
                updateUserStats(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).get());
            };
        }
    }

    @Transactional
    public void updateUserStats(WPUser user){
        if(userStatsRepo.existsByUserId(user.getId())){
            UserStats Stats = userStatsRepo.findByUserId(user.getId());
            long views = Stats.getProfileView() + 1 ;
            Stats.setProfileView(views);
            List<Post> list = postRepository.findByAuthor(user.getId().intValue());
            int count = 0;
            float relevance=0;
            float performance=0;
            for(Post p:list){
                if(statsRepo.existsByArtId(p.getId())){
                    stats PostStats = statsRepo.getStatByArtID(p.getId());
                    count ++;
                    relevance=relevance+PostStats.getRelevance();
                    performance=performance+PostStats.getPerformance();
                }
            }
            if(count !=0){
            relevance=relevance/count;
            performance=performance/count;
            Stats.setAveragePerformance(performance);
            Stats.setAverageRelevance(relevance);}
            userStatsRepo.save(Stats);


        }else{userStatsRepo.save(new UserStats(user.getId(), (float) 1,(float) 1, 1));}
    }
    @Transactional
    public void updateDailyClicks(long id){
        stats Stats = statsRepo.getStatByArtID(id);
        HashMap<String,Long> daily = (HashMap<String, Long>) Stats.getViewsLastYear();
        Calendar calendar = Calendar.getInstance();
        int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        long views = daily.get(Integer.toString(currentDayOfYear));
        views++;
        daily.put(Integer.toString(currentDayOfYear),views);
        Stats.setViewsLastYear((Map<String,Long>) daily);
        Stats.setRelevance(getRelevance(daily,currentDayOfYear,7));
        statsRepo.save(Stats);

    }

    public float getRelevance(HashMap<String,Long>viewsLastYear,int currentDayOfYear,int time){
        int counter =currentDayOfYear-time;
        long views=0;
        while(counter<=currentDayOfYear){
            views=views+(viewsLastYear.get(Integer.toString(counter)));
            counter++;
        }
        return (float)views/time;
    }

    @Transactional
    public void updateTagStats(long id,boolean searchSuccess){
        TagStat Stats = tagStatRepo.getStatById((int)id);
        HashMap<String,Long> daily = (HashMap<String, Long>) Stats.getViewsLastYear();
        Calendar calendar = Calendar.getInstance();
        int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        long views = daily.get(Integer.toString(currentDayOfYear));
        views++;
        daily.put(Integer.toString(currentDayOfYear),views);
        Stats.setViewsLastYear((Map<String,Long>) daily);
        views = Stats.getViews();
        views ++;
        Stats.setViews(views);
        Stats.setRelevance(getRelevance(daily,currentDayOfYear,7));
        if(searchSuccess){
            int searchS = Stats.getSearchSuccess();
            searchS++;
            Stats.setSearchSuccess(searchS);
        }
        tagStatRepo.save(Stats);
    }

    public void checkTheTag(long id,boolean searchSuccess){
        List<Long> tagTaxIds= termRelRepo.getTaxIdByObject(id);
        List<Long> tagIds= termTaxRepo.getTermIdByTaxId(tagTaxIds);
        for(Long l:tagIds){
            if(tagStatRepo.existsByTagId(l.intValue())){
                updateTagStats(l.intValue(),searchSuccess);}
            else{ tagStatRepo.save(new TagStat(l.intValue(),0,0,(float)0,(float)0));
                updateTagStats(l.intValue(),searchSuccess);}
    }}

    public void updateUserActivity(){
        List<WPUser> users = wpUserRepo.findAll();
        List<Post> posts= new ArrayList<>();
        UserStats stats = null ;
        float postfreq = 0 ;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postTime= now.minusMonths(1);
        int counter =0;
        for(WPUser user: users){
            posts=postRepository.findByAuthor(user.getId().intValue());
            for (Post post:posts){
                if(postTime.isBefore(post.getDate())&& post.getStatus().equals("publish")){counter ++;}
            }
            if(counter!=0){
            postfreq=(float)30/counter;}
            if (userStatsRepo.existsByUserId(user.getId())){
                stats = userStatsRepo.findByUserId(user.getId());
            }else{stats = new UserStats(user.getId(), 1,1,1);}
            stats.setPostFrequence(postfreq);
            userStatsRepo.save(stats);
        }
    }

}








