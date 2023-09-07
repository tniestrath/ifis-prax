package com.analysetool.services;

import com.analysetool.api.PostController;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.DashConfig;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.analysetool.util.IPHelper;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@Service
public class LogService {



    private PostRepository postRepository;
    private PostStatsRepository statsRepo;
    private TagStatRepository tagStatRepo;
    private WpTermRelationshipsRepository termRelRepo;
    private WPTermRepository termRepo;
    private WpTermTaxonomyRepository termTaxRepo;
    private WPUserRepository wpUserRepo;
    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;
    private UserStatsRepository userStatsRepo;

    private CommentsRepository commentRepo;
    private SysVarRepository sysVarRepo;
    private BufferedReader br;
    private String path = "";
    //^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}) regex für ip matching
    private String BlogSSPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /blog/(\\S+)/.*s=(\\S+)\".*"; //search +1, view +1,(bei match) vor blog view pattern
    private String ArtikelSSPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /artikel/(\\S+)/.*s=(\\S+)\".*";//search +1, view +1,(bei match) vor artikel view pattern
    //private String BlogViewPattern = "^.*GET \/blog\/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
    private String BlogViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /blog/(\\S+)/";
    private String RedirectPattern = "/.*GET .*goto=.*\"(https?:/.*/(artikel|blog|news)/(\\S*)/)";
    private String RedirectUserPattern ="/.*GET .*goto=.*\"(https?:/.*/(user)/(\\S*)/)";
    private String UserViewPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /user/(\\S+)/";

    //Blog view +1 bei match
    //private String ArtikelViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*GET /artikel/(\\S+)";//Artikel view +1 bei match
    private String ArtikelViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /artikel/(\\S+)/";
    private String PresseViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /news/(\\S+)/";
    //private String PresseSSViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /pressemitteilung/(\\S+)/.*s=(\\S+)";
    private String PresseSSViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /news/(\\S+)/.*s=(\\S+)\".*";

   // private String ReffererPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET.*\"https?:/.*/artikel|blog|pressemitteilung/(\\S*)/";
    private String ReffererPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET.*\"(https?:/.*/(artikel|blog|pressemitteilung)/(\\S*)/)";
    // private String SearchPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /s=(\\S+) ";
   private String SearchPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /\\?s=(\\S+) .*";

    Pattern pattern1_1 = Pattern.compile(ArtikelViewPattern);
    Pattern pattern1_2 = Pattern.compile(ArtikelSSPattern);
    Pattern pattern2_1 = Pattern.compile(BlogViewPattern);
    Pattern pattern2_2 = Pattern.compile(BlogSSPattern);
    Pattern pattern3=Pattern.compile(RedirectPattern);
    Pattern pattern4=Pattern.compile(UserViewPattern);
    Pattern pattern5_1 = Pattern.compile(PresseViewPatter);
    Pattern pattern5_2= Pattern.compile(PresseSSViewPatter);
    Pattern pattern4_2=Pattern.compile(RedirectUserPattern);
    Pattern pattern6_1= Pattern.compile(SearchPattern);
    Pattern pattern7=Pattern.compile(ReffererPattern);
    private String lastLine = "";
    private int lineCounter = 0;
    private int lastLineCounter = 0;
    private boolean liveScanning ;


    //Toter Code wird bis zum fertigen ConfigReader hier gelassen.
    //private String Pfad=Application.class.getClassLoader().getResource("access.log").getPath();
    //private String Pfad = Paths.get(Application.class.getClassLoader().getResource("access.log").toURI()).toString();
    private DashConfig config;
    private String Pfad;

    private Calendar kalender = Calendar.getInstance();
    private int aktuellesJahr = kalender.get(Calendar.YEAR);
    @Autowired
    private SearchStatsRepository searchStatRepo;

    private HashMap<String,ArrayList<LocalDateTime>> userViewTimes= new HashMap<>();
    private HashMap<String, Integer> userViews = new HashMap<>();
    private HashMap<String, Integer> impressions = new HashMap<>();
    @Autowired
    private universalStatsRepository uniRepo;


