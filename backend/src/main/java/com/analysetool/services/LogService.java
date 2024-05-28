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
    @Autowired
    private SocialsImpressionsService socialsImpressionsService;
    @Autowired
    private AnbieterSearchRepository anbieterSearchRepo;
    @Autowired
    private AnbieterFailedSearchBufferRepository anbieterSearchFailRepo;
    @Autowired
    private GeoNamesPostalRepository geoNamesRepo;
    @Autowired
    private FinalSearchStatDLCRepository fDLCRepo;
    @Autowired
    private TagCatStatRepository tagCatRepo;

    @Autowired
    private RankingTotalProfileRepository rankingTotalProfileRepo;
    @Autowired
    private RankingTotalContentRepository rankingTotalContentRepo;
    @Autowired
    private RankingGroupProfileRepository rankingGroupProfileRepo;
    @Autowired
    private RankingGroupContentRepository rankingGroupContentRepo;

    private final CommentsRepository commentRepo;
    private final SysVarRepository sysVarRepo;

    private BufferedReader br;
    private String path = "";
    //^(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}) regex für ip matching
    //private final String BlogSSPattern = "^.*GET /blog/(\\S+)/.*s=(\\S+)\".*"; //search +1, view +1,(bei match) vor blog view pattern
    private final String ArtikelSSPattern = "^.*GET /artikel/([^/]+)/.*s=([^&\"]+)\"";

    private final String BlackHolePattern = "^.*GET /?blackhole";
    private final String PresseSSViewPatter = "^.*GET /news/([^/]+)/.*s=([^&\"]+)\"";
    private final String BlogSSPattern = "^.*GET /blog/([^/]+)/.*s=([^&\"]+)\"";
    private final String WhitepaperSSPattern = "^.*GET /whitepaper/([^/]+)/.*s=([^&\"]+)\"";//search +1, view +1,(bei match) vor artikel view pattern
    private final String AnbieterSSView = "^.*GET /user/([^/]+)/.*s=([^&\"]+)\"";
    //private final String ArtikelSSPattern = "^.*GET /artikel/([^ ]+)/.*[?&]s=([^&\"]+).*";

    //private String BlogViewPattern = "^.*GET \/blog\/.* HTTP/1\\.1\" 200 .*$\n";//Blog view +1 bei match
   // private final String WhitepaperSSPattern = "^.*GET /whitepaper/(\\S+)/.*s=(\\S+)\".*";
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

    private final String ratgeberSelfViewAlterEgo = "^.*GET /Selbstlernakademie/sec-aware-nrw-scorm12-8DUduEVd/scormcontent/index\\.html";

    private final String NewsViewPatter = "^.*GET /news/(\\S+)/";

    private final String WhitepaperViewPattern = "^.*GET /whitepaper/(\\S+)/";

    private final String contentDownload = "^.*\"GET /wp-content/uploads/\\d{4}/\\d{2}/([^ ]+)\\.pdf";

    //private final String ContentDownload = "^.*GET /wp-content/uploads/[\\d]{4}/[\\d]{2}/(\\S+?)(-\\d{3})?.pdf";

    private final String PodcastViewPattern = "^.*GET /podcast/(\\S+)/";

    private final String videoViewPatter = "^.*GET /videos/(\\S+)/";


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

    private final String postImpressionFacebook = "^.*GET /(artikel|blogeintrag|news)/([^/]+).*facebookexternalhit.*";
    private final String postImpressionLinkedin="^.*GET /(artikel|blogeintrag|news)/([^/]+).*(linkedin|LinkedIn|LinkedInBot).*";
    private final String postImpressionTwitter="^.*GET /(artikel|blogeintrag|news)/([^/]+).*Twitterbot/1.0.*";
    private final String postImpressionTwitterFacebookCombo="^.*GET /(artikel|blogeintrag|news)/([^/]+).*(?=.*facebookexternalhit)(?=.*Twitterbot).*";
    private final String userImpressionFacebook="^.*GET /user/([^/]+).*facebookexternalhit.*";
    private final String userImpressionLinkedin="^.*GET /user/([^/]+).*(linkedin|LinkedIn|LinkedInBot).*";
    private final String userImpressionTwitter=".*GET /user/([^/]+).*Twitterbot.*";
    private final String userImpressionTwitterFacebookCombo="^.*GET /user/([^/]+).*(?=.*facebookexternalhit)(?=.*Twitterbot).*";

    private final String eventCatView = "^.*GET /veranstaltungen/";
    private final String eventView="^.*GET /veranstaltungen/(\\S+)/";

    private final String eventSSView="^.*GET /veranstaltungen/([^/]+)/.*s=([^&\"]+)\"";

    private final String anbieterVerzeichnisPatter = "^.*GET /anbieterverzeichnis/";

    private final String tagCatPatter = "GET \\/themenfeld\\/([^\\/]+)\\/";

   private final String forumDiskussionsthemen = "^.*GET /marktplatz-forum/([^/]+)/ HTTP.*";

   private final String forumTopic = "^.*GET /marktplatz-forum/[^/]+/([^/]+)/.* HTTP.*";

   private final String forumSearch= "^.*GET /marktplatz-forum/\\?wpfs=(.*?)&wpfin=(.*?)&wpfd=(.*?)&wpfob=(.*?)&wpfo=(.*?)&wpfpaged=(.*?)(?:\\s|$)";

    final Pattern forumDiskussionsthemenPattern = Pattern.compile(forumDiskussionsthemen);
    final Pattern forumTopicPattern = Pattern.compile(forumTopic);
    final Pattern articleViewPattern = Pattern.compile(ArtikelViewPattern);

    final Pattern blackHoleTrapPattern = Pattern.compile(BlackHolePattern);
    final Pattern articleSearchSuccessPattern = Pattern.compile(ArtikelSSPattern);

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
    final Pattern ratgeberSelfViewPatternAlterEgoPattern = Pattern.compile(ratgeberSelfViewAlterEgo);
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
    final Pattern videoViewPattern = Pattern.compile(videoViewPatter);
    final Pattern eventCatPattern = Pattern.compile(eventCatView);
    final Pattern anbieterCatPattern = Pattern.compile(anbieterVerzeichnisPatter);
    final Pattern tagCatPattern = Pattern.compile(tagCatPatter);

    final Pattern forumSearchPattern= Pattern.compile(forumSearch);

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
    @Autowired
    private ForumService forumService;

    private final Map<String, UserViewsByHourDLC> userViewsHourDLCMap = new HashMap<>();
    private final Map<String, ContentDownloadsHourly> contentDownloadsMap = new HashMap<>();

    private final Map<String,PostClicksByHourDLC> postClicksMap = new HashMap<>();
    private final Map<String,UserRedirectsHourly> userRedirectsMap = new HashMap<>();

    private final Map<String, List<FinalSearchStatDLC>> searchDLCMap = new ConcurrentHashMap<>();

    private final Map<Integer,ForumDiskussionsthemenClicksByHour> forumDiscussionClicksMap = new ConcurrentHashMap<>();

    private final Map<Integer,ForumTopicsClicksByHour> forumTopicsClicksMap = new ConcurrentHashMap<>();

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

    /**
     * returns a LocalDateTime representation of the creation date for the given file.
     * common use is for the access.log
     * @param filePath the path to the file.
     * @return the creation DateTime of the file as a LocalDateTime Object.
     */
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

    /**
     * Initializes and starts a "run" - which is the process of reading the new entries in access.log.
     * Otherwise, an exact duplicate of runScheduled.
     * @throws ParseException .
     */
    @PostConstruct
    public void init() throws ParseException {
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

    /**
     * A Scheduled CRON-Job that starts reading all new lines in access.log and calculates data out from them.
     * @throws ParseException if parsing th access.log fails.
     */
    @Scheduled(cron = "0 0 * * * *") //einmal die Stunde
    //@Scheduled(cron = "0 */2 * * * *") //alle 2min
    public void runScheduled() throws ParseException {
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

    /**
     * Prepares for the actual reading of lines, then reads lines.
     * Additionally, after the reading is finished, different types of clean-up duties are performed.
     * @param liveScanning sets whether the current reading is happening on the live-log, or not.
     * @param path the filePath to the access.log that should be read.
     * @param SystemVariabeln a collection of internal data used in determining where to start reading.
     * @throws ParseException if parsing the given file fails.
     */
    public void run(boolean liveScanning, String path,SysVar SystemVariabeln) throws ParseException {
        this.liveScanning = liveScanning;
        this.path = path;
        lastLineCounter=SystemVariabeln.getLastLineCount();
        lastLine = SystemVariabeln.getLastLine();
        lineCounter = 0;
        new Constants(postTypeRepo, termRepo);
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

        updatePostTypes();

        doAutoClean();

        if(LocalDateTime.now().getHour() == 5) {
            endDay();
        }
        sysVarRepo.save(SystemVariabeln);
    }


    /**
     * Reads through all eligible lines, filters out the unusable, then prepares and continues evaluating data from each line.
     * Behaves as an identifier and distributor of more specific tasks.
     * @param sysVar a collection of internal data used in determining where to start reading.
     * @throws IOException .
     * @throws ParseException .
     * @throws JSONException .
     */
    @SuppressWarnings("GrazieInspection")
    public void findAMatch(SysVar sysVar) throws IOException, ParseException, JSONException {
        String line;

        int totalClicks = 0;
        int internalClicks = 0;
        int sensibleClicks = 0;

        int viewsArticle = 0;
        int viewsNews = 0;
        int viewsBlog = 0;
        int viewsPodcast = 0;
        int viewsVideos = 0;
        int viewsWhitepaper = 0;
        int viewsEvents = 0;
        int viewsRatgeber = 0;
        int viewsRatgeberPost = 0;
        int viewsRatgeberGlossar = 0;
        int viewsRatgeberBuch = 0;
        int viewsRatgeberSelf = 0;
        int viewsMain = 0;
        int viewsAnbieter = 0;
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
        int userVideos = 0;
        int userWhitepaper = 0;
        int userEvents = 0;
        int userRatgeber = 0;
        int userRatgeberPost = 0;
        int userRatgeberGlossar = 0;
        int userRatgeberBuch = 0;
        int userRatgeberSelf = 0;
        int userMain = 0;
        int userAnbieter = 0;
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
                notNonsense.add("securitynews");
                notNonsense.add("ifis-news");
                notNonsense.add("/veranstaltungen/");

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

                    //Does it match a forum discussion view
                    Matcher matched_forum_topic_view = forumTopicPattern.matcher(request);

                    //Does it match a forum discussion view
                    Matcher matched_forum_discussion_view = forumDiskussionsthemenPattern.matcher(request);

                    //Does it match an article-type?
                    Matcher matched_articleView = articleViewPattern.matcher(request);
                    Matcher matched_articleSearchSuccess = articleSearchSuccessPattern.matcher(line);

                    //You activated my Trap
                    Matcher matched_blackHole = blackHoleTrapPattern.matcher(request);

                    //Does it match a blog-type?
                    Matcher matched_blogView = blogViewPattern.matcher(request);
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


                    //Does it match a ratgeber-subpost-view?
                    Matcher matched_ratgeber_post = ratgeberPostViewPattern.matcher(request);

                    //Does it match a ratgeber-glossar view?
                    Matcher matched_ratgeber_glossar = ratgeberGlossarViewPattern.matcher(request);

                    //Does it match a ratgeber-buch view?
                    Matcher matched_ratgeber_buch = ratgeberBuchViewPattern.matcher(request);

                    //Does it match a ratgeber-self-help view?
                    Matcher matched_ratgeber_self = ratgeberSelfViewPattern.matcher(request);
                    Matcher matched_ratgeber_self_alter = ratgeberSelfViewPatternAlterEgoPattern.matcher(request);

                    //Does it match a podcast-view?
                    Matcher matched_podcast_view = podcastViewPattern.matcher(request);

                    //Does it match a content-download?
                    //Matcher matched_content_download= contentDownloadPattern.matcher(line);
                    matched_content_download = contentDownloadPattern.matcher(line);
                    //Does it match an event-View?
                    Matcher matched_event_view = eventViewPattern.matcher(request);
                    //Does it match an event-category view?
                    Matcher matched_event_cat = eventCatPattern.matcher(request);
                    //Does it match an event Search Success?
                    Matcher matched_event_search_success = eventSSPattern.matcher(line);

                    //Does it match a user Search Success?
                    Matcher matched_user_search_success = anbieterSSPattern.matcher(line);

                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_linkedin_redirect = outgoingRedirectPatternLinkedin.matcher(request);

                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_facebook_redirect = outgoingRedirectPatternFacebook.matcher(request);
                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_twitter_redirect = outgoingRedirectPatternTwitter.matcher(request);
                    //Does it match an outgoing socials redirect?
                    Matcher matched_outgoing_youtube_redirect = outgoingRedirectPatternYoutube.matcher(request);
                    //Does it match user-redirect?
                    Matcher matched_userRedirect = userRedirectPattern.matcher(request);
                    //Does it match a video-view?
                    Matcher matched_videoView = videoViewPattern.matcher(request);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_facebook = postImpressionFacebookPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_twitter = postImpressionTwitterPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_LinkedIn = postImpressionLinkedinPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_post_impression_FacebookTwitterCombo = postImpressionFacebookTwitterComboPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_facebook = userImpressionFacebookPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_twitter = userImpressionTwitterPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_LinkedIn = userImpressionLinkedInPattern.matcher(line);
                    //Does it match a socials impression?
                    Matcher matched_user_impression_FacebookTwitterCombo = userImpressionTwitterFacebookComboPattern.matcher(line);

                    //Does it match a anbieterverzeichnis-view?
                    Matcher matched_anbieterverzeichnis_view = anbieterCatPattern.matcher(request);

                    //Does it match a tag-category view? (/themenfeld/)
                    Matcher matched_tagcat_view = tagCatPattern.matcher(request);
                    //Does it match a forum search?
                    Matcher matched_forum_search = forumSearchPattern.matcher(request);

                    //Find out which pattern matched
                    String whatMatched = "";
                    Matcher patternMatcher = null;

                    if (matched_articleSearchSuccess.find()) {
                        whatMatched = "articleSS";
                        patternMatcher = matched_articleSearchSuccess;
                    } else if (matched_blogSearchSuccess.find()) {
                        whatMatched = "blogSS";
                        patternMatcher = matched_blogSearchSuccess;
                    } else if (matched_newsSearchSuccess.find()) {
                        whatMatched = "newsSS";
                        patternMatcher = matched_newsSearchSuccess;
                    } else if (matched_whitepaperSearchSuccess.find()) {
                        whatMatched = "wpSS";
                        patternMatcher = matched_whitepaperSearchSuccess;
                    } else if (matched_event_search_success.find()) {
                        whatMatched = "eventSS";
                        patternMatcher = matched_event_search_success;
                    } else if (matched_user_search_success.find()) {
                        whatMatched = "userSS";
                        patternMatcher = matched_user_search_success;
                    } else if (matched_post_impression_facebook.find()) {
                        whatMatched = "postImpressionFacebook";
                        patternMatcher = matched_post_impression_facebook;
                    } else if (matched_post_impression_twitter.find()) {
                        whatMatched = "postImpressionTwitter";
                        patternMatcher = matched_post_impression_twitter;
                    } else if (matched_post_impression_LinkedIn.find()) {
                        whatMatched = "postImpressionLinkedIn";
                        patternMatcher = matched_post_impression_LinkedIn;
                    } else if (matched_post_impression_FacebookTwitterCombo.find()) {
                        whatMatched = "postImpressionFacebookTwitterCombo";
                        patternMatcher = matched_post_impression_FacebookTwitterCombo;
                    } else if (matched_user_impression_facebook.find()) {
                        whatMatched = "userImpressionFacebook";
                        patternMatcher = matched_user_impression_facebook;
                    } else if (matched_user_impression_twitter.find()) {
                        whatMatched = "userImpressionTwitter";
                        patternMatcher = matched_user_impression_twitter;
                    } else if (matched_user_impression_LinkedIn.find()) {
                        whatMatched = "userImpressionLinkedIn";
                        patternMatcher = matched_user_impression_LinkedIn;
                    } else if (matched_user_impression_FacebookTwitterCombo.find()) {
                        whatMatched = "userImpressionFacebookTwitterCombo";
                        patternMatcher = matched_user_impression_FacebookTwitterCombo;
                    } else if (matched_articleView.find()) {
                        whatMatched = "articleView";
                        patternMatcher = matched_articleView;
                    } else if (matched_blogView.find()) {
                        whatMatched = "blogView";
                        patternMatcher = matched_blogView;
                    } else if (matched_newsView.find()) {
                        whatMatched = "newsView";
                        patternMatcher = matched_newsView;
                    } else if (matched_whitepaperView.find()) {
                        whatMatched = "wpView";
                        patternMatcher = matched_whitepaperView;
                    }  else if(matched_podcast_view.find()) {
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
                    } else if(matched_ratgeber_self.find()){
                        whatMatched = "ratgeberSelf";
                        patternMatcher = matched_ratgeber_self;
                    } else if(matched_ratgeber_self_alter.find()) {
                        whatMatched = "ratgeberSelf";
                        patternMatcher = matched_ratgeber_self_alter;
                    } else if(matched_content_download.find()){
                        whatMatched = "contentDownload";
                        patternMatcher = matched_content_download;
                    } else if(matched_outgoing_linkedin_redirect.find()){
                        whatMatched = "socialsLinkedInRedirect";
                        patternMatcher = matched_outgoing_linkedin_redirect;
                    } else if(matched_outgoing_twitter_redirect.find()){
                        whatMatched = "socialsTwitterRedirect";
                        patternMatcher = matched_outgoing_twitter_redirect;
                    } else if(matched_outgoing_facebook_redirect.find()){
                        whatMatched = "socialsFacebookRedirect";
                        patternMatcher = matched_outgoing_facebook_redirect;
                    } else if(matched_outgoing_youtube_redirect.find()){
                        whatMatched = "socialsYouTubeRedirect";
                        patternMatcher = matched_outgoing_youtube_redirect;
                    } else if(matched_event_view.find()) {
                        whatMatched = "eventView";
                        patternMatcher = matched_event_view;
                    } else if(matched_event_cat.find()) {
                        whatMatched = "eventCat";
                        patternMatcher = matched_event_cat;
                    } else if(matched_userRedirect.find()){
                        whatMatched = "userRedirect";
                        patternMatcher = matched_userRedirect;
                    } else if (matched_videoView.find()) {
                        whatMatched = "videoView";
                        patternMatcher = matched_videoView;
                    } else if(matched_anbieterverzeichnis_view.find()) {
                        whatMatched = "anbieterCat";
                        patternMatcher = matched_anbieterverzeichnis_view;
                    } else if(matched_blackHole.find()) {
                        whatMatched = "bLaCkHoLe";
                        patternMatcher = matched_blackHole;
                    } else if(matched_tagcat_view.find()) {
                        whatMatched = "tagCat";
                        patternMatcher = matched_tagcat_view;
                    } else if(matched_forum_topic_view.find()) {
                        whatMatched = "forumTopicView";
                        patternMatcher = matched_forum_topic_view;
                    } else if(matched_forum_discussion_view.find()) {
                        whatMatched = "forumDiscussionView";
                        patternMatcher = matched_forum_discussion_view;
                    } else if(matched_forum_search.find()) {
                        whatMatched = "forumSearch";
                        patternMatcher = matched_forum_search;
                    }

                    //If user existed,
                    // but only clicked nonsense so far and now clicked something else,
                    // add them as a UniqueUser.
                    if(!isUnique) {
                        UniqueUser temp = uniqueUserRepo.findByIP(ip);
                        boolean wasOnlyNonsense = new JSONArray(temp.getNonsense()).length() == temp.getAmount_of_clicks() + 1;

                        if ((!whatMatched.equals("") || isNotNonsense) && wasOnlyNonsense){
                            uniqueUsers++;
                        }

                    }
                    //If the user is unique, AND has made a sensible request, mark him as unique and add them as a unique user.
                    if(isUnique && (!whatMatched.equals("") || isNotNonsense)) {
                        uniqueUsers++;
                    }

                    //If the user is new, initialize them.
                    if(isUnique) {
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

                            updateUniSingleLine("article", isUnique, dateLog);
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

                            updateUniSingleLine("blog", isUnique, dateLog);
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

                            updateUniSingleLine("news", isUnique, dateLog);
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

                            updateUniSingleLine("whitepaper", isUnique, dateLog);
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

                            updateUniSingleLine("podcast", isUnique, dateLog);
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

                            updateUniSingleLine("main", isUnique, dateLog);
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

                            updateUniSingleLine("ueber", isUnique, dateLog);
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

                            updateUniSingleLine("impressum", isUnique, dateLog);
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

                            updateUniSingleLine("preis", isUnique, dateLog);
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

                            updateUniSingleLine("partner", isUnique, dateLog);
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

                            updateUniSingleLine("datenschutz", isUnique, dateLog);
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

                            updateUniSingleLine("newsletter", isUnique, dateLog);
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

                            updateUniSingleLine("image", isUnique, dateLog);
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

                            updateUniSingleLine("agb", isUnique, dateLog);
                        }
                        case "userView" -> {
                            try {
                                if(wpUserRepo.findByNicename(patternMatcher.group(1)).isPresent()) {
                                    updateUserStats(wpUserRepo.findByNicename(patternMatcher.group(1)).get().getId(), dateLog);
                                }
                            } catch (Exception e) {
                                System.out.println(patternMatcher.group(1));
                            }

                            updateUniSingleLine("anbieter", isUnique, dateLog);
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

                            updateUniSingleLine("ratgeber", isUnique, dateLog);

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

                                    updateUniSingleLine("ratgeberPost", isUnique, dateLog);
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

                                    updateUniSingleLine("ratgeberGlossar", isUnique, dateLog);
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

                                    updateUniSingleLine("ratgeberBuch", isUnique, dateLog);
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

                                    updateUniSingleLine("ratgeberSelf", isUnique, dateLog);
                                }
                            }


                        } case "userRedirect" -> {
                        } case "eventView", "eventCat" -> {
                            if(!request.contains("Calendar") && !request.contains("calendar")) {
                                viewsEvents++;
                                if (isUnique) {
                                    userEvents++;
                                } else if (new JSONArray(uniqueUserRepo.findByIP(ip).getEvents()).length() < 2) {
                                    userEvents++;
                                }
                                updateUniqueUser(ip, "events", dateLog);

                                updateUniSingleLine("events", isUnique, dateLog);
                            } else {
                                whatMatched = "";
                            }
                        }
                        case "videoView" -> {
                            viewsVideos++;
                            if (isUnique) {
                                userVideos++;
                            }  else if (new JSONArray(uniqueUserRepo.findByIP(ip).getVideo()).length() < 2) {
                                userVideos++;
                            }
                            updateUniqueUser(ip, "video", dateLog);

                            updateUniSingleLine("videos", isUnique, dateLog);
                        }
                        case "anbieterCat" -> {
                            viewsAnbieter++;
                            if (isUnique) {
                                userAnbieter++;
                            } else if (new JSONArray(uniqueUserRepo.findByIP(ip).getAnbieter()).length() < 2) {
                                userAnbieter++;
                            }
                            updateUniqueUser(ip, "anbieter", dateLog);

                            updateUniSingleLine("anbieter", isUnique, dateLog);
                        }
                        default -> {
                            if(!isNotNonsense) {
                                updateUniqueUser(ip, "nonsense", dateLog);
                            }
                        }
                    }

                    if(!whatMatched.equals("") && patternMatcher != null) {
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
        //updateUniStats(totalClicks, internalClicks, sensibleClicks, viewsArticle, viewsNews, viewsBlog, viewsPodcast, viewsVideos, viewsWhitepaper, viewsEvents,  viewsRatgeber,viewsRatgeberPost, viewsRatgeberGlossar, viewsRatgeberBuch, viewsRatgeberSelf, viewsMain, viewsAnbieter, viewsUeber, viewsAGBS, viewsImpressum, viewsPreisliste, viewsPartner, viewsDatenschutz, viewsNewsletter, viewsImage, uniqueUsers, userArticle, userNews, userBlog, userPodcast, userVideos, userWhitepaper, userEvents, userRatgeber, userRatgeberPost, userRatgeberGlossar, userRatgeberBuch, userRatgeberSelf, userMain, userAnbieter, userUeber, userAGBS, userImpressum, userPreisliste, userPartner, userDatenschutz, userNewsletter, userImage, serverErrors);
        //Service weil Springs AOP ist whack und batch operationen am besten extern aufgerufen werden sollen
        userViewsByHourDLCService.persistAllUserViewsHour(userViewsHourDLCMap);
        postClicksByHourDLCService.persistAllPostClicksHour(postClicksMap);
        userRedirectService.persistAllUserRedirectsHourly(userRedirectsMap);

        //decomment to enable tracking
        forumService.persistAllForumDiscussionsClicksHour(forumDiscussionClicksMap);
        forumService.persistAllForumTopicsClicksHour(forumTopicsClicksMap);

        //maps clearen nur um sicher zu gehen
        cleanMaps();
        updateFinalSearchStatsAndTemporarySearchStats();
    }

    /**
     * Converts a single line of the access.log into usable data and writes them into the database.
     * @param line the line of the access.log to scan.
     * @param ip the ip that accessed the server.
     * @param whatMatched a String representation of what type the access the line represented.
     * @param dateLog the dateTime of the access.log line.
     * @param patternMatcher the regex-matcher that has found the "best" result.
     */
    public void processLine(String line, String ip, String whatMatched, LocalDateTime dateLog, Matcher patternMatcher) {
        lastLine = line;

        switch(whatMatched) {
            case "articleView", "blogView", "newsView", "wpView", "ratgeberPost", "podView", "videoView" -> {
                try {
                    UpdatePerformanceAndViews(dateLog, postRepository.getIdByName(patternMatcher.group(1)));
                    updateIPsByPost(ip, postRepository.getIdByName(patternMatcher.group(1)));
                    updatePostClicksMap(postRepository.getIdByName(patternMatcher.group(1)), dateLog);
                } catch (Exception e) {
                    System.out.println("VIEW PROCESS LINE EXCEPTION " + line);
                }
            }
            case "articleSS", "blogSS", "newsSS", "wpSS" -> {
                try {
                    Long postId = postRepository.getIdByName(patternMatcher.group(1));
                    updateSearchDLCMap(ip, patternMatcher.group(2), postId, dateLog, "post");
                    updatePostClicksMap(postId, dateLog);
                    UpdatePerformanceAndViews(dateLog, postId);
                } catch (Exception e) {
                    System.out.println("SS PROCESS LINE EXCEPTION " + line);
                }
            }
            case "eventSS" -> {
                try {
                    if (eventRepo.getActiveEventBySlug(patternMatcher.group(1).replace("+", "-")).isPresent()) {
                        long postId = eventRepo.getActiveEventBySlug(patternMatcher.group(1).replace("+", "-")).get().getPostID();
                        updateSearchDLCMap(ip, patternMatcher.group(2), postId, dateLog, "post");
                        updateIPsByPost(ip, postId);
                        updatePostClicksMap(postId, dateLog);
                        UpdatePerformanceAndViews(dateLog, postId);
                    }
                } catch (Exception e) {
                    System.out.println("EVENTSS EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "userSS" -> {
                try {
                    if (wpUserRepo.findByNicename(patternMatcher.group(1).replace("+", "-")).isPresent()) {
                        Long userId = wpUserRepo.findByNicename(patternMatcher.group(1).replace("+", "-")).get().getId();
                        updateSearchDLCMap(ip, patternMatcher.group(2), userId, dateLog, "user");
                        updateUserStats(userId, dateLog);
                        updateIPsByUser(ip, userId);
                    }
                } catch (Exception e) {
                    System.out.println("USERSS EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "userView" -> {
                try {
                    if (wpUserRepo.findByNicename(patternMatcher.group(1).replace("+", "-")).isPresent()) {
                        Long userId = wpUserRepo.findByNicename(patternMatcher.group(1).replace("+", "-")).get().getId();
                        updateUserStats(userId, dateLog);
                        updateIPsByUser(ip, userId);
                    }
                } catch (Exception e) {
                    System.out.println("USERVIEW EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "contentDownload" -> {
                try {
                    System.out.println("FOUND CONTENT DOWNLOAD \n\n");
                    //Edit Filename to make sure very similar files still work as intended
                    String filename = "/" + patternMatcher.group(1) + ".pdf";
                    if (postRepository.getParentFromListAnd(postMetaRepo.getAllWhitepaperFileAttachmentPostIds(), filename).isPresent()) {
                        long id = postRepository.getParentFromListAnd(postMetaRepo.getAllWhitepaperFileAttachmentPostIds(), filename).get();
                        ContentDownloadsHourly download;
                        if (contentDownloadsHourlyRepo.getByPostIdUniIdHour(id, uniRepo.getLatestUniStat().getId(), LocalDateTime.now().getHour()).isEmpty()) {
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
                } catch (Exception e) {
                    System.out.println("CONTENT DOWNLOAD EXCEPTION BEI: " + line);
                }
            }
            case "userRedirect" -> {
                try {
                    if (wpUserMetaRepository.getUserByURL(patternMatcher.group(1)) != null) {
                        UserRedirectsHourly redirects;
                        if (userRedirectRepo.getByUniIdAndHourAndUserId(uniRepo.getLatestUniStat().getId(), dateLog.getHour(), wpUserMetaRepository.getUserByURL(patternMatcher.group(1))).isPresent()) {
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
                } catch (Exception e) {
                    System.out.println("USERREDIRECT EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "socialsLinkedInRedirect", "socialsTwitterRedirect", "socialsYouTubeRedirect", "socialsFacebookRedirect" -> {
                try {
                    Integer latestUniId = uniRepo.getLatestUniStat().getId();
                    Integer hour = dateLog.getHour();
                    OutgoingSocialsRedirects redirects;

                    if (outgoingSocialsRepo.findByUniIdAndHour(latestUniId, hour).isPresent()) {
                        redirects = outgoingSocialsRepo.findByUniIdAndHour(latestUniId, hour).get();
                    } else {
                        redirects = new OutgoingSocialsRedirects();
                        redirects.setHour(dateLog.getHour());
                        redirects.setUniId(uniRepo.getLatestUniStat().getId());
                        redirects.setLinkedin(0L);
                        redirects.setFacebook(0L);
                        redirects.setTwitter(0L);
                        redirects.setYoutube(0L);

                    }

                    outgoingSocialsRepo.save(updateSocialsRedirects(whatMatched, redirects));

                } catch (Exception e) {
                    System.out.println("SOCIALSREDIRECT EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "eventView" -> {
                try {
                    if (eventRepo.getActiveEventBySlug(patternMatcher.group(1)).isPresent()) {
                        long postId = eventRepo.getActiveEventBySlug(patternMatcher.group(1)).get().getPostID();
                        UpdatePerformanceAndViews(dateLog, postId);
                        updateIPsByPost(ip, postId);
                        updatePostClicksMap(postId, dateLog);
                    }
                } catch (Exception e) {
                    System.out.println("EVENTVIEW EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "postImpressionFacebook", "postImpressionTwitter", "postImpressionLinkedIn", "postImpressionFacebookTwitterCombo" -> {
                try {
                    long id = postRepository.getIdByName(patternMatcher.group(2));
                    socialsImpressionsService.updateSocialsImpressionsPost(whatMatched, dateLog, id);
                } catch (Exception e) {
                    System.out.println("POST-SOCIAL EXCEPTION" + line);
                }
            }
            case "userImpressionFacebook", "userImpressionTwitter", "userImpressionLinkedIn", "userImpressionFacebookTwitterCombo" -> {
                try {
                    if (wpUserRepo.findByNicename(patternMatcher.group(1).replace("+", "-")).isPresent()) {
                        Long userId = wpUserRepo.findByNicename(patternMatcher.group(1).replace("+", "-")).get().getId();
                        socialsImpressionsService.updateSocialsImpressionsUser(whatMatched, dateLog, userId);
                    }
                } catch (Exception e) {
                    System.out.println("USER-SOCIAL EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }

            case "tagCat" -> {
                updateTagCatStat(patternMatcher.group(1));
            }

            case "forumDiscussionView" ->{
                try {
                    //decomment to enable
                    updateForumDiscussionClicksMap(patternMatcher.group(1),dateLog);
                }catch(Exception e){
                    System.out.println("FORUM DISCUSSION VIEW EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "forumTopicView" ->{
                //decomment to enable
                try {
                    updateForumTopicClicksMap(patternMatcher.group(1),dateLog);
                }catch(Exception e){
                    System.out.println("FORUM TOPIC VIEW EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }
            }
            case "forumSearch" -> {
                try{
                System.out.println(line);
                System.out.println(patternMatcher.group(1)+" "+patternMatcher.group(2)+" "+patternMatcher.group(3)+" "+patternMatcher.group(4)+" "+patternMatcher.group(5)+" "+patternMatcher.group(6));
                String land = IPHelper.getCountryISO(ip);
                String region = IPHelper.getSubISO(ip);
                String stadt = IPHelper.getCityName(ip);
                Integer stunde = dateLog.getHour();
                Integer uniId = uniRepo.getLatestUniStat().getId();
                Integer suchZeitraum = Integer.parseInt(patternMatcher.group(3));
                Integer seitenAnzahl = Integer.parseInt(patternMatcher.group(6));

                ForumSearch search = new ForumSearch(patternMatcher.group(1),patternMatcher.group(2),suchZeitraum ,patternMatcher.group(4),patternMatcher.group(5),seitenAnzahl,uniId,stunde,land,region,stadt);

                forumService.saveSearchData(search);}catch(Exception e){
                    System.out.println("FORUM SEARCH EXCEPTION BEI: " + line);
                    e.printStackTrace();
                }

            }
        }

    }

    private void updateForumDiscussionClicksMap(String slug,LocalDateTime logDate){
       Integer forumId = forumService.getForumIdBySlug(slug);

        if (forumId == null) {
            System.out.println("Warning: forumId is null for slug: " + slug);
            return;
        }

       if(forumDiscussionClicksMap.containsKey(forumId)){
          ForumDiskussionsthemenClicksByHour clicks = forumDiscussionClicksMap.get(forumId);
          Long counter = clicks.getClicks() + 1;
          clicks.setClicks(counter);
          forumDiscussionClicksMap.put(forumId,clicks);
       }else{
           ForumDiskussionsthemenClicksByHour clicks = new ForumDiskussionsthemenClicksByHour();
           Integer uniId = uniRepo.getLatestUniStat().getId();
           Integer hour = logDate.getHour();
           clicks.setClicks(1L);
           clicks.setForumIdInteger(forumId);
           clicks.setUniId(uniId);
           clicks.setHour(hour);
           forumDiscussionClicksMap.put(forumId,clicks);
       }
    }
    private void updateForumTopicClicksMap(String slug,LocalDateTime logDate){
        Integer topicId = forumService.getTopicIdBySlug(slug);

        if (topicId == null) {
            System.out.println("Warning: topicId is null for slug: " + slug);
            return;
        }

        if(forumTopicsClicksMap.containsKey(topicId)){
            ForumTopicsClicksByHour clicks = forumTopicsClicksMap.get(topicId);
            Long counter = clicks.getClicks() + 1;
            clicks.setClicks(counter);
            forumTopicsClicksMap.put(topicId,clicks);
        }else{
            ForumTopicsClicksByHour clicks = new ForumTopicsClicksByHour();
            Integer uniId = uniRepo.getLatestUniStat().getId();
            Integer hour = logDate.getHour();
            clicks.setClicks(1L);
            clicks.setTopicIdInteger(topicId);
            clicks.setUniId(uniId);
            clicks.setHour(hour);
            forumTopicsClicksMap.put(topicId,clicks);
        }
    }

    /**
     * Updates respective data from UniversalStats table.
     * @throws ParseException .
     */
    private void updateUniStats(int totalClicks, int internalClicks, int sensibleClicks, int viewsArticle, int viewsNews, int viewsBlog, int viewsPodcast, int viewsVideos, int viewsWhitepaper, int viewsEvents, int viewsRatgeber, int viewsRatgeberPost, int viewsRatgeberGlossar, int viewsRatgeberBuch, int viewsRatgeberSelf,  int viewsMain, int viewsAnbieter, int viewsUeber, int viewsAGBS, int viewsImpressum, int viewsPreisliste, int viewsPartner, int viewsDatenschutz, int viewsNewsletter, int viewsImage, int uniqueUsers, int userArticle, int userNews, int userBlog, int userPodcast, int userVideos, int userWhitepaper, int userEvents, int userRatgeber, int userRatgeberPost, int userRatgeberGlossar, int userRatgeberBuch, int userRatgeberSelf, int userMain, int userAnbieter, int userUeber, int userAGBS, int userImpressum, int userPreisliste, int userPartner, int userDatenschutz, int userNewsletter, int userImage, int serverErrors) throws ParseException {
        Date dateTime = Calendar.getInstance().getTime();
        String dateStirng = Calendar.getInstance().get(Calendar.YEAR) + "-";
        dateStirng += Calendar.getInstance().get(Calendar.MONTH) + 1  < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : Calendar.getInstance().get(Calendar.MONTH) + 1;
        dateStirng += "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String uniLastDateString = sdf.format(uniRepo.getLatestUniStat().getDatum());
        Date date = sdf.parse(dateStirng);
        final int curHour = LocalDateTime.now().getHour();

        try {
            checkForAndFixMissingRows(date, curHour);
        } catch (Exception e) {
            System.out.println("HOOPLA FIX MISSING ROWS ERROR IN UPDATE UNISTATS");
            e.printStackTrace();
        }


        //Updating UniversalStats
        {
            UniversalStats uni;
            {
                if (dateStirng.equalsIgnoreCase(uniLastDateString)) {
                    uni = uniRepo.findTop1ByOrderByDatumDesc();
                    uni.setBesucherAnzahl(uniHourlyRepo.getSumUsersForUniId(uni.getId()) + uniqueUsers);
                    uni.setSensibleClicks(uni.getSensibleClicks() + sensibleClicks);
                    uni.setTotalClicks(uni.getTotalClicks() + totalClicks);
                    uni.setInternalClicks(uni.getInternalClicks() + internalClicks);
                    uni.setServerErrors(uni.getServerErrors() + serverErrors);
                    uni.setAnbieterProfileAnzahl(wpUserRepo.count());
                    uni = setNewsArticelBlogCountForUniversalStats(date, uni);
                    uni = setAccountTypeAllUniStats(uni);
                } else {
                    uni = new UniversalStats();
                    uni.setBesucherAnzahl((long) uniqueUsers);
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
                        cat.setBesucherVideos(userVideos / 4);
                        cat.setBesucherWhitepaper(userWhitepaper / 4);
                        cat.setBesucherEvents(userEvents / 4);
                        cat.setBesucherRatgeber(userRatgeber / 4);
                        cat.setBesucherRatgeber(userRatgeberPost / 4);
                        cat.setBesucherRatgeber(userRatgeberGlossar / 4);
                        cat.setBesucherRatgeber(userRatgeberBuch / 4);
                        cat.setBesucherRatgeberSelf(userRatgeberSelf / 4);
                        cat.setBesucherMain(userMain / 4);
                        cat.setBesucherAnbieter(userAnbieter / 4);
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
                        cat.setViewsVideos(viewsVideos / 4);
                        cat.setViewsWhitepaper(viewsWhitepaper / 4);
                        cat.setViewsEvents(viewsEvents / 4);
                        cat.setViewsRatgeber(viewsRatgeber / 4);
                        cat.setViewsRatgeber(viewsRatgeberPost / 4);
                        cat.setViewsRatgeber(viewsRatgeberGlossar / 4);
                        cat.setViewsRatgeber(viewsRatgeberBuch / 4);
                        cat.setViewsRatgeberSelf(viewsRatgeberSelf / 4);
                        cat.setViewsMain(viewsMain / 4);
                        cat.setViewsAnbieter(viewsAnbieter / 4);
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
                    uniCategories.setBesucherVideos(userVideos);
                    uniCategories.setBesucherWhitepaper(userWhitepaper);
                    uniCategories.setBesucherEvents(userEvents);
                    uniCategories.setBesucherRatgeber(userRatgeber);
                    uniCategories.setBesucherRatgeberPost(userRatgeberPost);
                    uniCategories.setBesucherRatgeberGlossar(userRatgeberGlossar);
                    uniCategories.setBesucherRatgeberBuch(userRatgeberBuch);
                    uniCategories.setBesucherRatgeberSelf(userRatgeberSelf);
                    uniCategories.setBesucherMain(userMain);
                    uniCategories.setBesucherAnbieter(userAnbieter);
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
                    uniCategories.setViewsVideos(viewsVideos);
                    uniCategories.setViewsWhitepaper(viewsWhitepaper);
                    uniCategories.setViewsEvents(viewsEvents);
                    uniCategories.setViewsRatgeber(viewsRatgeber);
                    uniCategories.setViewsRatgeberPost(viewsRatgeberPost);
                    uniCategories.setViewsRatgeberGlossar(viewsRatgeberGlossar);
                    uniCategories.setViewsRatgeberBuch(viewsRatgeberBuch);
                    uniCategories.setViewsRatgeberSelf(viewsRatgeberSelf);
                    uniCategories.setViewsMain(viewsMain);
                    uniCategories.setViewsAnbieter(viewsAnbieter);
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
                uniCategories.setBesucherVideos(uniCategories.getBesucherVideos() + userVideos);
                uniCategories.setBesucherWhitepaper(uniCategories.getBesucherWhitepaper() + userWhitepaper);
                uniCategories.setBesucherEvents(uniCategories.getBesucherEvents() + userEvents);
                uniCategories.setBesucherRatgeber(uniCategories.getBesucherRatgeber() + userRatgeber);
                uniCategories.setBesucherRatgeberPost(uniCategories.getBesucherRatgeberPost() + userRatgeberPost);
                uniCategories.setBesucherRatgeberGlossar(uniCategories.getBesucherRatgeberGlossar() + userRatgeberGlossar);
                uniCategories.setBesucherRatgeberBuch(uniCategories.getBesucherRatgeberBuch() + userRatgeberBuch);
                uniCategories.setBesucherRatgeberSelf(uniCategories.getBesucherRatgeberSelf() + userRatgeberSelf);
                uniCategories.setBesucherMain(userMain + uniCategories.getBesucherMain());
                uniCategories.setBesucherAnbieter(uniCategories.getBesucherAnbieter() + userAnbieter);
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
                uniCategories.setViewsVideos(viewsVideos + uniCategories.getViewsVideos());
                uniCategories.setViewsWhitepaper(viewsWhitepaper + uniCategories.getViewsWhitepaper());
                uniCategories.setViewsEvents(viewsEvents + uniCategories.getViewsEvents());
                uniCategories.setViewsRatgeber(viewsRatgeber + uniCategories.getViewsRatgeber());
                uniCategories.setViewsRatgeberPost(viewsRatgeberPost + uniCategories.getViewsRatgeberPost());
                uniCategories.setViewsRatgeberGlossar(viewsRatgeberGlossar + uniCategories.getViewsRatgeberGlossar());
                uniCategories.setViewsRatgeberBuch(viewsRatgeberBuch + uniCategories.getViewsRatgeberBuch());
                uniCategories.setViewsRatgeberSelf(viewsRatgeberSelf + uniCategories.getViewsRatgeberSelf());
                uniCategories.setViewsMain(viewsMain + uniCategories.getViewsMain());
                uniCategories.setViewsAnbieter(viewsAnbieter + uniCategories.getViewsAnbieter());
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

    private void updateUniSingleLine(String updateColumn, boolean isUnique, LocalDateTime dateLog) {
        Date normalDate = java.sql.Date.valueOf(dateLog.toLocalDate());
        int curHour = dateLog.getHour();

        try {
            checkForAndFixMissingRows(normalDate, curHour);
        } catch (Exception e) {
            System.out.println("HOOPLA FIX MISSING ROWS ERROR IN UPDATE UNISTATS");
            e.printStackTrace();
        }

        UniversalStats uni;
        if(uniRepo.getUniversalStatsByDatum(normalDate).isPresent()) {
            uni = uniRepo.getUniversalStatsByDatum(normalDate).get();
            uni.setSensibleClicks(uni.getSensibleClicks() + 1);
            uni.setBesucherAnzahl(uni.getBesucherAnzahl() + (isUnique ? 1 : 0));
            uni.setTotalClicks(uni.getTotalClicks() + 1);
        }
        else {
            uni = new UniversalStats();
            uni.setBesucherAnzahl(1L);
            uni.setTotalClicks(1L);
            uni.setSensibleClicks(1L);

            uni.setDatum(normalDate);

            uni.setAnbieterProfileAnzahl(wpUserRepo.count());
            uni = setNewsArticelBlogCountForUniversalStats(normalDate, uni);
            uni = setAccountTypeAllUniStats(uni);
        }
        uniRepo.save(uni);

        UniversalCategoriesDLC uniCat;
        if(universalCategoriesDLCRepo.getByUniStatIdAndStunde(uni.getId(), curHour) != null) {
            uniCat = universalCategoriesDLCRepo.getByUniStatIdAndStunde(uni.getId(), curHour);
            switch (updateColumn) {
                case "article" -> {
                    uniCat.setViewsArticle(uniCat.getViewsArticle() + 1);
                    uniCat.setBesucherArticle(uniCat.getBesucherArticle() + (isUnique ? 1 : 0));
                }
                case "news" -> {
                    uniCat.setViewsNews(uniCat.getViewsNews() + 1);
                    uniCat.setBesucherNews(uniCat.getBesucherNews() + (isUnique ? 1 : 0));
                }
                case "blog" -> {
                    uniCat.setViewsBlog(uniCat.getViewsBlog() + 1);
                    uniCat.setBesucherBlog(uniCat.getBesucherBlog() + (isUnique ? 1 : 0));
                }
                case "podcast" -> {
                    uniCat.setViewsPodcast(uniCat.getViewsPodcast() + 1);
                    uniCat.setBesucherPodcast(uniCat.getBesucherPodcast() + (isUnique ? 1 : 0));
                }
                case "videos" -> {
                    uniCat.setViewsVideos(uniCat.getViewsVideos() + 1);
                    uniCat.setBesucherVideos(uniCat.getBesucherVideos() + (isUnique ? 1 : 0));
                }
                case "whitepaper" -> {
                    uniCat.setViewsWhitepaper(uniCat.getViewsWhitepaper() + 1);
                    uniCat.setBesucherWhitepaper(uniCat.getBesucherWhitepaper() + (isUnique ? 1 : 0));
                }
                case "events" -> {
                    uniCat.setViewsEvents(uniCat.getViewsEvents() + 1);
                    uniCat.setBesucherEvents(uniCat.getBesucherEvents() + (isUnique ? 1 : 0));
                }
                case "ratgeber" -> {
                    uniCat.setViewsRatgeber(uniCat.getViewsRatgeber() + 1);
                    uniCat.setBesucherRatgeber(uniCat.getBesucherRatgeber() + (isUnique ? 1 : 0));
                }
                case "ratgeberPost" -> {
                    uniCat.setViewsRatgeberPost(uniCat.getViewsRatgeberPost() + 1);
                    uniCat.setBesucherRatgeberPost(uniCat.getBesucherRatgeberPost() + (isUnique ? 1 : 0));
                }
                case "ratgeberGlossar" -> {
                    uniCat.setViewsRatgeberGlossar(uniCat.getViewsRatgeberGlossar() + 1);
                    uniCat.setBesucherRatgeberGlossar(uniCat.getBesucherRatgeberGlossar() + (isUnique ? 1 : 0));
                }
                case "ratgeberBuch" -> {
                    uniCat.setViewsRatgeberBuch(uniCat.getViewsRatgeberBuch() + 1);
                    uniCat.setBesucherRatgeberBuch(uniCat.getBesucherRatgeberBuch() + (isUnique ? 1 : 0));
                }
                case "ratgeberSelf" -> {
                    uniCat.setViewsRatgeberSelf(uniCat.getViewsRatgeberSelf() + 1);
                    uniCat.setBesucherRatgeberSelf(uniCat.getBesucherRatgeberSelf() + (isUnique ? 1 : 0));
                }
                case "main" -> {
                    uniCat.setViewsMain(uniCat.getViewsMain() + 1);
                    uniCat.setBesucherMain(uniCat.getBesucherMain() + (isUnique ? 1 : 0));
                }
                case "anbieter" -> {
                    uniCat.setViewsAnbieter(uniCat.getViewsAnbieter() + 1);
                    uniCat.setBesucherAnbieter(uniCat.getBesucherAnbieter() + (isUnique ? 1 : 0));
                }
                case "ueber" -> {
                    uniCat.setViewsUeber(uniCat.getViewsUeber() + 1);
                    uniCat.setBesucherUeber(uniCat.getBesucherUeber() + (isUnique ? 1 : 0));
                }
                case "impressum" -> {
                    uniCat.setViewsImpressum(uniCat.getViewsImpressum() + 1);
                    uniCat.setBesucherImpressum(uniCat.getBesucherImpressum() + (isUnique ? 1 : 0));
                }
                case "preis" -> {
                    uniCat.setViewsPreisliste(uniCat.getViewsPreisliste() + 1);
                    uniCat.setBesucherPreisliste(uniCat.getBesucherPreisliste() + (isUnique ? 1 : 0));
                }
                case "partner" -> {
                    uniCat.setViewsPartner(uniCat.getViewsPartner() + 1);
                    uniCat.setBesucherPartner(uniCat.getBesucherPartner() + (isUnique ? 1 : 0));
                }
                case "datenschutz" -> {
                    uniCat.setViewsDatenschutz(uniCat.getViewsDatenschutz() + 1);
                    uniCat.setBesucherDatenschutz(uniCat.getBesucherDatenschutz() + (isUnique ? 1 : 0));
                }
                case "newsletter" -> {
                    uniCat.setViewsNewsletter(uniCat.getViewsNewsletter() + 1);
                    uniCat.setBesucherNewsletter(uniCat.getBesucherNewsletter() + (isUnique ? 1 : 0));
                }
                case "image" -> {
                    uniCat.setViewsImage(uniCat.getViewsImage() + 1);
                    uniCat.setBesucherImage(uniCat.getBesucherImage() + (isUnique ? 1 : 0));
                }
                case "agb" -> {
                    uniCat.setViewsAGBS(uniCat.getViewsAGBS() + 1);
                    uniCat.setBesucherAGBS(uniCat.getBesucherAGBS() + (isUnique ? 1 : 0));
                }

            }
        }
        else {
            uniCat = new UniversalCategoriesDLC();
            uniCat.setUniStatId(uniRepo.getLatestUniStat().getId());
            uniCat.setStunde(curHour);
        }

        UniversalStatsHourly uniHourly;
        if(uniHourlyRepo.getByStundeAndUniStatId(curHour, uni.getId()) != null) {
            uniHourly = uniHourlyRepo.getByStundeAndUniStatId(curHour, uni.getId());
            uniHourly.setSensibleClicks(uni.getSensibleClicks() + 1);
            uniHourly.setBesucherAnzahl(uni.getBesucherAnzahl() + (isUnique ? 1 : 0));
            uniHourly.setTotalClicks(uni.getTotalClicks() + 1);
        }
        else {
            uniHourly = new UniversalStatsHourly();
            setAccountTypeAllUniStats(uniHourly);
            setNewsArticelBlogCountForUniversalStats(uniHourly);

            uniHourly.setStunde(curHour);
            uniHourly.setUniStatId(uni.getId());
        }


    }



    /**
     * Checks whether any rows have been missed since the last uni-stats update, if so, it adds them.
     * @param curDate the CURRENT date.
     * @param curHour the CURRENT hour.
     */
    private void checkForAndFixMissingRows(Date curDate, int curHour) throws ParseException {

        fixMissingInUni(curDate);

        fixMissingInCat(curHour);

    }

    /**
     * Should always be called after fixMissingDays.
     * Will generate all missing hours since the last generated hour, up until curHour.
     * @param curHour the CURRENT hour.
     */
    private void fixMissingInCat(int curHour) {


        //Whether the current hour is later than the last entry's hour, but the day isn't fully generated yet (So 23 is not generated).
        boolean missingHoursInCurrentDay = universalCategoriesDLCRepo.getLast().getStunde() < curHour && universalCategoriesDLCRepo.getLast().getStunde() != 23;

        /*Whether the last generated entry of uniCat is earlier than the last updated day - fixMissingDays should have generated the missing day in uniStats
        So all days that have been skipped should be present in UniStats, but not yet in uniCat.
        */
        boolean missingDay = uniRepo.getLatestUniStat().getId() > universalCategoriesDLCRepo.getLast().getUniStatId();

        while(missingDay || missingHoursInCurrentDay) {
            if(missingHoursInCurrentDay) {

                UniversalCategoriesDLC cat = new UniversalCategoriesDLC();
                cat.setUniStatId(universalCategoriesDLCRepo.getLast().getUniStatId());
                cat.setStunde(universalCategoriesDLCRepo.getLastStunde() + 1);
                universalCategoriesDLCRepo.save(cat);

                missingHoursInCurrentDay = universalCategoriesDLCRepo.getLast().getStunde() < curHour && universalCategoriesDLCRepo.getLast().getStunde() != 23;
            }
            if(missingDay) {
                UniversalCategoriesDLC cat = new UniversalCategoriesDLC();
                cat.setUniStatId(universalCategoriesDLCRepo.getLast().getUniStatId() + 1);
                cat.setStunde(0);
                universalCategoriesDLCRepo.save(cat);

                missingDay = uniRepo.getLatestUniStat().getId() > universalCategoriesDLCRepo.getLast().getUniStatId();

                if(missingDay) {
                    for(int i = 1; i <= 23; i++) {
                        cat = new UniversalCategoriesDLC();
                        cat.setUniStatId(universalCategoriesDLCRepo.getLast().getUniStatId());
                        cat.setStunde(i);
                        universalCategoriesDLCRepo.save(cat);
                    }
                } else {
                    for(int i = 1; i <= curHour; i++) {
                        cat = new UniversalCategoriesDLC();
                        cat.setUniStatId(universalCategoriesDLCRepo.getLast().getUniStatId());
                        cat.setStunde(i);
                        universalCategoriesDLCRepo.save(cat);
                    }
                }
            }
        }

    }


    private void fixMissingInUni(Date curDate) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        LocalDate currentDate = curDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate oldDate = uniRepo.getLatestUniStat().getDatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for(LocalDate date : oldDate.plusDays(1).datesUntil(currentDate).toList()) {
            String dateStirng = date.getYear() + "-" + (date.getMonthValue() > 10 ? date.getMonthValue() : "0" + date.getMonthValue()) + "-" + (date.getDayOfMonth() > 10 ? date.getDayOfMonth() : "0" + date.getDayOfMonth());
            Date fixerDate = sdf.parse(dateStirng);

            uniRepo.save(new UniversalStats(fixerDate));

        }
    }

    /**
     * Set all values of universalStats for how many users of each account-type are currently subscribed.
     * @param uniHourly the object to update for.
     */
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

    /**
     * Sets counts of posts for each post-type for the given object.
     * @param uniHourly the universalstats-hourly object to set stats for.
     */
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

    /**
     * Aggregate function for all behaviors that occur at the end of a day.
     * Mostly used for clean-up functions, IP-Deletions and to persist daily-type data.
     */
    public void endDay() {
        try {
            updateClicksBy();
        } catch (Exception e) {
            System.out.println("-------------------------------------------------------------FEHLER BEI UPDATE CLICKS");
            e.printStackTrace();
        }
        try {
            updateGeo();
        } catch (Exception e) {
            System.out.println("---------------------------------------------------------------------FEHLER BEI UPDATEGEO");
            e.printStackTrace();
        }
        try {
            permanentifyAllUsers();
        } catch (Exception e) {
            System.out.println("FEHLER BEI PERMANENTIFY ALL USERS");
            e.printStackTrace();
        }
        uniRepo.getSecondLastUniStats().get(1).setBesucherAnzahl((long) uniqueUserRepo.getUserCountGlobal());


        List<UserStats> userStats = userStatsRepo.findAll();
        userStatsRepo.saveAll(userStats);

        //Update the ranking-buffer
        try {
            updateRankings();
        } catch (Exception ignored) {}
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

    /**
     * Increases values for a specific date.
     * @param id the postId to update for.
     * @param logDatum the date to update for.
     * @param logUhrzeit the time to update for.
     */
    @Transactional
    public void erhoeheWertFuerLogDatum(long id, LocalDate logDatum, LocalTime logUhrzeit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String logDatumString = logDatum.format(formatter);

        PostStats postStats = statsRepo.findByArtIdAndYear(id, logDatum.getYear());

        HashMap<String, Long> daily = (HashMap<String, Long>) postStats.getViewsLastYear();
        long aktuellerWert = daily.getOrDefault(logDatumString, 0L);
        daily.put(logDatumString, aktuellerWert + 1);

        Map<String, Long> viewsPerHour = erhoeheViewsPerHour2(postStats, logUhrzeit);
        postStats.setViewsPerHour(viewsPerHour);

        postStats.setViewsLastYear(daily);
        postStats.setRelevance(getRelevance2(daily, logDatumString, 7));

        statsRepo.save(postStats);
    }

    /**
     * Updates a field representing the views of each hour.
     * @param stats the stats to update for.
     * @param logUhrzeit the time to update with.
     * @return the new and increased map.
     */
    public  Map<String, Long> erhoeheViewsPerHour2(PostStats stats, LocalTime logUhrzeit) {
        Map<String, Long> viewsPerHour = stats.getViewsPerHour();
        int stunde = logUhrzeit.getHour();
        long views = viewsPerHour.getOrDefault(Integer.toString(stunde), 0L);
        views++;
        viewsPerHour.put(Integer.toString(stunde), views);

        return viewsPerHour;
    }

    /**
     * Updates performance and views for a post.
     * @param dateLog the date to update with.
     * @param id the id of the post to update the stats for.
     */
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

                if (statsRepo.existsByArtIdAndYear(id, logDate.getYear())) {
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

                    statsRepo.updateClicksAndPerformanceByArtId(views, id, logDate.getYear(), performance);

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

    /**
     * Updates the stats of a user.
     * @param id the id of the user to update.
     * @param dateLog the date of access to update stats with.
     */
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

    /**
     * Updates UserViewsByHourDLC, a table representing the views a user has received for each hour.
     * @param userId the id of the user to update for.
     * @param dateLog the date of access to update with.
     */
    private void updateUserViewsByHourDLCList(long userId,LocalDateTime dateLog){
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

    /**
     * Updates an internal map that contains posts and their clicks (hourly).
     * @param postId the post to update the map for.
     * @param dateLog the date of access to update with.
     */
    private void updatePostClicksMap(Long postId,LocalDateTime dateLog){
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

    /**
     * Cleans internal maps to ensure persistance.
     */
    private void cleanMaps(){
        userRedirectsMap.clear();
        postClicksMap.clear();
        contentDownloadsMap.clear();
        userViewsHourDLCMap.clear();
    }

    /**
     * Fetches relevance from a post's data.
     * @param viewsLastYear a posts views during a year.
     * @param currentDateString a String representation of the current date.
     * @param time the current hour.
     * @return a decimal float containing the "relevance" for the given Parameters.
     */
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

    /**
     * Updates tag/term data in tagstat table.
     * @param id the id of tag to update.
     * @param searchSuccess whether a search has succeeded.
     */
    @Transactional
    public void updateTagStats(long id,boolean searchSuccess){

        if(termTaxRepo.findByTermId((int) id).getTaxonomy().equalsIgnoreCase("post_tag")) {
            TagStat stats;
            int uniId = uniRepo.getLatestUniStat().getId();
            int hour = LocalDateTime.now().getHour();

            if (tagStatRepo.getByTagIdDayAndHour(id, uniId, hour).isPresent()) {
                stats = tagStatRepo.getByTagIdDayAndHour(id, uniId, hour).get();
                stats.setViews(stats.getViews() + 1);
                stats.setSearchSuccess(searchSuccess ? stats.getSearchSuccess() + 1 : stats.getSearchSuccess());
            } else {
                stats = new TagStat();
                stats.setHour(hour);
                stats.setUniId(uniId);
                stats.setTagId((int) id);
                stats.setSearchSuccess(searchSuccess ? 1 : 0);
                stats.setViews(1);
            }
            tagStatRepo.save(stats);
        }
    }

    /**
     * Updates all this posts tags increasing their views by 1.
     * @param id a posts id.
     * @param searchSuccess whether a search has succeeded.
     */
    public void checkTheTag(long id,boolean searchSuccess){
        List<Long> tagTaxIds= termRelRepo.getTaxIdByObject(id);
        List<Long> tagIds= termTaxRepo.getTermIdByTaxId(tagTaxIds);
        for(Long l:tagIds){
            if(tagStatRepo.existsByTagId(l.intValue())){
                updateTagStats(l.intValue(),searchSuccess);}
            else {
                if (termTaxRepo.findByTermId(l.intValue()).getTaxonomy().equals("post_tag")) {
                    updateTagStats(l.intValue(), searchSuccess);
                }
            }
        }
    }

    /**
     * Updates the letter-count for a single post.
     * @param id the id of the post to update for.
     */
    public void updateLetterCount(long id) {

        int lettercount = Jsoup.clean(postRepository.getContentById(id), Safelist.none()).length();
        statsRepo.updateLetterCount(lettercount, id, LocalDateTime.now().getYear());
    }

    /**
     * Updates letter-count for all posts.
     */
    public void updateLetterCountForAll () {
        for(Post p : postRepository.findAllUserPosts()) {
            if (statsRepo.existsByArtId(p.getId())) {
                if ((statsRepo.getLetterCount(p.getId()) == 0L) || statsRepo.getLetterCount(p.getId()) == null) {
                    updateLetterCount(p.getId());
                }
            }
        }
    }

    /**
     * Counts words in a post and updates stats for it.
     * @param id the id of the post to update for.
     */
    public void updateCountWordsForPost(long id) {
        // Hole den Inhalt des Posts und bereinige ihn
        String content = Jsoup.clean(postRepository.getContentById(id), Safelist.none());

        // Trenne den String anhand der bekannten Worttrenner und zähle die Anzahl der resultierenden Wörter
        String[] words = content.split("\\s+|,|;|\\.|\\?|!");
        int wordCount = words.length;

        statsRepo.updateWordCount(wordCount,id);
    }

    /**
     * Updates Word-Count statistic for all posts.
     */
    public void updateWordCountForAll () {
        for(Post p : postRepository.findAllUserPosts()) {
            updateCountWordsForPost(p.getId());
        }
    }

    /**
     * Fetch the date for yesterday.
     * @return a string representation of the date in yyyyMMdd format.
     */
    public static String getLastDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Vortag
        Date vortag = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(vortag);
    }

    /**
     * Sets counts for different types of posts in a universal-stats row.
     * @param dateStr the date to update for.
     * @param uniStats the id for the uni-row to update.
     * @return the updated UniStats (usage not recommended).
     */
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

    /**
     * Counts all types of users and updates universal-stats for them.
     * @param uniStats the uni-stats to update.
     * @return the updated UniStats (usage not recommended).
     */
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

    /**
     * Updates the IPs by Post table, containing IPs that have accessed a post.
     * @param ip the ip to update with.
     * @param id the id of the post to update for.
     * @throws JSONException no.
     */
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

    /**
     * Updates the IPs by User table, containing IPs that have accessed a user's page.
     * @param ip the ip to update with.
     * @param id the id of the user to update for.
     * @throws JSONException no.
     */
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

    /**
     * Updates different geolocation tables for the current states of other tables.
     * Also deletes old values from temporary, daily-type tables.
     */
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
                    clicksByBundesland = clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip)) == null
                            ? new ClicksByBundesland() : clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip));

                    clicksByBundesland.setUniStatId(uniId);
                    //If the ip can be matched to a bundesland, update and save it. Otherwise, don't.
                    if (IPHelper.getSubISO(ip) != null) {
                        clicksByBundesland.setBundesland(IPHelper.getSubISO(ip));
                        clicksByBundesland.setClicks(clicksByBundesland.getClicks() + 1);
                        clicksByBundeslandRepo.save(clicksByBundesland);
                        //If the ip can be matched to a city, set, update and save ClicksByBundeslandCitiesDLC
                        if (IPHelper.getCityName(ip) != null) {
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
                clicksByCountry = clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip)) == null
                        ? new ClicksByCountry() : clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip));
                clicksByCountry.setUniStatId(uniId);
                clicksByCountry.setCountry(IPHelper.getCountryName(ip));
                clicksByCountry.setClicks(clicksByCountry.getClicks() + 1);
                clicksByCountryRepo.save(clicksByCountry);

            }
        }
    }

    /**
     * Updates different geolocation tables, for a single user.
     * @param ip the ip to update Geolocation with.
     */
    private void updateClicksByIpLiveDay(String ip) {
        int uniId = uniRepo.getLatestUniStat().getId();
        ClicksByCountry clicksByCountry;
        ClicksByBundesland clicksByBundesland;
        ClicksByBundeslandCitiesDLC clicksByBundeslandCitiesDLC;
        //If the country is Germany, try to update ClicksByBundesland
        if (IPHelper.getCountryISO(ip) != null) {
            if (IPHelper.getCountryISO(ip).equals("DE")) {
                clicksByBundesland = clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip)) == null
                        ? new ClicksByBundesland() : clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, IPHelper.getSubISO(ip));

                clicksByBundesland.setUniStatId(uniId);
                //If the ip can be matched to a bundesland, update and save it. Otherwise, don't.
                if (IPHelper.getSubISO(ip) != null) {
                    clicksByBundesland.setBundesland(IPHelper.getSubISO(ip));
                    clicksByBundesland.setClicks(clicksByBundesland.getClicks() + 1);
                    clicksByBundeslandRepo.save(clicksByBundesland);
                    //If the ip can be matched to a city, set, update and save ClicksByBundeslandCitiesDLC
                    if (IPHelper.getCityName(ip) != null) {
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
            clicksByCountry = clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip)) == null
                    ? new ClicksByCountry() : clicksByCountryRepo.getByUniIDAndCountry(uniId, IPHelper.getCountryName(ip));
            clicksByCountry.setUniStatId(uniId);
            clicksByCountry.setCountry(IPHelper.getCountryName(ip));
            clicksByCountry.setClicks(clicksByCountry.getClicks() + 1);
            clicksByCountryRepo.save(clicksByCountry);

        }
    }

    /**
     * Updates Geolocation-Stats.
     */
    private void updateGeo() {
        updatePostGeo();
        updateUserGeo();
    }

    /**
     * Updates Geolocation-Stats for Posts, uses temporary tables.
     */
    private void updatePostGeo() {
        PostGeo postGeo;
        try {
            for (IPsByPost post : iPsByPostRepository.findAll()) {
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
            e.printStackTrace();
        }
    }

    /**
     * Updates Geolocation-Stats for User, uses temporary tables.
     */
    private void updateUserGeo() {
        UserGeo userGeo;
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
            e.printStackTrace();
        }
    }

    /**
     * Deletes all old IPs that were used in Geolocation.
     */
    private void deleteOldIPs() {
        iPsByUserRepository.deleteAll();
        iPsByPostRepository.deleteAll();
        uniqueUserRepo.deleteAll();
    }

    /**
     * Updates a auxiliary table used to determine posts types quicker.
     */
    private void updatePostTypes() {
        postTypeRepo.deleteAll(postTypeRepo.getDefault());
        for(Integer id : postRepository.getIdsOfUntyped()) {
            PostTypes type = new PostTypes();
            type.setPost_id(Long.valueOf(id));
            type.setType(postController.getType(id));
            postTypeRepo.save(type);
        }
    }

    /**
     * Updates a UniqueUser row.
     * @param ip the ip of the user to update.
     * @param category the category that was clicked.
     * @param clickTime the time that the click happened at.
     * @throws JSONException no.
     */
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
            case "anbieter" -> {
                user.setAnbieter(new JSONArray(user.getAnbieter()).put(clicks).toString());
            }
            case "video" -> {
                user.setVideo(new JSONArray(user.getVideo()).put(clicks).toString());
            }
            case "events" -> {
                user.setEvents(new JSONArray(user.getEvents()).put(clicks).toString());
            }
            case "nonsense" -> {
                user.setNonsense(new JSONArray(user.getNonsense()).put(clicks).toString());
            }
        }

        uniqueUserRepo.save(user);

    }

    /**
     * Initializes a UniqueUser-row with rudimentary values.
     * @param ip the ip to create a row for.
     * @param clickTime the time that the click happened at.
     */
    private void initUniqueUser(String ip, LocalDateTime clickTime) {
        UniqueUser user = new UniqueUser();
        user.setIp(ip);
        user.setArticle(new JSONArray().put(0).toString());
        user.setBlog(new JSONArray().put(0).toString());
        user.setNews(new JSONArray().put(0).toString());
        user.setWhitepaper(new JSONArray().put(0).toString());
        user.setEvents(new JSONArray().put(0).toString());
        user.setPodcast(new JSONArray().put(0).toString());
        user.setVideo(new JSONArray().put(0).toString());
        user.setRatgeber(new JSONArray().put(0).toString());
        user.setMain(new JSONArray().put(0).toString());
        user.setAnbieter(new JSONArray().put(0).toString());
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

    /**
     * Persists UniqueUser-row's data by writing it into other tables and deleting the row in UniqueUser.
     * @param ip the ip to permanentify stats for.
     * @throws JSONException no.
     */
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
            int videoLength = new JSONArray(user.getMain()).length() - 1;
            int wpLength = new JSONArray(user.getWhitepaper()).length() - 1;
            int eventLength = new JSONArray(user.getMain()).length() - 1;
            int ratgeberLength = new JSONArray(user.getRatgeber()).length() - 1;
            int mainLength = new JSONArray(user.getMain()).length() - 1;
            int anbieterLength = new JSONArray(user.getAnbieter()).length() - 1;
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
                uniAvg.setVideo(((uniAvg.getVideo() * oldClicks) + videoLength) / clicks);
                uniAvg.setWhitepaper(((uniAvg.getWhitepaper() * oldClicks) + wpLength) / clicks);
                uniAvg.setEvents(((uniAvg.getEvents() * oldClicks) + eventLength) / clicks);
                uniAvg.setRatgeber(((uniAvg.getRatgeber() * oldClicks) + ratgeberLength) / clicks);
                uniAvg.setMain(((uniAvg.getMain() * oldClicks) + mainLength) / clicks);
                uniAvg.setAnbieter(((uniAvg.getAnbieter() * oldClicks) + anbieterLength) / clicks);
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
                    uniTime.setVideo(user.getTime_spent() * ((float) videoLength / clicks));
                    uniTime.setWhitepaper(user.getTime_spent() * ((float) wpLength / clicks));
                    uniTime.setEvents(user.getTime_spent() * ((float) eventLength / clicks));
                    uniTime.setRatgeber(user.getTime_spent() * ((float) ratgeberLength / clicks));
                    uniTime.setMain(user.getTime_spent() * ((float) mainLength / clicks));
                    uniTime.setAnbieter(user.getTime_spent() * ((float) anbieterLength / clicks));
                    uniTime.setFooter(user.getTime_spent() * ((float) footerLength / clicks));

                    uniTimeSpentRepo.save(uniTime);
                }
            }
            uniqueUserRepo.delete(uniqueUserRepo.findByIP(ip));
        }
    }

    /**
     * Persists all UniqueUsers by converting their data into permanent data and deleting the old rows.
     * @throws JSONException .
     */
    private void permanentifyAllUsers() throws JSONException {
        for(UniqueUser user : uniqueUserRepo.findAll()) {
            String ip = user.getIp();
            permanentifyUser(ip);
        }
    }

    /**
     * Initializes a row for UniAverages.
     */
    private void initUniAverages() {
        UniversalAverageClicksDLC uniAverage = new UniversalAverageClicksDLC();
        uniAverage.setArticle(0);
        uniAverage.setBlog(0);
        uniAverage.setMain(0);
        uniAverage.setAnbieter(0);
        uniAverage.setPodcast(0);
        uniAverage.setVideo(0);
        uniAverage.setFooter(0);
        uniAverage.setNews(0);
        uniAverage.setRatgeber(0);
        uniAverage.setWhitepaper(0);
        uniAverage.setEvents(0);
        uniAverage.setAmount_clicks(0);
        uniAverage.setAmount_users(0);
        uniAverage.setUni_stat_id(uniRepo.getLatestUniStat().getId());
        uniAverageClicksRepo.save(uniAverage);
    }

    /**
     * Initializes a row for UniTime.
     */
    private void initUniTime() {
        UniversalTimeSpentDLC uniTime = new UniversalTimeSpentDLC();
        uniTime.setArticle(0);
        uniTime.setBlog(0);
        uniTime.setMain(0);
        uniTime.setAnbieter(0);
        uniTime.setPodcast(0);
        uniTime.setVideo(0);
        uniTime.setFooter(0);
        uniTime.setNews(0);
        uniTime.setRatgeber(0);
        uniTime.setWhitepaper(0);
        uniTime.setEvents(0);
        uniTime.setAmount_clicks(0);
        uniTime.setAmount_users(0);
        uniTime.setTotal_time(0);
        uniTime.setUni_stat_id(uniRepo.getLatestUniStat().getId());
        uniTimeSpentRepo.save(uniTime);
    }

    /**
     * Updates a SearchDLC-row.
     * @param ip the ip to update with.
     * @param searchQuery the search to update for.
     * @param Id the id to update for.
     * @param dateLog the date to update with.
     * @param matchCase no clue.
     */
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

    /**
     * Attempts to find matches between searches and search successes, linking their table entries.
     * @param tempSearches .
     * @param finalSearches .
     */
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

    /**
     * Updates Search-Stat Tables.
     */
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

    /**
     * Updates social-media redirects.
     * @param whatMatched what social-media redirect happened? In String representation.
     * @param redirects the row of SocialsRedirects to update.
     * @return the OutgoingSocialRedirects-Row after the update (usage not recommended).
     */
    private OutgoingSocialsRedirects updateSocialsRedirects(String whatMatched ,  OutgoingSocialsRedirects redirects){
        Long counter;

        switch (whatMatched) {
            case "socialsLinkedInRedirect" -> {
                counter = redirects.getLinkedin();
                counter++;
                redirects.setLinkedin(counter);
            }
            case "socialsFacebookRedirect" -> {
                counter = redirects.getFacebook();
                counter++;
                redirects.setFacebook(counter);
            }
            case "socialsTwitterRedirect" -> {
                counter = redirects.getTwitter();
                counter++;
                redirects.setTwitter(counter);
            }
            case "socialsYouTubeRedirect" -> {
                counter = redirects.getYoutube();
                counter++;
                redirects.setYoutube(counter);
            }
        }
        return redirects;
    }

    /**
     * Updates an auxiliary table to ease access to Anbieter-Search data.
     */
    public void updateAnbieterFailedSearchBuffer() {
        removeNoLongerFails();
        addNewFails();
    }

    /**
     * Adds newly failed searches to Anbieter-Search-Table.
     */
    private void addNewFails() {
        for(AnbieterSearch a : anbieterSearchRepo.findAllNoneFound()) {
            if(anbieterSearchFailRepo.getByData(a.getSearch(), a.getCity_name(), a.getPlz(), a.getUmkreis()).isEmpty()) {
                AnbieterFailedSearchBuffer b = new AnbieterFailedSearchBuffer();
                b.setSearch(a.getSearch());
                if(a.getCity_name().equals("") && a.getPlz() != 0) {
                    b.setCity(geoNamesRepo.getCityByPlz(a.getPlz()));
                } else {
                    b.setCity(a.getCity_name());
                }
                b.setPlz(a.getPlz());
                b.setUmkreis(a.getUmkreis());
                b.setCount(anbieterSearchRepo.getCountForData(a.getSearch(), a.getCity_name(), a.getPlz(), a.getUmkreis()));
                anbieterSearchFailRepo.save(b);
            } else {
                AnbieterFailedSearchBuffer b = anbieterSearchFailRepo.getByData(a.getSearch(), a.getCity_name(), a.getPlz(), a.getUmkreis()).get();
                b.setCount(anbieterSearchRepo.getCountForData(a.getSearch(), a.getCity_name(), a.getPlz(), a.getUmkreis()));
                anbieterSearchFailRepo.save(b);
            }
        }
    }

    /**
     * Removes searches that no longer fail from Anbieter-Search-Table.
     */
    private void removeNoLongerFails() {
        for(AnbieterFailedSearchBuffer a : anbieterSearchFailRepo.findAll()) {
            if(!(anbieterSearchRepo.findCountNotZeroForData(a.getSearch(), a.getCity(), a.getPlz(), a.getUmkreis()).isEmpty())) {
                anbieterSearchFailRepo.delete(a);
            }
        }
    }

    /**
     * Deletes all rows that are a standard search with no filters set from table.
     */
    private void deleteStandardSearch() {
        anbieterSearchRepo.deleteAll(anbieterSearchRepo.findAllSchmutzSearch());
    }

    /**
     * Cleans FinalSearchStatDLC table from entries that couldn't be matched correctly.
     */
    private void cleanFinalSearchStatDLC() {
        for(FinalSearchStatDLC f : fDLCRepo.findAll()) {
            if(f.getFinalSearchId() == null || (f.getPostId() == null && f.getUserId() == null)) {
                fDLCRepo.delete(f);
            }
        }
    }

    /**
     * Does a routine of cleaning and updating tables.
     * Currently, cleans: AnbieterFailedSearchBuffer, AnbieterSearch, and FinalSearchStatDLC
     */
    private void doAutoClean() {
        updateAnbieterFailedSearchBuffer();
        deleteStandardSearch();
        cleanFinalSearchStatDLC();
    }

    /**
     * Increments or creates a row of TagCatStat, increasing its views.
     * @param slug the slug of the tag to update for.
     */
    private void updateTagCatStat(String slug) {
        try {
            TagCatStat cat = tagCatRepo.getTagCatStatByTagIdAndTime(termRepo.findBySlug(slug).getId().intValue(), uniRepo.getLatestUniStat().getId(), LocalDateTime.now().getHour());
            if (cat == null) {
                cat = new TagCatStat();
                cat.setTagId(termRepo.findBySlug(slug).getId().intValue());
                cat.setHour(LocalDateTime.now().getHour());
                cat.setUniId(uniRepo.getLatestUniStat().getId());
                cat.setViews(1);
                cat.setTagName(slug);
            } else {
                cat.setViews(cat.getViews() + 1);
            }
            tagCatRepo.save(cat);
        } catch (Exception ignored) {}

    }

    public void updateRankings() {
        userController.updateUserRankingBuffer();
    }

}