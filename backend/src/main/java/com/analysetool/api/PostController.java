package com.analysetool.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/posts")
public class PostController {
    private Calendar kalender = Calendar.getInstance();
    private int aktuellesJahr = kalender.get(Calendar.YEAR);
    @Autowired
    private PostStatsRepository statRepository;
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private UserStatsRepository userStatsRepo;
    @Autowired
    private TagStatRepository tagStatRepo;
    @Autowired
    private WPTermRepository termRepo;
    @Autowired
    private WpTermRelationshipsRepository termRelRepo;
    @Autowired
    private WpTermTaxonomyRepository taxTermRepo;
    @Autowired
    private WPUserRepository userRepo;
    PostRepository postRepository;
    PostStatsRepository statsRepo;
    WpTermRelationshipsRepository termRelationRepo;
    WPTermRepository wpTermRepo;
    WpTermTaxonomyRepository wpTermTaxonomyRepo;

    @Autowired
    private PostMetaRepository postMetaRepo;

    @Autowired
    public PostController(
            PostRepository postRepository, PostStatsRepository statsRepo, WpTermRelationshipsRepository termRelationRepo, WPTermRepository wpTermRepo, WpTermTaxonomyRepository wpTermTaxonomyRepo
    ){
       this.postRepository = postRepository;
       this.statsRepo=statsRepo;
       this.termRelationRepo = termRelationRepo;
       this.wpTermRepo = wpTermRepo;
       this.wpTermTaxonomyRepo = wpTermTaxonomyRepo;
    }

    @GetMapping("/getall")
    public List<Post> getAllPosts() {
        return postRepository.findAll();

        /*JSONArray list = new JSONArray();
        List<Post> posts;
        List<String> tags = new ArrayList<>();
        DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");
        if (published){
            posts = postRepository.findPublishedPosts();
        } else {
            posts = postRepository.findAll();
        }
        if (stats) {
            String type = "";
            if (!posts.isEmpty()) {
                for (Post i : posts) {
                    if (i.getType().equals("post")) {
                        PostStats PostStats = null;
                        if (statsRepo.existsByArtId(i.getId())) {
                            PostStats = statsRepo.getStatByArtID(i.getId());
                        }
                        List<Long> tagIDs = null;
                        if (termRelationRepo.existsByObjectId(i.getId())) {
                            tagIDs = termRelationRepo.getTaxIdByObject(i.getId());
                        }
                        List<WPTerm> terms = new ArrayList<>();
                        if (tagIDs != null) {
                            for (long l : tagIDs) {
                                if (wpTermRepo.existsById(l)) {
                                    if (wpTermRepo.findById(l).isPresent()) {
                                        terms.add(wpTermRepo.findById(l).get());
                                    }
                                }
                            }
                        }
                        for (WPTerm t : terms) {
                            if (wpTermTaxonomyRepo.existsById(t.getId())) {
                                if (wpTermTaxonomyRepo.findById(t.getId()).isPresent()) {
                                    WpTermTaxonomy tt = wpTermTaxonomyRepo.findById(t.getId()).get();
                                    if (Objects.equals(tt.getTaxonomy(), "category") && tt.getTermId() != 1) {
                                        if (wpTermRepo.findById(tt.getTermId()).isPresent()) {
                                            type = wpTermRepo.findById(tt.getTermId()).get().getSlug();
                                            switch (type) {
                                                case "artikel":
                                                    break;
                                                case "blog":
                                                    break;
                                                case "news":
                                                    break;
                                                default:
                                                    type = "default";
                                                    break;
                                            }
                                        }
                                    } else {
                                        tags.add(wpTermRepo.findById(tt.getTermId()).get().getName());
                                    }
                                }
                            }
                        }

                        JSONObject obj = new JSONObject();
                        Date date = onlyDate.parse(i.getDate().toString());
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                        obj.put("id", i.getId());
                        obj.put("title", i.getTitle());
                        obj.put("date", formattedDate);
                        obj.put("type", type);
                        obj.put("tags", tags);
                        if (PostStats != null) {
                            obj.put("performance", PostStats.getPerformance());
                            obj.put("relevance", PostStats.getRelevance());
                        } else {
                            obj.put("performance", 0);
                            obj.put("relevance", 0);
                        }
                        if (!obj.get("type").equals("default")) {
                            list.put(obj);
                        }
                    }
                }
            }
        }
        return list.toString();*/
    }

