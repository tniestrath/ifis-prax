package com.analysetool.services;

import com.analysetool.api.PostController;
import com.analysetool.api.UserController;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
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
import java.io.IOException;
import java.net.URLDecoder;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private PostMetaRepository postMetaRepo;

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
    private UserController userController;

    @Autowired
    private UniversalAverageClicksDLCRepository uniAverageClicksRepo;

    @Autowired
    private UniversalTimeSpentDLCRepository uniTimeSpentRepo;

    @Autowired
    private PostClicksByHourDLCService postClicksByHourDLCService;

    @Autowired
    private ContentDownloadsHourlyRepository contentDownloadsHourlyRepo;
    @Autowired
    private EventsRepository eventRepo;
    @Autowired
    private FinalSearchStatService finalSearchService;
    @Autowired
    private TemporarySearchStatService temporarySearchService;
    @Autowired
    private OutgoingSocialsRedirectsRepository outgoingSocialsRepo;
    @Autowired
    private TrackingBlacklistRepository tbRepo;

    private final CommentsRepository commentRepo;
    private final SysVarRepository sysVarRepo;

    private BufferedReader br;
    private String path = "";
    //^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}) regex für ip matching
    //private final String BlogSSPattern = "^.*GET /blog/(\\S+)/.*s=(\\S+)\".*"; //search +1, view +1,(bei match) vor blog view pattern
    private final String ArtikelSSPattern = "^.*GET /artikel/([^/]+)/.*s=([^&\"]+)\"";
    private final String PresseSSViewPatter = "^.*GET /news/([^/]+)/.*s=([^&\"]+)\"";
    private final String BlogSSPattern = "^.*GET /blog/([^/]+)/.*s=([^&\"]+)\"";
    private final String WhitepaperSSPattern = "^.*GET /whitepaper/([^/]+)/.*s=([^&\"]+)\"";//search +1, view +1,(bei match) vor artikel view pattern
    private final String AnbieterSSView = "^.*GET /user/([^/]+)/.*s=([^&\"]+)\"";
    //private final String ArtikelSSPattern = "^.*GET /artikel/([^ ]+)/.*[?&]s=([^&\"]+).*";

    //private String BlogViewPattern = "^.*GET \/blog\/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
   // private final String WhitepaperSSPattern = "^.*GET /whitepaper/(\\S+)/.*s=(\\S+)\".*";

    private final String blogCategoryPattern= "^.*GET /blog/";
    private final String BlogViewPattern = "^.*GET /blogeintrag/(\\S+)/";
    private final String RedirectPattern = "/.*GET .*goto=.*\"(https?:/.*/(artikel|blog|news)/(\\S*)/)";
    private final String UserViewPattern="^.*GET /user/(\\S+)/";

    //Blog view +1 bei match
    //private String ArtikelViewPattern = "^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*GET /artikel/(\\S+)";//Artikel view +1 bei match
    private final String ArtikelViewPattern = "^.*GET /artikel/(\\S+)/";

    private final String ratgeberView = "^.*GET /ratgeber/cyber-risk-check/(\\S+)/";

    private final String ratgeberGlossarView = "^.*GET /ratgeber/glossar-cyber-sicherheit/";

    private final String ratgeberBuchView = "^.*GET /ratgeber/cyber-sicherheit/";

    private final String ratgeberSelfView = "^.*GET /selbstlernangebot-it-sicherheit/";

    private final String NewsViewPatter = "^.*GET /news/(\\S+)/";

    private final String WhitepaperViewPattern = "^.*GET /whitepaper/(\\S+)/";

    private final String contentDownload = "^.*\"GET /wp-content/uploads/\\d{4}/\\d{2}/([^ ]+)\\.pdf";

    //private final String ContentDownload = "^.*GET /wp-content/uploads/[\\d]{4}/[\\d]{2}/(\\S+?)(-\\d{3})?.pdf";

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

    private final String RedirectUserPattern =".*GET .*goto/(.*) HTTP/";

    private final String outgoingRedirectLinkedIn ="^.*GET /goto/https://(www.)?linkedin.com/company/marktplatz-it-sicherheit*";


    private final String outgoingRedirectFacebook ="^.*GET /goto/https://(www.)?facebook.com/Internet.Sicherheit.ifis*";

    private final String outgoingRedirectYoutube =".*^GET /goto/https://(www.)?youtube.com/user/InternetSicherheitDE*";

    private final String outgoingRedirectTwitter =".*^GET /goto/https://(www.)?twitter.com/_securitynews*";

    private final String postImpressionFacebook = "^.*GET /artikel|blogeintrag|news/([^/]+)/.*[facebookexternalhit/1.1 (\\+http://www.facebook.com/externalhit_uatext.php)]";
    private final String postImpressionLinkedin="^.*GET /artikel|blogeintrag|news/([^/]+)/.*linkedin|LinkedIn";
    private final String postImpressionTwitter="^.*GET /artikel|blogeintrag|news/([^/]+)/.*[Twitterbot/1.0]";
    private final String postImpressionTwitterFacebookCombo="^.*GET /artikel|blogeintrag|news/([^/]+)/.*facebookexternalhit/1.1 Facebot Twitterbot/1.0";
    private final String userImpressionFacebook="^.*GET /user/([^/]+)/.*[facebookexternalhit/1.1 (\\+http://www.facebook.com/externalhit_uatext.php)]";
    private final String userImpressionLinkedin="^.*GET /user/([^/]+)/.*linkedin|LinkedIn";
    private final String userImpressionTwitter=".*GET /user/([^/]+)/.*[Twitterbot/1.0]";
    private final String userImpressionTwitterFacebookCombo="^.*GET /user/([^/]+)/.*facebookexternalhit/1.1 Facebot Twitterbot/1.0";

    private final String eventView="^.*GET /veranstaltungen/(\\S+)/";

    private final String eventSSView="^.*GET /veranstaltungen/([^/]+)/.*s=([^&\"]+)\"";

    final Pattern articleViewPattern = Pattern.compile(ArtikelViewPattern);
    final Pattern articleSearchSuccessPattern = Pattern.compile(ArtikelSSPattern);
    final Pattern blogCategory = Pattern.compile(blogCategoryPattern);
    final Pattern blogViewPattern = Pattern.compile(BlogViewPattern);
    final Pattern blogSearchSuccessPattern = Pattern.compile(BlogSSPattern);
    Pattern redirectPattern = Pattern.compile(RedirectPattern);
    final Pattern userViewPattern = Pattern.compile(UserViewPattern);
    final Pattern newsViewPattern = Pattern.compile(NewsViewPatter);
    final Pattern newsSearchSuccessPattern = Pattern.compile(PresseSSViewPatter);
    final Pattern userRedirectPattern = Pattern.compile(RedirectUserPattern);
    Pattern searchPattern = Pattern.compile(SearchPattern);
    final Pattern patternWhitepaperView = Pattern.compile(WhitepaperViewPattern);
    final Pattern patternWhitepaperSearchSuccess = Pattern.compile(WhitepaperSSPattern);
    final Pattern patternPreMatch = Pattern.compile(prePattern);
    Pattern reffererPattern=Pattern.compile(ReffererPattern);
    final Pattern mainPagePattern = Pattern.compile(mainPage);
    final Pattern ueberPattern = Pattern.compile(ueber);
    final Pattern impressumPattern = Pattern.compile(impressum);
    final Pattern agbsPattern = Pattern.compile(agbs);
    final Pattern datenschutzerklaerungPattern = Pattern.compile(datenschutzerklaerung);
    final Pattern preislistePattern = Pattern.compile(preisliste);
    final Pattern partnerPattern = Pattern.compile(partner);
    final Pattern newsletterPattern = Pattern.compile(newsletter);
    final Pattern imagePattern = Pattern.compile(image);
    final Pattern ratgeberPostViewPattern = Pattern.compile(ratgeberView);
    final Pattern ratgeberGlossarViewPattern = Pattern.compile(ratgeberGlossarView);
    final Pattern ratgeberBuchViewPattern = Pattern.compile(ratgeberBuchView);
    final Pattern ratgeberSelfViewPattern = Pattern.compile(ratgeberSelfView);
    final Pattern podcastViewPattern = Pattern.compile(PodcastViewPattern);
    final Pattern contentDownloadPattern= Pattern.compile(contentDownload);
    final Pattern eventViewPattern = Pattern.compile(eventView);
    final Pattern eventSSPattern = Pattern.compile(eventSSView);
    final Pattern anbieterSSPattern = Pattern.compile(AnbieterSSView);
    final Pattern outgoingRedirectPatternLinkedin = Pattern.compile(outgoingRedirectLinkedIn);
    final Pattern outgoingRedirectPatternTwitter = Pattern.compile(outgoingRedirectTwitter);
    final Pattern outgoingRedirectPatternFacebook = Pattern.compile(outgoingRedirectFacebook);
    final Pattern outgoingRedirectPatternYoutube = Pattern.compile(outgoingRedirectYoutube);
    final Pattern postImpressionFacebookPattern=Pattern.compile(postImpressionFacebook);
    final Pattern postImpressionTwitterPattern=Pattern.compile(postImpressionTwitter);
    final Pattern postImpressionLinkedinPattern=Pattern.compile(postImpressionLinkedin);
    final Pattern postImpressionFacebookTwitterComboPattern=Pattern.compile(postImpressionTwitterFacebookCombo);
    final Pattern userImpressionFacebookPattern=Pattern.compile(userImpressionFacebook);
    final Pattern userImpressionTwitterPattern=Pattern.compile(userImpressionTwitter);
    final Pattern userImpressionLinkedInPattern=Pattern.compile(userImpressionLinkedin);
    final Pattern userImpressionTwitterFacebookComboPattern=Pattern.compile(userImpressionTwitterFacebookCombo);
    private String lastLine = "";
    private int lineCounter = 0;
    private int lastLineCounter = 0;
    private boolean liveScanning ;

    //Set User-Agents that shouldn't be counted as click
    final String[] blacklistUserAgents = {
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

    private final DashConfig config;
    private final String Pfad;

    private final Calendar kalender = Calendar.getInstance();
    private final int aktuellesJahr = kalender.get(Calendar.YEAR);
    @Autowired
    private SearchStatsRepository searchStatRepo;

    private final HashMap<String,ArrayList<LocalDateTime>> userViewTimes= new HashMap<>();
    private final HashMap<String, Integer> impressions = new HashMap<>();
    @Autowired
    private universalStatsRepository uniRepo;
    @Autowired
    private UniversalStatsHourlyRepository uniHourlyRepo;

    @Autowired
    private UserViewsByHourDLCRepository userViewHourDLCRepo;

    @Autowired
    private UserViewsByHourDLCService userViewsByHourDLCService;

    @Autowired
    private ContentDownloadsHourlyService contentDownloadsHourlyService;

    @Autowired
    private UserRedirectsHourlyService userRedirectService;

    @Autowired
    private UserRedirectsHourlyRepository userRedirectRepo;

    private final Map<String, UserViewsByHourDLC> userViewsHourDLCMap = new HashMap<>();
    private final Map<String, ContentDownloadsHourly> contentDownloadsMap = new HashMap<>();

    private final Map<String,PostClicksByHourDLC> postClicksMap = new HashMap<>();
    private final Map<String,UserRedirectsHourly> userRedirectsMap = new HashMap<>();

    private final Map<String, List<FinalSearchStatDLC>> searchDLCMap = new ConcurrentHashMap<>();


    @Autowired
    public LogService(PostRepository postRepository, PostStatsRepository PostStatsRepository, TagStatRepository tagStatRepo, WpTermRelationshipsRepository termRelRepo, WPTermRepository termRepo, WpTermTaxonomyRepository termTaxRepo, WPUserRepository wpUserRepo, UserStatsRepository userStatsRepo, CommentsRepository commentRepo, SysVarRepository sysVarRepo, DashConfig config) {
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

    public void run(boolean liveScanning, String path,SysVar SystemVariabeln) throws ParseException {
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
        int sensibleClicks = 0;

        int viewsArticle = 0;
        int viewsNews = 0;
        int viewsBlog = 0;
        int viewsPodcast = 0;
        int viewsWhitepaper = 0;
        int viewsRatgeber = 0;
        int viewsRatgeberPost = 0;
        int viewsRatgeberGlossar = 0;
        int viewsRatgeberBuch = 0;
        int viewsRatgeberSelf = 0;
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
        int userRatgeberSelf = 0;
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

        String last_ip = null;
        String last_request = null;
        List<String> blacklist2 = tbRepo.getAllIps();

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

                //Does it match a content-download
                Matcher matched_content_download= contentDownloadPattern.matcher(line);

                //Filter für Request-Types.
                boolean isDevAccess = request.contains("/api/")
                        || (request.contains("/wp-content") && !matched_content_download.find()) || request.contains("/wp-includes")
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

                List<String> notNonsense = new ArrayList<>();
                notNonsense.add("author");
                notNonsense.add("author");

                boolean isNotNonsense = false;

                for(String entry : notNonsense) {
                    isNotNonsense = request.contains(entry);
                }


                //Schaue, ob der UserAgent auf der Blacklist steht.
                boolean isBlacklisted = false;
                for (String item : blacklistUserAgents) {
                    isBlacklisted = userAgent.matches("^.*" + item + ".*") && !isBlacklisted;
                }
                if(blacklist2 != null) {
                    for (String blacklistItem : blacklist2) {
                        isBlacklisted = isBlacklisted || ip.equals(blacklistItem);
                    }
                }
                //Falls keiner der Filter zutrifft und der Teil des Logs noch nicht gelesen wurde, behandle die Zeile.
                if ((dateLog.isAfter(dateLastRead) || dateLog.isEqual(dateLastRead)) && !isDevAccess && !isInternal && !isServerError && !isBlacklisted && isSuccessfulRequest && !isSpam && isGet) {

                    sysVar.setLastTimeStamp(dateFormatter.format(dateLog));

                    //erhöhe Clicks und Besucher, falls anwendbar
                    totalClicks++;

                    //Does it match an article-type?
                    Matcher matched_articleView = articleViewPattern.matcher(request);
                    Matcher matched_articleSearchSuccess = articleSearchSuccessPattern.matcher(line);

                    //Does it match a blog-type?
                    Matcher matched_blogView = blogViewPattern.matcher(request);
                    Matcher matched_blogCat = blogViewPattern.matcher(request);
                    Matcher matched_blogSearchSuccess = blogSearchSuccessPattern.matcher(line);

                    //Does it match a news-type?
                    Matcher matched_newsView = newsViewPattern.matcher(request);
                    Matcher matched_newsSearchSuccess = newsSearchSuccessPattern.matcher(line);

                    //Does it match a whitepaper-type?
                    Matcher matched_whitepaperView = patternWhitepaperView.matcher(request);
                    Matcher matched_whitepaperSearchSuccess = patternWhitepaperSearchSuccess.matcher(line);

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

                    //Does it match a ratgeber-self-help view
                    Matcher matched_ratgeber_self = ratgeberSelfViewPattern.matcher(request);

                    //Does it match a podcast-view
                    Matcher matched_podcast_view = podcastViewPattern.matcher(request);

                    //Does it match a content-download
                    //Matcher matched_content_download= contentDownloadPattern.matcher(line);
                    matched_content_download= contentDownloadPattern.matcher(line);
                    //Does it match an event-View?
                    Matcher matched_event_view= eventViewPattern.matcher(request);

                    //Does it match an event Search Success?
                    Matcher matched_event_search_success= eventSSPattern.matcher(line);

                    //Does it match an user Search Success?
                    Matcher matched_user_search_success= anbieterSSPattern.matcher(line);

                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_linkedin_redirect= outgoingRedirectPatternLinkedin.matcher(request);

                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_facebook_redirect= outgoingRedirectPatternFacebook.matcher(request);
                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_twitter_redirect= outgoingRedirectPatternTwitter.matcher(request);
                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_youtube_redirect= outgoingRedirectPatternYoutube.matcher(request);
                    //Does it match user-redirect?
                    Matcher matched_userRedirect = userRedirectPattern.matcher(request);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_facebook=postImpressionFacebookPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_twitter=postImpressionTwitterPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_LinkedIn=postImpressionLinkedinPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_FacebookTwitterCombo=postImpressionFacebookTwitterComboPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_facebook=userImpressionFacebookPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_twitter=userImpressionTwitterPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_LinkedIn=userImpressionLinkedInPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_FacebookTwitterCombo=userImpressionTwitterFacebookComboPattern.matcher(line);

                    //Find out which pattern matched
                    String whatMatched = "";
                    Matcher patternMatcher = null;

                    if(matched_articleSearchSuccess.find()) {
                        whatMatched = "articleSS";
                        patternMatcher = matched_articleSearchSuccess;
                    } else if(matched_blogSearchSuccess.find()) {
                        whatMatched = "blogSS";
                        patternMatcher = matched_blogSearchSuccess;
                    } else if(matched_newsSearchSuccess.find()) {
                        whatMatched = "newsSS";
                        patternMatcher = matched_newsSearchSuccess;
                    } else if(matched_whitepaperSearchSuccess.find()) {
                        whatMatched = "wpSS";
                        patternMatcher = matched_whitepaperSearchSuccess;
                    } else if(matched_event_search_success.find()) {
                        whatMatched = "eventSS";
                        patternMatcher = matched_event_search_success;
                    } else if(matched_user_search_success.find()) {
                        whatMatched = "userSS";
                        patternMatcher = matched_user_search_success;
                    } else if(matched_post_impression_facebook.find()) {
                        whatMatched = "postImpressionFacebook";
                        patternMatcher = matched_post_impression_facebook;
                    }else if(matched_post_impression_twitter.find()) {
                        whatMatched = "postImpressionTwitter";
                        patternMatcher = matched_post_impression_twitter;
                    } else if(matched_post_impression_LinkedIn.find()) {
                        whatMatched = "postImpressionLinkedIn";
                        patternMatcher = matched_post_impression_LinkedIn;
                    } else if(matched_post_impression_FacebookTwitterCombo.find()) {
                        whatMatched = "postImpressionFacebookTwitterCombo";
                        patternMatcher = matched_post_impression_FacebookTwitterCombo;
                    } else if(matched_user_impression_facebook.find()) {
                        whatMatched = "userImpressionFacebook";
                        patternMatcher = matched_user_impression_facebook;
                    }else if(matched_user_impression_twitter.find()) {
                        whatMatched = "userImpressionTwitter";
                        patternMatcher = matched_user_impression_twitter;
                    } else if(matched_user_impression_LinkedIn.find()) {
                        whatMatched = "userImpressionLinkedIn";
                        patternMatcher = matched_user_impression_LinkedIn;
                    } else if(matched_user_impression_FacebookTwitterCombo.find()) {
                        whatMatched = "userImpressionFacebookTwitterCombo";
                        patternMatcher = matched_user_impression_FacebookTwitterCombo;
                    }  else if(matched_articleView.find()) {
                        whatMatched = "articleView";
                        patternMatcher = matched_articleView;
                    } else if(matched_blogView.find() || matched_blogCat.find()) {
                        if(matched_blogView.find()) {
                            whatMatched = "blogView";
                            patternMatcher = matched_blogView;
                        } else {
                            whatMatched = "blogCat";
                        }
                    } else if(matched_newsView.find()) {
                        whatMatched = "newsView";
                        patternMatcher = matched_newsView;
                    } else if(matched_whitepaperView.find()) {
                        whatMatched = "wpView";
                        patternMatcher = matched_whitepaperView;
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
                    }else if(matched_ratgeber_self.find()){
                        whatMatched = "ratgeberSelf";
                        patternMatcher = matched_ratgeber_self;
                    } else if(matched_content_download.find()){
                        whatMatched = "contentDownload";
                        patternMatcher = matched_content_download;
                    } else if(matched_outgoing_linkedin_redirect.find()){
                        whatMatched = "socialsLinkedInRedirect";
                        patternMatcher = matched_outgoing_linkedin_redirect;
                    }else if(matched_outgoing_twitter_redirect.find()){
                        whatMatched = "socialsTwitterRedirect";
                        patternMatcher = matched_outgoing_twitter_redirect;
                    }else if(matched_outgoing_facebook_redirect.find()){
                        whatMatched = "socialsFacebookRedirect";
                        patternMatcher = matched_outgoing_facebook_redirect;
                    }else if(matched_outgoing_youtube_redirect.find()){
                        whatMatched = "socialsYouTubeRedirect";
                        patternMatcher = matched_outgoing_youtube_redirect;
                    } else if(matched_event_view.find()){
                        whatMatched = "eventView";
                        patternMatcher = matched_event_view;
                    }else if(matched_userRedirect.find()){
                        whatMatched = "userRedirect";
                        patternMatcher = matched_userRedirect;
                    }

                    //If the user is unique, AND has made a sensible request, mark him as unique and add him as a unique user.
                    if(isUnique) {
                        uniqueUsers++;
                        initUniqueUser(ip, dateLog);
                    }

                    switch (whatMatched) {
                        case "articleView", "articleSS" -> {
                            //Erhöhe Clicks für Artikel um 1.
                            viewsArticle++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userArticle++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getArticle()).length() < 2) {
                                    userArticle++;
                                }
                            }
                            updateUniqueUser(ip, "article", dateLog);
                        }

                        case "blogView", "blogSS", "blogCat" -> {
                            //Erhöhe Clicks für Blog um 1.
                            viewsBlog++;

                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userBlog++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getBlog()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getNews()).length() < 2) {
                                    userNews++;
                                }
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getWhitepaper()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getPodcast()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getMain()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getUeber()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getImpressum()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getPreisliste()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getPartner()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getDatenschutz()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getNewsletter()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getImage()).length() < 2) {
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
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getAgb()).length() < 2) {
                                    userAGBS++;
                                }
                            }
                            updateUniqueUser(ip, "agb", dateLog);
                        }
                        case "userView" -> {
                            try {
                                if(wpUserRepo.findByNicename(patternMatcher.group(1)).isPresent()) {
                                    updateUserStats(wpUserRepo.findByNicename(patternMatcher.group(1)).get().getId(), dateLog);
                                }
                            } catch (Exception e) {
                                System.out.println(patternMatcher.group(1));
                            }
                        }
                        case "ratgeberPost", "ratgeberGlossar", "ratgeberBuch", "ratgeberSelf" -> {
                            //Erhöhe Clicks für RatgeberViews um 1.
                            viewsRatgeber++;
                            //Wenn der user unique ist, erstelle eine Zeile in UniqueUser
                            if (isUnique) {
                                userRatgeber++;
                            } else {
                                if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() < 2) {
                                    userRatgeber++;
                                }
                            }
                            updateUniqueUser(ip, "ratgeber", dateLog);

                            //Update stats for more concrete type of Ratgeber
                            switch (whatMatched) {
                                case "ratgeberPost" -> {
                                    viewsRatgeberPost++;
                                    if (isUnique) {
                                        userRatgeberPost++;
                                    } else {
                                        if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() < 2) {
                                            userRatgeberPost++;
                                        }
                                    }
                                }
                                case "ratgeberGlossar" -> {
                                    viewsRatgeberGlossar++;
                                    if (isUnique) {
                                        userRatgeberGlossar++;
                                    } else {
                                        if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() < 2) {
                                            userRatgeberGlossar++;
                                        }
                                    }
                                }
                                case "ratgeberBuch" -> {
                                    viewsRatgeberBuch++;
                                    if (isUnique) {
                                        userRatgeberBuch++;
                                    } else {
                                        if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() < 2) {
                                            userRatgeberBuch++;
                                        }
                                    }
                                }
                                case "ratgeberSelf" -> {
                                    viewsRatgeberSelf++;
                                    if (isUnique) {
                                        userRatgeberSelf++;
                                    } else {
                                        if (new JSONArray(uniqueUserRepo.findByIP(ip).getRatgeber()).length() < 2) {
                                            userRatgeberSelf++;
                                        }
                                    }
                                }
                            }


                        } case "userRedirect" -> {
                        } case "eventView" -> {
                        }
                        default -> {
                            if(!isNotNonsense) {
                                updateUniqueUser(ip, "nonsense", dateLog);
                                System.out.println(line);
                            }
                        }
                    }

                    if(!whatMatched.equals("")) {
                        sensibleClicks++;
                    }

                    processLine(line, ip, whatMatched, dateLog, patternMatcher);

                } else if((dateLog.isAfter(dateLastRead) || dateLog.isEqual(dateLastRead))) {
                    //noinspection StatementWithEmptyBody
                    if(isBlacklisted) {
                        //System.out.println("BANNED!!!!!!!!! : " + line);
                    }
                    //noinspection StatementWithEmptyBody
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
        updateUniStats(totalClicks, internalClicks, sensibleClicks, viewsArticle, viewsNews, viewsBlog, viewsPodcast, viewsWhitepaper, viewsRatgeber,viewsRatgeberPost, viewsRatgeberGlossar, viewsRatgeberBuch, viewsRatgeberSelf, viewsMain, viewsUeber, viewsAGBS, viewsImpressum, viewsPreisliste, viewsPartner, viewsDatenschutz, viewsNewsletter, viewsImage, uniqueUsers, userArticle, userNews, userBlog, userPodcast, userWhitepaper, userRatgeber, userRatgeberPost, userRatgeberGlossar, userRatgeberBuch, userRatgeberSelf, userMain, userUeber, userAGBS, userImpressum, userPreisliste, userPartner, userDatenschutz, userNewsletter, userImage, serverErrors);
        //Service weil Springs AOP ist whack und batch operationen am besten extern aufgerufen werden sollen
        userViewsByHourDLCService.persistAllUserViewsHour(userViewsHourDLCMap);
        postClicksByHourDLCService.persistAllPostClicksHour(postClicksMap);
        userRedirectService.persistAllUserRedirectsHourly(userRedirectsMap);
        //maps clearen nur um sicher zu gehen
        cleanMaps();
        updateFinalSearchStatsAndTemporarySearchStats();
    }

    private void updateUniStats(int totalClicks, int internalClicks, int sensibleClicks, int viewsArticle, int viewsNews, int viewsBlog, int viewsPodcast, int viewsWhitepaper, int viewsRatgeber, int viewsRatgeberPost, int viewsRatgeberGlossar, int viewsRatgeberBuch, int viewsRatgeberSelf,  int viewsMain, int viewsUeber, int viewsAGBS, int viewsImpressum, int viewsPreisliste, int viewsPartner, int viewsDatenschutz, int viewsNewsletter, int viewsImage, int uniqueUsers, int userArticle, int userNews, int userBlog, int userPodcast, int userWhitepaper, int userRatgeber, int userRatgeberPost, int userRatgeberGlossar, int userRatgeberBuch, int userRatgeberSelf, int userMain, int userUeber, int userAGBS, int userImpressum, int userPreisliste, int userPartner, int userDatenschutz, int userNewsletter, int userImage, int serverErrors) throws ParseException {
        Date dateTime = Calendar.getInstance().getTime();
        String dateStirng = Calendar.getInstance().get(Calendar.YEAR) + "-";
        dateStirng += Calendar.getInstance().get(Calendar.MONTH) + 1  < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : Calendar.getInstance().get(Calendar.MONTH) + 1;
        dateStirng += "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String uniLastDateString = sdf.format(uniRepo.getLatestUniStat().getDatum());
        Date date = sdf.parse(dateStirng);
        System.out.println("Date Stirng" + dateStirng);
        System.out.println("uniLastDateString" + uniLastDateString);
        System.out.println(dateStirng.equalsIgnoreCase(uniLastDateString));
        final int curHour = LocalDateTime.now().getHour();

        //Updating UniversalStats
        {
            UniversalStats uni;
            {
                if (dateStirng.equalsIgnoreCase(uniLastDateString)) {
                    uni = uniRepo.findTop1ByOrderByDatumDesc();
                    uni.setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());
                    uni.setSensibleClicks(uni.getSensibleClicks() + sensibleClicks);
                    uni.setTotalClicks(uni.getTotalClicks() + totalClicks);
                    uni.setInternalClicks(uni.getInternalClicks() + internalClicks);
                    uni.setServerErrors(uni.getServerErrors() + serverErrors);
                    uni.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uni = setNewsArticelBlogCountForUniversalStats(date, uni);
                    uni = setAccountTypeAllUniStats(uni);
                } else {
                    uni = new UniversalStats();
                    uni.setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());
                    uni.setSensibleClicks((long) sensibleClicks);
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
                uniHourly1.setSensibleClicks((long) sensibleClicks / 4);
                uniHourly1.setTotalClicks((long) totalClicks / 4);
                uniHourly1.setInternalClicks(internalClicks / 4);
                uniHourly1.setServerErrors(serverErrors / 4);
                uniHourly1.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly1);
                setAccountTypeAllUniStats(uniHourly1);

                uniHourly2.setStunde(2);
                uniHourly2.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly2.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly2.setSensibleClicks((long) sensibleClicks / 4);
                uniHourly2.setTotalClicks((long) totalClicks / 4);
                uniHourly2.setInternalClicks(internalClicks / 4);
                uniHourly2.setServerErrors(serverErrors / 4);
                uniHourly2.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly2);
                setAccountTypeAllUniStats(uniHourly2);

                uniHourly3.setStunde(3);
                uniHourly3.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly3.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly3.setSensibleClicks((long) sensibleClicks / 4);
                uniHourly3.setTotalClicks((long) totalClicks / 4);
                uniHourly3.setInternalClicks(internalClicks / 4);
                uniHourly3.setServerErrors(serverErrors / 4);
                uniHourly3.setAnbieterProfileAnzahl(wpUserRepo.count());
                setNewsArticelBlogCountForUniversalStats(uniHourly3);
                setAccountTypeAllUniStats(uniHourly3);

                uniHourly4.setUniStatId(uniRepo.getLatestUniStat().getId());
                uniHourly4.setBesucherAnzahl((long) uniqueUsers / 4);
                uniHourly4.setTotalClicks((long) sensibleClicks / 4);
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
                    uniHourly.setSensibleClicks((long) sensibleClicks);
                    uniHourly.setTotalClicks((long) totalClicks);
                    uniHourly.setInternalClicks(internalClicks);
                    uniHourly.setServerErrors(serverErrors);
                } else {
                    //Since stats for the current hour are the last that were created, update it.
                    uniHourly = uniHourlyRepo.getLast();
                    //Identifiers already exist, so skip to updating stats.
                    uniHourly.setBesucherAnzahl(uniHourly.getBesucherAnzahl() + (long) uniqueUsers);
                    uniHourly.setSensibleClicks(uniHourly.getSensibleClicks() + (long) sensibleClicks);
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
                        cat.setBesucherGlobal(uniqueUsers / 4);
                        cat.setBesucherArticle(userArticle / 4);
                        cat.setBesucherNews(userNews / 4);
                        cat.setBesucherBlog(userBlog / 4);
                        cat.setBesucherPodcast(userPodcast / 4);
                        cat.setBesucherWhitepaper(userWhitepaper / 4);
                        cat.setBesucherRatgeber(userRatgeber / 4);
                        cat.setBesucherRatgeber(userRatgeberPost / 4);
                        cat.setBesucherRatgeber(userRatgeberGlossar / 4);
                        cat.setBesucherRatgeber(userRatgeberBuch / 4);
                        cat.setBesucherRatgeberSelf(userRatgeberSelf / 4);
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
                        cat.setViewsRatgeberSelf(viewsRatgeberSelf / 4);
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
                    uniCategories.setBesucherGlobal(uniqueUsers);
                    uniCategories.setBesucherArticle(userArticle);
                    uniCategories.setBesucherNews(userNews);
                    uniCategories.setBesucherBlog(userBlog);
                    uniCategories.setBesucherPodcast(userPodcast);
                    uniCategories.setBesucherWhitepaper(userWhitepaper);
                    uniCategories.setBesucherRatgeber(userRatgeber);
                    uniCategories.setBesucherRatgeberPost(userRatgeberPost);
                    uniCategories.setBesucherRatgeberGlossar(userRatgeberGlossar);
                    uniCategories.setBesucherRatgeberBuch(userRatgeberBuch);
                    uniCategories.setBesucherRatgeberSelf(userRatgeberSelf);
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
                    uniCategories.setViewsRatgeberSelf(viewsRatgeberSelf);
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
                uniCategories.setBesucherGlobal(uniCategories.getBesucherGlobal() + uniqueUsers - userArticle - userNews - userBlog - userPodcast - userWhitepaper - userRatgeber - userRatgeberSelf - userMain - userUeber - userImpressum - userPreisliste - userPartner - userDatenschutz - userNewsletter - userImage - userAGBS);
                uniCategories.setBesucherArticle(uniCategories.getBesucherArticle() + userArticle);
                uniCategories.setBesucherNews(uniCategories.getBesucherNews() + userNews);
                uniCategories.setBesucherBlog(uniCategories.getBesucherBlog() + userBlog);
                uniCategories.setBesucherPodcast(uniCategories.getBesucherPodcast() + userPodcast);
                uniCategories.setBesucherWhitepaper(uniCategories.getBesucherWhitepaper() + userWhitepaper);
                uniCategories.setBesucherRatgeber(uniCategories.getBesucherRatgeber() + userRatgeber);
                uniCategories.setBesucherRatgeberPost(uniCategories.getBesucherRatgeberPost() + userRatgeberPost);
                uniCategories.setBesucherRatgeberGlossar(uniCategories.getBesucherRatgeberGlossar() + userRatgeberGlossar);
                uniCategories.setBesucherRatgeberBuch(uniCategories.getBesucherRatgeberBuch() + userRatgeberBuch);
                uniCategories.setBesucherRatgeberSelf(uniCategories.getBesucherRatgeberSelf() + userRatgeberSelf);
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
                uniCategories.setViewsRatgeberSelf(viewsRatgeberSelf + uniCategories.getViewsRatgeberSelf());
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

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    private void setAccountTypeAllUniStats(UniversalStatsHourly uniHourly) {
        HashMap<String, Integer> counts = new HashMap<>();

        for(WPUser user : wpUserRepo.findAll()) {
            switch(userController.getType(Math.toIntExact(user.getId()))) {
                case "basis" -> {
                    counts.put("Basic", counts.get("Basic") == null ? 1 : counts.get("Basic") + 1);
                }
                case "basis-plus" -> {
                    counts.put("Basic-Plus", counts.get("Basic-Plus") == null ? 1 : counts.get("Basic-Plus") + 1);
                }
                case "plus" -> {
                    counts.put("Plus", counts.get("Plus") == null ? 1 : counts.get("Plus") + 1);
                }
                case "premium" -> {
                    counts.put("Premium", counts.get("Premium") == null ? 1 : counts.get("Premium") + 1);
                }
                case "sponsor" -> {
                    counts.put("Sponsor", counts.get("Sponsor") == null ? 1 : counts.get("Sponsor") + 1);
                }
                case "none" -> {
                    counts.put("Anbieter", counts.get("Anbieter") == null ? 1 : counts.get("Anbieter") + 1);
                }
            }
        }

        uniHourly.setAnbieter_abolos_anzahl(counts.getOrDefault("Anbieter", 0));
        uniHourly.setAnbieterBasicAnzahl(counts.getOrDefault("Basic",0));
        uniHourly.setAnbieterBasicPlusAnzahl(counts.getOrDefault("Basic-Plus",0));
        uniHourly.setAnbieterPlusAnzahl(counts.getOrDefault("Plus",0));
        uniHourly.setAnbieterPremiumAnzahl(counts.getOrDefault("Premium",0));
        uniHourly.setAnbieterPremiumSponsorenAnzahl(counts.getOrDefault("Sponsor",0));

    }

    private void setNewsArticelBlogCountForUniversalStats(UniversalStatsHourly uniHourly) {

        List<Post> posts = postRepository.findAllUserPosts();

        long artikelCounter = 0;
        long newsCounter = 0;
        long blogCounter = 0;

        int tagIdBlog = termRepo.findBySlug(Constants.getInstance().getBlogSlug()).getId().intValue();
        int tagIdArtikel = termRepo.findBySlug(Constants.getInstance().getArtikelSlug()).getId().intValue();
        int tagIdPresse = termRepo.findBySlug(Constants.getInstance().getNewsSlug()).getId().intValue();

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


        List<UserStats> userStats = userStatsRepo.findAll();
        userStatsRepo.saveAll(userStats);


        //Delete Post-Types for Posts, that no longer exist
        deleteNullPostTypes();
        //Just in case permanentify failed
        deleteOldIPs();
    }

    /**
     * Deletes Post-Type rows for Posts, that no longer exist.
     */
    private void deleteNullPostTypes() {
        for(PostTypes type : postTypeRepo.findAll()) {
            if(!postRepository.existsById(type.getPost_id())) {
                postTypeRepo.delete(type);
            }
        }
    }


    public void processLine(String line, String ip, String whatMatched, LocalDateTime dateLog, Matcher patternMatcher) {
        lastLine = line;

        switch(whatMatched) {
            case "articleView", "blogView", "newsView", "wpView", "ratgeberPost", "podView":
                try {
                    UpdatePerformanceAndViews(dateLog, postRepository.getIdByName(patternMatcher.group(1)));
                    updateIPsByPost(ip, postRepository.getIdByName(patternMatcher.group(1)));
                    updatePostClicksMap(postRepository.getIdByName(patternMatcher.group(1)),dateLog);
                } catch (Exception e) {
                    System.out.println("VIEW PROCESS LINE EXCEPTION " + line);
                }
                break;
            case "articleSS", "blogSS", "newsSS", "wpSS":
                try {
                    Long postId = postRepository.getIdByName(patternMatcher.group(1));
                    updateSearchDLCMap(ip,patternMatcher.group(2),postId,dateLog,"post");
                    updatePostClicksMap(postId,dateLog);
                    UpdatePerformanceAndViews(dateLog, postId);
                } catch(Exception e) {
                    System.out.println("SS PROCESS LINE EXCEPTION " +line);
                }
                break;
            case "eventSS":
                try {
                    if(eventRepo.getActiveEventBySlug(patternMatcher.group(1).replace("+","-")).isPresent()){
                        long postId = eventRepo.getActiveEventBySlug(patternMatcher.group(1).replace("+","-")).get().getPostID();
                        updateSearchDLCMap(ip,patternMatcher.group(2),postId,dateLog,"post");
                        updateIPsByPost(ip, postId);
                        updatePostClicksMap(postId,dateLog);
                        UpdatePerformanceAndViews(dateLog, postId);
                    }
                }   catch (Exception e) {
                    System.out.println("EVENTSS EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
                break;
            case "userSS":
                try {
                    if(wpUserRepo.findByNicename(patternMatcher.group(1).replace("+","-")).isPresent()) {
                        Long userId = wpUserRepo.findByNicename(patternMatcher.group(1).replace("+","-")).get().getId();
                        updateSearchDLCMap(ip,patternMatcher.group(2),userId,dateLog,"user");
                        updateUserStats(userId,dateLog);
                        updateIPsByUser(ip, userId);
                    }
                } catch (Exception e) {
                    System.out.println("USERSS EXCEPTION BEI: " + line);
                    e.printStackTrace();
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
            case "contentDownload":
                try {
                    System.out.println("FOUND CONTENT DOWNLOAD \n\n");
                    //Edit Filename to make sure very similar files still work as intended
                    String filename = "/" + patternMatcher.group(1) + ".pdf";
                    if(postRepository.getParentFromListAnd(postMetaRepo.getAllWhitepaperFileAttachmentPostIds(), filename).isPresent()) {
                        long id = postRepository.getParentFromListAnd(postMetaRepo.getAllWhitepaperFileAttachmentPostIds(), filename).get();
                        ContentDownloadsHourly download;
                        if(contentDownloadsHourlyRepo.getByPostIdUniIdHour(id, uniRepo.getLatestUniStat().getId(), LocalDateTime.now().getHour()).isEmpty()) {
                            download = new ContentDownloadsHourly();
                            download.setUniId(uniRepo.getLatestUniStat().getId());
                            download.setHour(LocalDateTime.now().getHour());
                            download.setPostId(postRepository.getParentFromListAnd(postMetaRepo.getAllWhitepaperFileAttachmentPostIds(), filename).get());
                            download.setDownloads(1L);
                        } else {
                            download = contentDownloadsHourlyRepo.getByPostIdUniIdHour(id, uniRepo.getLatestUniStat().getId(), LocalDateTime.now().getHour()).get();
                            download.setDownloads(download.getDownloads() + 1);
                        }
                        contentDownloadsHourlyRepo.save(download);
                    }
                }
                catch (Exception e){
                    System.out.println("CONTENT DOWNLOAD EXCEPTION BEI: "+ line);
                }
                break;
            case "userRedirect":
                try {
                    if(wpUserMetaRepository.getUserByURL(patternMatcher.group(1)) != null) {
                        UserRedirectsHourly redirects;
                        if(userRedirectRepo.getByUniIdAndHourAndUserId(uniRepo.getLatestUniStat().getId(), dateLog.getHour(), wpUserMetaRepository.getUserByURL(patternMatcher.group(1))).isPresent()) {
                           redirects = userRedirectRepo.getByUniIdAndHourAndUserId(uniRepo.getLatestUniStat().getId(), dateLog.getHour(), wpUserMetaRepository.getUserByURL(patternMatcher.group(1))).get();
                       } else {
                           redirects = new UserRedirectsHourly();
                           redirects.setHour(dateLog.getHour());
                           redirects.setUniId(uniRepo.getLatestUniStat().getId());
                           redirects.setUserId(wpUserMetaRepository.getUserByURL(patternMatcher.group(1)));
                           redirects.setRedirects(0L);
                       }
                        redirects.setRedirects(redirects.getRedirects() + 1);
                        userRedirectRepo.save(redirects);
                    }
                }
                catch (Exception e) {
                    System.out.println("USERREDIRECT EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
                break;
            case "socialsLinkedInRedirect","socialsTwitterRedirect","socialsYouTubeRedirect","socialsFacebookRedirect":
                try {
                        Integer latestUniId= uniRepo.getLatestUniStat().getId();
                        Integer hour = dateLog.getHour();
                        OutgoingSocialsRedirects redirects;

                        if(outgoingSocialsRepo.findByUniIdAndHour(latestUniId,hour).isPresent()) {
                            redirects = outgoingSocialsRepo.findByUniIdAndHour(latestUniId,hour).get();
                        } else {
                            redirects = new OutgoingSocialsRedirects();
                            redirects.setHour(dateLog.getHour());
                            redirects.setUniId(uniRepo.getLatestUniStat().getId());
                            redirects.setLinkedin(0L);
                            redirects.setFacebook(0L);
                            redirects.setTwitter(0L);
                            redirects.setYoutube(0L);

                        }

                        outgoingSocialsRepo.save( updateSocialsRedirects(whatMatched , redirects));

                }
                catch (Exception e) {
                    System.out.println("SOCIALSREDIRECT EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
                break;
            case "eventView":
                try {
                    if(eventRepo.getActiveEventBySlug(patternMatcher.group(1).replace("+","-")).isPresent()){
                        long postId = eventRepo.getActiveEventBySlug(patternMatcher.group(1).replace("+","-")).get().getPostID();
                        UpdatePerformanceAndViews(dateLog, postId);
                        updateIPsByPost(ip, postId);
                        updatePostClicksMap(postId,dateLog);
                    }
                }
                catch (Exception e) {
                    System.out.println("EVENTVIEW EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
                break;
            case "agb", "image", "newsletter", "datenschutz", "partner", "preisliste", "impressum", "ueber", "main", "ratgeberBuch", "ratgeberGlossar":

            default:
                break;
        }

    }

    public String hashIp(String ip){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512(); // 512-bit output
        byte[] hashBytes = digestSHA3.digest(ip.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(hashBytes);
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

        return LocalTime.of(Integer.parseInt(logHour), Integer.parseInt(logMinute), Integer.parseInt(logSecond));
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
            long views = Stats.getProfileView() + 1 ;
            Stats.setProfileView(views);
            userStatsRepo.save(Stats);
        }else {
            userStatsRepo.save(new UserStats(id, 1));
        }
        updateUserViewsByHourDLCList(id,dateLog);
    }

    public void updateUserViewsByHourDLCList(long userId,LocalDateTime dateLog){
        int uniId = uniRepo.getLatestUniStat().getId();
        String key = uniId + "_" + userId;

        UserViewsByHourDLC userViews = userViewsHourDLCMap.get(key);

        if (userViews != null) {

            userViews.setViews(userViews.getViews() + 1);

        } else {

            UserViewsByHourDLC newUserViews = new UserViewsByHourDLC(uniId,userId,dateLog.getHour(),1L);

            userViewsHourDLCMap.put(key, newUserViews);
        }

    }

    public void updateContentDownloadMap(Long postId,LocalDateTime dateLog){
        int uniId = uniRepo.getLatestUniStat().getId();
        String key = uniId + "_" + postId;

        ContentDownloadsHourly contentDownloads = contentDownloadsMap.get(key);
        if (contentDownloads != null) {

            contentDownloads.setDownloads(contentDownloads.getDownloads() + 1);

        } else {

            ContentDownloadsHourly newContentDownload = new ContentDownloadsHourly(uniId,postId,dateLog.getHour(),1L);

            contentDownloadsMap.put(key, newContentDownload);
        }

    }

    public void updatePostClicksMap(Long postId,LocalDateTime dateLog){
        int uniId = uniRepo.getLatestUniStat().getId();
        String key = uniId + "_" + postId;

        PostClicksByHourDLC postClicks = postClicksMap.get(key);
        if (postClicks != null) {

            postClicks.setClicks(postClicks.getClicks() + 1);

        } else {

            PostClicksByHourDLC newPostClicks = new PostClicksByHourDLC(uniId,postId,dateLog.getHour(),1L);

            postClicksMap.put(key, newPostClicks);
        }

    }

    public void updateUserRedirectsMap(Long userId, LocalDateTime dateLog) {
        int uniId = uniRepo.getLatestUniStat().getId();
        String key = uniId + "_" + userId;

        UserRedirectsHourly userRedirects = userRedirectsMap.get(key);
        if (userRedirects != null) {
            userRedirects.setRedirects(userRedirects.getRedirects() + 1);
        } else {
            UserRedirectsHourly newUserRedirects = new UserRedirectsHourly(uniId, userId, dateLog.getHour(), 1L);
            userRedirectsMap.put(key, newUserRedirects);
        }
    }

    public void cleanMaps(){
        userRedirectsMap.clear();
        postClicksMap.clear();
        contentDownloadsMap.clear();
        userViewsHourDLCMap.clear();
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
        TagStat Stats;
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
        return dateFormat.format(vortag);
    }

    public static String getDay(int zuruek){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -zuruek); // Vortag
        Date vortag = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(vortag);
    }

    public static String generateLogFileNameLastDay() {
       return "access.log-" + getLastDay() + ".gz";
    }

    public UniversalStats setNewsArticelBlogCountForUniversalStats(UniversalStats uniStats){

        List<Post> posts = postRepository.findAllUserPosts();

        long artikelCounter = 0 ;
        long newsCounter =0;
        long blogCounter = 0;
        long whitepaperCounter = 0;
        long podcastCounter = 0;

        int tagIdBlog = termRepo.findBySlug(Constants.getInstance().getBlogSlug()).getId().intValue();
        int tagIdArtikel = termRepo.findBySlug(Constants.getInstance().getArtikelSlug()).getId().intValue();
        int tagIdPodcast = termRepo.findBySlug(Constants.getInstance().getPodastSlug()).getId().intValue();
        int tagIdWhitepaper = termRepo.findBySlug(Constants.getInstance().getWhitepaperSlug()).getId().intValue();
        int tagIdPresse = termRepo.findBySlug(Constants.getInstance().getNewsSlug()).getId().intValue();

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

                        if (termTax.getTermId() == tagIdWhitepaper) {
                            whitepaperCounter++ ;
                        }

                        if (termTax.getTermId() == tagIdPodcast) {
                            podcastCounter++ ;
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

        int tagIdBlog = termRepo.findBySlug(Constants.getInstance().getBlogSlug()).getId().intValue();
        int tagIdArtikel = termRepo.findBySlug(Constants.getInstance().getArtikelSlug()).getId().intValue();
        int tagIdPodcast = termRepo.findBySlug(Constants.getInstance().getPodastSlug()).getId().intValue();
        int tagIdWhitepaper = termRepo.findBySlug(Constants.getInstance().getWhitepaperSlug()).getId().intValue();
        int tagIdPresse = termRepo.findBySlug(Constants.getInstance().getNewsSlug()).getId().intValue();

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

                        if (termTax.getTermId() == tagIdWhitepaper) {
                            whiteCounter++;
                        }

                        if (termTax.getTermId() == tagIdPodcast) {
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

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    public UniversalStats setAccountTypeAllUniStats(UniversalStats uniStats){
        HashMap<String, Integer> counts = new HashMap<>();

        for(WPUser user : wpUserRepo.findAll()) {
            switch(userController.getType(Math.toIntExact(user.getId()))) {
                case "basis" -> {
                    counts.put("Basic", counts.get("Basic") == null ? 1 : counts.get("Basic") + 1);
                }
                case "basis-plus" -> {
                    counts.put("Basic-Plus", counts.get("Basic-Plus") == null ? 1 : counts.get("Basic-Plus") + 1);
                }
                case "plus" -> {
                    counts.put("Plus", counts.get("Plus") == null ? 1 : counts.get("Plus") + 1);
                }
                case "premium" -> {
                    counts.put("Premium", counts.get("Premium") == null ? 1 : counts.get("Premium") + 1);
                }
                case "sponsor" -> {
                    counts.put("Sponsor", counts.get("Sponsor") == null ? 1 : counts.get("Sponsor") + 1);
                }
                case "none" -> {
                    counts.put("Anbieter", counts.get("Anbieter") == null ? 1 : counts.get("Anbieter") + 1);
                }
            }
        }

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
        PostGeo postGeo;
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
        UserGeo userGeo;
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
        postTypeRepo.deleteAll(postTypeRepo.getDefault());
        for(Integer id : postRepository.getIdsOfUntyped()) {
            PostTypes type = new PostTypes();
            type.setPost_id(Long.valueOf(id));
            type.setType(postController.getType(id));
            postTypeRepo.save(type);
        }
    }

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    private void updateUniqueUser(String ip, String category, LocalDateTime clickTime) throws JSONException {
        UniqueUser user = uniqueUserRepo.findByIP(ip);
        //Check whether user has clicked within last hour, if not, permanentify the user and open a new User.
        if(clickTime.isAfter(user.getFirst_click().plusMinutes(20).plusSeconds(user.getTime_spent()))) {
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
            case "nonsense" -> {
                user.setNonsense(new JSONArray(user.getNonsense()).put(clicks).toString());
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
        user.setNonsense(new JSONArray().put(0).toString());
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

                uniAvg.setArticle(((uniAvg.getArticle() * oldClicks) + articleLength) / clicks);
                uniAvg.setNews(((uniAvg.getNews() * oldClicks) + newsLength) / clicks);
                uniAvg.setBlog(((uniAvg.getBlog() * oldClicks) + blogLength) / clicks);
                uniAvg.setPodcast(((uniAvg.getPodcast() * oldClicks) + podcastLength) / clicks);
                uniAvg.setWhitepaper(((uniAvg.getWhitepaper() * oldClicks) + wpLength) / clicks);
                uniAvg.setRatgeber(((uniAvg.getRatgeber() * oldClicks) + ratgeberLength) / clicks);
                uniAvg.setMain(((uniAvg.getMain() * oldClicks) + mainLength) / clicks);
                uniAvg.setFooter(((uniAvg.getFooter() * oldClicks) + footerLength) / clicks);

                uniAverageClicksRepo.save(uniAvg);

                //Update time spent for the deleted user if user spent time
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



    public void updateSearchDLCMap(String ip, String searchQuery, Long Id, LocalDateTime dateLog, String matchCase) {

        int uniId = uniRepo.getLatestUniStat().getId();
        String cleanedQuery = searchQuery;

        try {
            cleanedQuery = URLDecoder.decode(searchQuery, StandardCharsets.UTF_8);
            cleanedQuery = cleanedQuery.toLowerCase();
            // Entfernen von nicht-alphanumerischen Zeichen außer Leerzeichen
            cleanedQuery = cleanedQuery.replaceAll("[^a-z0-9 ]", "");

            //  Ersetzen von mehreren aufeinanderfolgenden Leerzeichen durch ein einzelnes Leerzeichen
            cleanedQuery = cleanedQuery.replaceAll("\\s+", " ").trim();
        } catch (Exception e) {
            System.out.println("Fehler beim Decodieren des URL-Strings, weiter mit codierter Suchanfrage :" + e.getMessage());
        }

        String key = ip + "_" + cleanedQuery;
        List<FinalSearchStatDLC> searchDLCList = searchDLCMap.getOrDefault(key, new ArrayList<>());

        FinalSearchStatDLC newSearchDLC = new FinalSearchStatDLC(uniId, dateLog.getHour());

        switch (matchCase) {
            case "user" -> newSearchDLC.setUserId(Id);
            case "post" -> newSearchDLC.setPostId(Id);
        }

        searchDLCList.add(newSearchDLC);
        searchDLCMap.put(key, searchDLCList);
    }



    public void linkSearchSuccessesWithSearches(List<TemporarySearchStat> tempSearches, List<FinalSearchStat> finalSearches) {
        // Erstelle zuerst eine Map für die Zuordnung von TempID zu FinalSearchStat
        System.out.println(searchDLCMap);
        Map<Long, FinalSearchStat> tempIdToFinalSearchMap = new ConcurrentHashMap<>();
        for (FinalSearchStat finalSearch : finalSearches) {
            tempIdToFinalSearchMap.put(finalSearch.getTempId(), finalSearch);
        }

        // Durchlaufen aller DLC-Objekte und aktualisieren mit den entsprechenden FinalSearchStat-IDs
        for (TemporarySearchStat tempSearch : tempSearches) {

            String cleanedQuery = tempSearch.getSearchQuery().toLowerCase();
            // Entfernen von nicht-alphanumerischen Zeichen außer Leerzeichen
            cleanedQuery = cleanedQuery.replaceAll("[^a-z0-9 ]", "");

            // Optional: Ersetzen von mehreren aufeinanderfolgenden Leerzeichen durch ein einzelnes Leerzeichen
            cleanedQuery = cleanedQuery.replaceAll("\\s+", " ").trim();

            String key = tempSearch.getSearchIp() + "_" + cleanedQuery;
            List<FinalSearchStatDLC> searchDLCList = searchDLCMap.get(key);
            System.out.println(key);
            if (searchDLCList != null) {
                for (FinalSearchStatDLC searchDLC : searchDLCList) {
                    FinalSearchStat matchingFinalSearch = tempIdToFinalSearchMap.get(tempSearch.getId());
                    if (matchingFinalSearch != null) {
                        searchDLC.setFinalSearchId(matchingFinalSearch.getId());
                    }
                }
            }
        }
        Boolean saveSuccess = finalSearchService.saveAllDLCBooleanFromMap(searchDLCMap);
        System.out.println("SearchDLC save success: " + saveSuccess);
    }



    private void updateFinalSearchStatsAndTemporarySearchStats(){
        //List<TemporarySearchStat> alleTempSearches= temporarySearchService.getAllSearchStat();
        CopyOnWriteArrayList<TemporarySearchStat> alleTempSearches= temporarySearchService.getAllSearchStatConcurrent();
        List<TemporarySearchStat> zuEntfernendeTemSearches = new CopyOnWriteArrayList<>();
        List<FinalSearchStat> zuSpeicherndeFinalSearches = new CopyOnWriteArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int hour;
        int uniId = uniRepo.getLatestUniStat().getId();

        for(TemporarySearchStat stat: alleTempSearches){
           String country = IPHelper.getCountryISO(stat.getSearchIp());
           String state = IPHelper.getSubISO(stat.getSearchIp());
           String city = IPHelper.getCityName(stat.getSearchIp());
           calendar.setTime(stat.getDate());
           hour = calendar.get(Calendar.HOUR_OF_DAY);
           String searchQuery = stat.getSearchQuery().toLowerCase();

           FinalSearchStat search = new FinalSearchStat(uniId,hour,country,state,city,stat.getFoundArtikelCount(),stat.getFoundBlogCount(),stat.getFoundNewsCount(),stat.getFoundWhitepaperCount(),stat.getFoundRatgeberCount(),stat.getFoundPodcastCount(),stat.getFoundAnbieterCount(),stat.getFoundEventsCount(),searchQuery, stat.getId());
           if(!zuSpeicherndeFinalSearches.contains(search)){
           zuSpeicherndeFinalSearches.add(search);
           }else{zuEntfernendeTemSearches.add(stat);}
        }
        System.out.println(zuSpeicherndeFinalSearches);
        Boolean saveSuccess = finalSearchService.saveAllBoolean(zuSpeicherndeFinalSearches);
        System.out.println(saveSuccess);
        if(saveSuccess){
            alleTempSearches.removeAll(zuEntfernendeTemSearches);
            linkSearchSuccessesWithSearches(alleTempSearches,zuSpeicherndeFinalSearches);
            Boolean deleteSuccess =temporarySearchService.deleteAllSearchStatBooleanIn(alleTempSearches);
            if(!deleteSuccess){
                System.out.println("Löschen von TemporarySearch mit Ids zwischen "+alleTempSearches.get(0).getId()+" und "+alleTempSearches.get(alleTempSearches.size()-1).getId()+" nicht Möglich!");
            }
            System.out.println("Final Search Stats saved ! Temporary Search Stats deleted !");
        }else{
            System.out.println("Erstellen/Speichern von FinalSearch der TemporarySearches mit Ids zwischen "+alleTempSearches.get(0).getId()+" und "+alleTempSearches.get(alleTempSearches.size()-1).getId()+" nicht Möglich!");
        }
    }


    private OutgoingSocialsRedirects updateSocialsRedirects(String whatMatched ,  OutgoingSocialsRedirects redirects){
        Long counter;

        //System.out.println(whatMatched);

        switch (whatMatched) {
            case "socialsLinkedInRedirect" -> {
                counter = redirects.getLinkedin();
                counter++;
                //System.out.println("COOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOUUUUUUUUUUUUUUUUUUUUUUUUUNNNNNNNNNNNNNNNNTTTTTTTTERRRR----------------------->"+counter);
                redirects.setLinkedin(counter);
            }
            case "socialsFacebookRedirect" -> {
                counter = redirects.getFacebook();
                counter++;
                //System.out.println("COOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOUUUUUUUUUUUUUUUUUUUUUUUUUNNNNNNNNNNNNNNNNTTTTTTTTERRRR----------------------->"+counter);
                redirects.setFacebook(counter);
            }
            case "socialsTwitterRedirect" -> {
                counter = redirects.getTwitter();
                counter++;
                //System.out.println("COOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOUUUUUUUUUUUUUUUUUUUUUUUUUNNNNNNNNNNNNNNNNTTTTTTTTERRRR----------------------->"+counter);
                redirects.setTwitter(counter);
            }
            case "socialsYouTubeRedirect" -> {
                counter = redirects.getYoutube();
                counter++;
                //System.out.println("COOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOUUUUUUUUUUUUUUUUUUUUUUUUUNNNNNNNNNNNNNNNNTTTTTTTTERRRR----------------------->"+counter);
                redirects.setYoutube(counter);
            }
        }
        //System.out.println("UPPPPDAAAATEEE REEEEDDIRREEEEEECCCCTTTTT----------------------------->>>>> "+redirects);
        return redirects;
    }
}