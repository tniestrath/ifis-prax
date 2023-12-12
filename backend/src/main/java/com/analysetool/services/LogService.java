package com.analysetool.services;

import com.analysetool.api.PostController;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.DashConfig;
import com.analysetool.util.IPHelper;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private ClicksByBundeslandCitiesDLCRepository clicksByBundeslandCityRepo;

    @Autowired
    private PostTypeRepository postTypeRepo;

    @Autowired
    private PostController postController;

    @Autowired
    private UniversalAverageClicksDLCRepository uniAverageClicksRepo;

    @Autowired
    private UniversalTimeSpentDLCRepository uniTimeSpentRepo;

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

    private final String ratgeberView = "^.*GET /ratgeber/cyber-risk-check/(\\S+)/";

    private final String ratgeberGlossarView = "^.*GET /ratgeber/glossar-cyber-sicherheit/";

    private final String ratgeberBuchView = "^.*GET /ratgeber/cyber-sicherheit/";
    private final String NewsViewPatter = "^.*GET /news/(\\S+)/";
    //private String PresseSSViewPatter = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) - - \\[([\\d]{2})/([a-zA-Z]{3})/([\\d]{4}):([\\d]{2}:[\\d]{2}:[\\d]{2}).*GET /pressemitteilung/(\\S+)/.*s=(\\S+)";
    private final String PresseSSViewPatter = "^.*GET /news/(\\S+)/.*s=(\\S+)\".*";

    private final String WhitepaperViewPattern = "^.*GET /whitepaper/(\\S+)/";

    private final String PodcastViewPattern = "^.*GET /podcast/(\\S+)/";


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
    Pattern ratgeberPostViewPattern = Pattern.compile(ratgeberView);
    Pattern ratgeberGlossarViewPattern = Pattern.compile(ratgeberGlossarView);
    Pattern ratgeberBuchViewPattern = Pattern.compile(ratgeberBuchView);

    Pattern podcastViewPattern = Pattern.compile(PodcastViewPattern);


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
        "Index",
        "Go-http-client",
        "Iframely",
        "http"
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

        try {
            updatePostTypes();
        } catch (JSONException e) {
            System.out.println("JSON EXCEPTION BEI UPDATEPOSTTYPES");
        }

        if(LocalDateTime.now().getHour() == 5) {
            endDay();
        }
        sysVarRepo.save(SystemVariabeln);
    }


    public void findAMatch(SysVar sysVar) throws IOException, ParseException, JSONException {
        String line;

        int totalClicks = 0;
        int internalClicks = 0;
        int viewsArticle = 0;
        int viewsNews = 0;
        int viewsBlog = 0;
        int viewsPodcast = 0;
        int viewsWhitepaper = 0;
        int viewsRatgeber = 0;
        int viewsRatgeberPost = 0;
        int viewsRatgeberGlossar = 0;
        int viewsRatgeberBuch = 0;
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
        int userRatgeberPost = 0;
        int userRatgeberGlossar = 0;
        int userRatgeberBuch = 0;
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

        Map<String,Long> viewsByHour = new HashMap<>();

        String last_ip = null;
        String last_request = null;
        JSONArray blacklist2 = null;
        try {
            blacklist2 = new JSONArray(getClass().getResource("blacklist.json").getFile());
        } catch (Exception ignored){}

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

                boolean isGet = request.contains("GET") && !request.contains("HEAD") && !request.contains("POST");


                //Schaue, ob der UserAgent auf der Blacklist steht.
                boolean isBlacklisted = false;
                for (String item : blacklistUserAgents) {
                    isBlacklisted = userAgent.matches("^.*" + item + ".*") && !isBlacklisted;
                }
                if(blacklist2 != null) {
                    for (int i = 0; i < blacklist2.length(); i++) {
                        isBlacklisted = isBlacklisted || ip.equals(blacklist2.get(i));
                    }
                }

                if(isBlacklisted) {
                    System.out.println(request + userAgent + " : BANNED");
                }

                //Falls keiner der Filter zutrifft und der Teil des Logs noch nicht gelesen wurde, behandle die Zeile.
                if ((dateLog.isAfter(dateLastRead) || dateLog.isEqual(dateLastRead)) && !isDevAccess && !isInternal && !isServerError && !isBlacklisted && isSuccessfulRequest && !request.contains("securitynews") && !isSpam && isGet) {

                    sysVar.setLastTimeStamp(dateFormatter.format(dateLog));
                    erhoeheViewsPerHour2(viewsByHour, dateLog.toLocalTime());

                    //erhöhe Clicks und Besucher, falls anwendbar
                    totalClicks++;
                    if(isUnique) {
                        uniqueUsers++;
                        initUniqueUser(ip, dateLog);
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

                    //Does it match a ratgeber-subpost-view
                    Matcher matched_ratgeber_post = ratgeberPostViewPattern.matcher(request);

                    //Does it match a ratgeber-glossar view
                    Matcher matched_ratgeber_glossar = ratgeberGlossarViewPattern.matcher(request);

                    //Does it match a ratgeber-buch view
                    Matcher matched_ratgeber_buch = ratgeberBuchViewPattern.matcher(request);

                    //Does it match a podcast-view
                    Matcher matched_podcast_view = podcastViewPattern.matcher(request);

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
                    } else if(matched_podcast_view.find()) {
                        whatMatched = "podView";
                        patternMatcher = matched_podcast_view;
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
                    } else if(matched_ratgeber_post.find()) {
                        whatMatched = "ratgeberPost";
                        patternMatcher = matched_ratgeber_post;
                    } else if(matched_ratgeber_glossar.find()) {
                        whatMatched = "ratgeberGlossar";
                        patternMatcher = matched_ratgeber_glossar;
                    } else if(matched_ratgeber_buch.find()) {
                        whatMatched = "ratgeberBuch";
                        patternMatcher = matched_ratgeber_buch;
                    }

                    switch (whatMatched) {
                        case "articleView", "articleSS" -> {
                            //Erhöhe Clicks für Artikel um 1.
                            viewsArticle++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userArticle++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getArticle()).length() > 1) {
                                    userArticle++;
                                }
                            }
                            updateUniqueUser(ip, "article", dateLog);
                        }

                        case "blogView", "blogSS" -> {
                            //Erhöhe Clicks für Blog um 1.
                            viewsBlog++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userBlog++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getBlog()).length() > 1) {
                                    userBlog++;
                                }
                            }
                            updateUniqueUser(ip, "blog", dateLog);
                        }

                        case "newsView", "newsSS" -> {
                            //Erhöhe Clicks für News um 1.
                            viewsNews++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userNews++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getNews()).length() > 1) {
                                    userNews++;
                                }
                                user = uniqueUserRepo.findByIP(ip);
                            }
                            updateUniqueUser(ip, "news", dateLog);
                        }

                        case "wpView", "wpSS" -> {
                            //Erhöhe Clicks für Whitepaper um 1.
                            viewsWhitepaper++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userWhitepaper++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getWhitepaper()).length() > 1) {
                                    userWhitepaper++;
                                }
                            }
                            updateUniqueUser(ip, "whitepaper", dateLog);
                        }
                        case "podView" -> {
                            //Erhöhe Clicks für Podcast um 1.
                            viewsPodcast++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userPodcast++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getPodcast()).length() > 1) {
                                    userPodcast++;
                                }
                            }
                            updateUniqueUser(ip, "podcast", dateLog);
                        }
                        case "main" -> {
                            viewsMain++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userMain++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getMain()).length() > 1) {
                                    userMain++;
                                }
                            }
                            updateUniqueUser(ip, "main", dateLog);
                        }
                        case "ueber" -> {
                            //Erhöhe Clicks für Ueber-Uns um 1.
                            viewsUeber++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userUeber++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getUeber()).length() > 1) {
                                    userUeber++;
                                }
                            }
                            updateUniqueUser(ip, "ueber", dateLog);
                        }
                        case "impressum" -> {
                            //Erhöhe Clicks für Impressum um 1.
                            viewsImpressum++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userImpressum++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getImpressum()).length() > 1) {
                                    userImpressum++;
                                }
                            }
                            updateUniqueUser(ip, "impressum", dateLog);
                        }
                        case "preisliste" -> {
                            //Erhöhe Clicks für Preisliste um 1.
                            viewsPreisliste++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userPreisliste++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getPreisliste()).length() > 1) {
                                    userPreisliste++;
                                }
                            }
                            updateUniqueUser(ip, "preisliste", dateLog);
                        }
                        case "partner" -> {
                            //Erhöhe Clicks für Partner um 1.
                            viewsPartner++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userPartner++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getPartner()).length() > 1) {
                                    userPartner++;
                                }
                            }
                            updateUniqueUser(ip, "partner", dateLog);
                        }
                        case "datenschutz" -> {
                            //Erhöhe Clicks für Datenschutzerkl. um 1.
                            viewsDatenschutz++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userDatenschutz++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getDatenschutz()).length() > 1) {
                                    userDatenschutz++;
                                }
                            }
                            updateUniqueUser(ip, "datenschutz", dateLog);
                        }
                        case "newsletter" -> {
                            //Erhöhe Clicks für Newsletter um 1.
                            viewsNewsletter++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userNewsletter++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getNewsletter()).length() > 1) {
                                    userNewsletter++;
                                }
                            }
                            updateUniqueUser(ip, "newsletter", dateLog);
                        }
                        case "image" -> {
                            //Erhöhe Clicks für Image um 1.
                            viewsImage++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userImage++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getImage()).length() > 1) {
                                    userImage++;
                                }
                            }
                            updateUniqueUser(ip, "image", dateLog);
                        }
                        case "agb" -> {
                            //Erhöhe Clicks für AGBS um 1.
                            viewsAGBS++;
                            //Wenn der User Unique ist, erstelle eine Zeile in UniqueUser.
                            if (isUnique) {
                                userAGBS++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getAgb()).length() > 1) {
                                    userAGBS++;
                                }
                            }
                            updateUniqueUser(ip, "agb", dateLog);
                        }
                        case "userView" -> {
                            try {
                                updateUserStats(wpUserRepo.findByNicename(patternMatcher.group(1)).get().getId(),dateLog);
                            } catch (Exception e) {
                                System.out.println(patternMatcher.group(1));
                            }
                        }
                        case "ratgeberPost", "ratgeberGlossar", "ratgeberBuch" -> {
                            //Erhöhe Clicks für RatgeberViews um 1.
                            viewsRatgeber++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userRatgeber++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() > 1) {
                                    userRatgeber++;
                                }
                            }
                            updateUniqueUser(ip, "ratgeber", dateLog);

                            //Update stats for more concrete type of Ratgeber
                            if(whatMatched.equals("ratgeberPost")) {
                                viewsRatgeberPost++;
                                if (isUnique) {
                                    userRatgeberPost++;
                                } else {
                                    if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() > 1) {
                                        userRatgeberPost++;
                                    }
                                }
                            } else if(whatMatched.equals("ratgeberGlossar")) {
                                viewsRatgeberGlossar++;
                                if (isUnique) {
                                    userRatgeberGlossar++;
                                } else {
                                    if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() > 1) {
                                        userRatgeberGlossar++;
                                    }
                                }
                            } else {
                                viewsRatgeberBuch++;
                                if (isUnique) {
                                    userRatgeberBuch++;
                                } else {
                                    if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() > 1) {
                                        userRatgeberBuch++;
                                    }
                                }
                            }


                        }
                        default -> System.out.println(line);
                    }

                    processLine(line, ip, whatMatched, dateLog, patternMatcher);
                    //A bunch of variables necessary to update UniStats

                } else if((dateLog.isAfter(dateLastRead) || dateLog.isEqual(dateLastRead))) {
                    if(isBlacklisted) {
                        //System.out.println("BANNED!!!!!!!!! : " + line);
                    }
                    if(isSpam && !isInternal) {
                        //System.out.println("SPAM!!!: " + ip + " " + request + " " + userAgent);
                    }
                    if(isServerError) {
                        serverErrors++;
                    }
                    if(isInternal) {
                        internalClicks++;
                    }
                }
                last_request = request;
                last_ip = ip;
            }

        }
        updateUniStats(totalClicks, internalClicks, viewsArticle, viewsNews, viewsBlog, viewsPodcast, viewsWhitepaper, viewsRatgeber,viewsRatgeberPost, viewsRatgeberGlossar, viewsRatgeberBuch, viewsMain, viewsUeber, viewsAGBS, viewsImpressum, viewsPreisliste, viewsPartner, viewsDatenschutz, viewsNewsletter, viewsImage, uniqueUsers, userArticle, userNews, userBlog, userPodcast, userWhitepaper, userRatgeber, userRatgeberPost, userRatgeberGlossar, userRatgeberBuch, userMain, userUeber, userAGBS, userImpressum, userPreisliste, userPartner, userDatenschutz, userNewsletter, userImage, serverErrors, viewsByHour);
    }

    private void updateUniStats(int totalClicks, int internalClicks, int viewsArticle, int viewsNews, int viewsBlog, int viewsPodcast, int viewsWhitepaper, int viewsRatgeber, int viewsRatgeberPost, int viewsRatgeberGlossar, int viewsRatgeberBuch, int viewsMain, int viewsUeber, int viewsAGBS, int viewsImpressum, int viewsPreisliste, int viewsPartner, int viewsDatenschutz, int viewsNewsletter, int viewsImage, int uniqueUsers, int userArticle, int userNews, int userBlog, int userPodcast, int userWhitepaper, int userRatgeber, int userRatgeberPost, int userRatgeberGlossar, int userRatgeberBuch, int userMain, int userUeber, int userAGBS, int userImpressum, int userPreisliste, int userPartner, int userDatenschutz, int userNewsletter, int userImage, int serverErrors, Map<String, Long> viewsByHour) throws ParseException {
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
                    uni.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uni = setNewsArticelBlogCountForUniversalStats(date, uni);
                    uni = setAccountTypeAllUniStats(uni);
                } else {
                    uni = new UniversalStats();
                    uni.setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());
                    uni.setTotalClicks(totalClicks);
                    uni.setInternalClicks(internalClicks);
                    uni.setServerErrors(serverErrors);
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

                uniHourly2.setStunde(2);
                uniHourly2.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly2.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly2.setTotalClicks((long) totalClicks / 4);
                uniHourly2.setInternalClicks(internalClicks / 4);
                uniHourly2.setServerErrors(serverErrors / 4);
                uniHourly2.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly2);
                setAccountTypeAllUniStats(uniHourly2);

                uniHourly3.setStunde(3);
                uniHourly3.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly3.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly3.setTotalClicks((long) totalClicks / 4);
                uniHourly3.setInternalClicks(internalClicks / 4);
                uniHourly3.setServerErrors(serverErrors / 4);
                uniHourly3.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly3);
                setAccountTypeAllUniStats(uniHourly3);

                uniHourly4.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly4.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly4.setTotalClicks((long) totalClicks / 4);
                uniHourly4.setInternalClicks(internalClicks / 4);
                uniHourly4.setServerErrors(serverErrors / 4);
                uniHourly4.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly4);
                setAccountTypeAllUniStats(uniHourly4);

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
                    int i = 1;
                    for(UniversalCategoriesDLC cat : catList) {
                        cat.setUniStatId(uniRepo.getSecondLastUniStats().get(0).getId());
                        cat.setStunde(i);
                        i++;
                        //Create entries for users.
                        cat.setBesucherGlobal(usersGlobal / 4);
                        cat.setBesucherArticle(userArticle / 4);
                        cat.setBesucherNews(userNews / 4);
                        cat.setBesucherBlog(userBlog / 4);
                        cat.setBesucherPodcast(userPodcast / 4);
                        cat.setBesucherWhitepaper(userWhitepaper / 4);
                        cat.setBesucherRatgeber(userRatgeber / 4);
                        cat.setBesucherRatgeber(userRatgeberPost / 4);
                        cat.setBesucherRatgeber(userRatgeberGlossar / 4);
                        cat.setBesucherRatgeber(userRatgeberBuch / 4);
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
                        cat.setViewsRatgeber(viewsRatgeberPost / 4);
                        cat.setViewsRatgeber(viewsRatgeberGlossar / 4);
                        cat.setViewsRatgeber(viewsRatgeberBuch / 4);
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
                    uniCategories.setBesucherRatgeberPost(userRatgeberPost);
                    uniCategories.setBesucherRatgeberGlossar(userRatgeberGlossar);
                    uniCategories.setBesucherRatgeberBuch(userRatgeberBuch);
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
                    uniCategories.setViewsRatgeberPost(viewsRatgeberPost);
                    uniCategories.setViewsRatgeberGlossar(viewsRatgeberGlossar);
                    uniCategories.setViewsRatgeberBuch(viewsRatgeberBuch);
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
                uniCategories.setBesucherRatgeberPost(uniCategories.getBesucherRatgeberPost() + userRatgeberPost);
                uniCategories.setBesucherRatgeberGlossar(uniCategories.getBesucherRatgeberGlossar() + userRatgeberGlossar);
                uniCategories.setBesucherRatgeberBuch(uniCategories.getBesucherRatgeberBuch() + userRatgeberBuch);
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
                uniCategories.setViewsRatgeberPost(viewsRatgeberPost + uniCategories.getViewsRatgeberPost());
                uniCategories.setViewsRatgeberGlossar(viewsRatgeberGlossar + uniCategories.getViewsRatgeberGlossar());
                uniCategories.setViewsRatgeberBuch(viewsRatgeberBuch + uniCategories.getViewsRatgeberBuch());
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

    public void endDay() {
        try {
            System.out.println("UPDATE CLICKS AUSGEFÜHRT HIER!");
            updateClicksBy();
            System.out.println("KEIN FEHLER BEI UPDATE CLICKS");
        } catch (Exception e) {
            System.out.println("-------------------------------------------------------------FEHLER BEI UPDATE CLICKS");
            e.printStackTrace();
        }
        try {
            System.out.println("UPDATE GEO AUSGEFÜHRT HIER!");
            updateGeo();
            System.out.println("KEIN FEHLER BEI UPDATE GEO");
        } catch (Exception e) {
            System.out.println("---------------------------------------------------------------------FEHLER BEI UPDATEGEO");
            e.printStackTrace();
        }
        try {
            System.out.println("PERMANENTIFY ALL USERS");
            permanentifyAllUsers();
        } catch (Exception e) {
            System.out.println("FEHLER BEI PERMANENTIFY ALL USERS");
            e.printStackTrace();
        }
        uniRepo.getSecondLastUniStats().get(1).setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());

<<<<<<< HEAD
        //set userstats views per hour to map with only 0 values so its only the views in last 24h distributed by hours
        List<UserStats> userStats = userStatsRepo.findAll();
        for(UserStats u:userStats){
            u.setViewsPerHour(u.setJson());
        }
        userStatsRepo.saveAll(userStats);
=======
        //Just in case permanentify failed
>>>>>>> 91c5d213cb4ca9e64f6457d4a727b273b82a5604
        deleteOldIPs();
    }


    public void processLine(String line, String ip, String whatMatched, LocalDateTime dateLog, Matcher patternMatcher) {
        lastLine = line;

        switch(whatMatched) {
            case "articleView", "blogView", "newsView", "wpView", "ratgeberPost", "podView":
                try {
                    UpdatePerformanceAndViews(dateLog, postRepository.getIdByName(patternMatcher.group(1)));
                    updateIPsByPost(ip, postRepository.getIdByName(patternMatcher.group(1)));
                } catch (Exception e) {
                    System.out.println("VIEW PROCESS LINE EXCEPTION " + line);
                }
                break;
            case "articleSS", "blogSS", "newsSS", "wpSS":
                try {
                    updatePerformanceViewsSearchSuccess(dateLog, postRepository.getIdByName(patternMatcher.group(1)));
                    updateSearchStats(dateLog, postRepository.getIdByName(patternMatcher.group(1)), ip, patternMatcher.group(2));
                } catch(Exception e) {
                    System.out.println("SS PROCESS LINE EXCEPTION " +line);
                }
                break;
            case "userView":
                try {
                    if(wpUserRepo.findByNicename(patternMatcher.group(1).replace("+","-")).isPresent()) {
                        Long userId = wpUserRepo.findByNicename(patternMatcher.group(1).replace("+","-")).get().getId();
                        updateUserStats(userId,dateLog);
                        updateIPsByUser(ip, userId);
                    }
                } catch (Exception e) {
                    System.out.println("USERVIEW EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
                break;
            case "ratgeberGlossar":
                break;
            case "ratgeberBuch":
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

    public String hashIp(String ip){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(hashBytes);
    }

    public void saveStatsToDatabase() {
        for (String user : userViews.keySet()) {
            UserStats userStats = userStatsRepo.findByUserId(Long.valueOf(user));

            long views = userViews.get(user);

            if (userStats == null) {
                userStats = new UserStats(Long.parseLong(user), views);
            } else {
                // Addiere die Werte zu den vorhandenen Statistiken
                userStats.setProfileView(userStats.getProfileView() + views);
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
    public void updateUserStats(long id,LocalDateTime dateLog){
        if(userStatsRepo.existsByUserId(id)) {

            UserStats Stats = userStatsRepo.findByUserId(id);
            if(Stats.getViewsPerHour().isEmpty()||Stats.getViewsPerHour()==null){
                Stats.setViewsPerHour(Stats.setJson());
            }
            long views = Stats.getProfileView() + 1 ;
            Stats.setViewsPerHour(erhoeheViewsPerHour2(Stats.getViewsPerHour(),dateLog.toLocalTime()));
            Stats.setProfileView(views);
            userStatsRepo.save(Stats);

        }else {

            userStatsRepo.save(new UserStats(id, 1));

        }
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
            if(iPsByPostRepository.getByID(id) != null) {
                IPsByPost iPsByPost = iPsByPostRepository.getByID(id);
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
        if(userStatsRepo.findByUserId(id) != null) {
            if(iPsByUserRepository.getByUserID(id) != null) {
                IPsByUser iPsByUser = iPsByUserRepository.getByUserID(id);
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
        int uniId = uniRepo.getSecondLastUniStats().get(1).getId();
        String ip;

        //For all UniqueUsers we have saved, iterate:
        for(UniqueUser user : uniqueUserRepo.findAll()) {
            ip = user.getIp();
            ClicksByCountry clicksByCountry;
            ClicksByBundesland clicksByBundesland;
            ClicksByBundeslandCitiesDLC clicksByBundeslandCitiesDLC;
            //If the country is Germany, try to update ClicksByBundesland
            if (IPHelper.getCountryISO(ip) != null) {
                if (IPHelper.getCountryISO(user.getIp()).equals("DE")) {
                    System.out.println("TRY TO UPDATE CLICKSBYBUNDESLAND");
                    clicksByBundesland = clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip)) == null
                            ? new ClicksByBundesland() : clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip));

                    clicksByBundesland.setUniStatId(uniId);
                    //If the ip can be matched to a bundesland, update and save it. Otherwise, don't.
                    if (IPHelper.getSubISO(ip) != null) {
                        System.out.println("SUCCESS IN TRY TO UPDATE CLICKSBYBUNDESLAND");
                        clicksByBundesland.setBundesland(IPHelper.getSubISO(ip));
                        clicksByBundesland.setClicks(clicksByBundesland.getClicks() + 1);
                        clicksByBundeslandRepo.save(clicksByBundesland);
                        //If the ip can be matched to a city, set, update and save ClicksByBundeslandCitiesDLC
                        if (IPHelper.getCityName(ip) != null) {
                            System.out.println("TRY TO UPDATE CLICKSBYBUNDESLAND");
                            clicksByBundeslandCitiesDLC = clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getSubISO(ip), IPHelper.getCityName(ip)) == null
                                    ? new ClicksByBundeslandCitiesDLC() : clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getSubISO(ip), IPHelper.getCityName(ip));
                            clicksByBundeslandCitiesDLC.setUni_id(uniId);
                            clicksByBundeslandCitiesDLC.setBundesland(IPHelper.getSubISO(ip));
                            clicksByBundeslandCitiesDLC.setCity(IPHelper.getCityName(ip));
                            clicksByBundeslandCitiesDLC.setClicks(clicksByBundeslandCitiesDLC.getClicks() + 1);
                            clicksByBundeslandCityRepo.save(clicksByBundeslandCitiesDLC);
                        }
                    }

                } else if(IPHelper.getCountryISO(ip).equals("BE")) {
                    System.out.println("TRY TO UPDATE CLICKSBYCITY FOR BELGIUM");
                    if (IPHelper.getCityName(ip) != null) {
                        clicksByBundeslandCitiesDLC = clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, "BG", IPHelper.getCityName(ip)) == null
                                ? new ClicksByBundeslandCitiesDLC() : clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, "BG", IPHelper.getCityName(ip));
                        clicksByBundeslandCitiesDLC.setUni_id(uniId);
                        clicksByBundeslandCitiesDLC.setBundesland("BG");
                        clicksByBundeslandCitiesDLC.setCity(IPHelper.getCityName(ip));
                        clicksByBundeslandCitiesDLC.setClicks(clicksByBundeslandCitiesDLC.getClicks() + 1);
                        clicksByBundeslandCityRepo.save(clicksByBundeslandCitiesDLC);
                    }
                } else if(IPHelper.getCountryISO(ip).equals("NL") || IPHelper.getCountryISO(ip).equals("AT") || IPHelper.getCountryISO(ip).equals("CH") || IPHelper.getCountryISO(ip).equals("LU")){
                    System.out.println("TRY TO UPDATE CLICKSBYCITY FOR OTHER COUNTRIES");
                    if (IPHelper.getCityName(ip) != null) {
                        clicksByBundeslandCitiesDLC = clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getCountryISO(ip), IPHelper.getCityName(ip)) == null
                                ? new ClicksByBundeslandCitiesDLC() : clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getCountryISO(ip), IPHelper.getCityName(ip));
                        clicksByBundeslandCitiesDLC.setUni_id(uniId);
                        clicksByBundeslandCitiesDLC.setBundesland(IPHelper.getCountryISO(ip));
                        clicksByBundeslandCitiesDLC.setCity(IPHelper.getCityName(ip));
                        clicksByBundeslandCitiesDLC.setClicks(clicksByBundeslandCitiesDLC.getClicks() + 1);
                        clicksByBundeslandCityRepo.save(clicksByBundeslandCitiesDLC);
                    }
                }
                //Update ClicksByCountry
                System.out.println("TRY TO UPDATE CLICKSBYCOUNTRY");
                clicksByCountry = clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip)) == null
                        ? new ClicksByCountry() : clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip));
                clicksByCountry.setUniStatId(uniId);
                clicksByCountry.setCountry(IPHelper.getCountryName(ip));
                clicksByCountry.setClicks(clicksByCountry.getClicks() + 1);
                clicksByCountryRepo.save(clicksByCountry);

            }
        }
    }

    private void updateClicksByIpLiveDay(String ip) {
        int uniId = uniRepo.getLatestUniStat().getId();
        ClicksByCountry clicksByCountry;
        ClicksByBundesland clicksByBundesland;
        ClicksByBundeslandCitiesDLC clicksByBundeslandCitiesDLC;
        //If the country is Germany, try to update ClicksByBundesland
        if (IPHelper.getCountryISO(ip) != null) {
            if (IPHelper.getCountryISO(ip).equals("DE")) {
                System.out.println("TRY TO UPDATE CLICKSBYBUNDESLAND FOR: " + ip);
                clicksByBundesland = clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip)) == null
                        ? new ClicksByBundesland() : clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip));

                clicksByBundesland.setUniStatId(uniId);
                //If the ip can be matched to a bundesland, update and save it. Otherwise, don't.
                if (IPHelper.getSubISO(ip) != null) {
                    System.out.println("SUCCESS IN TRY TO UPDATE CLICKSBYBUNDESLAND");
                    clicksByBundesland.setBundesland(IPHelper.getSubISO(ip));
                    clicksByBundesland.setClicks(clicksByBundesland.getClicks() + 1);
                    clicksByBundeslandRepo.save(clicksByBundesland);
                    //If the ip can be matched to a city, set, update and save ClicksByBundeslandCitiesDLC
                    if (IPHelper.getCityName(ip) != null) {
                        System.out.println("TRY TO UPDATE CLICKSBYBUNDESLAND");
                        clicksByBundeslandCitiesDLC = clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getSubISO(ip), IPHelper.getCityName(ip)) == null
                                ? new ClicksByBundeslandCitiesDLC() : clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getSubISO(ip), IPHelper.getCityName(ip));
                        clicksByBundeslandCitiesDLC.setUni_id(uniId);
                        clicksByBundeslandCitiesDLC.setBundesland(IPHelper.getSubISO(ip));
                        clicksByBundeslandCitiesDLC.setCity(IPHelper.getCityName(ip));
                        clicksByBundeslandCitiesDLC.setClicks(clicksByBundeslandCitiesDLC.getClicks() + 1);
                        clicksByBundeslandCityRepo.save(clicksByBundeslandCitiesDLC);
                    }
                }

            } else if(IPHelper.getCountryISO(ip).equals("BE")) {
                System.out.println("TRY TO UPDATE CLICKSBYCITY FOR BELGIUM");
                if (IPHelper.getCityName(ip) != null) {
                    clicksByBundeslandCitiesDLC = clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, "BG", IPHelper.getCityName(ip)) == null
                            ? new ClicksByBundeslandCitiesDLC() : clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, "BG", IPHelper.getCityName(ip));
                    clicksByBundeslandCitiesDLC.setUni_id(uniId);
                    clicksByBundeslandCitiesDLC.setBundesland("BG");
                    clicksByBundeslandCitiesDLC.setCity(IPHelper.getCityName(ip));
                    clicksByBundeslandCitiesDLC.setClicks(clicksByBundeslandCitiesDLC.getClicks() + 1);
                    clicksByBundeslandCityRepo.save(clicksByBundeslandCitiesDLC);
                }
            } else if(IPHelper.getCountryISO(ip).equals("NL") || IPHelper.getCountryISO(ip).equals("AT") || IPHelper.getCountryISO(ip).equals("CH") || IPHelper.getCountryISO(ip).equals("LU")){
                System.out.println("TRY TO UPDATE CLICKSBYCITY FOR OTHER COUNTRIES");
                if (IPHelper.getCityName(ip) != null) {
                    clicksByBundeslandCitiesDLC = clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getCountryISO(ip), IPHelper.getCityName(ip)) == null
                            ? new ClicksByBundeslandCitiesDLC() : clicksByBundeslandCityRepo.getByUniIDAndBundeslandAndCity(uniId, IPHelper.getCountryISO(ip), IPHelper.getCityName(ip));
                    clicksByBundeslandCitiesDLC.setUni_id(uniId);
                    clicksByBundeslandCitiesDLC.setBundesland(IPHelper.getCountryISO(ip));
                    clicksByBundeslandCitiesDLC.setCity(IPHelper.getCityName(ip));
                    clicksByBundeslandCitiesDLC.setClicks(clicksByBundeslandCitiesDLC.getClicks() + 1);
                    clicksByBundeslandCityRepo.save(clicksByBundeslandCitiesDLC);
                }
            }
            //Update ClicksByCountry
            System.out.println("TRY TO UPDATE CLICKSBYCOUNTRY");
            clicksByCountry = clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip)) == null
                    ? new ClicksByCountry() : clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip));
            clicksByCountry.setUniStatId(uniId);
            clicksByCountry.setCountry(IPHelper.getCountryName(ip));
            clicksByCountry.setClicks(clicksByCountry.getClicks() + 1);
            clicksByCountryRepo.save(clicksByCountry);

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
                System.out.println(post.getPost_id());
                postGeo = postGeoRepo.findByPostIdAndUniStatId(post.getPost_id(), uniRepo.getLatestUniStat().getId()) == null ? new PostGeo() : postGeoRepo.findByPostIdAndUniStatId(post.getPost_id(), uniRepo.getLatestUniStat().getId());
                postGeo.setPost_id(post.getPost_id());
                JSONArray json = new JSONArray(post.getIps());
                postGeo.setUniStatId(uniRepo.getSecondLastUniStats().get(1).getId());
                for (int i = 0; i < json.length(); i++) {
                    if(IPHelper.getCountryISO((String) json.get(i)) != null) {
                        if (IPHelper.getCountryISO((String) json.get(i)).equals("DE")) {
                            String subISO = IPHelper.getSubISO((String) json.get(i));
                            if (subISO == null) {
                                System.out.println("Folgende IP ist regionslos" + json.get(i));
                            } else if (subISO.equals("HH")) {
                                postGeo.setHh(postGeo.getHh() + 1);
                            } else if (subISO.equals("HB")) {
                                postGeo.setHb(postGeo.getHb() + 1);
                            } else if (subISO.equals("BE")) {
                                postGeo.setBe(postGeo.getBe() + 1);
                            } else if (subISO.equals("MV")) {
                                postGeo.setMv(postGeo.getMv() + 1);
                            } else if (subISO.equals("BB")) {
                                postGeo.setBb(postGeo.getBb() + 1);
                            } else if (subISO.equals("SN")) {
                                postGeo.setSn(postGeo.getSn() + 1);
                            } else if (subISO.equals("ST")) {
                                postGeo.setSt(postGeo.getSt() + 1);
                            } else if (subISO.equals("BY")) {
                                postGeo.setBye(postGeo.getBye() + 1);
                            } else if (subISO.equals("SL")) {
                                postGeo.setSl(postGeo.getSl() + 1);
                            } else if (subISO.equals("RP")) {
                                postGeo.setRp(postGeo.getRp() + 1);
                            } else if (subISO.equals("SH")) {
                                postGeo.setSh(postGeo.getSh() + 1);
                            } else if (subISO.equals("TH")) {
                                postGeo.setTh(postGeo.getTh() + 1);
                            } else if (subISO.equals("NI")) {
                                postGeo.setNb(postGeo.getNb() + 1);
                            } else if (subISO.equals("HE")) {
                                postGeo.setHe(postGeo.getHe() + 1);
                            } else if (subISO.equals("BW")) {
                                postGeo.setBW(postGeo.getBW() + 1);
                            } else if (subISO.equals("NW")) {
                                postGeo.setNW(postGeo.getNW() + 1);
                            } else {
                                System.out.println("Unbekanntes Bundesland entdeckt");
                            }
                        } else {
                            postGeo.setAusland(postGeo.getAusland() + 1);
                        }
                    } else {
                        System.out.println("Diese IP ist landlos" + json.get(i));
                    }
                }
                postGeoRepo.save(postGeo);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUserGeo() {
        UserGeo userGeo = null;
        System.out.println("USER GEO UPDATE");
        try {
            for (IPsByUser user : iPsByUserRepository.findAll()) {
                userGeo = userGeoRepo.findByUserIdAndUniStatId(user.getUser_id(), uniRepo.getLatestUniStat().getId()) == null ? new UserGeo() : userGeoRepo.findByUserIdAndUniStatId(user.getUser_id(), uniRepo.getLatestUniStat().getId());
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
                            case "NI" -> userGeo.setNb(userGeo.getNb() + 1);
                            case "HE" -> userGeo.setHe(userGeo.getHe() + 1);
                            case "BW" -> userGeo.setBW(userGeo.getBW() + 1);
                            case "NW" -> userGeo.setNW(userGeo.getNW() + 1);
                            default -> System.out.println("Unbekanntes Bundesland entdeckt");
                        }
                    } else {
                        userGeo.setAusland(userGeo.getAusland() + 1);
                    }
                }
                userGeoRepo.save(userGeo);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteOldIPs() {
        iPsByUserRepository.deleteAll();
        iPsByPostRepository.deleteAll();
        uniqueUserRepo.deleteAll();
    }

    private void updatePostTypes() throws JSONException, ParseException {
        for(Integer id : statsRepo.getIdsOfUntyped()) {
            PostTypes type = new PostTypes();
            type.setPost_id(Long.valueOf(id));
            type.setType(postController.getType(id));
            postTypeRepo.save(type);
        }
    }

    private void updateUniqueUser(String ip, String category, LocalDateTime clickTime) throws JSONException {
        UniqueUser user = uniqueUserRepo.findByIP(ip);
        //Check whether user has clicked within last hour, if not, permanentify the user and open a new User.
        if(clickTime.isAfter(user.getFirst_click().plusHours(1).plusSeconds(user.getTime_spent()))) {
            permanentifyUser(ip);
            uniqueUserRepo.delete(user);
            initUniqueUser(ip, clickTime);
            user = uniqueUserRepo.findByIP(ip);
        }
        //Get the highest Click in all Lists, then add 1 because we are currently processing a click
        int clicks = user.getAmount_of_clicks() + 1;
        user.setAmount_of_clicks(clicks);


        //calculate the time that passed between the first and current click
        user.setTime_spent((int) Duration.between(user.getFirst_click(), clickTime).toSeconds());

        //Update the List of clicked categories
        switch(category) {
            case "article" -> {
                user.setArticle(new JSONArray(user.getArticle()).put(clicks).toString());
            }
            case "blog" -> {
                user.setBlog(new JSONArray(user.getBlog()).put(clicks).toString());
            }
            case "news" -> {
                user.setNews(new JSONArray(user.getNews()).put(clicks).toString());
            }
            case "whitepaper" -> {
                user.setWhitepaper(new JSONArray(user.getWhitepaper()).put(clicks).toString());
            }
            case "podcast" -> {
                user.setPodcast(new JSONArray(user.getPodcast()).put(clicks).toString());
            }
            case "ratgeber" -> {
                user.setRatgeber(new JSONArray(user.getRatgeber()).put(clicks).toString());
            }
            case "main" -> {
                user.setMain(new JSONArray(user.getMain()).put(clicks).toString());
            }
            case "ueber" -> {
                user.setUeber(new JSONArray(user.getUeber()).put(clicks).toString());
            }
            case "impressum" -> {
                user.setImpressum(new JSONArray(user.getImpressum()).put(clicks).toString());
            }
            case "preisliste" -> {
                user.setPreisliste(new JSONArray(user.getPreisliste()).put(clicks).toString());
            }
            case "partner" -> {
                user.setPartner(new JSONArray(user.getPartner()).put(clicks).toString());
            }
            case "datenschutz" -> {
                user.setDatenschutz(new JSONArray(user.getDatenschutz()).put(clicks).toString());
            }
            case "newsletter" -> {
                user.setNewsletter(new JSONArray(user.getNewsletter()).put(clicks).toString());
            }
            case "image" -> {
                user.setImage(new JSONArray(user.getImage()).put(clicks).toString());
            }
            case "agb" -> {
                user.setAgb(new JSONArray(user.getAgb()).put(clicks).toString());
            }
        }

        uniqueUserRepo.save(user);

    }

    private void initUniqueUser(String ip, LocalDateTime clickTime) {
        UniqueUser user = new UniqueUser();
        user.setIp(ip);
        user.setArticle(new JSONArray().put(0).toString());
        user.setBlog(new JSONArray().put(0).toString());
        user.setNews(new JSONArray().put(0).toString());
        user.setWhitepaper(new JSONArray().put(0).toString());
        user.setPodcast(new JSONArray().put(0).toString());
        user.setRatgeber(new JSONArray().put(0).toString());
        user.setMain(new JSONArray().put(0).toString());
        user.setUeber(new JSONArray().put(0).toString());
        user.setImpressum(new JSONArray().put(0).toString());
        user.setPreisliste(new JSONArray().put(0).toString());
        user.setPartner(new JSONArray().put(0).toString());
        user.setDatenschutz(new JSONArray().put(0).toString());
        user.setNewsletter(new JSONArray().put(0).toString());
        user.setImage(new JSONArray().put(0).toString());
        user.setAgb(new JSONArray().put(0).toString());
        user.setFirst_click(clickTime);
        user.setTime_spent(0);
        user.setAmount_of_clicks(0);

        uniqueUserRepo.save(user);
    }

    private void permanentifyUser(String ip) throws JSONException {
        if(uniAverageClicksRepo.findById(uniRepo.getLatestUniStat().getId()).isEmpty()) {
            initUniAverages();
            initUniTime();
        }

        UniqueUser user = uniqueUserRepo.findByIP(ip);

        if(user != null) {
            int articleLength = new JSONArray(user.getArticle()).length() - 1;
            int newsLength = new JSONArray(user.getNews()).length() - 1;
            int blogLength = new JSONArray(user.getBlog()).length() - 1;
            int podcastLength = new JSONArray(user.getPodcast()).length() - 1;
            int wpLength = new JSONArray(user.getWhitepaper()).length() - 1;
            int ratgeberLength = new JSONArray(user.getRatgeber()).length() - 1;
            int mainLength = new JSONArray(user.getMain()).length() - 1;
            int amountOfFooters = 8;
            int footerLength = new JSONArray(user.getUeber()).length() + new JSONArray(user.getImpressum()).length() + new JSONArray(user.getPreisliste()).length() + new JSONArray(user.getPartner()).length() + new JSONArray(user.getDatenschutz()).length() + new JSONArray(user.getNewsletter()).length() + new JSONArray(user.getImage()).length() + new JSONArray(user.getAgb()).length() - amountOfFooters;

            //Update ClicksBy for User
            updateClicksByIpLiveDay(ip);

            //Update average-clicks for the deleted user, if user had clicks
            if(user.getAmount_of_clicks() > 0) {
                UniversalAverageClicksDLC uniAvg = uniAverageClicksRepo.getLatest();

                int oldClicks = uniAvg.getAmount_clicks();
                uniAvg.setAmount_clicks(uniAvg.getAmount_clicks() + user.getAmount_of_clicks());
                uniAvg.setAmount_users(uniAvg.getAmount_users() + 1);

                int clicks = uniAvg.getAmount_clicks();

                if(mainLength == clicks && clicks >= 15) {
                    JSONArray json = null;
                    if(getClass().getResource("blacklist.json") != null) {
                        json = new JSONArray(getClass().getResource("blacklist.json").getPath());
                        json.put(ip);

                        try {
                            try (FileWriter writer = new FileWriter(getClass().getResource("blacklist.json").getPath())) {
                                writer.write(json.toString());
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                uniAvg.setArticle(((uniAvg.getArticle() * oldClicks) + articleLength) / clicks);
                uniAvg.setNews(((uniAvg.getNews() * oldClicks) + newsLength) / clicks);
                uniAvg.setBlog(((uniAvg.getBlog() * oldClicks) + blogLength) / clicks);
                uniAvg.setPodcast(((uniAvg.getPodcast() * oldClicks) + podcastLength) / clicks);
                uniAvg.setWhitepaper(((uniAvg.getWhitepaper() * oldClicks) + wpLength) / clicks);
                uniAvg.setRatgeber(((uniAvg.getRatgeber() * oldClicks) + ratgeberLength) / clicks);
                uniAvg.setMain(((uniAvg.getMain() * oldClicks) + mainLength) / clicks);
                uniAvg.setFooter(((uniAvg.getFooter() * oldClicks) + footerLength) / clicks);

                uniAverageClicksRepo.save(uniAvg);

                //Update time spent for the deleted user, if user spent time
                if(user.getTime_spent() > 0) {
                    UniversalTimeSpentDLC uniTime = uniTimeSpentRepo.getLatest();
                    uniTime.setAmount_clicks(uniTime.getAmount_clicks() + user.getAmount_of_clicks());
                    uniTime.setAmount_users(uniTime.getAmount_users() + 1);

                    clicks = uniTime.getAmount_clicks();

                    uniTime.setTotal_time(uniTime.getTotal_time() + user.getTime_spent());

                    uniTime.setArticle(user.getTime_spent() * ((float) articleLength / clicks));
                    uniTime.setNews(user.getTime_spent() * ((float) newsLength / clicks));
                    uniTime.setBlog(user.getTime_spent() * ((float) blogLength / clicks));
                    uniTime.setPodcast(user.getTime_spent() * ((float) podcastLength / clicks));
                    uniTime.setWhitepaper(user.getTime_spent() * ((float) wpLength / clicks));
                    uniTime.setRatgeber(user.getTime_spent() * ((float) ratgeberLength / clicks));
                    uniTime.setMain(user.getTime_spent() * ((float) mainLength / clicks));
                    uniTime.setFooter(user.getTime_spent() * ((float) footerLength / clicks));

                    uniTimeSpentRepo.save(uniTime);
                }
            }
            uniqueUserRepo.delete(uniqueUserRepo.findByIP(ip));
        }
    }

    private void permanentifyAllUsers() throws JSONException {
        for(UniqueUser user : uniqueUserRepo.findAll()) {
            String ip = user.getIp();
            permanentifyUser(ip);
        }
    }

    private void initUniAverages() {
        UniversalAverageClicksDLC uniAverage = new UniversalAverageClicksDLC();
        uniAverage.setArticle(0);
        uniAverage.setBlog(0);
        uniAverage.setMain(0);
        uniAverage.setPodcast(0);
        uniAverage.setFooter(0);
        uniAverage.setNews(0);
        uniAverage.setRatgeber(0);
        uniAverage.setWhitepaper(0);
        uniAverage.setAmount_clicks(0);
        uniAverage.setAmount_users(0);
        uniAverage.setUni_stat_id(uniRepo.getLatestUniStat().getId());
        uniAverageClicksRepo.save(uniAverage);
    }

    private void initUniTime() {
        UniversalTimeSpentDLC uniTime = new UniversalTimeSpentDLC();
        uniTime.setArticle(0);
        uniTime.setBlog(0);
        uniTime.setMain(0);
        uniTime.setPodcast(0);
        uniTime.setFooter(0);
        uniTime.setNews(0);
        uniTime.setRatgeber(0);
        uniTime.setWhitepaper(0);
        uniTime.setAmount_clicks(0);
        uniTime.setAmount_users(0);
        uniTime.setTotal_time(0);
        uniTime.setUni_stat_id(uniRepo.getLatestUniStat().getId());
        uniTimeSpentRepo.save(uniTime);
    }
}