    @Autowired
    public LogService(PostRepository postRepository, PostStatsRepository PostStatsRepository, TagStatRepository tagStatRepo, WpTermRelationshipsRepository termRelRepo, WPTermRepository termRepo, WpTermTaxonomyRepository termTaxRepo, WPUserRepository wpUserRepo, UserStatsRepository userStatsRepo, CommentsRepository commentRepo, SysVarRepository sysVarRepo, DashConfig config) throws URISyntaxException {
        this.postRepository = postRepository;
        this.statsRepo = PostStatsRepository;
        this.tagStatRepo=tagStatRepo;
        this.termRelRepo=termRelRepo;
        this.termRepo=termRepo;
        this.termTaxRepo=termTaxRepo;
        this.wpUserRepo=wpUserRepo;
        this.userStatsRepo=userStatsRepo;
        this.commentRepo=commentRepo;
        this.sysVarRepo=sysVarRepo;
        this.config = config;
        Pfad = config.getAccess();
    }
    public LocalDateTime getCreationDateOfAccessLog(String filePath) {
        try {
            Path file = Paths.get(filePath);
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            System.out.println(LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault()));
            return LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            // Fehlerbehandlung, falls das Erstelldatum nicht geladen werden kann
            e.printStackTrace();
            return null;
        }
    }

    @PostConstruct
    public void init() {
        SysVar SystemVariabeln = new SysVar();
        if(sysVarRepo.findAll().isEmpty()){


            SystemVariabeln.setDate(LocalDateTime.now());
            SystemVariabeln.setDayInYear(LocalDateTime.now().getDayOfYear());
            SystemVariabeln.setDayInWeek(LocalDateTime.now().getDayOfWeek().getValue());
            SystemVariabeln.setDayInMonth(LocalDateTime.now().getDayOfMonth());
            SystemVariabeln.setLastLine("");
            SystemVariabeln.setLastLineCount(0);
            SystemVariabeln.setLogDate(getCreationDateOfAccessLog(Pfad));
            liveScanning = false ;

        }else {SystemVariabeln = sysVarRepo.findAll().get(sysVarRepo.findAll().size()-1);


                liveScanning= (getCreationDateOfAccessLog(Pfad).withHour(0).withMinute(0).withSecond(0).withNano(0).equals(SystemVariabeln.getLogDate().withHour(0).withMinute(0).withSecond(0).withNano(0)));
                System.out.println(getCreationDateOfAccessLog(Pfad).withSecond(0).withNano(0)+ "  "+SystemVariabeln.getLogDate().withSecond(0).withNano(0));

                SystemVariabeln.setDate(LocalDateTime.now());
                SystemVariabeln.setDayInYear(LocalDateTime.now().getDayOfYear());
                SystemVariabeln.setDayInWeek(LocalDateTime.now().getDayOfWeek().getValue());
                SystemVariabeln.setDayInMonth(LocalDateTime.now().getDayOfMonth());
                SystemVariabeln.setLastLine("");
                if(!liveScanning){SystemVariabeln.setLastLineCount(0);}
                SystemVariabeln.setLogDate(getCreationDateOfAccessLog(Pfad));
           // }

        }


        run(liveScanning,Pfad, SystemVariabeln);
        setUniversalStats();
        updateLetterCountForAll();

    }
    @Scheduled(cron = "0 0 * * * *") //einmal die Stunde
    //@Scheduled(cron = "0 */2 * * * *") //alle 2min
    public void runScheduled() {
        SysVar SystemVariabeln = new SysVar();
        if(sysVarRepo.findAll().isEmpty()){


            SystemVariabeln.setDate(LocalDateTime.now());
            SystemVariabeln.setDayInYear(LocalDateTime.now().getDayOfYear());
            SystemVariabeln.setDayInWeek(LocalDateTime.now().getDayOfWeek().getValue());
            SystemVariabeln.setDayInMonth(LocalDateTime.now().getDayOfMonth());
            SystemVariabeln.setLastLine("");
            SystemVariabeln.setLastLineCount(0);
            SystemVariabeln.setLogDate(getCreationDateOfAccessLog(Pfad));
            liveScanning = false;

        }else {SystemVariabeln = sysVarRepo.findAll().get(sysVarRepo.findAll().size()-1);

            liveScanning= (getCreationDateOfAccessLog(Pfad).withHour(0).withMinute(0).withSecond(0).withNano(0).equals(SystemVariabeln.getLogDate().withHour(0).withMinute(0).withSecond(0).withNano(0)));
                SystemVariabeln.setDate(LocalDateTime.now());
                SystemVariabeln.setDayInYear(LocalDateTime.now().getDayOfYear());
                SystemVariabeln.setDayInWeek(LocalDateTime.now().getDayOfWeek().getValue());
                SystemVariabeln.setDayInMonth(LocalDateTime.now().getDayOfMonth());
                SystemVariabeln.setLastLine("");
                if(!liveScanning){SystemVariabeln.setLastLineCount(0);}
                SystemVariabeln.setLogDate(getCreationDateOfAccessLog(Pfad));


        }


        run(liveScanning,Pfad, SystemVariabeln);
        updateLetterCountForAll();
    }
    public void run(boolean liveScanning, String path,SysVar SystemVariabeln)  {
        this.liveScanning = liveScanning;
        this.path = path;
        lastLineCounter=SystemVariabeln.getLastLineCount();
        lastLine = SystemVariabeln.getLastLine();
        lineCounter = 0 ;
        try  {
            br = new BufferedReader(new FileReader(path));
            findAMatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SystemVariabeln.setLastLineCount(lastLineCounter);
        SystemVariabeln.setLastLine(lastLine);
        updateWordCountForAll();
        saveStatsToDatabase();
        sysVarRepo.save(SystemVariabeln);
    }

    public void findAMatch() throws IOException {
        String line;

        boolean foundPattern = false;

        while ((line = br.readLine()) != null ) {
            if(lineCounter!=lastLineCounter){
                System.out.println("Counting up");
            while(lineCounter!=lastLineCounter && liveScanning){
                br.readLine();
                lineCounter++;

            }
            System.out.println("reached final position");
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
                    }else {
                        Matcher matcher5_1 = pattern5_1.matcher(line);

                        if (matcher5_1.find()) {
                            System.out.println("TEST NEWS GEFUNDEN");
                            Matcher matcher5_2 = pattern5_2.matcher(line);

                            if (matcher5_2.find()) {
                                System.out.println("TEST SEARCHSUCCESS GEFUNDEN");
                                // Do something with the matched 2.2 patterns
                                processLine(line, 8, matcher5_2);
                                foundPattern = false;
                                // System.out.println(line+" SEARCH FOUND");
                            } else {
                                //2.1 match
                                processLine(line, 7, matcher5_1);
                                foundPattern = false;
                                // System.out.println(line+" NO SEARCH");
                            }
                        }
                    }
                }

            Matcher matcher3=pattern3.matcher(line);
            if(matcher3.find()){
                processLine(line,5,matcher3);
                }
            Matcher matcher4=pattern4.matcher(line);
            if(matcher4.find()){
                processLine(line,6,matcher4);
            }
            Matcher matcher4_2=pattern4_2.matcher(line);
            if(matcher4_2.find()){
                processLine(line,9,matcher4);
            }
            Matcher matcher6_1=pattern6_1.matcher(line);
            if(matcher6_1.find()){
                processLine(line,10,matcher6_1);
            }



            lineCounter++;
            lastLineCounter++;
            //System.out.println(lineCounter+" "+lastLine);
            //br.readLine();

        }
        //updateuseraktivität
        System.out.println("UPDATING USER ACTIVITY");
        updateUserActivity((long)3);
        updateUserStatsForAllUsers();
        lineCounter = 0 ;
        System.out.println("END OF LOG");
    }


    public void processLine(String line,int patternNumber,Matcher matcher){
        lastLine=line;
        if (patternNumber==1){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 1.1");
            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        if (patternNumber==2){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 1.2");
            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }
        if (patternNumber==3){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 2.1");
            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        if (patternNumber==4){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 2.2");
            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }

        if(patternNumber==5){
            System.out.println(matcher.group(3)+" PROCESSING 3");
            //gibts das PostStats objekt? -nein = neues -ja = updaten
            long id =postRepository.getIdByName(matcher.group(3));
            if (statsRepo.existsByArtIdAndYear(id,aktuellesJahr)){
                PostStats stats=statsRepo.findByArtIdAndAndYear(id,aktuellesJahr);
                long views = stats.getClicks();
                long refferings =stats.getRefferings();
                refferings++;
                float article_reffering_rate= ((float)refferings/views);
                System.out.println("RefRate :"+article_reffering_rate);
                stats.setClicks(views);
                stats.setReferrings(refferings);
                statsRepo.save(stats);
                //statsRepo.updateRefferingsAndRateByArtId(article_reffering_rate,refferings,id);

            }else{  statsRepo.save(new PostStats(id,(float) 0,(float) 0,0,0,1,(float) 0));
            }

        }

        if(patternNumber==6){
            System.out.println(matcher.group(1).replace("+","-")+" PROCESSING 4");
            if(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).isPresent()){
                //updateUserStats(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).get());
                userViewOrImpression(matcher);
            }
        }
        if (patternNumber==7){

            System.out.println(postRepository.getIdByName(matcher.group(1)+" "+matcher.group(1))+" PROCESSING 5.1");



            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        if (patternNumber==8){

            System.out.println(postRepository.getIdByName(matcher.group(5))+matcher.group(6)+" PROCESSING 5.2");


            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }

        if(patternNumber==9){
            System.out.println(matcher.group(1).replace("+","-")+" PROCESSING 4_2");
            if(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).isPresent()){
                WPUser wpUser=wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).get();
                if(userStatsRepo.existsByUserId(wpUser.getId())){
                    UserStats userStats = userStatsRepo.findByUserId(wpUser.getId());
                    long refferings = userStats.getRefferings();
                    long views = userStats.getProfileView();
                    refferings ++;
                    userStats.setRefferings(refferings);
                    if(views!=0){
                        userStats.setRefferingRate((float)refferings/views);
                    }
                    userStatsRepo.save(userStats);
                }else{
                    userStatsRepo.save(new UserStats(wpUser.getId(), (float) 0,(float) 0, 0,(float) 0,(float) 0,(float)0,(long)1));
                }

            };
        }
        if(patternNumber==10){
            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
            String ip = matcher.group(1);
            byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
            String ipHash = Hex.toHexString(hashBytes);
            IPHelper.getInstance();
            String country = IPHelper.getCountryISO(ip);
            String region = IPHelper.getSubISO(ip);
            String city = IPHelper.getCityName(ip);
            String location="";
            if(country!=null){
                location=country;
                if(region!=null)
                    location= location+" : "+region;
                if(city!=null)
                    location = location+" : "+city;
            }
            String day = matcher.group(2);
            String month = getMonthNumber(matcher.group(3));
            String year = matcher.group(4);
            String time = matcher.group(5);
            LocalDateTime dateTime = LocalDateTime.parse(String.format("%s-%s-%sT%s", year, month, day, time));
            searchStatRepo.save(new SearchStats(ipHash,matcher.group(6),dateTime,location));

        }
        if(patternNumber==11){

            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
            System.out.println(matcher.group(1)+" "+matcher.group(2)+" "+matcher.group(3)+" "+matcher.group(4)+" "+matcher.group(5)+" "+matcher.group(8));
            String day = matcher.group(2);
            String month = getMonthNumber(matcher.group(3));
            String year = matcher.group(4);
            String time = matcher.group(5);
            LocalDateTime searchSuccessTime = LocalDateTime.parse(String.format("%s-%s-%sT%s", year, month, day, time));
            LocalDate date = searchSuccessTime.toLocalDate() ;


            List<SearchStats> searchStatsForDate = searchStatRepo.findAllBySearchDate(date);

            long id = postRepository.getIdByName(matcher.group(8));
            byte[] hashBytesForComparison = digestSHA3.digest(matcher.group(1).getBytes(StandardCharsets.UTF_8));
            String hashForComparison = Hex.toHexString(hashBytesForComparison);

            for(SearchStats s : searchStatsForDate){

                if(hashForComparison.equals(s.getIpHashed()) && s.getSearchSuccessFlag() && s.getClickedPost().equals(Long.toString(id))){

                    LocalTime search_success_time = s.getSearch_success_time().toLocalTime();
                    String logHourMinuteSecond = matcher.group(5);

                // Trenne Stunden, Minuten und Sekunden
                    String[] timeParts = logHourMinuteSecond.split(":");
                    LocalTime refferer_time = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), Integer.parseInt(timeParts[2]));

               // Differenz = dwelltime
                    Duration difference = Duration.between(search_success_time, refferer_time);

                    LocalTime dwell_time;

                // Wenn die Differenz negativ ist oder länger als 24 Stunden beträgt
                    if (difference.isNegative() || difference.toHours() >= 24) {
                        dwell_time = LocalTime.of(0, 0, 0);
                    } else {
                        // Konvertiere die Dauer in Stunden, Minuten und Sekunden
                        long totalSeconds = difference.getSeconds();
                        int hours = (int) (totalSeconds / 3600);
                        int minutes = (int) ((totalSeconds % 3600) / 60);
                        int seconds = (int) (totalSeconds % 60);

                        dwell_time = LocalTime.of(hours, minutes, seconds);
                    }




                    s.setDwell_time(dwell_time);
                    searchStatRepo.save(s);
                }
            }

        }


    }
    public String hashIp(String ip){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
        String ipHash = Hex.toHexString(hashBytes);
        return ipHash;
    }

    public void saveStatsToDatabase() {
        for (String user : userViews.keySet()) {
            UserStats userStats = userStatsRepo.findByUserId(Long.valueOf(user));

            long views = userViews.get(user);
            long currentImpressions = impressions.getOrDefault(user, 0);

            if (userStats == null) {
                userStats = new UserStats(Long.valueOf(user), views, currentImpressions);
            } else {
                // Addiere die Werte zu den vorhandenen Statistiken
                userStats.setProfileView(userStats.getProfileView() + views);
                userStats.setImpressions(userStats.getImpressions() + currentImpressions);
            }

            userStatsRepo.save(userStats);
        }
    }

    public void userViewOrImpression(Matcher matcher) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        String ip = matcher.group(1);
        byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
        String ipHash = Hex.toHexString(hashBytes);

        WPUser currentUser = wpUserRepo.findByNicename(matcher.group(6).replace("+","-")).get();

        if (currentUser == null) {
            // Handle the case where no user is found.
            return;
        }

        String day = matcher.group(2);
        String month = matcher.group(3);
        String year = matcher.group(4);
        String time = matcher.group(5);
        LocalDateTime requestTime = LocalDateTime.parse(String.format("%s-%s-%sT%s", year, month, day, time));

        if (userViewTimes.containsKey(ipHash)) {
            ArrayList<LocalDateTime> times = userViewTimes.get(ipHash);

            // Check the time difference between the last request and the current one.
            if (Duration.between(times.get(times.size() - 1), requestTime).getSeconds() <= 3) {
                // This request is an impression.
                impressions.put(currentUser.getId().toString(), impressions.getOrDefault(currentUser, 0) + 1);
                times.add(requestTime);
            } else {
                // This request is a unique view.
                userViews.put(currentUser.getId().toString(), userViews.getOrDefault(currentUser, 0) + 1);
                times.add(requestTime);
            }
        } else {
            ArrayList<LocalDateTime> times = new ArrayList<>();
            times.add(requestTime);
            userViewTimes.put(ipHash, times);
            // This is the first time seeing this IP for the user, so it's a unique view.
            userViews.put(currentUser.getId().toString(), userViews.getOrDefault(currentUser, 0) + 1);
        }
    }

    public void updateSearchStats(Matcher matcher) {

        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        byte[] hashBytes = digestSHA3.digest(matcher.group(1).getBytes(StandardCharsets.UTF_8));
        String hashedIp = Hex.toHexString(hashBytes);


        String day = matcher.group(2);
        String month = getMonthNumber(matcher.group(3));
        String year = matcher.group(4);
        String time = matcher.group(5);
        LocalDateTime searchSuccessTime = LocalDateTime.parse(String.format("%s-%s-%sT%s", year, month, day, time));
        LocalDate date = searchSuccessTime.toLocalDate();  // Replace with the date you want to search for
        System.out.println("GRUPPE 7: "+matcher.group(7));
        List<SearchStats> searchStatsForDate = searchStatRepo.findAllBySearchDate(date);
        long id = postRepository.getIdByName(matcher.group(6));
        for(SearchStats s : searchStatsForDate) {
            //hier weiter searchstring equals nicht so viel sinn mit klicked post
            if(hashedIp.equals(s.getIpHashed()) && !s.getSearchSuccessFlag() && s.getSearchString().equals(matcher.group(7))) {
                s.setSearchSuccessFlag(true);

                s.setClickedPost(String.valueOf(id));



                s.setSearch_success_time(searchSuccessTime);

                searchStatRepo.save(s);
            }
        }
    }
    public void updatePerformanceViewsSearchSuccess(Matcher matcher) {

        // Extrahiere Datum und Uhrzeit aus dem Log mit dem neuen Matcher
        String logDay = matcher.group(2);
        String logMonth = matcher.group(3);
        String logYear = matcher.group(4);
        String logHourMinuteSecond = matcher.group(5);

        // Trenne Stunden, Minuten und Sekunden
        String[] timeParts = logHourMinuteSecond.split(":");
        String logHour = timeParts[0];
        String logMinute = timeParts[1];
        String logSecond = timeParts[2];

        // Konvertiere den Monat in eine Zahl
        int monthNumber = Integer.parseInt(getMonthNumber(logMonth));

        // Erstelle LocalDate und LocalTime Objekte
        LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), monthNumber, Integer.parseInt(logDay));
        LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));

        try {
            long id = postRepository.getIdByName(matcher.group(6));
            checkTheTag(id, true);

            if (statsRepo.existsByArtIdAndYear(id, aktuellesJahr)) {
                PostStats stats = statsRepo.findByArtIdAndAndYear(id, aktuellesJahr);
                long views = stats.getClicks();
                views++;
                long searchSuccess = stats.getSearchSuccess();
                searchSuccess++;



                LocalDateTime PostTimestamp = postRepository.getPostDateById(id);
                LocalDateTime Now =  LocalDateTime.now();
                Duration duration = Duration.between(PostTimestamp, Now);
                long diffInDays = duration.toDays();
                float performance = views;
                if (diffInDays>0&&views > 0){
                    performance = (float)views/diffInDays;
                }
                statsRepo.updateClicksSearchSuccessRateAndPerformance(id, views, searchSuccess, performance);

                // Rufe die angepasste Methode mit dem extrahierten Datum und der Uhrzeit auf
                erhoeheWertFuerLogDatum(id, logDate, logTime);
            } else {
                statsRepo.save(new PostStats(id, (float) 0, (float) 0, 1, 1, 0, (float) 0));
                //erhoeheWertFuerHeutigesDatum(id);
                erhoeheWertFuerLogDatum(id, logDate, logTime);
            }
        } catch (Exception e) {
            System.out.println("IGNORE " + matcher.group(2).substring(0, matcher.group(2).length() - 1) + " BECAUSE: " + e.getMessage());
        }
    }
    @Transactional
    public void erhoeheWertFuerLogDatum(long id, LocalDate logDatum, LocalTime logUhrzeit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String logDatumString = logDatum.format(formatter);

        PostStats postStats = statsRepo.findByArtIdAndAndYear(id, logDatum.getYear());

        HashMap<String, Long> daily = (HashMap<String, Long>) postStats.getViewsLastYear();
        long aktuellerWert = daily.getOrDefault(logDatumString, 0L);
        daily.put(logDatumString, aktuellerWert + 1);

        Map<String, Long> viewsPerHour = erhoeheViewsPerHour2(postStats, logUhrzeit);
        postStats.setViewsPerHour(viewsPerHour);

        postStats.setViewsLastYear(daily);
        postStats.setRelevance(getRelevance2(daily, logDatumString, 7));

        statsRepo.save(postStats);
    }

    public Map<String, Long> erhoeheViewsPerHour2(PostStats stats, LocalTime logUhrzeit) {
        Map<String, Long> viewsPerHour = stats.getViewsPerHour();
        int stunde = logUhrzeit.getHour();
        long views = viewsPerHour.getOrDefault(Integer.toString(stunde), 0L);
        views++;
        viewsPerHour.put(Integer.toString(stunde), views);

        return viewsPerHour;
    }

    //ToDo Toten Code aufräumen
  /*  public void updatePerformanceViewsSearchSuccess(Matcher matcher) {
        try{
            long id =postRepository.getIdByName(matcher.group(6));
            checkTheTag(id,true);
            if (statsRepo.existsByArtIdAndYear(id,aktuellesJahr)){
                PostStats stats = statsRepo.findByArtIdAndAndYear(id,aktuellesJahr);
                long views = stats.getClicks();
                views ++;
                long searchSuccess= stats.getSearchSuccess();
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
           // updateDailyClicks(id);
            erhoeheWertFuerHeutigesDatum( id);
        }else{  statsRepo.save(new PostStats(id,(float) 0,(float) 0,1,1,0,(float) 0));
            //updateDailyClicks(id);
            erhoeheWertFuerHeutigesDatum( id);
        }
            }
    catch(Exception e){

            System.out.println("IGNORE "+matcher.group(2).substring(0,matcher.group(2).length()-1)+" BECAUSE: "+e.getMessage());
    }
    }*/
    //ToDo Toten Code aufräumen
   /* public void UpdatePerformanceAndViews(Matcher matcher) {
        try{
            long id =postRepository.getIdByName(matcher.group(6));
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
                //updateDailyClicks(id);
                erhoeheWertFuerHeutigesDatum( id);
        }else{  statsRepo.save(new PostStats(id,(float) 0,(float) 0,1,0,0,(float) 0));//updateDailyClicks(id);
                erhoeheWertFuerHeutigesDatum( id);}
            }
    catch(Exception e){
            System.out.println("IGNORE "+matcher.group(2).substring(0,matcher.group(2).length()-1)+" BECAUSE: "+e.getMessage());
       // e.printStackTrace();
    }
    }*/
   public void UpdatePerformanceAndViews(Matcher matcher) {

       // Extrahiere Datum und Uhrzeit aus dem Log mit dem neuen Matcher
       String logDay = matcher.group(2);
       String logMonth = matcher.group(3);
       String logYear = matcher.group(4);
       String logHourMinuteSecond = matcher.group(5);

       // Trenne Stunden, Minuten und Sekunden
       String[] timeParts = logHourMinuteSecond.split(":");
       String logHour = timeParts[0];
       String logMinute = timeParts[1];
       String logSecond = timeParts[2];

       // Konvertiere den Monat in eine Zahl
       int monthNumber = Integer.parseInt(getMonthNumber(logMonth));

       // Erstelle LocalDate und LocalTime Objekte
       LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), monthNumber, Integer.parseInt(logDay));
       LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));

       try {
           long id = postRepository.getIdByName(matcher.group(6));
           checkTheTag(id, false);

           if (statsRepo.existsByArtId(id)) {
               long views = statsRepo.getClicksByArtId(id);
               views++;



               LocalDateTime PostTimestamp = postRepository.getPostDateById(id);
               LocalDateTime Now =  LocalDateTime.now();
               Duration duration = Duration.between(PostTimestamp, Now);
               long diffInDays = duration.toDays();
               float performance = views;
               if (diffInDays>0&&views > 0){
                   performance = (float)views/diffInDays;
               }

               statsRepo.updateClicksAndPerformanceByArtId(views, id, performance);

               // Rufe die angepasste Methode mit dem extrahierten Datum und der Uhrzeit auf
               erhoeheWertFuerLogDatum(id, logDate, logTime);
           } else {
               statsRepo.save(new PostStats(id, (float) 0, (float) 0, 1, 0, 0, (float) 0));
               //erhoeheWertFuerHeutigesDatum(id);
               erhoeheWertFuerLogDatum(id, logDate, logTime);
           }
       } catch (Exception e) {
           System.out.println("IGNORE " + matcher.group(6) + " BECAUSE: " + e.getMessage());
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
                    PostStats PostStats = statsRepo.getStatByArtID(p.getId());
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


        }else{userStatsRepo.save(new UserStats(user.getId(), (float) 0,(float) 0, 0,(float) 0,(float) 0,(float)0,(long)0));}
    }
    @Transactional
    public void updateUserStatsForAllUsers() {
        List<WPUser> allUsers = wpUserRepo.findAll();

        for (WPUser user : allUsers) {
            if (userStatsRepo.existsByUserId(user.getId())) {
                UserStats stats = userStatsRepo.findByUserId(user.getId());
                long views = stats.getProfileView() + 1;
                stats.setProfileView(views);

                List<Post> posts = postRepository.findByAuthor(user.getId().intValue());
                int count = 0;
                float relevance = 0;
                float performance = 0;

                for (Post post : posts) {
                    if (statsRepo.existsByArtId(post.getId())) {
                        PostStats postStats = statsRepo.getStatByArtID(post.getId());
                        count++;
                        relevance += postStats.getRelevance();
                        performance += postStats.getPerformance();
                    }
                }

                if (count != 0) {
                    relevance = relevance / count;
                    performance = performance / count;
                    stats.setAveragePerformance(performance);
                    stats.setAverageRelevance(relevance);
                }

                userStatsRepo.save(stats);
            } else {
                userStatsRepo.save(new UserStats(user.getId(), (float) 0,(float) 0, 0,(float) 0,(float) 0,(float)0,(long)0));
            }
        }
    }

    @Transactional
    public void updateDailyClicks(long id){
        PostStats PostStats = statsRepo.getStatByArtID(id);
        HashMap<String,Long> daily = (HashMap<String, Long>) PostStats.getViewsLastYear();
        Calendar calendar = Calendar.getInstance();
        int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        long views = daily.get(Integer.toString(currentDayOfYear));
        views++;
        daily.put(Integer.toString(currentDayOfYear),views);
        PostStats.setViewsLastYear((Map<String,Long>) daily);
        PostStats.setRelevance(getRelevance(daily,currentDayOfYear,7));
        statsRepo.save(PostStats);

    }

    @Transactional
    public void erhoeheWertFuerHeutigesDatum(long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        PostStats postStats = statsRepo.findByArtIdAndAndYear(id,aktuellesJahr);
        HashMap<String, Long> daily = (HashMap<String, Long>) postStats.getViewsLastYear();

        // Das heutige Datum im Format dd.MM abrufen
        String heutigesDatum = LocalDate.now().format(formatter);

        // Den Wert für das heutige Datum in der HashMap um 1 erhöhen
        long aktuellerWert = daily.getOrDefault(heutigesDatum, 0L);
        daily.put(heutigesDatum, aktuellerWert + 1);
        postStats.setViewsPerHour(erhoeheViewsPerHour(postStats));
        postStats.setViewsLastYear(daily);
        postStats.setRelevance(getRelevance2(daily, heutigesDatum, 7));

        statsRepo.save(postStats);
    }

    @Transactional
    public Map<String,Long> erhoeheViewsPerHour(PostStats stats){
        Map<String,Long> viewsPerHour =stats.getViewsPerHour();
        LocalTime jetzt = LocalTime.now();
        int stunde = jetzt.getHour();
        if(stunde != 0){stunde--;}else{stunde=23;}
        long views= viewsPerHour.getOrDefault(Integer.toString(stunde),0L);
        views++;
        viewsPerHour.put(Integer.toString(stunde),views);
        stats.setViewsPerHour(viewsPerHour);

        return viewsPerHour;
    }
    public static float getRelevance2(HashMap<String, Long> viewsLastYear, String currentDateString, int time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd.MM");

        // Add the current year to the date string
        String year = String.valueOf(LocalDate.now().getYear());
        LocalDate currentDate = LocalDate.parse(year + "-" + currentDateString, formatter);

        long views = 0;

        for (int i = 0; i < time; i++) {
            String dateKey = currentDate.minusDays(i).format(DateTimeFormatter.ofPattern("dd.MM"));
            views += viewsLastYear.getOrDefault(dateKey, 0L);
        }

        return (float) views / time;
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

    public void updateUserActivity(Long period){
        List<WPUser> users = wpUserRepo.findAll();
        List<Post> posts= new ArrayList<>();
        UserStats stats = null ;
        float postfreq = 0 ;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime postTime= now.minusMonths(period);
        long daysDifference = ChronoUnit.DAYS.between(postTime, now);
        int counter =0;
        for(WPUser user: users){
            posts=postRepository.findByAuthor(user.getId().intValue());
            counter = 0 ;
            if(!posts.isEmpty()){
            for (Post post:posts){
                if(postTime.isBefore(post.getDate())&& post.getStatus().equals("publish") && post.getType().equals("post")){counter ++;}
            }
            if(counter!=0){
            postfreq=(float)daysDifference/counter;
            }}else{postfreq=0;}
            if (userStatsRepo.existsByUserId(user.getId())){
                stats = userStatsRepo.findByUserId(user.getId());
            }else{stats = new UserStats(user.getId(), (float) 0,(float) 0, 1,(float) 0,(float) 0,(float)0,(long)0);}
            stats.setPostFrequence(postfreq);
            userStatsRepo.save(stats);
            updateInteractionRate(user,stats,posts);
        }

    }

    public void updateInteractionRate(WPUser user,UserStats stats, List<Post>posts){
        int commentCount=0;
        int answeredComments=0;
        float interactionRate=0;
        List<Comments> comments = new ArrayList<>();
        for(Post post:posts){
            if(post.getStatus().equals("publish") && post.getType().equals("post")){
                comments=commentRepo.findByPostId(post.getId());
                for(Comments comment:comments){
                    if (comment.getUserId() == user.getId()) {
                        if (commentRepo.findByCommentId(comment.getParentCommentId()).getUserId() != user.getId()) {
                            answeredComments++;
                        }
                    } else {
                        commentCount++;
                    }
                }
            }

        }
        if(answeredComments!=0){
        interactionRate=(float)answeredComments/commentCount;}
        stats.setInteractionRate(interactionRate);
        userStatsRepo.save(stats);
       // System.out.println("Interaktionsrate: "+interactionRate+" id: "+user.getId());
    }
    @Transactional
    public void updateViewsByLocation(Matcher matcher) {
        String ip = matcher.group(1);
        try {
            long id = postRepository.getIdByName(matcher.group(6));
            IPHelper.getInstance();
            String country = IPHelper.getCountryISO(ip);
            String region = IPHelper.getSubISO(ip);
            String city = IPHelper.getCityName(ip);

            if (statsRepo.existsByArtIdAndYear(id, aktuellesJahr)) {
                PostStats stats = statsRepo.getStatByArtID(id);

                // Holt die aktuelle Map oder erstellt eine neue, falls sie null ist.
                Map<String, Map<String, Map<String, Long>>> viewsByLocation = stats.getViewsByLocation();
                if (viewsByLocation == null) {
                    viewsByLocation = new HashMap<>();
                    stats.setViewsByLocation(viewsByLocation);
                }

                // Standard Schlüssel setzen
                String countryKey = "global";
                String regionKey = "gesamt";

                if (country != null && !country.isEmpty()) {
                    countryKey = country;
                    regionKey = (region != null && !region.isEmpty()) ? region : "gesamt";
                    if(!country.equals("DE")) {
                        regionKey = country;
                    }

                    // Map der Regionen für das gegebene Land holen
                    Map<String, Map<String, Long>> regions = viewsByLocation.computeIfAbsent(countryKey, k -> new HashMap<>());

                    // Map der Städte für die gegebene Region holen
                    Map<String, Long> cities = regions.computeIfAbsent(regionKey, k -> new HashMap<>());

                    // Aktualisieren der Anzahl der Views für die Stadt
                    if (city != null && !city.isEmpty()) {
                        cities.merge(city, 1L, Long::sum);
                    }

                    // Aktualisieren der "gesamt" Views für Region
                    cities.merge("gesamt", 1L, Long::sum);

                    // Aktualisieren der "gesamt" Views für Land
                    Map<String, Long> countryTotal = regions.computeIfAbsent("gesamt", k -> new HashMap<>());
                    countryTotal.merge("gesamt", 1L, Long::sum);
                }

                // Aktualisieren der "gesamt" Views für Global
                Map<String, Map<String, Long>> globalTotal = viewsByLocation.computeIfAbsent("global", k -> new HashMap<>());
                globalTotal.computeIfAbsent("gesamt", k -> new HashMap<>()).merge("gesamt", 1L, Long::sum);

                // Persistieren der Änderungen
                statsRepo.save(stats);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getMonthNumber(String monthName) {
        Map<String, String> months = new HashMap<>();
        months.put("Jan", "01");
        months.put("Feb", "02");
        months.put("Mar", "03");
        months.put("Apr", "04");
        months.put("May", "05");
        months.put("Jun", "06");
        months.put("Jul", "07");
        months.put("Aug", "08");
        months.put("Sep", "09");
        months.put("Oct", "10");
        months.put("Nov", "11");
        months.put("Dec", "12");

        return months.getOrDefault(monthName.substring(0, 3), "00");
    }


    public void updateLetterCount(long id) {

        int lettercount = Jsoup.clean(postRepository.getContentById(id), Safelist.none()).length();
        statsRepo.updateLetterCount(lettercount, id);
    }

    public void updateLetterCountForAll () {
        for(Post p : postRepository.findAllUserPosts()) {
            if (statsRepo.existsByArtId(p.getId())) {
                if ((statsRepo.getLetterCount(p.getId()) == 0L) || statsRepo.getLetterCount(p.getId()) == null) {
                  //  System.out.println(statsRepo.getLetterCount(p.getId()));
                    updateLetterCount(p.getId());
                }
            }
        }
    }

    public void countWordsInPost(long id) {
        // Hole den Inhalt des Posts und bereinige ihn
        String content = Jsoup.clean(postRepository.getContentById(id), Safelist.none());

        // Trenne den String anhand der bekannten Worttrenner und zähle die Anzahl der resultierenden Wörter
        String[] words = content.split("\\s+|,|;|\\.|\\?|!");
        int wordCount = words.length;

        statsRepo.updateWordCount(wordCount,id);
    }

    public void updateWordCountForAll () {
        for(Post p : postRepository.findAllUserPosts()) {
            countWordsInPost(p.getId());
        }
    }

    public static String getLastDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Vortag
        Date vortag = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(vortag);
        return formattedDate;
    }

    public static String getDay(int zuruek){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -zuruek); // Vortag
        Date vortag = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(vortag);
        return formattedDate;
    }

    public static String generateLogFileNameLastDay() {
       return "access.log-" + getLastDay() + ".gz";
    }
    public void setUniversalStats() {
        int daysToLookBack = 14; // Anzahl der Tage, die zurückgeschaut werden sollen

        if (!sysVarRepo.findAll().get(sysVarRepo.findAll().size() - 1).isFlagScanLast14()) {
            while (daysToLookBack > 0) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    Date date = dateFormat.parse(getDay(daysToLookBack));

                    if (uniRepo.findByDatum(date).isEmpty()) {
                        String pathOfOldLog = "/var/log/nginx/access.log-" + getDay(daysToLookBack) + ".gz";
                        FileInputStream fileInputStream = new FileInputStream(pathOfOldLog);
                        GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                        InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        universalStats uniStats = proccessLinesOfOldLog(new universalStats(date), bufferedReader);
                        uniStats.setAnbieterProfileAnzahl(wpUserRepo.count());
                        //m
                        uniStats = setNewsArticelBlogCountForUniversalStats(date.toString(),uniStats);

                        uniStats = setAccountTypeAllUniStats(uniStats);

                        uniRepo.save(uniStats);
                    } else {
                        System.out.println("Tag " + daysToLookBack + " bereits in der Statistik");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                daysToLookBack--; // Verringert die Anzahl der zurückzuschauenden Tage
            }

            SysVar sysVar =sysVarRepo.findAll().get(sysVarRepo.findAll().size() - 1);
            sysVar.setFlagScanLast14(true);
            sysVarRepo.save(sysVar);

        } else {
            // Ihr bisheriger Code für den Fall, dass das Flag gesetzt ist
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = dateFormat.parse(getLastDay());
                if (uniRepo.findByDatum(date).isEmpty()) {
                    String pathOfOldLog = "/var/log/nginx/access.log-" + getLastDay() + ".gz";
                    FileInputStream fileInputStream = new FileInputStream(pathOfOldLog);
                    GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                    InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    universalStats uniStats = proccessLinesOfOldLog(new universalStats(date), bufferedReader);
                    uniStats.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uniStats = setNewsArticelBlogCountForUniversalStats(uniStats);
                    uniStats = setAccountTypeAllUniStats(uniStats);
                    uniRepo.save(uniStats);
                } else {
                    System.out.println("Vortag bereits in der Statistik");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public universalStats proccessLinesOfOldLog(universalStats uniStat,BufferedReader bufferedReader) throws IOException {

        ArrayList<String> uniqueIps = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Matcher matcher1_1 = pattern1_1.matcher(line);

            if (matcher1_1.find()) {
                Matcher matcher1_2 = pattern1_2.matcher(line);


                if (matcher1_2.find()) {
                    // Do something with the matched 1.2 patterns
                    if(!uniqueIps.contains(hashIp(matcher1_2.group(1)))){uniqueIps.add(hashIp(matcher1_2.group(1)));}

                } else {//1.1 matched

                    if(!uniqueIps.contains(hashIp(matcher1_1.group(1)))){uniqueIps.add(hashIp(matcher1_1.group(1)));}

                }
            }
            // }
            else {
                Matcher matcher2_1 = pattern2_1.matcher(line);

                if (matcher2_1.find()) {
                    Matcher matcher2_2 = pattern2_2.matcher(line);

                    if (matcher2_2.find()) {
                        // Do something with the matched 2.2 patterns

                        if(!uniqueIps.contains(hashIp(matcher2_2.group(1)))){uniqueIps.add(hashIp(matcher2_2.group(1)));}

                    } else {
                        //2.1 match

                        if(!uniqueIps.contains(hashIp(matcher2_1.group(1)))){uniqueIps.add(hashIp(matcher2_1.group(1)));}

                    }
                } else {
                    Matcher matcher5_1 = pattern5_1.matcher(line);

                    if (matcher5_1.find()) {

                        Matcher matcher5_2 = pattern5_2.matcher(line);
                        if (matcher5_2.find()) {
                            if(!uniqueIps.contains(hashIp(matcher5_2.group(1)))){uniqueIps.add(hashIp(matcher5_2.group(1)));}
                        } else {
                            if(!uniqueIps.contains(hashIp(matcher5_1.group(1)))){uniqueIps.add(hashIp(matcher5_1.group(1)));}
                        }
                    }
                }
            }

            Matcher matcher3 = pattern3.matcher(line);
            if (matcher3.find()) {
                if(!uniqueIps.contains(hashIp(matcher3.group(1)))){uniqueIps.add(hashIp(matcher3.group(1)));}
            }
            Matcher matcher4 = pattern4.matcher(line);
            if (matcher4.find()) {
                if(!uniqueIps.contains(hashIp(matcher4.group(1)))){uniqueIps.add(hashIp(matcher4.group(1)));}
            }
            Matcher matcher4_2 = pattern4_2.matcher(line);
            if (matcher4_2.find()) {
                if(!uniqueIps.contains(hashIp(matcher4_2.group(1)))){uniqueIps.add(hashIp(matcher4_2.group(1)));}
            }
            Matcher matcher6_1 = pattern6_1.matcher(line);
            if (matcher6_1.find()) {

                if(!uniqueIps.contains(hashIp(matcher6_1.group(1)))){uniqueIps.add(hashIp(matcher6_1.group(1)));}

            }
        }

        uniStat.setBesucherAnzahl((long) uniqueIps.size());
        return uniStat;
    }

    public universalStats setNewsArticelBlogCountForUniversalStats(universalStats uniStats){

        List<Post> posts = postRepository.findAllUserPosts();

        long artikelCounter = 0 ;
        long newsCounter =0;
        long blogCounter = 0;

        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();

        for (Post post : posts) {
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : termTaxRepo.findByTermTaxonomyId(l)) {

                        if (termTax.getTermId() == tagIdBlog) {
                            blogCounter++ ;
                        }

                        if (termTax.getTermId() == tagIdArtikel) {
                            artikelCounter++ ;
                        }

                        if (termTax.getTermId() == tagIdPresse) {
                            newsCounter++ ;
                        }
                    }


                }
            }

        uniStats.setAnzahlArtikel(artikelCounter);
        uniStats.setAnzahlNews(newsCounter);
        uniStats.setAnzahlBlog(blogCounter);

        return uniStats ;
    }
    public universalStats setNewsArticelBlogCountForUniversalStats(String dateStr, universalStats uniStats) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date givenDate = sdf.parse(dateStr);

        List<Post> posts = postRepository.findAllUserPosts();

        long artikelCounter = 0;
        long newsCounter = 0;
        long blogCounter = 0;

        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();

        for (Post post : posts) {
            // Konvertiere post_date zu yyyyMMdd Format für den Vergleich
            String postDateStr = sdf.format(post.getDate());

            // Zähle nur die Posts, die am gegebenen Tag oder davor veröffentlicht wurden
            if (postDateStr.compareTo(dateStr) <= 0) {
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : termTaxRepo.findByTermTaxonomyId(l)) {
                        if (termTax.getTermId() == tagIdBlog) {
                            blogCounter++;
                        }

                        if (termTax.getTermId() == tagIdArtikel) {
                            artikelCounter++;
                        }

                        if (termTax.getTermId() == tagIdPresse) {
                            newsCounter++;
                        }
                    }
                }
            }
        }

        // Setze die Zählungen im universalStats-Objekt
        uniStats.setAnzahlArtikel(artikelCounter);
        uniStats.setAnzahlNews(newsCounter);
        uniStats.setAnzahlBlog(blogCounter);

        return uniStats;
    }

    public universalStats setAccountTypeAllUniStats(universalStats uniStats){
        HashMap<String, Integer> counts = new HashMap<>();

        wpUserMetaRepository.getWpCapabilities().forEach(s -> {

            if (( s.contains("um_anbieter") || s.contains("um_basis-anbieter") ) && !s.contains("plus"))
                counts.put("Basic", counts.get("Basic") == null ? 1 : counts.get("Basic") + 1);
            if (s.contains("um_plus-anbieter"))
                counts.put("Plus", counts.get("Plus") == null ? 1 : counts.get("Plus") + 1);
            if (!s.contains("sponsoren") && s.contains("um_premium-anbieter"))
                counts.put("Premium", counts.get("Premium") == null ? 1 : counts.get("Premium") + 1);
            if (s.contains("um_premium-anbieter-sponsoren"))
                counts.put("Sponsor", counts.get("Sponsor") == null ? 1 : counts.get("Sponsor") + 1);
            if (s.contains("um_basis-anbieter-plus"))
                counts.put("Basic-Plus", counts.get("Basic-Plus") == null ? 1 : counts.get("Basic-Plus") + 1);
        });
        uniStats.setAnbieterBasicAnzahl(counts.getOrDefault("Basic",0));
        uniStats.setAnbieterBasicPlusAnzahl(counts.getOrDefault("Basic-Plus",0));
        uniStats.setAnbieterPlusAnzahl(counts.getOrDefault("Plus",0));
        uniStats.setAnbieterPremiumAnzahl(counts.getOrDefault("Premium",0));
        uniStats.setAnbieterPremiumSponsorenAnzahl(counts.getOrDefault("Sponsor",0));

        long umsatzBasicPlus =uniStats.getAnbieterBasicPlusAnzahl()*200;
        long umsatzPlus =uniStats.getAnbieterPlusAnzahl()*1000;
        long umsatzPremium =uniStats.getAnbieterPremiumAnzahl()*1500;
        long umsatzSponsor = uniStats.getAnbieterPremiumSponsorenAnzahl()*3000;

        uniStats.setUmsatz(umsatzBasicPlus+umsatzPlus+umsatzPremium+umsatzSponsor);

        return uniStats;
    }

}








