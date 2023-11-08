package com.analysetool.services;

import com.analysetool.api.PostController;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.DashConfig;
import com.analysetool.util.IPHelper;
import com.analysetool.util.MapHelper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private UniqueUserRepository uniqueUserRepo;

    @Autowired
    private UniversalCategoriesDLCRepository universalCategoriesDLCRepo;

    @Autowired
    private IPsByPostRepository iPsByPostRepository;

    @Autowired
    private IPsByUserRepository iPsByUserRepository;

    @Autowired
    private ClicksByCountryRepository clicksByCountryRepo;

    @Autowired
    private ClicksByBundeslandRepository clicksByBundeslandRepo;

    @Autowired
    private PostGeoRepository postGeoRepo;

    @Autowired
    private UserGeoRepository userGeoRepo;

    private final CommentsRepository commentRepo;
    private final SysVarRepository sysVarRepo;

    private BufferedReader br;
    private String path = "";
    //^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}) regex für ip matching
    private final String BlogSSPattern = "^.*GET /blog/(\\S+)/.*s=(\\S+)\".*"; //search +1, view +1,(bei match) vor blog view pattern
    private final String ArtikelSSPattern = "^.*GET /artikel/(\\S+)/.*s=(\\S+)\".*";//search +1, view +1,(bei match) vor artikel view pattern
    //private String BlogViewPattern = "^.*GET \/blog\/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
    private final String WhitepaperSSPattern = "^.*GET /whitepaper/(\\S+)/.*s=(\\S+)\".*";
    private final String BlogViewPattern = "^.*GET /blog/(\\S+)/";
    private final String RedirectPattern = "/.*GET .*goto=.*\"(https?:/.*/(artikel|blog|news)/(\\S*)/)";
    private final String RedirectUserPattern ="/.*GET .*goto=.*\"(https?:/.*/(user)/(\\S*)/)";
    private final String UserViewPattern="^.*GET /user/(\\S+)/";

    //Blog view +1 bei match
    //private String ArtikelViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*GET /artikel/(\\S+)";//Artikel view +1 bei match
    private final String ArtikelViewPattern = "^.*GET /artikel/(\\S+)/";

    private final String ratgeberViewPattern = "^.*GET /ratgeber/(\\S+)/";
    private final String NewsViewPatter = "^.*GET /news/(\\S+)/";
    //private String PresseSSViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /pressemitteilung/(\\S+)/.*s=(\\S+)";
    private final String PresseSSViewPatter = "^.*GET /news/(\\S+)/.*s=(\\S+)\".*";

    private final String WhitepaperViewPattern = "^.*GET /whitepaper/(\\S+)/";
    private final String PodcastPattern = "^.*GET /its-couch/";


    // private String ReffererPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET.*\"https?:/.*/artikel|blog|pressemitteilung/(\\S*)/";
    private final String ReffererPattern="^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET.*\"(https?:/.*/(artikel|blog|pressemitteilung)/(\\S*)/)";
    // private String SearchPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /s=(\\S+) ";
   private final String SearchPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /\\?s=(\\S+) .*";

   private final String prePattern = "^([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}).*\\[([\\d]{2}/[a-zA-Z]{3}/[\\d]{4}:[\\d]{2}:[\\d]{2}:[\\d]{2}).*\\\"(.*)\\\".\\{(...)\\}.*\\[(.*)\\]";

   private final String mainPage = "^.*GET /(startseite|( )+)";

   private final String ueber = "^.*GET /ueber-uns/";

   private final String impressum = "^.*GET /impressum/";

    private final String agbs = "^.*GET /agbs/";

    private final String datenschutzerklaerung = "^.*GET /datenschutzerklaerung/";

    private final String preisliste = "^.*GET /preisliste/";

    private final String partner = "^.*GET /unsere-partner-und-sponsoren/";

    private final String newsletter = "^.*GET /newsletter/";

    private final String image = "^.*GET /ziel-des-marktplatz-it-sicherheit/";


    Pattern articleViewPattern = Pattern.compile(ArtikelViewPattern);
    Pattern articleSearchSuccessPattern = Pattern.compile(ArtikelSSPattern);
    Pattern blogViewPattern = Pattern.compile(BlogViewPattern);
    Pattern blogSearchSuccessPattern = Pattern.compile(BlogSSPattern);
    Pattern redirectPattern = Pattern.compile(RedirectPattern);
    Pattern userViewPattern = Pattern.compile(UserViewPattern);
    Pattern newsViewPattern = Pattern.compile(NewsViewPatter);
    Pattern newsSearchSuccessPattern = Pattern.compile(PresseSSViewPatter);
    Pattern userRedirectPattern = Pattern.compile(RedirectUserPattern);
    Pattern searchPattern = Pattern.compile(SearchPattern);
    Pattern patternPodcast = Pattern.compile(PodcastPattern);
    Pattern patternWhitepaperView = Pattern.compile(WhitepaperViewPattern);
    Pattern patternWhitepaperSearchSuccess = Pattern.compile(WhitepaperSSPattern);
    Pattern patternPreMatch = Pattern.compile(prePattern);
    Pattern reffererPattern=Pattern.compile(ReffererPattern);
    Pattern mainPagePattern = Pattern.compile(mainPage);
    Pattern ueberPattern = Pattern.compile(ueber);
    Pattern impressumPattern = Pattern.compile(impressum);
    Pattern agbsPattern = Pattern.compile(agbs);
    Pattern datenschutzerklaerungPattern = Pattern.compile(datenschutzerklaerung);
    Pattern preislistePattern = Pattern.compile(preisliste);
    Pattern partnerPattern = Pattern.compile(partner);
    Pattern newsletterPattern = Pattern.compile(newsletter);
    Pattern imagePattern = Pattern.compile(image);
    Pattern ratgeberPattern = Pattern.compile(ratgeberViewPattern);


    private String lastLine = "";
    private int lineCounter = 0;
    private int lastLineCounter = 0;
    private boolean liveScanning ;

    //Set User-Agents that shouldn't be counted as click
    String[] blacklistUserAgents = {
    "bot",
    "spider",
    "crawl",
    "parse",
    "fetch",
    "Zabbix",
    "Facebook",
    "Frog",
    "Majestic",
    "Apache",
    "Scrape",
    "Scrapy",
    "HTTrack",
    "Moreover",
    "Sitesucker",
    "Webz.io",
    "-",
    "Index",
    "Go-http-client",
    "Iframely"
    };

    ArrayList<String> blacklistResponseCodes = new ArrayList<>();


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
    private UniversalStatsHourlyRepository uniHourlyRepo;
    @Autowired
    private PostController postController;


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


    public void findAMatch(SysVar sysVar) throws IOException, ParseException {
        String line;

        int totalClicks = 0;
        int internalClicks = 0;
        int viewsArticle = 0;
        int viewsNews = 0;
        int viewsBlog = 0;
        int viewsPodcast = 0;
        int viewsWhitepaper = 0;
        int viewsRatgeber = 0;
        int viewsMain = 0;
        int viewsUeber = 0;
        int viewsAGBS = 0;
        int viewsImpressum = 0;
        int viewsPreisliste = 0;
        int viewsPartner = 0;
        int viewsDatenschutz = 0;
        int viewsNewsletter = 0;
        int viewsImage = 0;


        int uniqueUsers = 0;
        int userArticle = 0;
        int userNews = 0;
        int userBlog = 0;
        int userPodcast = 0;
        int userWhitepaper = 0;
        int userRatgeber = 0;
        int userMain = 0;
        int userUeber = 0;
        int userAGBS = 0;
        int userImpressum = 0;
        int userPreisliste = 0;
        int userPartner = 0;
        int userDatenschutz = 0;
        int userNewsletter = 0;
        int userImage = 0;

        int serverErrors = 0;


        Map<String, Map<String, Map<String, Long>>> viewsByLocation = new HashMap<>();
        Map<String,Long> viewsByHour = new HashMap<>();

        String last_ip = null;
        String last_request = null;

        while ((line = br.readLine()) != null ) {
            UniqueUser user = null;

            Matcher pre_Matched = patternPreMatch.matcher(line);

            if (pre_Matched.find()) {
                //Schreibe Gruppen in lesbare Variablen.
                String ip = pre_Matched.group(1);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss");
                LocalDateTime dateLog = LocalDateTime.from(dateFormatter.parse(pre_Matched.group(2)));
                String request = pre_Matched.group(3);
                String responseCode = pre_Matched.group(4);
                String userAgent = pre_Matched.group(5);

                //Bilde filternde Variablen aus der Zeile.
                LocalDateTime dateLastRead = LocalDateTime.from(dateFormatter.parse(sysVar.getLastTimeStamp()));
                //Filter für Request-Types.
                boolean isDevAccess = request.contains("/api/")
                        || request.contains("/wp-content") || request.contains("/wp-includes")
                        || request.contains("/wp-admin") || request.contains("/robots.txt");
                //Filter für IPs.
                boolean isUnique = (uniqueUserRepo.findByIP(ip) == null);
                boolean isInternal = ip.startsWith("10.") || ip.startsWith("127.");
                //Filter für Response Codes.
                boolean isSuccessfulRequest = Integer.parseInt(responseCode) >= 200 && Integer.parseInt(responseCode) < 400;
                boolean isServerError = Integer.parseInt(responseCode) >= 500;
                //Filter for Spam
                boolean isSpam = false;
                if(!(last_ip == null || last_request == null)) {
                    if (request.equals(last_request) && ip.equals(last_ip)) {
                        isSpam = true;
                    }
                }

                //Schaue, ob der UserAgent auf der Blacklist steht.
                boolean isBlacklisted = false;
                for (String item : blacklistUserAgents) {
                    isBlacklisted = userAgent.matches("(?i).*" + item + ".*");
                }

                if(isBlacklisted) {
                    System.out.println(request + userAgent);
                }

                //Falls keiner der Filter zutrifft und der Teil des Logs noch nicht gelesen wurde, behandle die Zeile.
                if ((dateLog.isAfter(dateLastRead) || dateLog.isEqual(dateLastRead)) && !isDevAccess && !isInternal && !isServerError && !isBlacklisted && isSuccessfulRequest && !request.contains("securitynews") && !isSpam) {

                    sysVar.setLastTimeStamp(dateFormatter.format(dateLog));
                    setViewsByLocation(ip, viewsByLocation);
                    erhoeheViewsPerHour2(viewsByHour, dateLog.toLocalTime());

                    //erhöhe Clicks und Besucher, falls anwendbar
                    totalClicks++;
                    if(isUnique) {
                        uniqueUsers++;
                        user = new UniqueUser();
                        user.setGlobal(1);
                        user.setIp(ip);
                        user.setCategory("global");
                        uniqueUserRepo.save(user);
                    } else {
                        user = uniqueUserRepo.findByIP(ip);
                        user.setGlobal(1);
                        uniqueUserRepo.save(user);
                    }

                    //Does it match an article-type?
                    Matcher matched_articleView = articleViewPattern.matcher(request);
                    Matcher matched_articleSearchSuccess = articleSearchSuccessPattern.matcher(request);

                    //Does it match a blog-type?
                    Matcher matched_blogView = blogViewPattern.matcher(request);
                    Matcher matched_blogSearchSuccess = blogSearchSuccessPattern.matcher(request);

                    //Does it match a news-type?
                    Matcher matched_newsView = newsViewPattern.matcher(request);
                    Matcher matched_newsSearchSuccess = newsSearchSuccessPattern.matcher(request);

                    //Does it match a whitepaper-type?
                    Matcher matched_whitepaperView = patternWhitepaperView.matcher(request);
                    Matcher matched_whitepaperSearchSuccess = patternWhitepaperSearchSuccess.matcher(request);

                    //Does it match podcast-type?
                    Matcher matched_podcastView = patternPodcast.matcher(request);

                    //Does it match the main-page-type?
                    Matcher matched_main_page = mainPagePattern.matcher(request);

                    //Does it match the ueber-uns-type?
                    Matcher matched_ueber = ueberPattern.matcher(request);

                    //Does it match the impressum-type?
                    Matcher matched_impressum = impressumPattern.matcher(request);

                    //Does it match the preisliste-type?
                    Matcher matched_preisliste = preislistePattern.matcher(request);

                    //Does it match the partner-type?
                    Matcher matched_partner = partnerPattern.matcher(request);

                    //Does it match the datenschutzerklaerung-type?
                    Matcher matched_datenschutz = datenschutzerklaerungPattern.matcher(request);

                    //Does it match the newsletter-type?
                    Matcher matched_newsletter = newsletterPattern.matcher(request);

                    //Does it match the image-film-type?
                    Matcher matched_image = imagePattern.matcher(request);

                    //Does it match the agb-type?
                    Matcher matched_AGBS = agbsPattern.matcher(request);

                    //Does it match user-view?
                    Matcher matched_userViews = userViewPattern.matcher(request);

                    //Does it match a ratgeber-view
                    Matcher matched_ratgeber = ratgeberPattern.matcher(request);

                    //Find out which pattern matched
                    String whatMatched = "";
                    Matcher patternMatcher = null;
                    if(matched_articleView.find()) {
                        whatMatched = "articleView";
                        patternMatcher = matched_articleView;
                    } else if(matched_articleSearchSuccess.find()) {
                        whatMatched = "articleSS";
                        patternMatcher = matched_articleSearchSuccess;
                    } else if(matched_blogView.find()) {
                        whatMatched = "blogView";
                        patternMatcher = matched_blogView;
                    } else if(matched_blogSearchSuccess.find()) {
                        whatMatched = "blogSS";
                        patternMatcher = matched_blogSearchSuccess;
                    } else if(matched_newsView.find()) {
                        whatMatched = "newsView";
                        patternMatcher = matched_newsView;
                    }else if(matched_newsSearchSuccess.find()) {
                        whatMatched = "newsSS";
                        patternMatcher = matched_newsSearchSuccess;
                    }else if(matched_whitepaperView.find()) {
                        whatMatched = "wpView";
                        patternMatcher = matched_whitepaperView;
                    } else if(matched_whitepaperSearchSuccess.find()) {
                        whatMatched = "wpSS";
                        patternMatcher = matched_whitepaperSearchSuccess;
                    } else if(matched_podcastView.find()) {
                        whatMatched = "podView";
                        patternMatcher = matched_podcastView;
                    } else if(matched_main_page.find()) {
                        whatMatched = "main";
                        patternMatcher = matched_main_page;
                    } else if(matched_ueber.find()) {
                        whatMatched = "ueber";
                        patternMatcher = matched_ueber;
                    } else if(matched_impressum.find()) {
                        whatMatched = "impressum";
                        patternMatcher = matched_impressum;
                    } else if(matched_preisliste.find()) {
                        whatMatched = "preisliste";
                        patternMatcher = matched_preisliste;
                    } else if(matched_partner.find()) {
                        whatMatched = "partner";
                        patternMatcher = matched_partner;
                    } else if(matched_datenschutz.find()) {
                        whatMatched = "datenschutz";
                        patternMatcher = matched_datenschutz;
                    } else if(matched_newsletter.find()) {
                        whatMatched = "newsletter";
                        patternMatcher = matched_newsletter;
                    } else if(matched_image.find()) {
                        whatMatched = "image";
                        patternMatcher = matched_image;
                    } else if(matched_AGBS.find()) {
                        whatMatched = "agb";
                        patternMatcher = matched_AGBS;
                    } else if(matched_userViews.find()) {
                        whatMatched = "userView";
                        patternMatcher = matched_userViews;
                    } else if(matched_ratgeber.find()) {
                        whatMatched = "ratgeber";
                        patternMatcher = matched_ratgeber;
                    }

                    switch (whatMatched) {
                        case "articleView", "articleSS" -> {
                            //Erhöhe Clicks für Artikel um 1.
                            viewsArticle++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userArticle++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("article");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getArticle() == 0) {
                                    userArticle++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setArticle(1);
                            uniqueUserRepo.save(user);
                        }

                        case "blogView", "blogSS" -> {
                            //Erhöhe Clicks für Blog um 1.
                            viewsBlog++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userBlog++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("blog");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getBlog() == 0) {
                                    userBlog++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setBlog(1);
                            uniqueUserRepo.save(user);
                        }

                        case "newsView", "newsSS" -> {
                            //Erhöhe Clicks für News um 1.
                            viewsNews++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userNews++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("news");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getNews() == 0) {
                                    userNews++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setNews(1);
                            uniqueUserRepo.save(user);
                        }

                        case "wpView", "wpSS" -> {
                            //Erhöhe Clicks für Whitepaper um 1.
                            viewsWhitepaper++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userWhitepaper++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("whitepaper");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getWhitepaper() == 0) {
                                    userWhitepaper++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setWhitepaper(1);
                            uniqueUserRepo.save(user);
                        }
                        case "podView" -> {
                            //Erhöhe Clicks für Podcast um 1.
                            viewsPodcast++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userPodcast++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("podcast");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getPodcast() == 0) {
                                    userPodcast++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setPodcast(1);
                            uniqueUserRepo.save(user);
                        }
                        case "main" -> {
                            viewsMain++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userMain++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("main");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getMain() == 0) {
                                    userMain++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setMain(1);
                            uniqueUserRepo.save(user);
                        }
                        case "ueber" -> {
                            //Erhöhe Clicks für Ueber-Uns um 1.
                            viewsUeber++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userUeber++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("ueber");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getUeber() == 0) {
                                    userUeber++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setUeber(1);
                            uniqueUserRepo.save(user);
                        }
                        case "impressum" -> {
                            //Erhöhe Clicks für Impressum um 1.
                            viewsImpressum++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userImpressum++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("impressum");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getImpressum() == 0) {
                                    userImpressum++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setImpressum(1);
                            uniqueUserRepo.save(user);
                        }
                        case "preisliste" -> {
                            //Erhöhe Clicks für Preisliste um 1.
                            viewsPreisliste++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userPreisliste++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("preisliste");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getPreisliste() == 0) {
                                    userPreisliste++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setPreisliste(1);
                            uniqueUserRepo.save(user);
                        }
                        case "partner" -> {
                            //Erhöhe Clicks für Partner um 1.
                            viewsPartner++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userPartner++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("partner");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getPartner() == 0) {
                                    userPartner++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setPartner(1);
                            uniqueUserRepo.save(user);
                        }
                        case "datenschutz" -> {
                            //Erhöhe Clicks für Datenschutzerkl. um 1.
                            viewsDatenschutz++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userDatenschutz++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("datenschutz");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getDatenschutz() == 0) {
                                    userDatenschutz++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setDatenschutz(1);
                            uniqueUserRepo.save(user);
                        }
                        case "newsletter" -> {
                            //Erhöhe Clicks für Newsletter um 1.
                            viewsNewsletter++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userNewsletter++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("newsletter");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getNewsletter() == 0) {
                                    userNewsletter++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setNewsletter(1);
                            uniqueUserRepo.save(user);
                        }
                        case "image" -> {
                            //Erhöhe Clicks für Image um 1.
                            viewsImage++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userImage++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("image");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getImage() == 0) {
                                    userImage++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setImage(1);
                            uniqueUserRepo.save(user);
                        }
                        case "agb" -> {
                            //Erhöhe Clicks für AGBS um 1.
                            viewsAGBS++;
                            //Wenn der User Unique ist, erstelle eine Zeile in UniqueUser.
                            if (isUnique) {
                                userAGBS++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("agbs");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getAgb() == 0) {
                                    userAGBS++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setAgb(1);
                            uniqueUserRepo.save(user);
                        }
                        case "userView" -> {
                            try {
                                updateUserStats(wpUserRepo.findByNicename(patternMatcher.group(1)).get().getId());
                            } catch (Exception e) {
                                System.out.println(patternMatcher.group(1));
                            }
                        }
                        case "ratgeber" -> {
                            //Erhöhe Clicks für Artikel um 1.
                            viewsRatgeber++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userRatgeber++;
                                user = uniqueUserRepo.findByIP(ip);
                                user.setCategory("ratgeber");
                                user.setIp(ip);
                            } else {
                                if (uniqueUserRepo.findByIP(ip).getRatgeber() == 0) {
                                    userRatgeber++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            user.setRatgeber(1);
                            uniqueUserRepo.save(user);
                        }
                        default -> System.out.println(line);
                    }

                    processLine(line, ip, whatMatched, dateLog, patternMatcher);
                    //A bunch of variables necessary to update UniStats

                }
                last_request = request;
                last_ip = ip;

                if(isServerError) {
                    serverErrors++;
                }
                if(isSpam && !isInternal) {
                    System.out.println("SPAM!!!: " + ip + " " + request + " " + userAgent);
                }
                if(isInternal) {
                    internalClicks++;
                }

            }

        }
        updateUniStats(totalClicks, internalClicks, viewsArticle, viewsNews, viewsBlog, viewsPodcast, viewsWhitepaper, viewsRatgeber, viewsMain, viewsUeber, viewsAGBS, viewsImpressum, viewsPreisliste, viewsPartner, viewsDatenschutz, viewsNewsletter, viewsImage, uniqueUsers, userArticle, userNews, userBlog, userPodcast, userWhitepaper, userRatgeber, userMain, userUeber, userAGBS, userImpressum, userPreisliste, userPartner, userDatenschutz, userNewsletter, userImage, serverErrors, viewsByLocation, viewsByHour);
        updateGeo();


    }

    private void updateUniStats(int totalClicks, int internalClicks, int viewsArticle, int viewsNews, int viewsBlog, int viewsPodcast, int viewsWhitepaper, int viewsRatgeber, int viewsMain, int viewsUeber, int viewsAGBS, int viewsImpressum, int viewsPreisliste, int viewsPartner, int viewsDatenschutz, int viewsNewsletter, int viewsImage, int uniqueUsers, int userArticle, int userNews, int userBlog, int userPodcast, int userWhitepaper, int userRatgeber, int userMain, int userUeber, int userAGBS, int userImpressum, int userPreisliste, int userPartner, int userDatenschutz, int userNewsletter, int userImage, int serverErrors, Map<String, Map<String, Map<String, Long>>> viewsByLocation, Map<String, Long> viewsByHour) throws ParseException {
        Date dateTime = Calendar.getInstance().getTime();
        String dateStirng = Calendar.getInstance().get(Calendar.YEAR) + "-";
        dateStirng += Calendar.getInstance().get(Calendar.MONTH) + 1  < 10 ? "0" + Calendar.getInstance().get(Calendar.MONTH) + 1 : Calendar.getInstance().get(Calendar.MONTH) + 1;
        dateStirng += "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String uniLastDateString = sdf.format(uniRepo.getLatestUniStat().getDatum());
        Date date = sdf.parse(dateStirng);
        final int curHour = LocalDateTime.now().getHour();

        //Updating UniversalStats
        {
            UniversalStats uni;
            {
                if (dateStirng.equalsIgnoreCase(uniLastDateString)) {
                    uni = uniRepo.findTop1ByOrderByDatumDesc();
                    uni.setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());
                    uni.setTotalClicks(uni.getTotalClicks() + totalClicks);
                    uni.setInternalClicks(uni.getInternalClicks() + internalClicks);
                    uni.setServerErrors(uni.getServerErrors() + serverErrors);
                    MapHelper.mergeLocationMaps(viewsByLocation, uni.getViewsByLocation());
                    uni.setViewsByLocation(viewsByLocation);
                    MapHelper.mergeTimeMaps(viewsByHour, uni.getViewsPerHour());
                    uni.setViewsPerHour(viewsByHour);
                    uni.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uni = setNewsArticelBlogCountForUniversalStats(date, uni);
                    uni = setAccountTypeAllUniStats(uni);
                } else {
                    uni = new UniversalStats();
                    uni.setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());
                    uni.setTotalClicks(totalClicks);
                    uni.setInternalClicks(internalClicks);
                    uni.setServerErrors(serverErrors);
                    uni.setViewsByLocation(viewsByLocation);
                    uni.setViewsPerHour(viewsByHour);
                    uni.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uni = setNewsArticelBlogCountForUniversalStats(date, uni);
                    uni = setAccountTypeAllUniStats(uni);
                    uni.setDatum(date);
                }
                uniRepo.save(uni);
            }
        }

        //Update UniversalStatsHourly for this hour
        UniversalStatsHourly uniHourly;
        {
            //If it's 4, and we don't have values for 1am to 3am generate them.
            if(curHour == 4
                    && uniHourlyRepo.getByStundeAndUniStatId(1, uniHourlyRepo.getLast().getUniStatId()) == null
                    && uniHourlyRepo.getByStundeAndUniStatId(2, uniHourlyRepo.getLast().getUniStatId()) == null
                    && uniHourlyRepo.getByStundeAndUniStatId(3, uniHourlyRepo.getLast().getUniStatId()) == null
                    && uniHourlyRepo.getLastStunde() != curHour) {
                UniversalStatsHourly uniHourly1 = new UniversalStatsHourly();
                UniversalStatsHourly uniHourly2 = new UniversalStatsHourly();
                UniversalStatsHourly uniHourly3 = new UniversalStatsHourly();
                UniversalStatsHourly uniHourly4 = new UniversalStatsHourly();

                uniHourly1.setStunde(1);
                uniHourly1.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly1.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly1.setTotalClicks((long) totalClicks / 4);
                uniHourly1.setInternalClicks(internalClicks / 4);
                uniHourly1.setServerErrors(serverErrors / 4);
                uniHourly1.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly1);
                setAccountTypeAllUniStats(uniHourly1);
                uniHourly1.setViewsByLocation(viewsByLocation);

                uniHourly2.setStunde(2);
                uniHourly2.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly2.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly2.setTotalClicks((long) totalClicks / 4);
                uniHourly2.setInternalClicks(internalClicks / 4);
                uniHourly2.setServerErrors(serverErrors / 4);
                uniHourly2.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly2);
                setAccountTypeAllUniStats(uniHourly2);
                uniHourly2.setViewsByLocation(viewsByLocation);

                uniHourly3.setStunde(3);
                uniHourly3.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly3.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly3.setTotalClicks((long) totalClicks / 4);
                uniHourly3.setInternalClicks(internalClicks / 4);
                uniHourly3.setServerErrors(serverErrors / 4);
                uniHourly3.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly3);
                setAccountTypeAllUniStats(uniHourly3);
                uniHourly3.setViewsByLocation(viewsByLocation);

                uniHourly4.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly4.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly4.setTotalClicks((long) totalClicks / 4);
                uniHourly4.setInternalClicks(internalClicks / 4);
                uniHourly4.setServerErrors(serverErrors / 4);
                uniHourly4.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly4);
                setAccountTypeAllUniStats(uniHourly4);
                uniHourly4.setViewsByLocation(viewsByLocation);

                uniHourlyRepo.save(uniHourly1);
                uniHourlyRepo.save(uniHourly2);
                uniHourlyRepo.save(uniHourly3);
                uniHourlyRepo.save(uniHourly4);
            } else {
                //If it isn't 4, or we have values for 1am to 3am, create/update stats as usual.
                if ((uniHourlyRepo.getLast() == null || uniHourlyRepo.getLastStunde() != curHour)) {
                    //Since the stat-row for this hour does not exist, make one
                    uniHourly = new UniversalStatsHourly();
                    //Set identifiers for a row.
                    uniHourly.setUniStatId(uniRepo.getSecondLastUniStats().get(0).getId());
                    uniHourly.setStunde(curHour);
                    //Set the stats-
                    uniHourly.setBesucherAnzahl((long) uniqueUsers);
                    uniHourly.setTotalClicks((long) totalClicks);
                    uniHourly.setInternalClicks(internalClicks);
                    uniHourly.setServerErrors(serverErrors);
                } else {
                    //Since stats for the current hour are the last that were created, update it.
                    uniHourly = uniHourlyRepo.getLast();
                    //Identifiers already exist, so skip to updating stats.
                    uniHourly.setBesucherAnzahl(uniHourly.getBesucherAnzahl() + (long) uniqueUsers);
                    uniHourly.setTotalClicks(uniHourly.getTotalClicks() + (long) totalClicks);
                    uniHourly.setInternalClicks(uniHourly.getInternalClicks() + internalClicks);
                    uniHourly.setServerErrors(uniHourly.getServerErrors() + serverErrors);
                }
                uniHourly.setViewsByLocation(viewsByLocation);
                uniHourly.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly);
                setAccountTypeAllUniStats(uniHourly);
                uniHourlyRepo.save(uniHourly);
            }
        }

        //Update UniversalStats with categories
        {
            UniversalCategoriesDLC uniCategories;
            if(universalCategoriesDLCRepo.getLastStunde() != curHour) {
                //Create and identify a new row of UniversalCategoriesDLC
                int viewsGlobal = totalClicks - viewsArticle - viewsNews - viewsBlog - viewsPodcast - viewsWhitepaper - viewsRatgeber - viewsMain - viewsUeber - viewsImpressum - viewsPreisliste - viewsPartner - viewsDatenschutz - viewsNewsletter - viewsImage - viewsAGBS;
                int usersGlobal = uniqueUsers - userArticle - userNews - userBlog - userPodcast - userWhitepaper - userRatgeber - userMain - userUeber - userImpressum - userPreisliste - userPartner - userDatenschutz - userNewsletter - userImage - userAGBS;
                if(curHour == 4
                        && universalCategoriesDLCRepo.getByUniStatIdAndStunde(universalCategoriesDLCRepo.getLast().getUniStatId(), 1) == null
                        && universalCategoriesDLCRepo.getByUniStatIdAndStunde(universalCategoriesDLCRepo.getLast().getUniStatId(), 2) == null
                        && universalCategoriesDLCRepo.getByUniStatIdAndStunde(universalCategoriesDLCRepo.getLast().getUniStatId(), 3) == null
                        && universalCategoriesDLCRepo.getByUniStatIdAndStunde(universalCategoriesDLCRepo.getLast().getUniStatId(), 4) == null) {
                    ArrayList<UniversalCategoriesDLC> catList = new ArrayList<>();
                    catList.add(new UniversalCategoriesDLC());
                    catList.add(new UniversalCategoriesDLC());
                    catList.add(new UniversalCategoriesDLC());
                    catList.add(new UniversalCategoriesDLC());

                    for(UniversalCategoriesDLC cat : catList) {
                        cat.setUniStatId(uniRepo.getSecondLastUniStats().get(0).getId());
                        cat.setStunde(curHour);
                        //Create entries for users.
                        cat.setBesucherGlobal(usersGlobal / 4);
                        cat.setBesucherArticle(userArticle / 4);
                        cat.setBesucherNews(userNews / 4);
                        cat.setBesucherBlog(userBlog / 4);
                        cat.setBesucherPodcast(userPodcast / 4);
                        cat.setBesucherWhitepaper(userWhitepaper / 4);
                        cat.setBesucherRatgeber(userRatgeber / 4);
                        cat.setBesucherMain(userMain / 4);
                        cat.setBesucherUeber(userUeber / 4);
                        cat.setBesucherImpressum(userImpressum / 4);
                        cat.setBesucherPreisliste(userPreisliste / 4);
                        cat.setBesucherPartner(userPartner / 4);
                        cat.setBesucherAGBS(userAGBS / 4);
                        cat.setBesucherDatenschutz(userDatenschutz / 4);
                        cat.setBesucherNewsletter(userNewsletter / 4);
                        cat.setBesucherImage(userImage / 4);
                        //Create entries for views.
                        cat.setViewsGlobal(viewsGlobal / 4);
                        cat.setViewsArticle(viewsArticle / 4);
                        cat.setViewsNews(viewsNews / 4);
                        cat.setViewsBlog(viewsBlog / 4);
                        cat.setViewsPodcast(viewsPodcast / 4);
                        cat.setViewsWhitepaper(viewsWhitepaper / 4);
                        cat.setViewsRatgeber(viewsRatgeber / 4);
                        cat.setViewsMain(viewsMain / 4);
                        cat.setViewsUeber(viewsUeber / 4);
                        cat.setViewsImpressum(viewsImpressum / 4);
                        cat.setViewsPreisliste(viewsPreisliste / 4);
                        cat.setViewsPartner(viewsPartner / 4);
                        cat.setViewsDatenschutz(viewsDatenschutz / 4);
                        cat.setViewsNewsletter(viewsNewsletter / 4);
                        cat.setViewsImage(viewsImage / 4);
                        cat.setViewsAGBS(viewsAGBS / 4);
                        universalCategoriesDLCRepo.save(cat);
                    }




                } else {
                    uniCategories = new UniversalCategoriesDLC();
                    uniCategories.setUniStatId(uniRepo.getSecondLastUniStats().get(0).getId());
                    uniCategories.setStunde(curHour);
                    //Create entries for users.
                    uniCategories.setBesucherGlobal(usersGlobal);
                    uniCategories.setBesucherArticle(userArticle);
                    uniCategories.setBesucherNews(userNews);
                    uniCategories.setBesucherBlog(userBlog);
                    uniCategories.setBesucherPodcast(userPodcast);
                    uniCategories.setBesucherWhitepaper(userWhitepaper);
                    uniCategories.setBesucherRatgeber(userRatgeber);
                    uniCategories.setBesucherMain(userMain);
                    uniCategories.setBesucherUeber(userUeber);
                    uniCategories.setBesucherImpressum(userImpressum);
                    uniCategories.setBesucherPreisliste(userPreisliste);
                    uniCategories.setBesucherPartner(userPartner);
                    uniCategories.setBesucherAGBS(userAGBS);
                    uniCategories.setBesucherDatenschutz(userDatenschutz);
                    uniCategories.setBesucherNewsletter(userNewsletter);
                    uniCategories.setBesucherImage(userImage);
                    //Create entries for views.
                    uniCategories.setViewsGlobal(viewsGlobal);
                    uniCategories.setViewsArticle(viewsArticle);
                    uniCategories.setViewsNews(viewsNews);
                    uniCategories.setViewsBlog(viewsBlog);
                    uniCategories.setViewsPodcast(viewsPodcast);
                    uniCategories.setViewsWhitepaper(viewsWhitepaper);
                    uniCategories.setViewsRatgeber(viewsRatgeber);
                    uniCategories.setViewsMain(viewsMain);
                    uniCategories.setViewsUeber(viewsUeber);
                    uniCategories.setViewsImpressum(viewsImpressum);
                    uniCategories.setViewsPreisliste(viewsPreisliste);
                    uniCategories.setViewsPartner(viewsPartner);
                    uniCategories.setViewsDatenschutz(viewsDatenschutz);
                    uniCategories.setViewsNewsletter(viewsNewsletter);
                    uniCategories.setViewsImage(viewsImage);
                    uniCategories.setViewsAGBS(viewsAGBS);
                    //Save to db.
                    universalCategoriesDLCRepo.save(uniCategories);
                }
            } else {
                //Since entry for this hour already exists, find it.
                uniCategories = universalCategoriesDLCRepo.getLast();
                uniCategories.setUniStatId(uniRepo.getSecondLastUniStats().get(0).getId());
                //Update users
                uniCategories.setBesucherGlobal(uniCategories.getBesucherGlobal() + uniqueUsers - userArticle - userNews - userBlog - userPodcast - userWhitepaper - userRatgeber - userMain - userUeber - userImpressum - userPreisliste - userPartner - userDatenschutz - userNewsletter - userImage - userAGBS);
                uniCategories.setBesucherArticle(uniCategories.getBesucherArticle() + userArticle);
                uniCategories.setBesucherNews(uniCategories.getBesucherNews() + userNews);
                uniCategories.setBesucherBlog(uniCategories.getBesucherBlog() + userBlog);
                uniCategories.setBesucherPodcast(uniCategories.getBesucherPodcast() + userPodcast);
                uniCategories.setBesucherWhitepaper(uniCategories.getBesucherWhitepaper() + userWhitepaper);
                uniCategories.setBesucherRatgeber(uniCategories.getBesucherRatgeber() + userRatgeber);
                uniCategories.setBesucherMain(userMain + uniCategories.getBesucherMain());
                uniCategories.setBesucherUeber(userUeber + uniCategories.getBesucherUeber());
                uniCategories.setBesucherImpressum(userImpressum + uniCategories.getBesucherImpressum());
                uniCategories.setBesucherPreisliste(userPreisliste + uniCategories.getBesucherPreisliste());
                uniCategories.setBesucherPartner(userPartner + uniCategories.getBesucherPartner());
                uniCategories.setBesucherDatenschutz(userDatenschutz + uniCategories.getBesucherDatenschutz());
                uniCategories.setBesucherNewsletter(userNewsletter + uniCategories.getBesucherNewsletter());
                uniCategories.setBesucherImage(userImage + uniCategories.getBesucherImage());
                uniCategories.setBesucherAGBS(userAGBS + uniCategories.getBesucherAGBS());
                //update views
                uniCategories.setViewsGlobal(totalClicks + uniCategories.getViewsGlobal() - viewsArticle - viewsNews - viewsBlog - viewsPodcast - viewsWhitepaper - viewsRatgeber - viewsMain - viewsUeber - viewsImpressum - viewsPreisliste - viewsPartner - viewsDatenschutz - viewsNewsletter - viewsImage - viewsAGBS);
                uniCategories.setViewsArticle(viewsArticle + uniCategories.getViewsArticle());
                uniCategories.setViewsNews(viewsNews + uniCategories.getViewsNews());
                uniCategories.setViewsBlog(viewsBlog + uniCategories.getViewsBlog());
                uniCategories.setViewsPodcast(viewsPodcast + uniCategories.getViewsPodcast());
                uniCategories.setViewsWhitepaper(viewsWhitepaper + uniCategories.getViewsWhitepaper());
                uniCategories.setViewsRatgeber(viewsRatgeber + uniCategories.getViewsRatgeber());
                uniCategories.setViewsMain(viewsMain + uniCategories.getViewsMain());
                uniCategories.setViewsUeber(viewsUeber + uniCategories.getViewsUeber());
                uniCategories.setViewsImpressum(viewsImpressum + uniCategories.getViewsImpressum());
                uniCategories.setViewsPreisliste(viewsPreisliste + uniCategories.getViewsPreisliste());
                uniCategories.setViewsPartner(viewsPartner + uniCategories.getViewsPartner());
                uniCategories.setViewsDatenschutz(viewsDatenschutz + uniCategories.getViewsDatenschutz());
                uniCategories.setViewsNewsletter(viewsNewsletter + uniCategories.getViewsNewsletter());
                uniCategories.setViewsImage(viewsImage + uniCategories.getViewsImage());
                uniCategories.setViewsAGBS(viewsAGBS + uniCategories.getViewsAGBS());
                //save to db.
                universalCategoriesDLCRepo.save(uniCategories);
            }
        }
    }

    private UniversalStatsHourly setAccountTypeAllUniStats(UniversalStatsHourly uniHourly) {
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
        uniHourly.setAnbieter_abolos_anzahl(counts.getOrDefault("Anbieter", 0));
        uniHourly.setAnbieterBasicAnzahl(counts.getOrDefault("Basic",0));
        uniHourly.setAnbieterBasicPlusAnzahl(counts.getOrDefault("Basic-Plus",0));
        uniHourly.setAnbieterPlusAnzahl(counts.getOrDefault("Plus",0));
        uniHourly.setAnbieterPremiumAnzahl(counts.getOrDefault("Premium",0));
        uniHourly.setAnbieterPremiumSponsorenAnzahl(counts.getOrDefault("Sponsor",0));

        long umsatzBasicPlus =uniHourly.getAnbieterBasicPlusAnzahl()*200;
        long umsatzPlus =uniHourly.getAnbieterPlusAnzahl()*1000;
        long umsatzPremium =uniHourly.getAnbieterPremiumAnzahl()*1500;
        long umsatzSponsor = uniHourly.getAnbieterPremiumSponsorenAnzahl()*3000;

        return uniHourly;

    }

    private UniversalStatsHourly setNewsArticelBlogCountForUniversalStats(UniversalStatsHourly uniHourly) {

        List<Post> posts = postRepository.findAllUserPosts();

        long artikelCounter = 0;
        long newsCounter = 0;
        long blogCounter = 0;

        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();

        for (Post post : posts) {

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

        // Setze die Zählungen im universalStats-Objekt
        uniHourly.setAnzahlArtikel(artikelCounter);
        uniHourly.setAnzahlNews(newsCounter);
        uniHourly.setAnzahlBlog(blogCounter);

        return uniHourly;
    }

    @Scheduled(cron = "0 20 0 * * ?")
    public void endDay() throws JSONException, ParseException {
        updateClicksBy();
        updateGeo();
        uniRepo.getSecondLastUniStats().get(1).setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());
        uniqueUserRepo.deleteAll();
    }


    public void processLine(String line, String ip, String whatMatched, LocalDateTime dateLog, Matcher patternMatcher) {
        lastLine = line;

        switch(whatMatched) {
            case "articleView", "blogView", "newsView", "wpView", "ratgeber":
                try {
                    UpdatePerformanceAndViews(dateLog, postRepository.getIdByName(patternMatcher.group(1)));
                    updateViewsByLocation(ip, postRepository.getIdByName(patternMatcher.group(1)));
                    updateIPsByPost(ip, postRepository.getIdByName(patternMatcher.group(1)));
                } catch (Exception e) {
                    System.out.println("VIEW PROCESS LINE EXCEPTION " + line);
                }
                break;
            case "articleSS", "blogSS", "newsSS", "wpSS":
                try {
                    updatePerformanceViewsSearchSuccess(dateLog, postRepository.getIdByName(patternMatcher.group(1)));
                    updateViewsByLocation(ip, postRepository.getIdByName(patternMatcher.group(1)));
                    updateSearchStats(dateLog, postRepository.getIdByName(patternMatcher.group(1)), ip, patternMatcher.group(2));
                } catch(Exception e) {
                    System.out.println("SS PROCESS LINE EXCEPTION " +line);
                }
                break;
            case "podView":
                break;
            case "userView":
                try {
                    if(wpUserRepo.findByNicename(patternMatcher.group(6).replace("+","-")).isPresent()) {
                        updateUserStats(wpUserRepo.findByNicename(patternMatcher.group(1)).get().getId());
                        updateIPsByUser(ip, wpUserRepo.findByNicename(patternMatcher.group(1)).get().getId());
                    }
                } catch (Exception e) {
                    System.out.println("USERVIEW EXCEPTION BEI: " + line);
                }
                break;
            case "main":
                break;
            case "ueber":
                break;
            case "impressum":
                break;
            case "preisliste":
                break;
            case "partner":
                break;
            case "datenschutz":
                break;
            case "newsletter":
                break;
            case "image":
                break;
            case "agb":
                break;

            default:
                break;
        }

    }

    @Deprecated
    public void processLine(String line,String patternName, Matcher preMatcher, Matcher patternMatcher){
        lastLine=line;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss");
        LocalDateTime dateLog = LocalDateTime.from(dateFormatter.parse(preMatcher.group(2)));

        if (patternName.equals("articleView")){
            UpdatePerformanceAndViews(preMatcher,patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
        }
        if (patternName.equals("articleSearchSuccess")){
            updatePerformanceViewsSearchSuccess(preMatcher, patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
            updateSearchStats(preMatcher,patternMatcher);
        }
        if (patternName.equals("blogView")){
            UpdatePerformanceAndViews(preMatcher, patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
        }
        if (patternName.equals("blogSearchSuccess")){
            updatePerformanceViewsSearchSuccess(preMatcher, patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
            updateSearchStats(preMatcher, patternMatcher);
        }

        if (patternName.equals("newsView")){
            UpdatePerformanceAndViews(preMatcher,patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
        }
        if (patternName.equals("newsSearchSuccess")){
            updatePerformanceViewsSearchSuccess(preMatcher, patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
            updateSearchStats(preMatcher, patternMatcher);
        }

        if(patternName.equals("whitepaperSearchSuccess")) {
            //Stolen behaviour from articleSearchSuccess
            System.out.println("TEST Gruppe1: "+ patternMatcher.group(6));
            System.out.println(postRepository.getIdByName(patternMatcher.group(6))+patternMatcher.group(6)+" PROCESSING Whitepaper with Search");
            updatePerformanceViewsSearchSuccess(preMatcher, patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
            updateSearchStats(preMatcher, patternMatcher);
        }

        if(patternName.equals("whitepaperView")) {
            //Stolen behaviour from articleView
            System.out.println(postRepository.getIdByName(patternMatcher.group(1)) + patternMatcher.group(1)+" PROCESSING Whitepaper View");
            UpdatePerformanceAndViews(preMatcher, patternMatcher);
            updateViewsByLocation(preMatcher, patternMatcher);
        }

        if(patternName.equals("userView")){
            if(wpUserRepo.findByNicename(patternMatcher.group(6).replace("+","-")).isPresent()){
                //updateUserStats(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).get());
                //userViewOrImpression(preMatcher,patternMatcher);
            }
        }

        //ToDo: Add these to the new findAMatch, and to the new processLine (just add cases to switches)

        if(patternName.equals("redirect")){
            //gibts das PostStats objekt? -nein = neues -ja = updaten
            long id =postRepository.getIdByName(patternMatcher.group(3));
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

        if(patternName.equals("userViewRedirect")){
            if(wpUserRepo.findByNicename(patternMatcher.group(6).replace("+","-")).isPresent()){
                WPUser wpUser=wpUserRepo.findByNicename(patternMatcher.group(6).replace("+","-")).get();
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
            String ip = preMatcher.group(1);
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
            try {
                searchStatRepo.save(new SearchStats(ipHash, patternMatcher.group(6), dateLog, location));
            } catch(Exception e) {
                System.out.println(patternMatcher.group(6));
            }

        }
        /*
        if(patternName.equals("userView")){
            if(wpUserRepo.findByNicename(patternMatcher.group(6).replace("+","-")).isPresent()){
                //updateUserStats(wpUserRepo.findByNicename(matcher.group(1).replace("+","-")).get());
                userViewOrImpression(preMatcher,patternMatcher);
            }
        }
        */

        //ToDo: If it became necessary, add behaviour for new Patterns here.

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
        return Hex.toHexString(hashBytes);
    }

    public void saveStatsToDatabase() {
        for (String user : userViews.keySet()) {
            UserStats userStats = userStatsRepo.findByUserId(Long.valueOf(user));

            long views = userViews.get(user);
            long currentImpressions = impressions.getOrDefault(user, 0);

            if (userStats == null) {
                userStats = new UserStats(Long.parseLong(user), views, currentImpressions);
            } else {
                // Addiere die Werte zu den vorhandenen Statistiken
                userStats.setProfileView(userStats.getProfileView() + views);
                userStats.setImpressions(userStats.getImpressions() + currentImpressions);
            }

            userStatsRepo.save(userStats);
        }
    }

    public void updateSearchStats(LocalDateTime dateLog, long id, String ip, String searchString) {
        if (id != 0){
            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
            byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
            String hashedIp = Hex.toHexString(hashBytes);

            LocalDate date = dateLog.toLocalDate();  // Replace with the date you want to search for
            System.out.println("GRUPPE 1: "+ ip);
            List<SearchStats> searchStatsForDate = searchStatRepo.findAllBySearchDate(date);
            for(SearchStats s : searchStatsForDate) {
                //hier weiter searchstring equals nicht so viel sinn mit klicked post
                if(hashedIp.equals(s.getIpHashed()) && !s.getSearchSuccessFlag() && s.getSearchString().equals(searchString)) {
                    s.setSearchSuccessFlag(true);
                    s.setClickedPost(String.valueOf(id));
                    s.setSearch_success_time(dateLog);
                    searchStatRepo.save(s);}
            }
        }
    }

    public void updateSearchStats(Matcher preMatcher,Matcher patternMatcher) {
        if (!patternMatcher.group(1).matches("\\d+")){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        byte[] hashBytes = digestSHA3.digest(patternMatcher.group(1).getBytes(StandardCharsets.UTF_8));
        String hashedIp = Hex.toHexString(hashBytes);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss");
            LocalDateTime dateLog = LocalDateTime.from(dateFormatter.parse(preMatcher.group(2)));

            LocalDate date = dateLog.toLocalDate();  // Replace with the date you want to search for
        System.out.println("GRUPPE 1: "+patternMatcher.group(1));
        List<SearchStats> searchStatsForDate = searchStatRepo.findAllBySearchDate(date);
        long id = postRepository.getIdByName(patternMatcher.group(1));
        for(SearchStats s : searchStatsForDate) {
            //hier weiter searchstring equals nicht so viel sinn mit klicked post
            if(hashedIp.equals(s.getIpHashed()) && !s.getSearchSuccessFlag() && s.getSearchString().equals(patternMatcher.group(7))) {
                s.setSearchSuccessFlag(true);

                s.setClickedPost(String.valueOf(id));



                s.setSearch_success_time(dateLog);

                searchStatRepo.save(s);}
            }
        }
    }
    public LocalTime getLocalTimeFromMatcher(Matcher matcher){
        String logHourMinuteSecond = matcher.group(2);
        // Trenne Stunden, Minuten und Sekunden
        String[] timeParts = logHourMinuteSecond.split(":");
        String logHour = timeParts[0];
        String logMinute = timeParts[1];
        String logSecond = timeParts[2];

        LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));
        return logTime;
    }

    public void updatePerformanceViewsSearchSuccess(LocalDateTime dateLog, long id) {
        if (id != 0){
            // Extrahiere Datum und Uhrzeit aus dem Log mit dem neuen Matcher
            String logDay = String.valueOf(dateLog.getDayOfMonth());
            String logYear = String.valueOf(dateLog.getYear());

            String logHour = String.valueOf(dateLog.getHour());
            String logMinute = String.valueOf(dateLog.getMinute());
            String logSecond = String.valueOf(dateLog.getSecond());


            // Erstelle LocalDate und LocalTime Objekte
            LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), dateLog.getMonth().getValue(), Integer.parseInt(logDay));
            LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));

            try {
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
                System.out.println("updatePerformanceViewsSearchSuccess Exception");
            }
        }
    }

    public void updatePerformanceViewsSearchSuccess(Matcher preMatcher, Matcher patternMatcher) {
        if (!patternMatcher.group(1).matches("\\d+")){
        // Extrahiere Datum und Uhrzeit aus dem Log mit dem neuen Matcher
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss");
            LocalDateTime dateLog = LocalDateTime.from(dateFormatter.parse(preMatcher.group(2)));
        String logDay = String.valueOf(dateLog.getDayOfMonth());
        String logYear = String.valueOf(dateLog.getYear());

        String logHour = String.valueOf(dateLog.getHour());
        String logMinute = String.valueOf(dateLog.getMinute());
        String logSecond = String.valueOf(dateLog.getSecond());


        // Erstelle LocalDate und LocalTime Objekte
        LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), dateLog.getMonth().getValue(), Integer.parseInt(logDay));
        LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));

        try {
            long id = postRepository.getIdByName(patternMatcher.group(1));
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
            System.out.println("updatePerformanceViewsSearchSuccess Exception");
        }
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

    public void UpdatePerformanceAndViews(LocalDateTime dateLog, long id) {
        if(id != 0) {
            String logDay = String.valueOf(dateLog.getDayOfMonth());
            String logYear = String.valueOf(dateLog.getYear());

            String logHour = String.valueOf(dateLog.getHour());
            String logMinute = String.valueOf(dateLog.getMinute());
            String logSecond = String.valueOf(dateLog.getSecond());

            // Erstelle LocalDate und LocalTime Objekte
            LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), dateLog.getMonth().getValue(), Integer.parseInt(logDay));
            LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));
            try {
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
                System.out.println("IGNORE " + id + " BECAUSE: " + e.getMessage());
            }



        }


    }

   public void UpdatePerformanceAndViews(Matcher preMatcher,Matcher patternMatcher) {
       if (!patternMatcher.group(1).matches("\\d+")){
       // Extrahiere Datum und Uhrzeit aus dem Log mit dem neuen Matcher
           DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss");
           LocalDateTime dateLog = LocalDateTime.from(dateFormatter.parse(preMatcher.group(2)));
           String logDay = String.valueOf(dateLog.getDayOfMonth());
           String logYear = String.valueOf(dateLog.getYear());

       String logHour = String.valueOf(dateLog.getHour());
       String logMinute = String.valueOf(dateLog.getMinute());
       String logSecond = String.valueOf(dateLog.getSecond());

       // Erstelle LocalDate und LocalTime Objekte
       LocalDate logDate = LocalDate.of(Integer.parseInt(logYear), dateLog.getMonth().getValue(), Integer.parseInt(logDay));
       LocalTime logTime = LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));

       try {
           long id = postRepository.getIdByName(patternMatcher.group(1));
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
           System.out.println("IGNORE " + patternMatcher.group(1) + " BECAUSE: " + e.getMessage());
       }
       }
   }

    @Transactional
    public void updateUserStats(long id){
        if(userStatsRepo.existsByUserId(id)) {
            UserStats Stats = userStatsRepo.findByUserId(id);
            long views = Stats.getProfileView() + 1 ;
            Stats.setProfileView(views);
            List<Post> list = postRepository.findByAuthor((int)id);
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
                Stats.setAverageRelevance(relevance);
            }
            userStatsRepo.save(Stats);


        }else{userStatsRepo.save(new UserStats(id, (float) 0,(float) 0, 0,(float) 0,(float) 0,(float)0,(long)0));}
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
        TagStat Stats = null;
        try {
            Stats = tagStatRepo.getStatById((int) id);
        } catch (Exception e) {
            return;
        }
        if(termTaxRepo.findByTermId(Stats.getTagId()).getTaxonomy().equalsIgnoreCase("post_tag")) {
            HashMap<String, Long> daily = (HashMap<String, Long>) Stats.getViewsLastYear();
            Calendar calendar = Calendar.getInstance();
            int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            long views = daily.get(Integer.toString(currentDayOfYear));
            views++;
            daily.put(Integer.toString(currentDayOfYear), views);
            Stats.setViewsLastYear(daily);
            views = Stats.getViews();
            views++;
            Stats.setViews(views);
            Stats.setRelevance(getRelevance(daily, currentDayOfYear, 7));
            if (searchSuccess) {
                int searchS = Stats.getSearchSuccess();
                searchS++;
                Stats.setSearchSuccess(searchS);
            }
            tagStatRepo.save(Stats);
        } else {
            tagStatRepo.delete(Stats);
        }
    }

    public void checkTheTag(long id,boolean searchSuccess){
        List<Long> tagTaxIds= termRelRepo.getTaxIdByObject(id);
        List<Long> tagIds= termTaxRepo.getTermIdByTaxId(tagTaxIds);
        for(Long l:tagIds){
            if(tagStatRepo.existsByTagId(l.intValue())){
                updateTagStats(l.intValue(),searchSuccess);}
            else {
                if (termTaxRepo.findByTermId(l.intValue()).getTaxonomy().equals("post_tag")) {
                    tagStatRepo.save(new TagStat(l.intValue(), 0, 0, (float) 0, (float) 0));
                    updateTagStats(l.intValue(), searchSuccess);
                }
            }
        }
    }

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
    public void updateViewsByLocation(String ip, long id) {
        if (id != 0){
            try {
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
    }


    @Transactional
    public void updateViewsByLocation(Matcher preMatcher, Matcher patternMatcher) {
        if (!patternMatcher.group(1).matches("\\d+")){
        String ip = preMatcher.group(1);
        try {
            long id = postRepository.getIdByName(patternMatcher.group(1));
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
        }}
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
        long whiteCounter = 0;
        long podCounter = 0;

        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();
        int tagIdWhite = termRepo.findBySlug("whitepaper").getId().intValue();
        int tagIdPod = termRepo.findBySlug("podcast").getId().intValue();

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

                        if (termTax.getTermId() == tagIdWhite) {
                            whiteCounter++;
                        }

                        if (termTax.getTermId() == tagIdPod) {
                            podCounter++;
                        }
                    }
                }
            }
        }

        // Setze die Zählungen im universalStats-Objekt
        uniStats.setAnzahlArtikel(artikelCounter);
        uniStats.setAnzahlNews(newsCounter);
        uniStats.setAnzahlBlog(blogCounter);
        uniStats.setAnzahlWhitepaper(whiteCounter);
        uniStats.setAnzahlPodcast(podCounter);

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

    private void updateIPsByPost(String ip, long id) throws JSONException {
        if(postRepository.findById(id).isPresent()) {
            if(iPsByPostRepository.findById(id).isPresent()) {
                IPsByPost iPsByPost = iPsByPostRepository.findById(id).get();
                JSONArray obj = new JSONArray(iPsByPost.getIps());
                obj.put(ip);
                iPsByPost.setIps(obj.toString());
                iPsByPostRepository.save(iPsByPost);
            } else {
                IPsByPost iPsByPost = new IPsByPost();
                iPsByPost.setPost_id(id);
                JSONArray array = new JSONArray();
                array.put(ip);
                iPsByPost.setIps(array.toString());
                iPsByPostRepository.save(iPsByPost);
            }
        }
    }

    private void updateIPsByUser(String ip, long id) throws JSONException {
        if(userStatsRepo.findById((int)id).isPresent()) {
            if(iPsByUserRepository.findById(id).isPresent()) {
                IPsByUser iPsByUser = iPsByUserRepository.findById(id).get();
                JSONArray obj = new JSONArray(iPsByUser.getIps());
                obj.put(ip);
                iPsByUser.setIps(obj.toString());
                iPsByUserRepository.save(iPsByUser);
            } else {
                IPsByUser iPsByUser = new IPsByUser();
                iPsByUser.setUser_id(id);
                JSONArray array = new JSONArray();
                array.put(ip);
                iPsByUser.setIps(array.toString());
                iPsByUserRepository.save(iPsByUser);
            }
        }
    }

    private void updateClicksBy() {
        int uniId = uniRepo.getLatestUniStat().getId();
        String ip;
        for(UniqueUser user : uniqueUserRepo.findAll()) {
            ip = user.getIp();
            ClicksByCountry clicksByCountry;
            ClicksByBundesland clicksByBundesland = new ClicksByBundesland();
            clicksByBundesland.setUniStatId(uniId);
            if(IPHelper.getCountryISO(user.getIp()).equals("DE")) {
                //Update ClicksByBundesland
                clicksByBundesland = clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getCountryName(ip)) == null
                        ? new ClicksByBundesland() : clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip));
                clicksByBundesland.setUniStatId(uniId);
                clicksByBundesland.setBundesland(IPHelper.getCountryName(ip));
                clicksByBundesland.setClicks(clicksByBundesland.getClicks() + 1);
            }
            //Update ClicksByCountry
            clicksByCountry = clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip)) == null
                    ? new ClicksByCountry() : clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip));
            clicksByCountry.setUniStatId(uniId);
            clicksByCountry.setCountry(IPHelper.getCountryName(ip));
            clicksByCountry.setClicks(clicksByCountry.getClicks() + 1);


        }
    }

    private void updateGeo() {
        updatePostGeo();
        updateUserGeo();
    }

    private void updatePostGeo() {
        PostGeo postGeo = null;
        System.out.println("POST GEO UPDATE");
        try {
            for (IPsByPost post : iPsByPostRepository.findAll()) {
                postGeo = postGeoRepo.findById(post.getPost_id()).isEmpty() ? new PostGeo() : postGeoRepo.findById(post.getPost_id()).get();
                postGeo.setPost_id(post.getPost_id());
                JSONArray json = new JSONArray(post.getIps());
                postGeo.setUniStatId(uniRepo.getSecondLastUniStats().get(1).getId());
                for (int i = 0; i < json.length(); i++) {
                    if (IPHelper.getCountryISO((String) json.get(i)).equals("DE")) {
                        switch (IPHelper.getSubISO((String) json.get(i))) {
                            case "HH" -> postGeo.setHh(postGeo.getHh() + 1);
                            case "HB" -> postGeo.setHb(postGeo.getHb() + 1);
                            case "BE" -> postGeo.setBe(postGeo.getBe() + 1);
                            case "MV" -> postGeo.setMv(postGeo.getMv() + 1);
                            case "BB" -> postGeo.setBb(postGeo.getBb() + 1);
                            case "SN" -> postGeo.setSn(postGeo.getSn() + 1);
                            case "ST" -> postGeo.setSt(postGeo.getSt() + 1);
                            case "BY" -> postGeo.setBye(postGeo.getBye() + 1);
                            case "SL" -> postGeo.setSl(postGeo.getSl() + 1);
                            case "RP" -> postGeo.setRp(postGeo.getRp() + 1);
                            case "SH" -> postGeo.setSh(postGeo.getSh() + 1);
                            case "TH" -> postGeo.setTh(postGeo.getTh() + 1);
                            case "NB" -> postGeo.setNb(postGeo.getNb() + 1);
                            case "HE" -> postGeo.setHe(postGeo.getHe() + 1);
                            case "BW" -> postGeo.setBW(postGeo.getBW() + 1);
                            case "NW" -> postGeo.setNW(postGeo.getNW() + 1);
                            default -> System.out.println("Unbekanntes Bundesland entdeckt");
                        }
                    } else {
                        postGeo.setAusland(postGeo.getAusland() + 1);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        if(postGeo != null) {
            postGeoRepo.save(postGeo);
        }
    }

    private void updateUserGeo() {
        UserGeo userGeo = null;
        System.out.println("USER GEO UPDATE");
        try {
            for (IPsByUser user : iPsByUserRepository.findAll()) {
                userGeo = userGeoRepo.findById(user.getUser_id()).isEmpty() ? new UserGeo() : userGeoRepo.findById(user.getUser_id()).get();
                userGeo.setUser_id(user.getUser_id());
                userGeo.setUniStatId(uniRepo.getSecondLastUniStats().get(1).getId());
                JSONArray json = new JSONArray(user.getIps());
                for (int i = 0; i < json.length(); i++) {
                    if (IPHelper.getCountryISO((String) json.get(i)).equals("DE")) {
                        switch (IPHelper.getSubISO((String) json.get(i))) {
                            case "HH" -> userGeo.setHh(userGeo.getHh() + 1);
                            case "HB" -> userGeo.setHb(userGeo.getHb() + 1);
                            case "BE" -> userGeo.setBe(userGeo.getBe() + 1);
                            case "MV" -> userGeo.setMv(userGeo.getMv() + 1);
                            case "BB" -> userGeo.setBb(userGeo.getBb() + 1);
                            case "SN" -> userGeo.setSn(userGeo.getSn() + 1);
                            case "ST" -> userGeo.setSt(userGeo.getSt() + 1);
                            case "BY" -> userGeo.setBye(userGeo.getBye() + 1);
                            case "SL" -> userGeo.setSl(userGeo.getSl() + 1);
                            case "RP" -> userGeo.setRp(userGeo.getRp() + 1);
                            case "SH" -> userGeo.setSh(userGeo.getSh() + 1);
                            case "TH" -> userGeo.setTh(userGeo.getTh() + 1);
                            case "NB" -> userGeo.setNb(userGeo.getNb() + 1);
                            case "HE" -> userGeo.setHe(userGeo.getHe() + 1);
                            case "BW" -> userGeo.setBW(userGeo.getBW() + 1);
                            case "NW" -> userGeo.setNW(userGeo.getNW() + 1);
                            default -> System.out.println("Unbekanntes Bundesland entdeckt");
                        }
                    } else {
                        userGeo.setAusland(userGeo.getAusland() + 1);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        if(userGeo != null) {
            userGeoRepo.save(userGeo);
        }
    }
}