    @GetMapping("/publishedPosts")
    public List<Post> getPublishedPosts(){return postRepository.findPublishedPosts();}

    //ToDo Rename
    @GetMapping("/getPostsByAuthorLine")
    public String PostsByAuthor(@RequestParam int id) throws JSONException, ParseException {

        JSONArray list = new JSONArray();
        List<Post> posts = postRepository.findByAuthor(id);
        DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");

        if (!posts.isEmpty()) {
            for (Post i : posts) {
                JSONObject obj = new JSONObject();
                Date date = onlyDate.parse(i.getDate().toString());
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                obj.put("title", i.getTitle());
                obj.put("date", formattedDate);
                obj.put("count",1);

                if (list.length() > 0 && list.getJSONObject(list.length() - 1).getString("date").equals(formattedDate)) {
                    String currentId = list.getJSONObject(list.length() - 1).getString("title");
                    int currentCount = list.getJSONObject(list.length() - 1).getInt("count");
                    list.getJSONObject(list.length() - 1).put("title", currentId + "," + i.getTitle());
                    list.getJSONObject(list.length() - 1).put("count", currentCount + 1);
                } else {
                    list.put(obj);
                }
            }
        }
        return list.toString();
    }

    //ToDo Rename
    @GetMapping("/getPostsByAuthorLine2")
    public String PostsByAuthor2(@RequestParam int id) throws JSONException, ParseException {

        JSONArray list = new JSONArray();
        List<Post> posts = postRepository.findByAuthor(id);
        DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");

        String type = "";
        float maxPerformance =   statsRepo.getMaxPerformance();
        float maxRelevance = statsRepo.getMaxRelevance();
        if (!posts.isEmpty()) {
            for (Post i : posts) {
                if (i.getType().equals("post")) {
                    PostStats PostStats = null;
                    if (statsRepo.existsByArtId(i.getId())) {
                        PostStats = statsRepo.getStatByArtID(i.getId());
                    }
                    List<Long> tagIDs = null;
                    if (termRelationRepo.existsByObjectId(i.getId())) {
                        tagIDs = termRelationRepo.getTaxIdByObject(i.getId());
                    }
                    List<WPTerm> terms = new ArrayList<>();
                    if (tagIDs != null) {
                        for (long l : tagIDs) {
                            if (wpTermRepo.existsById(l)) {
                                if (wpTermRepo.findById(l).isPresent()) {
                                    terms.add(wpTermRepo.findById(l).get());
                                }
                            }
                        }
                    }
                    for (WPTerm t : terms) {
                        if (wpTermTaxonomyRepo.existsById(t.getId())) {
                            if (wpTermTaxonomyRepo.findById(t.getId()).isPresent()) {
                                WpTermTaxonomy tt = wpTermTaxonomyRepo.findById(t.getId()).get();
                                if (Objects.equals(tt.getTaxonomy(), "category") && tt.getTermId() != 1) {
                                    if (wpTermRepo.findById(tt.getTermId()).isPresent()) {
                                        type = wpTermRepo.findById(tt.getTermId()).get().getSlug();
                                        switch (type) {
                                            case "artikel":
                                                break;
                                            case "blog":
                                                break;
                                            case "news":
                                                break;
                                            default:
                                                type = "default";
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    JSONObject obj = new JSONObject();
                    Date date = onlyDate.parse(i.getDate().toString());
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                    obj.put("id", i.getId());
                    obj.put("title", i.getTitle());
                    obj.put("date", formattedDate);
                    obj.put("type", type);
                    if (PostStats != null) {
                        obj.put("performance", ((float)PostStats.getPerformance()/maxPerformance));
                        obj.put("relevance", ((float)PostStats.getRelevance()/maxRelevance));
                    } else {
                        obj.put("performance", 0);
                        obj.put("relevance", 0);
                    }

                    //ToDo Toten Code aufräumen
               /* if (list.length() > 0 && list.getJSONObject(list.length() - 1).getString("date").equals(formattedDate)) {
                    String currentId = list.getJSONObject(list.length() - 1).getString("title");
                   // double currentCount = list.getJSONObject(list.length() - 1).getDouble("performance");
                    list.getJSONObject(list.length() - 1).put("title", currentId + "," + i.getTitle());
                    //list.getJSONObject(list.length() - 1).put("performance", currentCount + 1);
                } else {
                    list.put(obj);
                }*/
                    if (!obj.get("type").equals("default")) {
                        list.put(obj);
                    }
                }
            }
        }
        return list.toString();
    }

    @GetMapping("/getNewestPostWithStatsByAuthor")
    public String getNewestPostWithStatsByAuthor(@RequestParam Long id) throws JSONException, ParseException {
        List<Post> posts = postRepository.findByAuthor(id.intValue());
        long newestId = 0;
        LocalDateTime newestTime = null;
        for (Post post : posts) {
            if (newestTime == null || newestTime.isBefore(post.getDate())) {
                if (post.getType().equals("post")){
                    newestTime = post.getDate();
                    newestId = post.getId();
                }
            }
        }
        return PostsById2(newestId);
    }

    @GetMapping("/getViewsOfPostDirstributedByHour")
    public String getViewsOfPostDistributedByHour(@RequestParam Long id)throws JSONException,ParseException{
        //wip
        String leViews="";
        if(statsRepo.existsByArtId(id)){
            PostStats stats = statsRepo.getStatByArtID(id);
            leViews=stats.getViewsPerDay().toString();

        }
        return leViews;
    }

    //ToDo Rename
    @GetMapping("/getPostWithStatsById")
    public String PostsById2(@RequestParam long id) throws JSONException, ParseException {
        if(!postRepository.findById(id).isPresent()) {return null;}
        Post post = postRepository.findById(id).get();
        List<String> tags = new ArrayList<>();
        String type = "default";

        PostStats PostStats = null;
        if(statsRepo.existsByArtId(post.getId())){
            PostStats = statsRepo.getStatByArtID(post.getId());
        }
        List<Long> tagIDs = null;
        if(termRelationRepo.existsByObjectId(post.getId())){
            tagIDs = termRelationRepo.getTaxIdByObject(post.getId());
        }
        List<WPTerm> terms = new ArrayList<>();
        if (tagIDs != null) {
            for (long l : tagIDs) {
                if (wpTermRepo.existsById(l)) {
                    if (wpTermRepo.findById(l).isPresent()) {
                        terms.add(wpTermRepo.findById(l).get());
                    }
                }
            }
        }
        for (WPTerm t: terms) {
            if (wpTermTaxonomyRepo.existsById(t.getId())){
                if (wpTermTaxonomyRepo.findById(t.getId()).isPresent()){
                    WpTermTaxonomy tt = wpTermTaxonomyRepo.findById(t.getId()).get();
                    if (Objects.equals(tt.getTaxonomy(), "category")){
                        if (wpTermRepo.findById(tt.getTermId()).isPresent() && tt.getTermId() != 1) {
                            type = wpTermRepo.findById(tt.getTermId()).get().getSlug();
                        }
                    } else if (Objects.equals(tt.getTaxonomy(), "post_tag")) {
                        tags.add(wpTermRepo.findById(tt.getTermId()).get().getName());
                    }
                }
            }
        }

        JSONObject obj = new JSONObject();
        DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = onlyDate.parse(post.getDate().toString());
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        obj.put("id", post.getId());
        obj.put("title", post.getTitle());
        obj.put("date", formattedDate);
        obj.put("tags", tags);
        obj.put("type", type);
        if(PostStats != null){
            float maxPerformance =   statsRepo.getMaxPerformance();
            float maxRelevance = statsRepo.getMaxRelevance();
            obj.put("performance", ((float)PostStats.getPerformance()/maxPerformance));
            obj.put("relevance", ((float)PostStats.getRelevance()/maxRelevance));
            obj.put("clicks", PostStats.getClicks().toString());
            obj.put("searchSuccesses", PostStats.getSearchSuccess());
            obj.put("searchSuccessRate", PostStats.getSearchSuccessRate());
            obj.put("referrings", PostStats.getRefferings());
            obj.put("lettercount", PostStats.getLettercount());
            obj.put("articleReferringRate", PostStats.getArticleReferringRate());
        }else {
            obj.put("performance",0);
            obj.put("relevance",0);
            obj.put("clicks", "0");
            obj.put("searchSuccesses",0);
            obj.put("searchSuccessRate",0);
            obj.put("referrings",0);
            obj.put("lettercount", 0);
            obj.put("articleReferringRate",0);}

        return obj.toString();
    }


    @GetMapping("/getPostStatsByIdWithAuthor")
    public JSONObject PostStatsByIdForFrontend(@RequestParam long id) throws JSONException, ParseException {
        if(!postRepository.findById(id).isPresent()) {return null;}
        Post post = postRepository.findById(id).get();
        List<String> tags = new ArrayList<>();
        String type = "default";

        PostStats PostStats = null;
        if(statsRepo.existsByArtId(post.getId())){
            PostStats = statsRepo.getStatByArtID(post.getId());
        }
        List<Long> tagIDs = null;
        if(termRelationRepo.existsByObjectId(post.getId())){
            tagIDs = termRelationRepo.getTaxIdByObject(post.getId());
        }
        List<WPTerm> terms = new ArrayList<>();
        if (tagIDs != null) {
            for (long l : tagIDs) {
                if (wpTermRepo.existsById(l)) {
                    if (wpTermRepo.findById(l).isPresent()) {
                        terms.add(wpTermRepo.findById(l).get());
                    }
                }
            }
        }
        for (WPTerm t: terms) {
            if (wpTermTaxonomyRepo.existsById(t.getId())){
                if (wpTermTaxonomyRepo.findById(t.getId()).isPresent()){
                    WpTermTaxonomy tt = wpTermTaxonomyRepo.findById(t.getId()).get();
                    if (Objects.equals(tt.getTaxonomy(), "category")){
                        if (wpTermRepo.findById(tt.getTermId()).isPresent() && tt.getTermId() != 1) {
                            type = wpTermRepo.findById(tt.getTermId()).get().getSlug();
                        }
                    } else if (Objects.equals(tt.getTaxonomy(), "post_tag")) {
                        tags.add(wpTermRepo.findById(tt.getTermId()).get().getName());
                    }
                }
            }
        }

        JSONObject obj = new JSONObject();
        DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = onlyDate.parse(post.getDate().toString());
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        obj.put("id", post.getId());
        obj.put("title", post.getTitle());
        obj.put("date", formattedDate);
        obj.put("tags", tags);
        obj.put("type", type);
        if(PostStats != null){
            float maxPerformance =   statsRepo.getMaxPerformance();
            float maxRelevance = statsRepo.getMaxRelevance();
            obj.put("performance", ((float)PostStats.getPerformance()/maxPerformance));
            obj.put("relevance", ((float)PostStats.getRelevance()/maxRelevance));
            obj.put("clicks", PostStats.getClicks().toString());
            obj.put("lettercount", PostStats.getLettercount());
        }else {
            obj.put("performance",0);
            obj.put("relevance",0);
            obj.put("clicks", "0");
            obj.put("searchSuccesses",0);
            obj.put("searchSuccessRate",0);
            obj.put("referrings",0);
            obj.put("lettercount", 0);
            obj.put("articleReferringRate",0);
        }

        obj.put("authors", postMetaRepo.getAuthorsByPostId(id));

        return obj;
    }

    //STATS

    //ToDo Toten Code aufräumen
   /* @GetMapping("/{id}")
    public Optional<PostStats> getStat(@PathVariable Long id) {
        return statRepository.findById(id);
    }*/

    @GetMapping
    public List<PostStats> getAllStats() {
        return statRepository.findAll();
    }

    @GetMapping("/maxPerformance")
    public float getMaxPerformance(){
        return statRepository.getMaxPerformance();
    }

    @GetMapping("/maxRelevance")
    public float getMaxRelevance(){
        return statRepository.getMaxRelevance();
    }
    @GetMapping("/getPerformanceByArtId")
    public float getPerformanceByArtId(@RequestParam int id){
        return statRepository.getPerformanceByArtID(id);
    }

    //ToDo Move -> Views eines Users sollten nicht im PostController sein.
    @GetMapping("/getViewsOfUser")
    public long getViewsOfUserById(@RequestParam Long id){
        List<Post> posts = postRepo.findByAuthor(id.intValue());
        long views = 0 ;
        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();

        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();
        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                PostStats Stat = statRepository.getStatByArtID(post.getId());
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : taxTermRepo.findByTermTaxonomyId(l)) {
                        if (termTax.getTermId() == tagIdBlog||termTax.getTermId() == tagIdArtikel||termTax.getTermId() == tagIdPresse) {
                            views = views + Stat.getClicks();
                        }
                    }


                }
            }
        }
        return views ;
    }

    @GetMapping("/getPostCountOfUser")
    public long getPostCountOfUserById(@RequestParam Long id){
        List<Post> posts = postRepo.findByAuthor(id.intValue());
        long PostCount = 0 ;
        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();

        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();
        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                PostStats Stat = statRepository.getStatByArtID(post.getId());
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : taxTermRepo.findByTermTaxonomyId(l)) {
                        if (termTax.getTermId() == tagIdBlog||termTax.getTermId() == tagIdArtikel||termTax.getTermId() == tagIdPresse) {
                            PostCount++ ;
                        }
                    }


                }
            }
        }
        return PostCount ;
    }




    @GetMapping("/bestPost")
    public String getBestPost(@RequestParam Long id, @RequestParam String type) throws JSONException {
        List<Post> Posts = postRepo.findByAuthor(id.intValue());
        if (Posts.size() == 0) {
            return null;
        }
        PostStats PostStats = null;
        float max = 0;
        long PostId = 0;



        for (Post post : Posts) {
            if (statRepository.existsByArtId(post.getId())) {
                PostStats = statRepository.getStatByArtID(post.getId());
                if (type.equals("relevance")) {
                    if (PostStats.getRelevance() > max) {
                        float maxRelevance = statsRepo.getMaxRelevance();
                        max = ((float)PostStats.getRelevance()/maxRelevance);
                        PostId = PostStats.getArtId();
                    }
                }
                if (type.equals("performance")) {
                    if (PostStats.getPerformance() > max) {
                        float maxPerformance =   statsRepo.getMaxPerformance();
                        max = ((float)PostStats.getPerformance()/maxPerformance);
                        PostId = PostStats.getArtId();
                    }
                }
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("ID", PostId);
        obj.put(type, max);
        obj.put("title", postRepo.findById(PostId).get().getTitle());
        return obj.toString();
    }

    //ToDo Rename
    @GetMapping("/getPostStat")
    public String getStat2(@RequestParam Long id) throws JSONException {
        PostStats Stat = statRepository.getStatByArtID(id);
        JSONObject obj = new JSONObject();
        obj.put("Post-Id",Stat.getArtId());
        float maxPerformance =   statsRepo.getMaxPerformance();
        float maxRelevance = statsRepo.getMaxRelevance();
        obj.put("performance", ((float)Stat.getPerformance()/maxPerformance));
        obj.put("relevanz", ((float)Stat.getRelevance()/maxRelevance));
        obj.put("Views",Stat.getClicks());
        obj.put("Refferings",Stat.getReferrings());
        obj.put("Article Reffering Rate",Stat.getArticleReferringRate());
        obj.put("Search Successes",Stat.getSearchSucces());
        obj.put("Search Success Rate",Stat.getSearchSuccessRate());

        return obj.toString();
    }

    @GetMapping("/getNewestStatsByAuthor")
    public String getNewestStatsByAuthor(@RequestParam Long id) throws JSONException{
        List<Post> posts =postRepo.findByAuthor(id.intValue()) ;
        long newestId = 0 ;
        LocalDateTime newestTime = null ;
        for(Post post : posts){
            if(newestTime == null || newestTime.isBefore(post.getDate())){
                newestTime = post.getDate();
                newestId = post.getId();
            }
        }
        long views = 0;
        long searchSuccesses = 0;
        float SearchSuccessRate = 0 ;
        long refferings = 0;
        float refrate = 0;
        float relevanz = 0;
        float performance = 0 ;

        if(statRepository.existsByArtId(newestId)){
            PostStats PostStats = statRepository.getStatByArtID(newestId);
            views = PostStats.getClicks();
            searchSuccesses = PostStats.getSearchSuccess();
            SearchSuccessRate = PostStats.getSearchSuccessRate();
            refferings = PostStats.getRefferings();
            refrate = PostStats.getArticleReferringRate();
            float maxPerformance =   statsRepo.getMaxPerformance();
            float maxRelevance = statsRepo.getMaxRelevance();
            relevanz = ((float)PostStats.getRelevance()/maxRelevance);
            performance = ((float)PostStats.getPerformance()/maxPerformance);
        }
        JSONObject obj = new JSONObject();
        obj.put("ID",newestId);
        obj.put("views",views);
        obj.put("Search Successes",searchSuccesses);
        obj.put("Search Success Rate",SearchSuccessRate);
        obj.put("refferings",refferings);
        obj.put("article reffering rate",refrate);
        obj.put("relevanz",relevanz);
        obj.put("performance",performance);


        return obj.toString();

    }

    @GetMapping("/getNewestStatsByAuthorSessionId")
    public String getNewestStatsByAuthorSessionId(@RequestParam String SessionId) throws JSONException{
        if(userRepo.existsByActivationKey(SessionId)){
            Long id = userRepo.findByActivationKey(SessionId).get().getId();
            List<Post> posts =postRepo.findByAuthor(id.intValue()) ;
            long newestId = 0 ;
            LocalDateTime newestTime = null ;
            for(Post post : posts){
                if(newestTime == null || newestTime.isBefore(post.getDate())){
                    newestTime = post.getDate();
                    newestId = post.getId();
                }
            }
            long views = 0;
            long searchSuccesses = 0;
            float SearchSuccessRate = 0 ;
            long refferings = 0;
            float refrate = 0;
            float relevanz = 0;
            float performance = 0 ;

            if(statRepository.existsByArtId(newestId)){
                PostStats PostStats = statRepository.getStatByArtID(newestId);
                views = PostStats.getClicks();
                searchSuccesses = PostStats.getSearchSuccess();
                SearchSuccessRate = PostStats.getSearchSuccessRate();
                refferings = PostStats.getRefferings();
                refrate = PostStats.getArticleReferringRate();
                float maxPerformance =   statsRepo.getMaxPerformance();
                float maxRelevance = statsRepo.getMaxRelevance();
                relevanz = ((float)PostStats.getRelevance()/maxRelevance);
                performance = ((float)PostStats.getPerformance()/maxPerformance);
            }
            JSONObject obj = new JSONObject();
            obj.put("ID",newestId);
            obj.put("views",views);
            obj.put("Search Successes",searchSuccesses);
            obj.put("Search Success Rate",SearchSuccessRate);
            obj.put("refferings",refferings);
            obj.put("article reffering rate",refrate);
            obj.put("relevanz",relevanz);
            obj.put("performance",performance);


            return obj.toString();}
        else{return "SESSION ID WRONG";}

    }
    @GetMapping("/getViewsOfPostDistributedByHours")
    public Map<String,Long>getViewsDistributedByHour(@RequestParam Long PostId){
        PostStats postStats= statsRepo.findByArtIdAndAndYear(PostId,aktuellesJahr);
        Map<String,Long>viewsPerHour=postStats.getViewsPerHour();
        return viewsPerHour;
    }

    @GetMapping("/maxViewsByLocation")
    public Map<String, Long> getMaxViewsByLocation(@RequestParam(defaultValue = "true") boolean includeCountry,
                                                     @RequestParam(defaultValue = "true") boolean includeRegion,
                                                     @RequestParam(defaultValue = "true") boolean includeCity) {
        List<PostStats> allPostStats = statsRepo.findAll();
        long maxViewsCountry = 0;
        long maxViewsRegion = 0;
        long maxViewsCity = 0;
        String maxLocationCountry = null;
        String maxLocationRegion = null;
        String maxLocationCity = null;

        for (PostStats postStats : allPostStats) {
            Map<String, Map<String, Map<String, Long>>> viewsByLocation = postStats.getViewsByLocation();

            for (String country : viewsByLocation.keySet()) {
                if ("global".equals(country)) continue;

                long countryTotal = 0;
                Map<String, Map<String, Long>> regions = viewsByLocation.get(country);

                for (String region : regions.keySet()) {
                    if ("gesamt".equals(region)) continue;

                    long regionTotal = 0;
                    Map<String, Long> cities = regions.get(region);

                    for (String city : cities.keySet()) {
                        if ("gesamt".equals(city)) continue;

                        long views = cities.get(city);
                        regionTotal += views;

                        if (includeCity && views > maxViewsCity) {
                            maxViewsCity = views;
                            maxLocationCity = city;
                        }
                    }

                    if (includeRegion && regionTotal > maxViewsRegion) {
                        maxViewsRegion = regionTotal;
                        maxLocationRegion = region;
                    }

                    countryTotal += regionTotal;
                }

                if (includeCountry && countryTotal > maxViewsCountry) {
                    maxViewsCountry = countryTotal;
                    maxLocationCountry = country;
                }
            }
        }

        Map<String, Long> result = new HashMap<>();
        if (maxLocationCountry != null) {
            result.put(maxLocationCountry, maxViewsCountry);
        }
        if (maxLocationRegion != null) {
            result.put(maxLocationRegion, maxViewsRegion);
        }
        if (maxLocationCity != null) {
            result.put(maxLocationCity, maxViewsCity);
        }
        return result;
    }

    /**
     *
     * @param sorter relevance | performance - chooses what statistic you want to sort by.
     * @return a jsonString containing the ids of the top3 posts found.
     */
    @GetMapping("/getTop3")
    public String getTop3(@RequestParam String sorter) {
        List<Long> top3 = null;
        String errorString = "";
        if(sorter.equalsIgnoreCase("relevance")) {
            top3 = statsRepo.getTop3Relevance();
        }
        if(sorter.equalsIgnoreCase("performance")) {
            top3 = statsRepo.getTop3Performance();
        }

        String jsonString = null;

        if(top3 == null) {
            errorString = "Wrong sorter / table error";
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                jsonString = objectMapper.writeValueAsString(top3);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                errorString = "JSON Mapping Error";
            }
        }
        System.out.println(errorString);
        return jsonString != null? jsonString : errorString;

    }

    @GetMapping("/getTopVariableID")
    public String getTopVariable(@RequestParam String sorter, int limit) {
        List<Long> top = null;
        String errorString = "";
        if(sorter.equalsIgnoreCase("relevance")) {
            top = statsRepo.getTopRelevanceID(limit);
        }
        if(sorter.equalsIgnoreCase("performance")) {
            top = statsRepo.getTopPerformanceID(limit);
        }

        String jsonString = null;

        if(top == null) {
            errorString = "Wrong sorter / table error";
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                jsonString = objectMapper.writeValueAsString(top);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                errorString = "JSON Mapping Error";
            }
        }
        System.out.println(errorString);
        return jsonString != null? jsonString : errorString;
    }

    @GetMapping("/getTopVariableWithStats")
    public String getTopVariableWithStats(String sorter, int limit) throws JSONException, ParseException {
        List<PostStats> top = null;
        String errorString = "";
        if(sorter.equalsIgnoreCase("relevance")) {
            top = statsRepo.getTopRelevance(limit);
        }
        if(sorter.equalsIgnoreCase("performance")) {
            top = statsRepo.getTopPerformance(limit);
        }
        String jsonString = null;
        JSONArray array = new JSONArray();

        if(top == null) {
            errorString = "Wrong sorter / table error";
        } else {
            for(PostStats stats : top) {
                JSONObject obj = PostStatsByIdForFrontend(stats.getArtId());
                array.put(obj);
            }
        }
        jsonString = array.toString();
        return jsonString != null? jsonString : errorString;
    }


    @GetMapping("/testLetterCount")
    public void updateLetterCount(int lettercount, long id) {
        statsRepo.updateLetterCount(lettercount, id);
    }

    @GetMapping("/getDate")
    public String getDate(long id) {
        return postRepository.getDateById(id).toString();
    }

    @GetMapping("/getAllDates")
    public String getAllDates() {
        Map<Integer, String> answer = new HashMap<>();
        for (Post post : postRepository.findAll()) {
            answer.put(post.getId().intValue(), postRepository.getDateById(post.getId()).toString());
        }
        return new JSONObject(answer).toString();
    }

    @GetMapping("/getRelevanceById")
    public float getRelevanceById(long id) {
        return statRepository.getRelevanceById(id);
    }

    @GetMapping("/getAllRelevance")
    public String getAllRelevance() {
        Map<Integer, Float> answer = new HashMap<>();
        for(Post post : postRepository.findAllUserPosts()){
            if(statRepository.existsByArtId(post.getId())) {
                answer.put(post.getId().intValue(), statRepository.getRelevanceById(post.getId()));
            }
        }

        return new JSONObject(answer).toString();
    }

    @GetMapping("/getAllPostsWithStats")
    public String getAll() throws JSONException, ParseException {
        List<Post> posts = postRepo.findAllUserPosts();

        List<JSONObject> stats = new ArrayList<>();


        for(Post post : posts) {
            JSONObject json = PostStatsByIdForFrontend(post.getId());
            if(json.get("type").toString().toLowerCase().contains("blog")  ||
                    json.get("type").toString().toLowerCase().contains("news") ||
                    json.get("type").toString().toLowerCase().contains("artikel") || json.get("type").toString().toLowerCase().contains("whitepaper")){
                stats.add(this.PostStatsByIdForFrontend(post.getId()));
            }
        }
        return new JSONArray(stats).toString();
    }


}

