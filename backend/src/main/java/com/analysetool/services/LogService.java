package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.DashConfig;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.SystemVariable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
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

import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@Service
public class LogService {



    private final PostRepository postRepository;
    private final PostStatsRepository statsRepo;
    private final TagStatRepository tagStatRepo;
    private final WpTermRelationshipsRepository termRelRepo;
    private final WPTermRepository termRepo;
    private final WpTermTaxonomyRepository termTaxRepo;
    private final WPUserRepository wpUserRepo;
    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;
    private final UserStatsRepository userStatsRepo;

    private final CommentsRepository commentRepo;
    private final SysVarRepository sysVarRepo;
    private BufferedReader br;
    private String path = "";
    //^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}) regex für ip matching
    private final String BlogSSPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /blog/(\\S+)/.*s=(\\S+)\".*"; //search +1, view +1,(bei match) vor blog view pattern
    private final String ArtikelSSPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /artikel/(\\S+)/.*s=(\\S+)\".*";//search +1, view +1,(bei match) vor artikel view pattern
    //private String BlogViewPattern = "^.*GET \/blog\/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
    private final String WhitepaperSSPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /whitepaper/(\\S+)/.*s=(\\S+)\".*";
    private final String BlogViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /blog/(\\S+)/";
    private final String RedirectPattern = "/.*GET .*goto=.*\"(https?:/.*/(artikel|blog|news)/(\\S*)/)";
    private final String RedirectUserPattern ="/.*GET .*goto=.*\"(https?:/.*/(user)/(\\S*)/)";
    private final String UserViewPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /user/(\\S+)/";

    //Blog view +1 bei match
    //private String ArtikelViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*GET /artikel/(\\S+)";//Artikel view +1 bei match
    private final String ArtikelViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /artikel/(\\S+)/";
    private final String PresseViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /news/(\\S+)/";
    //private String PresseSSViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /pressemitteilung/(\\S+)/.*s=(\\S+)";
    private final String PresseSSViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /news/(\\S+)/.*s=(\\S+)\".*";

    private final String WhitepaperViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /whitepaper/(\\S+)/";
    private final String PodcastPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /its-couch/";


    // private String ReffererPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET.*\"https?:/.*/artikel|blog|pressemitteilung/(\\S*)/";
    private final String ReffererPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET.*\"(https?:/.*/(artikel|blog|pressemitteilung)/(\\S*)/)";
    // private String SearchPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /s=(\\S+) ";
   private final String SearchPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /\\?s=(\\S+) .*";

   private final String prePattern = "^([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}).*\\[([\\d]{2}/[a-zA-Z]{3}/[\\d]{4}:[\\d]{2}:[\\d]{2}:[\\d]{2})";


    Pattern articleViewPattern = Pattern.compile(ArtikelViewPattern);
    Pattern articleSearchSuccessPattern = Pattern.compile(ArtikelSSPattern);
    Pattern blogViewPattern = Pattern.compile(BlogViewPattern);
    Pattern blogSearchSuccessPattern = Pattern.compile(BlogSSPattern);
    Pattern redirectPattern = Pattern.compile(RedirectPattern);
    Pattern userViewPattern = Pattern.compile(UserViewPattern);
    Pattern newsViewPattern = Pattern.compile(PresseViewPatter);
    Pattern newsSearchSuccessPattern = Pattern.compile(PresseSSViewPatter);
    Pattern userRedirectPattern = Pattern.compile(RedirectUserPattern);
    Pattern searchPattern = Pattern.compile(SearchPattern);
    Pattern referPattern = Pattern.compile(ReffererPattern);
    Pattern patternPodcast = Pattern.compile(PodcastPattern);
    Pattern patternWhitepaperView = Pattern.compile(WhitepaperViewPattern);
    Pattern patternWhitepaperSearchSuccess = Pattern.compile(WhitepaperSSPattern);
    Pattern patternPreMatch = Pattern.compile(prePattern);
    private String lastLine = "";
    private int lineCounter = 0;
    private int lastLineCounter = 0;
    private boolean liveScanning ;


    //Toter Code wird bis zum fertigen ConfigReader hier gelassen.
    //private String Pfad=Application.class.getClassLoader().getResource("access.log").getPath();
    //private String Pfad = Paths.get(Application.class.getClassLoader().getResource("access.log").toURI()).toString();
    private final DashConfig config;
    private final String Pfad;

    private final Calendar kalender = Calendar.getInstance();
    private final int aktuellesJahr = kalender.get(Calendar.YEAR);
    @Autowired
    private SearchStatsRepository searchStatRepo;

    private final HashMap<String,ArrayList<LocalDateTime>> userViewTimes= new HashMap<>();
    private final HashMap<String, Integer> userViews = new HashMap<>();
    private final HashMap<String, Integer> impressions = new HashMap<>();
    @Autowired
    private universalStatsRepository uniRepo;

    @Autowired
    private UniqueUserRepository uniqueUserRepo;


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
    public void init() throws IOException, ParseException {
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
        /*        if(!liveScanning){SystemVariabeln.setLastLineCount(0);

                }*/
                SystemVariabeln.setLogDate(getCreationDateOfAccessLog(Pfad));
           // }

        }


        run(liveScanning,Pfad, SystemVariabeln);

        updateLetterCountForAll();

    }
    @Scheduled(cron = "0 0 * * * *") //einmal die Stunde
    //@Scheduled(cron = "0 */2 * * * *") //alle 2min
    public void runScheduled() throws IOException, ParseException {
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
                //if(!liveScanning){SystemVariabeln.setLastLineCount(0);}
                SystemVariabeln.setLogDate(getCreationDateOfAccessLog(Pfad));


        }


        run(liveScanning,Pfad, SystemVariabeln);
        updateLetterCountForAll();
    }

    public void run(boolean liveScanning, String path,SysVar SystemVariabeln) throws IOException, ParseException {
        this.liveScanning = liveScanning;
        this.path = path;
        if(!liveScanning){

            String pathOfOldLog = "/var/log/nginx/access.log-" + getDay(0) + ".gz";
            FileInputStream fileInputStream = new FileInputStream(pathOfOldLog);
            GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
            br = new BufferedReader(inputStreamReader);
            findAMatch(SystemVariabeln);
            SystemVariabeln.setLastLineCount(0);
        }
        lastLineCounter=SystemVariabeln.getLastLineCount();
        lastLine = SystemVariabeln.getLastLine();
        lineCounter = 0;
        try  {
            br = new BufferedReader(new FileReader(path));
            findAMatch(SystemVariabeln);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //setUniversalStats(SystemVariabeln);
        SystemVariabeln.setLastLineCount(lastLineCounter);
        SystemVariabeln.setLastLine(lastLine);
        updateWordCountForAll();
        saveStatsToDatabase();

        sysVarRepo.save(SystemVariabeln);
    }

    public void findAMatchDeprecated() throws IOException {
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
                Matcher matched_articleView = articleViewPattern.matcher(line);

                if (matched_articleView.find()) {
                    Matcher matched_articleSearchSuccess = articleSearchSuccessPattern.matcher(line);

                    foundPattern = true;
                    if (matched_articleSearchSuccess.find()) {
                        // Do something with the matched 1.2 patterns
                        //System.out.println(line+"SEARCH FOUND");
                        processLine(line,"articleSearchSuccess",matched_articleSearchSuccess);
                        foundPattern = true;
                    } else {//1.1 matched
                        //System.out.println(line+"NO SEARCH");
                        processLine(line,"articleView",matched_articleView);
                        foundPattern = true;
                    }
                } else {
                    Matcher matched_blogView = blogViewPattern.matcher(line);

                    if (matched_blogView.find()) {
                        Matcher matched_blogSearchSuccess = blogSearchSuccessPattern.matcher(line);

                        if (matched_blogSearchSuccess.find()) {
                            // Do something with the matched 2.2 patterns
                            processLine(line, "blogSearchSuccess", matched_blogSearchSuccess);
                            foundPattern = false;
                           // System.out.println(line+" SEARCH FOUND");
                        } else {
                            //2.1 match
                            processLine(line, "blogView", matched_blogView);
                            foundPattern = false;
                           // System.out.println(line+" NO SEARCH");
                        }
                    }else {
                        Matcher matched_newsView = newsViewPattern.matcher(line);

                        if (matched_newsView.find()) {
                            System.out.println("TEST NEWS GEFUNDEN");
                            Matcher matched_newsSearchSuccess = newsSearchSuccessPattern.matcher(line);

                            if (matched_newsSearchSuccess.find()) {
                                System.out.println("TEST SEARCHSUCCESS GEFUNDEN");
                                // Do something with the matched 2.2 patterns
                                processLine(line, "newsSearchSuccess", matched_newsSearchSuccess);
                                foundPattern = false;
                                // System.out.println(line+" SEARCH FOUND");
                            } else {
                                //2.1 match
                                processLine(line, "newsView", matched_newsView);
                                foundPattern = false;
                                // System.out.println(line+" NO SEARCH");
                            }
                        } else {
                            Matcher matched_whitepaperView = patternWhitepaperView.matcher(line);

                            if(matched_whitepaperView.find()) {
                                Matcher matched_whitepaperSearchSuccess = patternWhitepaperSearchSuccess.matcher(line);

                                if(matched_whitepaperSearchSuccess.find()) {
                                    processLine(line, "whitepaperSearchSuccess", matched_whitepaperSearchSuccess);
                                } else {
                                    processLine(line, "whitepaperView", matched_whitepaperView);
                                }
                            } else {
                                Matcher matched_podcastView = patternPodcast.matcher(line);

                                if(matched_podcastView.find()) {
                                    //ToDo maybe implement SearchSuccess if applicable
                                    processLine(line, "podcastView", matched_podcastView);
                                }
                            }
                        }
                    }
                }

            Matcher matched_redirect = redirectPattern.matcher(line);
            if(matched_redirect.find()){
                processLine(line,"redirect",matched_redirect);
                }
            Matcher matched_userView = userViewPattern.matcher(line);
            if(matched_userView.find()){
                processLine(line,"userView",matched_userView);
            }
            Matcher matched_userRedirect = userRedirectPattern.matcher(line);
            if(matched_userRedirect.find()){
                processLine(line,"userViewRedirect",matched_userView);
            }
            Matcher matched_searchPattern = searchPattern.matcher(line);
            if(matched_searchPattern.find()){
                processLine(line,"search",matched_searchPattern);
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


    public void findAMatch(SysVar sysVar) throws IOException, ParseException {
        String line;
        boolean foundPattern = false;

        long besucherTotal = 0;
        long clicksTotal = 0;
        Map<String, Map<String, Map<String, Long>>> viewsByLoc = new HashMap<>();
        Map<String, Long> viewsByH = new HashMap<>();
        String dateString = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE);
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);



        while ((line = br.readLine()) != null ) {

            Matcher pre_Matched = patternPreMatch.matcher(line);

            if (pre_Matched.find()) {
                // Erstellen von Datumsobjekten für den Aufruf und den letzten Aufruf.
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss");
                LocalDateTime dateLog = LocalDateTime.from(dateFormatter.parse(pre_Matched.group(2)));
                LocalDateTime dateLastRead = LocalDateTime.from(dateFormatter.parse(sysVar.getLastTimeStamp()));

                String ip = pre_Matched.group(1);

                try {
                    viewsByLoc = uniRepo.getViewsByLocationByDate(date);
                    viewsByH = uniRepo.getViewsPerHourByDate(date);
                } catch (Exception ignored) {
                }

                //Generate Universal Stats
                clicksTotal++;
                if(isUniqueView(ip, dateLog)) besucherTotal++;
                setViewsByLocation(ip, viewsByLoc);
                erhoeheViewsPerHour2(viewsByH, dateLog.toLocalTime());


                if (dateLog.isAfter(dateLastRead) || dateLog.isEqual(dateLastRead)) {
                    System.out.println("Line war spät genug");
                    sysVar.setLastTimeStamp(dateFormatter.format(dateLog));
                    Matcher matched_articleView = articleViewPattern.matcher(line);

                    if (matched_articleView.find()) {
                        Matcher matched_articleSearchSuccess = articleSearchSuccessPattern.matcher(line);

                        foundPattern = true;
                        if (matched_articleSearchSuccess.find()) {
                            // Do something with the matched 1.2 patterns
                            //System.out.println(line+"SEARCH FOUND");
                            processLine(line, "articleSearchSuccess", matched_articleSearchSuccess);
                        } else {//1.1 matched
                            //System.out.println(line+"NO SEARCH");
                            processLine(line, "articleView", matched_articleView);
                        }
                        foundPattern = true;
                    } else {
                        Matcher matched_blogView = blogViewPattern.matcher(line);

                        if (matched_blogView.find()) {
                            Matcher matched_blogSearchSuccess = blogSearchSuccessPattern.matcher(line);

                            if (matched_blogSearchSuccess.find()) {
                                // Do something with the matched 2.2 patterns
                                processLine(line, "blogSearchSuccess", matched_blogSearchSuccess);
                                // System.out.println(line+" SEARCH FOUND");
                            } else {
                                //2.1 match
                                processLine(line, "blogView", matched_blogView);
                                // System.out.println(line+" NO SEARCH");
                            }
                            foundPattern = false;
                        } else {
                            Matcher matched_newsView = newsViewPattern.matcher(line);

                            if (matched_newsView.find()) {
                                System.out.println("TEST NEWS GEFUNDEN");
                                Matcher matched_newsSearchSuccess = newsSearchSuccessPattern.matcher(line);

                                if (matched_newsSearchSuccess.find()) {
                                    System.out.println("TEST SEARCHSUCCESS GEFUNDEN");
                                    // Do something with the matched 2.2 patterns
                                    processLine(line, "newsSearchSuccess", matched_newsSearchSuccess);
                                    // System.out.println(line+" SEARCH FOUND");
                                } else {
                                    //2.1 match
                                    processLine(line, "newsView", matched_newsView);
                                    // System.out.println(line+" NO SEARCH");
                                }
                                foundPattern = false;
                            } else {
                                Matcher matched_whitepaperView = patternWhitepaperView.matcher(line);

                                if (matched_whitepaperView.find()) {
                                    Matcher matched_whitepaperSearchSuccess = patternWhitepaperSearchSuccess.matcher(line);

                                    if (matched_whitepaperSearchSuccess.find()) {
                                        processLine(line, "whitepaperSearchSuccess", matched_whitepaperSearchSuccess);
                                    } else {
                                        processLine(line, "whitepaperView", matched_whitepaperView);
                                    }
                                } else {
                                    Matcher matched_podcastView = patternPodcast.matcher(line);

                                    if (matched_podcastView.find()) {
                                        //ToDo maybe implement SearchSuccess if applicable
                                        processLine(line, "podcastView", matched_podcastView);
                                    }
                                }
                            }
                        }
                    }

                    Matcher matched_redirect = redirectPattern.matcher(line);
                    if (matched_redirect.find()) {
                        processLine(line, "redirect", matched_redirect);
                    }
                    Matcher matched_userView = userViewPattern.matcher(line);
                    if (matched_userView.find()) {
                        processLine(line, "userView", matched_userView);
                    }
                    Matcher matched_userRedirect = userRedirectPattern.matcher(line);
                    if (matched_userRedirect.find()) {
                        processLine(line, "userViewRedirect", matched_userView);
                    }
                    Matcher matched_searchPattern = searchPattern.matcher(line);
                    if (matched_searchPattern.find()) {
                        processLine(line, "search", matched_searchPattern);
                    }
                }

                UniversalStats uniStats = null;
                if(uniRepo.findByDatum(date).isPresent()) {
                    uniStats = uniRepo.findByDatum(date).get();
                }

                System.out.println("Er kam bis VOR UniStats");
                uniStats.setBesucherAnzahl(besucherTotal);
                uniStats.setTotalClicks(clicksTotal);
                uniStats.setViewsByLocation(viewsByLoc);
                uniStats.setViewsPerHour(viewsByH);
                uniStats.setDatum(date);
                uniStats.setAnbieterProfileAnzahl(wpUserRepo.count());
                uniStats = setNewsArticelBlogCountForUniversalStats(date,uniStats);
                uniStats = setAccountTypeAllUniStats(uniStats);

                uniRepo.save(uniStats);

                System.out.println("Er kam bis NACH UniStats");




            } else {
                System.out.println(line);
            }
        }

    }


    public void processLine(String line,String patternName,Matcher matcher){
        lastLine=line;
        if (patternName.equals("articleView")){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 1.1");
            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        if (patternName.equals("articleSearchSuccess")){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 1.2");
            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }
        if (patternName.equals("blogView")){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 2.1");
            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        if (patternName.equals("blogSearchSuccess")){
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING 2.2");
            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }

        if(patternName.equals("redirect")){
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

        if(patternName.equals("userView")){
            System.out.println(matcher.group(1).replace("+","-")+" PROCESSING 4");
            if(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).isPresent()){
                //updateUserStats(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).get());
                userViewOrImpression(matcher);
            }
        }
        if (patternName.equals("newsView")){

            System.out.println(postRepository.getIdByName(matcher.group(1)+" "+matcher.group(1))+" PROCESSING 5.1");



            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        if (patternName.equals("newsSearchSuccess")){

            System.out.println(postRepository.getIdByName(matcher.group(5))+matcher.group(6)+" PROCESSING 5.2");


            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }

        if(patternName.equals("userViewRedirect")){
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

            }
        }
        if(patternName.equals("search")){
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
        if(patternName.equals("thisWasUnreached")){

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

        if(patternName.equals("whitepaperSearchSuccess")) {
            //Stolen behaviour from articleSearchSuccess
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING Whitepaper with Search");
            updatePerformanceViewsSearchSuccess(matcher);
            updateViewsByLocation(matcher);
            updateSearchStats(matcher);
        }

        if(patternName.equals("whitepaperView")) {
            //Stolen behaviour from articleView
            System.out.println("TEST Gruppe1: "+ matcher.group(1)+" Gruppe2 "+matcher.group(2) + "Gruppe3: "+ matcher.group(3));
            System.out.println(postRepository.getIdByName(matcher.group(6))+matcher.group(6)+" PROCESSING Whitepaper View");
            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        /*
        if(patternName.equals("podcastView")) {
            System.out.println("PODCAST VIEW WOOOOHOOO");
            UpdatePerformanceAndViews(matcher);
            updateViewsByLocation(matcher);
        }
        */

        System.out.println("UPDATING USER ACTIVITY");
        updateUserActivity((long)3);
        updateUserStatsForAllUsers();
        lineCounter = 0 ;
        System.out.println("END OF LOG");
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

    /**
     *
     * @param ip
     * @param time
     * @return
     */
    public boolean isUniqueView(String ip, LocalDateTime time) {
        //This method sets the time in seconds after which a user once again becomes a unique user
        int uniqueTimer = 3600 * 24;

        boolean isUnique = false;
        UniqueUsers Row;

        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
        String ipHash = Hex.toHexString(hashBytes);


        if(uniqueUserRepo.getAllIPs().contains(ipHash)) {
            List<LocalDateTime> times = uniqueUserRepo.getAccessTimesByIPHash(ipHash);
            Row = uniqueUserRepo.findByIPHash(ipHash);
            Row.setAccess_time(time);

            if (Duration.between(times.get(times.size() - 1), time).getSeconds() <= uniqueTimer) {
                //This user already made a request not longer than uniqueTimer ago, so this is not unique
                isUnique = false;
            } else {
                //This user has not made a request in for uniqueTimer seconds, so they are counted as a unique again.
                isUnique = true;
            }


        } else {
            //The IP was not yet used in a request to our Server, so it is a unique user.
            Row = new UniqueUsers();
            Row.setIp_hashed(ipHash);
            Row.setAccess_time(time);
            isUnique = true;
        }

        uniqueUserRepo.save(Row);
        return isUnique;
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
    public LocalTime getLocalTimeFromMatcher(Matcher matcher){
        String logHourMinuteSecond = matcher.group(5);
        // Trenne Stunden, Minuten und Sekunden
        String[] timeParts = logHourMinuteSecond.split(":");
        String logHour = timeParts[0];
        String logMinute = timeParts[1];
        String logSecond = timeParts[2];

        LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));
        return logTime;
    }

    public LocalDate getLocalDateFromMatcher(Matcher matcher){
        String logDay = matcher.group(2);
        String logMonth = matcher.group(3);
        String logYear = matcher.group(4);
        // Konvertiere den Monat in eine Zahl
        int monthNumber = Integer.parseInt(getMonthNumber(logMonth));
        LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), monthNumber, Integer.parseInt(logDay));
        return logDate;
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

    public  Map<String, Long> erhoeheViewsPerHour2(PostStats stats, LocalTime logUhrzeit) {
        Map<String, Long> viewsPerHour = stats.getViewsPerHour();
        int stunde = logUhrzeit.getHour();
        long views = viewsPerHour.getOrDefault(Integer.toString(stunde), 0L);
        views++;
        viewsPerHour.put(Integer.toString(stunde), views);

        return viewsPerHour;
    }
    public static Map<String, Long> erhoeheViewsPerHour2(Map<String, Long> viewsPerHour, LocalTime logUhrzeit) {
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
        PostStats.setViewsLastYear(daily);
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
        Stats.setViewsLastYear(daily);
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

    public static Map<String, Map<String, Map<String, Long>>> setViewsByLocation(String ip, Map<String, Map<String, Map<String, Long>>> viewsByLocation) {
        try {
            String country = IPHelper.getCountryISO(ip);
            String region = IPHelper.getSubISO(ip);
            String city = IPHelper.getCityName(ip);

            // Standard Schlüssel setzen
            String countryKey = "global";
            String regionKey = "gesamt";

            if (country != null && !country.isEmpty()) {
                countryKey = country;
                regionKey = (region != null && !region.isEmpty()) ? region : "gesamt";
                if (!country.equals("DE")) {
                    regionKey = country;
                }

                // Map der Regionen für das gegebene Land holen oder erstellen
                Map<String, Map<String, Long>> regions = viewsByLocation.computeIfAbsent(countryKey, k -> new HashMap<>());

                // Map der Städte für die gegebene Region holen oder erstellen
                Map<String, Long> cities = regions.computeIfAbsent(regionKey, k -> new HashMap<>());

                // Aktualisieren der Anzahl der Views für die Stadt
                if (city != null && !city.isEmpty()) {
                    cities.merge(city, 1L, Long::sum);
                }

                // Aktualisieren der "gesamt" Views für die Region
                cities.merge("gesamt", 1L, Long::sum);

                // Aktualisieren der "gesamt" Views für das Land
                Map<String, Long> countryTotal = regions.computeIfAbsent("gesamt", k -> new HashMap<>());
                countryTotal.merge("gesamt", 1L, Long::sum);
            }

            // Aktualisieren der "gesamt" Views für Global
            Map<String, Map<String, Long>> globalTotal = viewsByLocation.computeIfAbsent("global", k -> new HashMap<>());
            globalTotal.computeIfAbsent("gesamt", k -> new HashMap<>()).merge("gesamt", 1L, Long::sum);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return viewsByLocation;
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

    public void setUniversalStats(SysVar SystemVariabeln) {
        int daysToLookBack = 9; // Anzahl der Tage, die zurückgeschaut werden sollen

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

                        UniversalStats uniStats = proccessLinesOfOldLog(new UniversalStats(date), bufferedReader,SystemVariabeln);
                        uniStats.setAnbieterProfileAnzahl(wpUserRepo.count());
                        //m
                        uniStats = setNewsArticelBlogCountForUniversalStats(date,uniStats);

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
                Date date = dateFormat.parse(getDay(0));
                if (uniRepo.findByDatum(date).isEmpty()) {
                    /*String pathOfOldLog = "/var/log/nginx/access.log-" + getLastDay() + ".gz";
                    FileInputStream fileInputStream = new FileInputStream(pathOfOldLog);
                    GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                    InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);*/

                    br = new BufferedReader(new FileReader(Pfad));
                    UniversalStats uniStats = proccessLinesOfOldLog(new UniversalStats(date), br,SystemVariabeln);
                    uniStats.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uniStats = setNewsArticelBlogCountForUniversalStats(uniStats);
                    uniStats = setAccountTypeAllUniStats(uniStats);
                    uniRepo.save(uniStats);
                } else {
                    br = new BufferedReader(new FileReader(Pfad));
                    UniversalStats uniStats = proccessLinesOfOldLog(uniRepo.findTop1ByOrderByDatumDesc(), br,SystemVariabeln);
                    uniStats.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uniStats = setNewsArticelBlogCountForUniversalStats(uniStats);
                    uniStats = setAccountTypeAllUniStats(uniStats);
                    uniRepo.save(uniStats);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public UniversalStats proccessLinesOfOldLog(UniversalStats uniStat, BufferedReader bufferedReader, SysVar Systemvariabeln) throws IOException {

        Map<String, Map<String, Map<String, Long>>> viewsByLocation = uniStat.getViewsByLocation();//hiermit weiter und mit setViewsByLocation
        Map<String,Long>viewsPerHour=uniStat.getViewsPerHour();

        ArrayList<String> uniqueIps = new ArrayList<>();
        String line;
        long allClicks = 0;
        if(uniStat.getTotalClicks()!=null){allClicks=uniStat.getTotalClicks();}
        Long Besucher = (long) 0;
        if(uniStat.getBesucherAnzahl()!=null){Besucher=uniStat.getBesucherAnzahl();}

        long lastLineCount = Systemvariabeln.getLastLineCount();
        long lineCount = 0;

        while ((line = bufferedReader.readLine()) != null) {

            while(lastLineCount>lineCount){
                bufferedReader.readLine();
                lineCount++;
            }

            Matcher matcher1_1 = articleViewPattern.matcher(line);

            if (matcher1_1.find()) {
                Matcher matcher1_2 = articleSearchSuccessPattern.matcher(line);


                if (matcher1_2.find()) {
                    // Do something with the matched 1.2 patterns
                    if(!uniqueIps.contains(hashIp(matcher1_2.group(1)))){uniqueIps.add(hashIp(matcher1_2.group(1)));
                        viewsByLocation=setViewsByLocation(matcher1_2.group(1),viewsByLocation);
                    }
                    LocalTime logtime = getLocalTimeFromMatcher(matcher1_2);
                    viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                    allClicks++;

                } else {//1.1 matched

                    if(!uniqueIps.contains(hashIp(matcher1_1.group(1)))){uniqueIps.add(hashIp(matcher1_1.group(1)));
                        viewsByLocation=setViewsByLocation(matcher1_1.group(1),viewsByLocation);
                    }
                    LocalTime logtime = getLocalTimeFromMatcher(matcher1_1);
                    viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                    allClicks++;

                }
            }
            // }
            else {
                Matcher matcher2_1 = blogViewPattern.matcher(line);

                if (matcher2_1.find()) {
                    Matcher matcher2_2 = blogSearchSuccessPattern.matcher(line);

                    if (matcher2_2.find()) {
                        // Do something with the matched 2.2 patterns

                        if(!uniqueIps.contains(hashIp(matcher2_2.group(1)))){uniqueIps.add(hashIp(matcher2_2.group(1))); viewsByLocation=setViewsByLocation(matcher2_2.group(1),viewsByLocation);
                        }
                        LocalTime logtime = getLocalTimeFromMatcher(matcher2_2);
                        viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                        allClicks++;

                    } else {
                        //2.1 match

                        if(!uniqueIps.contains(hashIp(matcher2_1.group(1)))){uniqueIps.add(hashIp(matcher2_1.group(1))); viewsByLocation=setViewsByLocation(matcher2_1.group(1),viewsByLocation);
                        }
                        LocalTime logtime = getLocalTimeFromMatcher(matcher2_1);
                        viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                        allClicks++;

                    }
                } else {
                    Matcher matcher5_1 = newsViewPattern.matcher(line);

                    if (matcher5_1.find()) {

                        Matcher matcher5_2 = newsSearchSuccessPattern.matcher(line);
                        if (matcher5_2.find()) {
                            if(!uniqueIps.contains(hashIp(matcher5_2.group(1)))){uniqueIps.add(hashIp(matcher5_2.group(1))); viewsByLocation=setViewsByLocation(matcher5_2.group(1),viewsByLocation);
                            }
                            LocalTime logtime = getLocalTimeFromMatcher(matcher5_2);
                            viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                            allClicks++;
                        } else {
                            if(!uniqueIps.contains(hashIp(matcher5_1.group(1)))){uniqueIps.add(hashIp(matcher5_1.group(1))); viewsByLocation=setViewsByLocation(matcher5_1.group(1),viewsByLocation);
                            }
                            LocalTime logtime = getLocalTimeFromMatcher(matcher5_1);
                            viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                            allClicks++;
                        }
                    }
                }
            }

            Matcher matcher3 = redirectPattern.matcher(line);
            if (matcher3.find()) {
                if(!uniqueIps.contains(hashIp(matcher3.group(1)))){uniqueIps.add(hashIp(matcher3.group(1))); viewsByLocation=setViewsByLocation(matcher3.group(1),viewsByLocation);
                }
                LocalTime logtime = getLocalTimeFromMatcher(matcher3);
                viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                allClicks++;
            }
            Matcher matcher4 = userViewPattern.matcher(line);
            if (matcher4.find()) {
                if(!uniqueIps.contains(hashIp(matcher4.group(1)))){uniqueIps.add(hashIp(matcher4.group(1))); viewsByLocation=setViewsByLocation(matcher4.group(1),viewsByLocation);
                }
                LocalTime logtime = getLocalTimeFromMatcher(matcher4);
                viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                allClicks++;
            }
            Matcher matcher4_2 = userRedirectPattern.matcher(line);
            if (matcher4_2.find()) {
                if(!uniqueIps.contains(hashIp(matcher4_2.group(1)))){uniqueIps.add(hashIp(matcher4_2.group(1))); viewsByLocation=setViewsByLocation(matcher4_2.group(1),viewsByLocation);
                }
                LocalTime logtime = getLocalTimeFromMatcher(matcher4_2);
                viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                allClicks++;
            }
            Matcher matcher6_1 = searchPattern.matcher(line);
            if (matcher6_1.find()) {

                if(!uniqueIps.contains(hashIp(matcher6_1.group(1)))){uniqueIps.add(hashIp(matcher6_1.group(1))); viewsByLocation=setViewsByLocation(matcher6_1.group(1),viewsByLocation);
                }
                LocalTime logtime = getLocalTimeFromMatcher(matcher6_1);
                viewsPerHour=erhoeheViewsPerHour2(viewsPerHour,logtime);
                allClicks++;

            }
        }

        uniStat.setBesucherAnzahl((long) uniqueIps.size()+Besucher);
        uniStat.setTotalClicks(allClicks);
        uniStat.setViewsByLocation(viewsByLocation);
        uniStat.setViewsPerHour(viewsPerHour);
        return uniStat;
    }

    public UniversalStats setNewsArticelBlogCountForUniversalStats(UniversalStats uniStats){

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


    public UniversalStats setNewsArticelBlogCountForUniversalStats(Date dateStr, UniversalStats uniStats) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");

        String givenDateStr = sdf.format(dateStr);  // Konvertiere das eingegebene Date-Objekt in einen String

        List<Post> posts = postRepository.findAllUserPosts();

        long artikelCounter = 0;
        long newsCounter = 0;
        long blogCounter = 0;

        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();

        for (Post post : posts) {
            LocalDateTime postDateTime = post.getDate(); // Nehmen wir an, das ist vom Typ LocalDateTime
            String postDateStr = postDateTime.format(dtf); // Verwende DateTimeFormatter
            if (postDateStr.compareTo(givenDateStr) <= 0) {
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


    public UniversalStats setAccountTypeAllUniStats(UniversalStats uniStats){
        HashMap<String, Integer> counts = new HashMap<>();

        wpUserMetaRepository.getWpCapabilities().forEach(s -> {

            if (s.contains("um_anbieter"))
                counts.put("Anbieter", counts.get("Anbieter") == null ? 1 : counts.get("Anbieter") + 1);
            if (s.contains("um_basis-anbieter")  && !s.contains("plus"))
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
        uniStats.setAnbieter_abolos_anzahl(counts.getOrDefault("Anbieter", 0));
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








