package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.MathHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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


    /**
     * Endpoint for retrieval of a single posts full-statistics, identified by its id.
     * @param id the id of the post you want to fetch stats for.
     * @return a JSON String containing keys and values for each of a posts statistics, identifiers and adjacent information such as its type.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getPostStatsByIdWithAuthor")
    public String PostStatsByIdForFrontend(@RequestParam long id) throws JSONException, ParseException {
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
                        if (wpTermRepo.findById(tt.getTermId()).isPresent() && tt.getTermId() != 1 && tt.getTermId() != 552) {
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

        return obj.toString();
    }


    @GetMapping("/getBestPostByTagClicks")
    public Long getBestPostClicks(long tagId) {
        return statsRepo.getClicksByArtId(getBestPostByTag(tagId));
    }

    @GetMapping("/getBestPostByTag")
    public Long getBestPostByTag(long tagId) {
        Long bestPost = null;
        for(Post post : getPostsByTag(tagId)) {
            if(bestPost == null) bestPost = post.getId();
            if(statsRepo.getClicksByArtId(post.getId()) > statsRepo.getClicksByArtId(bestPost)) bestPost = post.getId();
        }
        return bestPost;
    }

    @GetMapping("/getAverageClicksByTag")
    public double getAverageClicksByTag(long tagId) {
        int value = 0;
        for(Post post : getPostsByTag(tagId)) {
            value += statsRepo.getClicksByArtId(post.getId());
        }
        return (double) value / getPostsByTag(tagId).size();
    }

    @GetMapping("/getPostsByTag")
    public List<Post> getPostsByTag(long tagId) {
        return postRepo.findAllUserPosts().stream().filter(post -> {
            try {
                return getTagsById(post.getId()).contains(tagId);
            } catch (JSONException e) {
                return false;
            }
        }).toList();
    }

    public List<Long> getTagsById(long id) throws JSONException {
        if(!postRepository.findById(id).isPresent()) {return null;}
        Post post = postRepository.findById(id).get();
        List<Long> tags = new ArrayList<>();

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
                    if (Objects.equals(tt.getTaxonomy(), "post_tag")) {
                        tags.add(tt.getTermId());
                    }
                }
            }
        }

        return tags;
    }

    //STATS

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

    /**
     *
     * @param id the id of the post you want the type of.
     * @return the type of Post "news" | "article" | "blog" | "podcast" | "whitepaper" | "ratgeber"
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getType(@RequestParam long id) throws JSONException, ParseException {
        if(!postRepository.findById(id).isPresent()) {return null;}
        Post post = postRepository.findById(id).get();
        List<String> tags = new ArrayList<>();
        String type = "default";
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
                        if (wpTermRepo.findById(tt.getTermId()).isPresent() && tt.getTermId() != 1 && tt.getTermId() != 552) {
                            type = wpTermRepo.findById(tt.getTermId()).get().getSlug();
                        }
                    } else if (Objects.equals(tt.getTaxonomy(), "post_tag")) {
                        tags.add(wpTermRepo.findById(tt.getTermId()).get().getName());
                    }
                }
            }
        }

        return type;
    }

    /**
     *
     * @return count of all user posts.
     */
    @GetMapping("/getCountTotalPosts")
    public int getCountTotalPosts() {
        return postRepo.findAllUserPosts().size();
    }

    /**
     *
     * @param type ("news" | "artikel" | "blog" | "whitepaper")
     * @return count of all posts with the type given.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getCountPostByType")
    public int getCountPostByType(String type) throws JSONException, ParseException {
        int count = 0;

        for(Post post : postRepo.findAllUserPosts()) {
            if(getType(post.getId()).equalsIgnoreCase(type)) count++;
        }
        return count;
    }

    @GetMapping("/getAveragesByTypesAll")
    public String getAverageByTypesAll() throws JSONException, ParseException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("news", 0);
        counts.put("artikel", 0);
        counts.put("blog", 0);
        counts.put("whitepaper", 0);
        counts.put("ratgeber", 0);
        counts.put("podcast", 0);

        clicks.put("news", 0);
        clicks.put("artikel", 0);
        clicks.put("blog", 0);
        clicks.put("whitepaper", 0);
        clicks.put("ratgeber", 0);
        clicks.put("podcast", 0);

        for(Post post : postRepo.findAll()) {
            switch (getType(post.getId())) {
                case "news" -> {
                    counts.put("news", counts.getInt("news") + 1);
                    try {
                        clicks.put("news", clicks.getInt("news") + statsRepo.getClicksByArtId(post.getId()));
                    } catch (Exception ignored) {}
                }
                case "artikel" -> {
                    counts.put("artikel", counts.getInt("artikel") + 1);
                    try {
                    clicks.put("artikel", clicks.getInt("artikel") + statsRepo.getClicksByArtId(post.getId()));
                    } catch (Exception ignored) {}
                }
                case "blog" -> {
                    counts.put("blog", counts.getInt("blog") + 1);
                    try {
                        clicks.put("blog", clicks.getInt("blog") + statsRepo.getClicksByArtId(post.getId()));
                    } catch (Exception ignored) {}
                }
                case "whitepaper" -> {
                    counts.put("whitepaper", counts.getInt("whitepaper") + 1);
                    try {
                        clicks.put("whitepaper", clicks.getInt("whitepaper") + statsRepo.getClicksByArtId(post.getId()));
                    } catch (Exception ignored) {}
                }
                case "ratgeber" -> {
                    counts.put("ratgeber", counts.getInt("ratgeber") + 1);
                    try {
                        clicks.put("ratgeber", clicks.getInt("ratgeber") + statsRepo.getClicksByArtId(post.getId()));
                    } catch (Exception ignored) {}
                }
                case "podcast" -> {
                    counts.put("podcast", counts.getInt("podcast") + 1);
                    clicks.put("podcast", clicks.getInt("podcast") + 1);
                }

            }
        }
        if(counts.getInt("news") != 0) {
            averages.put("news", clicks.getInt("news") / counts.getInt("news"));
        }
        if(counts.getInt("artikel") != 0) {
            averages.put("artikel", clicks.getInt("artikel") / counts.getInt("artikel"));
        }
        if(counts.getInt("blog") != 0) {
            averages.put("blog", clicks.getInt("blog") / counts.getInt("blog"));
        }
        if(counts.getInt("whitepaper") != 0) {
            averages.put("whitepaper", clicks.getInt("whitepaper") / counts.getInt("whitepaper"));
        }
        if(counts.getInt("ratgeber") != 0) {
            averages.put("ratgeber", clicks.getInt("ratgeber") / counts.getInt("ratgeber"));
        }
        if(counts.getInt("podcast") != 0) {
            averages.put("podcast", clicks.getInt("podcast") / counts.getInt("podcast"));
        }

        return averages.toString();

    }



    /**
     *
     * @param sorter sorter "relevance" | "performance" | "clicks" - chooses what statistic you want to sort by.
     * @param type "news" | "article" | "blog" | "podcast" | "whitepaper" | "ratgeber"
     * @return a JSON String of the Top Posts (as many as Limit) with post-type being type and sorted by sorter.
     */
    @GetMapping("/getTopWithType")
    public String getTopWithType(@RequestParam String sorter, String type, int limit) throws JSONException, ParseException {
        List<PostStats> top = null;
        String errorString = "";

        if(sorter.equalsIgnoreCase("relevance")) {
            top = statsRepo.findAllByOrderByRelevanceDesc();
        }
        if(sorter.equalsIgnoreCase("performance")) {
            top = statsRepo.findAllByOrderByPerformanceDesc();
        }
        if(sorter.equalsIgnoreCase("clicks")) {
            top = statsRepo.findAllByOrderByClicksDesc();
        }
        String jsonString = null;
        JSONArray array = new JSONArray();

        assert top != null;
        top = top.stream().filter(postStats -> {
            try {
                return type.equalsIgnoreCase(getType(postStats.getArtId()));
            } catch (JSONException | ParseException e) {
                throw new RuntimeException(e);
            }
        }).limit(limit).toList();


        for (PostStats stats : top) {
            JSONObject obj = new JSONObject(PostStatsByIdForFrontend(stats.getArtId()));
            array.put(obj);
        }
        jsonString = array.toString();
        return jsonString != null? jsonString : errorString;
    }

    /**
     * Endpoint for retrieval of the top 5 posts sorted by a specific sorter.
     * @param sorter "relevance" or "performance".
     * @return a JSON String containing the stats, identifiers etc. of the top5 posts compared by given metric.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getTop5")
    public String getTop5(String sorter) throws JSONException, ParseException {
        List<PostStats> top = null;
        String errorString = "";
        if(sorter.equalsIgnoreCase("relevance")) {
            top = statsRepo.getTop5Relevance();
        }
        if(sorter.equalsIgnoreCase("performance")) {
            top = statsRepo.getTop5Performance();
        }
        String jsonString = null;
        JSONArray array = new JSONArray();

        if(top == null) {
            errorString = "Wrong sorter / table error";
        } else {
            for(PostStats stats : top) {
                JSONObject obj = new JSONObject(PostStatsByIdForFrontend(stats.getArtId()));
                array.put(obj);
            }
        }
        jsonString = array.toString();
        return jsonString != null? jsonString : errorString;
    }

    /**
     * debug call to manually set the lettercount of a post.
     * @param lettercount the lettercount you want to set.
     * @param id the postId of the post you want to set lettercount for.
     */
    @GetMapping("/testLetterCount")
    public void updateLetterCount(int lettercount, long id) {
        statsRepo.updateLetterCount(lettercount, id);
    }

    /**
     * Endpoint for retrieval of a posts creation date.
     * @param id the id of the post you want the creation date for.
     * @return the Creation-Date of the post as a String.
     */
    @GetMapping("/getDate")
    public String getDate(long id) {
        return postRepository.getDateById(id).toString();
    }

    /**
     * Endpoint for retrieval of all Dates (value) of Posts-creation by postId (name) in a JSON String.
     * @return JSON String of: int PostId, String Date for all Posts.
     */
    @GetMapping("/getAllDates")
    public String getAllDates() {
        Map<Integer, String> answer = new HashMap<>();
        for (Post post : postRepository.findAll()) {
            answer.put(post.getId().intValue(), postRepository.getDateById(post.getId()).toString());
        }
        return new JSONObject(answer).toString();
    }

    /**
     * Endpoint for retrieval of Relevance for a specific Post identified by their ID.
     * @param id the id of the Post you want to get relevance stat for.
     * @return float of the posts relevance stat.
     */
    @GetMapping("/getRelevanceById")
    public float getRelevanceById(long id) {
        return statRepository.getRelevanceById(id);
    }

    /**
     * Endpoint for retrieval of posts with their relevance.
     * @return a JSON String containing: int postId (name) and float relevance (value).
     */
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

    /**
     * Endpoint for retrieval of ALL Posts that are not Original Content (User Posts (Blog, Article, Whitepaper), News)
     * @return a JSON String containing all stats, identifiers, type and more for all posts.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getAllPostsWithStats")
    public String getAll() throws JSONException, ParseException {
        List<Post> posts = postRepo.findAllUserPosts();

        List<JSONObject> stats = new ArrayList<>();


        for(Post post : posts) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(post.getId()));
            if(json.get("type").toString().toLowerCase().contains("blog")  ||
                    json.get("type").toString().toLowerCase().contains("news") ||
                    json.get("type").toString().toLowerCase().contains("artikel") ||
                    json.get("type").toString().toLowerCase().contains("whitepaper")
            ){
                stats.add(json);
            }
        }
        return new JSONArray(stats).toString();
    }

    /**
     * Endpoint to retrieve all podcast-posts-stats.
     * @return a JSON String of all "podcast" posts with their respective stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getAllPodcastsWithStats")
    public String getAllPodcasts() throws JSONException, ParseException {
        List<Post> posts = postRepo.findAllUserPosts();

        List<JSONObject> stats = new ArrayList<>();

        for(Post post : posts) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(post.getId()));
            if(json.get("type").toString().toLowerCase().contains("podcast")) {
                stats.add(json);
            }
        }
        return new JSONArray(stats).toString();

    }

    /**
     * Endpoint to retrieve all ratgeber-posts-stats.
     * @return a JSON String of all "ratgeber" posts with their respective stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getAllRatgeberWithStats")
    public String getAllRatgeber() throws JSONException, ParseException {
        List<Post> posts = postRepo.findAllUserPosts();

        List<JSONObject> stats = new ArrayList<>();

        for(Post post : posts) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(post.getId()));
            if(json.get("type").toString().toLowerCase().contains("ratgeber") || json.get("type").toString().toLowerCase().contains("cyber-risk-check")) {
                stats.add(json);
            }
        }
        return new JSONArray(stats).toString();

    }





////////////////////////////////////////////////////////////////////////
    /////Similar Posts

    /**
     * Endpoint to retrieve posts with similar tags based on a similarity percentage.
     *
     * <p>
     * This endpoint fetches posts that have tags similar to the given post,
     * based on a provided similarity percentage threshold. For example, if the similarity
     * percentage is set to 50, it will fetch posts that share at least 50% of their tags
     * with the given post.
     * </p>
     *
     * <p>
     * The result includes the post ID, similarity percentage, and relevance. Relevance is retrieved
     * from the `PostStats` repository for the current year.
     * </p>
     *
     * @param postId The ID of the post for which to find similar posts.
     * @param similarityPercentage The minimum percentage of tag similarity required to consider a post as similar.
     *
     * @return A JSON string representation of posts with their ID, similarity percentage, and relevance.
     *
     * @throws JSONException If any error occurs during JSON processing.
     */
    @GetMapping("/getSimilarPostByTagSimilarity")
    public String getSimilarPostByTagSimilarity(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {
        List<Long> termTaxonomyIdsForPostGiven = termRelRepo.getTaxIdByObject(postId);
        List<Long> tagIdsForPostGiven = taxTermRepo.getTermIdByTaxId(termTaxonomyIdsForPostGiven);

        JSONArray Ergebnis = new JSONArray();
        Map<Long, Float> postAndSimilarityMap = new HashMap<>();

        // Liste aller Posts holen
        List<wp_term_relationships> allPostsRelationships = termRelRepo.findAll();
        for (wp_term_relationships otherPostRel : allPostsRelationships) {
            Long otherPostId = otherPostRel.getObjectId();
            if (otherPostId.equals(postId)) continue; // Ignoriere den gegebenen Post

            List<Long> termTaxonomyIdsForOtherPost = termRelRepo.getTaxIdByObject(otherPostId);
            List<Long> tagIdsForOtherPost = taxTermRepo.getTermIdByTaxId(termTaxonomyIdsForOtherPost);

            // Ähnlichkeit der Tags berechnen
            int commonTagsCount = (int) tagIdsForPostGiven.stream().filter(tag -> tagIdsForOtherPost.contains(tag)).count();
            float currentSimilarityPercentage = (commonTagsCount * 1.0f / tagIdsForPostGiven.size()) * 100;

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }

        int currentYear = LocalDate.now().getYear();

        for (Map.Entry<Long, Float> entry : postAndSimilarityMap.entrySet()) {
            JSONObject obj = new JSONObject();
            Long otherPostId = entry.getKey();
            PostStats postStat = statsRepo.findByArtIdAndAndYear(otherPostId, currentYear);

            obj.put("postId", otherPostId);
            obj.put("similarity", entry.getValue());
            obj.put("relevance", postStat.getRelevance());

            Ergebnis.put(obj);
        }

        return Ergebnis.toString();
    }

    /**
     * Endpoint, um ähnliche Beiträge basierend auf Tag-Ähnlichkeit und Gesamt-Klicks abzurufen.
     *
     * @param postId Der ID des Beitrags, für den ähnliche Beiträge gesucht werden sollen.
     * @param similarityPercentage Der Mindestprozentsatz der Übereinstimmung von Tags, um als ähnlich betrachtet zu werden.
     * @return Ein JSON-String, der ähnliche Beiträge basierend auf Tag-Ähnlichkeit und Gesamt-Klicks repräsentiert.
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getSimilarPostByTagAndClicks")
    public String getSimilarPostByTagAndClicks(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {

        // 1. Tags und Taxonomie-IDs des gegebenen Beitrags abrufen
        List<Long> termTaxonomyIdsForPostGiven = termRelRepo.getTaxIdByObject(postId);
        List<Long> tagIdsForPostGiven = taxTermRepo.getTermIdByTaxId(termTaxonomyIdsForPostGiven);

        JSONArray Ergebnis = new JSONArray();
        Map<Long, Float> postAndSimilarityMap = new HashMap<>();

        // 2. Liste aller Beiträge holen und deren Ähnlichkeit mit dem gegebenen Beitrag berechnen
        List<wp_term_relationships> allPostsRelationships = termRelRepo.findAll();
        for (wp_term_relationships otherPostRel : allPostsRelationships) {
            Long otherPostId = otherPostRel.getObjectId();
            if (otherPostId.equals(postId)) continue;  // Ignoriere den gegebenen Beitrag

            List<Long> termTaxonomyIdsForOtherPost = termRelRepo.getTaxIdByObject(otherPostId);
            List<Long> tagIdsForOtherPost = taxTermRepo.getTermIdByTaxId(termTaxonomyIdsForOtherPost);

            // Ähnlichkeit der Tags berechnen
            int commonTagsCount = (int) tagIdsForPostGiven.stream().filter(tag -> tagIdsForOtherPost.contains(tag)).count();
            float currentSimilarityPercentage = (commonTagsCount * 1.0f / tagIdsForPostGiven.size()) * 100;

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }

        // 3. Ähnliche Beiträge basierend auf ihren Gesamt-Klicks hinzufügen
        for (Map.Entry<Long, Float> entry : postAndSimilarityMap.entrySet()) {
            JSONObject obj = new JSONObject();
            Long otherPostId = entry.getKey();
            PostStats postStat = statsRepo.findByArtIdAndAndYear(otherPostId, LocalDate.now().getYear());

            obj.put("postId", otherPostId);
            obj.put("similarity", entry.getValue());
            obj.put("totalClicks", postStat.getClicks());

            Ergebnis.put(obj);
        }

        return Ergebnis.toString();
    }




    /**
     * Holt ähnliche Beiträge basierend auf der Tag-Ähnlichkeit und bewertet sie nach der Gesamtzahl der Klicks
     * innerhalb eines gegebenen Datumsbereichs.
     *
     * @param postId              Die ID des Beitrags, zu dem ähnliche Beiträge gefunden werden sollen.
     * @param similarityPercentage Der minimale Tag-Ähnlichkeitsprozentsatz.
     * @param startDate           Das Startdatum des Datumsbereichs im Format "dd.MM".
     * @param endDate             Das Enddatum des Datumsbereichs im Format "dd.MM".
     * @return Eine Liste von ähnlichen Beiträgen und ihrer Klickanzahl im gegebenen Datumsbereich.
     * @throws JSONException wenn ein JSON-Fehler auftritt.
     */
    @GetMapping("/getSimilarPostByClicksInRange")
    public String getSimilarPostByClicksInRange(@RequestParam long postId, @RequestParam float similarityPercentage,
                                                @RequestParam String startDate, @RequestParam String endDate) throws JSONException {

        List<Long> termTaxonomyIdsForPostGiven = termRelRepo.getTaxIdByObject(postId);
        List<Long> tagIdsForPostGiven = taxTermRepo.getTermIdByTaxId(termTaxonomyIdsForPostGiven);

        JSONArray Ergebnis = new JSONArray();
        Map<Long, Float> postAndSimilarityMap = new HashMap<>();

        // Liste aller Posts holen
        List<wp_term_relationships> allPostsRelationships = termRelRepo.findAll();
        for (wp_term_relationships otherPostRel : allPostsRelationships) {
            Long otherPostId = otherPostRel.getObjectId();
            if (otherPostId.equals(postId)) continue; // Ignoriere den gegebenen Post

            List<Long> termTaxonomyIdsForOtherPost = termRelRepo.getTaxIdByObject(otherPostId);
            List<Long> tagIdsForOtherPost = taxTermRepo.getTermIdByTaxId(termTaxonomyIdsForOtherPost);

            // Ähnlichkeit der Tags berechnen
            int commonTagsCount = (int) tagIdsForPostGiven.stream().filter(tag -> tagIdsForOtherPost.contains(tag)).count();
            float currentSimilarityPercentage = (commonTagsCount * 1.0f / tagIdsForPostGiven.size()) * 100;

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }

        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        for (Map.Entry<Long, Float> entry : postAndSimilarityMap.entrySet()) {
            JSONObject obj = new JSONObject();
            Long otherPostId = entry.getKey();

            // Abruf der Ansichten für das letzte Jahr
            PostStats postStat = statsRepo.findByArtIdAndAndYear(otherPostId, LocalDate.now().getYear());
            Map<String, Long> viewsLastYear = postStat.getViewsLastYear();

            // Kumulieren der Ansichten im gegebenen Datumsbereich
            long totalViewsInRange = viewsLastYear.entrySet().stream()
                    .filter(e -> {
                        LocalDate date = LocalDate.of(LocalDate.now().getYear(),
                                Integer.parseInt(e.getKey().split("\\.")[1]),
                                Integer.parseInt(e.getKey().split("\\.")[0]));
                        return (date.isAfter(startLocalDate) || date.isEqual(startLocalDate)) &&
                                (date.isBefore(endLocalDate) || date.isEqual(endLocalDate));
                    })
                    .mapToLong(Map.Entry::getValue)
                    .sum();

            obj.put("postId", otherPostId);
            obj.put("similarity", entry.getValue());
            obj.put("clicks", totalViewsInRange);

            Ergebnis.put(obj);
        }

        return Ergebnis.toString();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////Ausreißer

    /**
     * Gibt ein JSONArray zurück, das zwei Arrays enthält:
     * 1. Ähnliche Posts basierend auf der Tag-Ähnlichkeit, ausgenommen die als Ausreißer identifizierten Posts.
     * 2. Die als Ausreißer identifizierten Posts basierend auf ihrer Relevanz.
     *
     * @param postId Der ID des Posts, für den ähnliche Posts gesucht werden sollen.
     * @param similarityPercentage Der Mindestprozentsatz der Tag-Ähnlichkeit, um als ähnlicher Post betrachtet zu werden.
     * @return Ein JSONArray, das die ähnlichen Posts und die Ausreißer enthält.
     * @throws JSONException Bei Problemen bei der Erstellung des JSONArrays.
     */
    @GetMapping("/getSimilarPostsAndOutliers")
    public String getSimilarPostsAndOutliers(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {
        // 1. Das Ergebnis der getSimilarPostByTagSimilarity Methode abrufen
        JSONArray originalPostsArray = new JSONArray(getSimilarPostByTagSimilarity(postId, similarityPercentage));

        // 2. Eine Liste der Relevanzwerte aus dem JSONArray extrahieren
        List<Float> relevanceList = new ArrayList<>();
        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            relevanceList.add((float) obj.getDouble("relevance"));
        }

        // 3. Die Ausreißer basierend auf den Relevanzwerten mit Hilfe der getOutliersFloat Methode ermitteln
        List<Float> outliersValues = MathHelper.getOutliersFloat(relevanceList);

        JSONArray postsArray = new JSONArray();
        JSONArray outliersArray = new JSONArray();

        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            float relevance = (float) obj.getDouble("relevance");

            if (outliersValues.contains(relevance)) {
                outliersArray.put(obj);
            } else {
                postsArray.put(obj);
            }
        }

        // 4. Ein neues JSONArray erstellen, das das bereinigte Ergebnis und die Ausreißer enthält
        JSONArray combinedResult = new JSONArray();
        combinedResult.put(postsArray);
        combinedResult.put(outliersArray);

        return combinedResult.toString();
    }

    /**
     * Endpoint, um ähnliche Beiträge und Ausreißer basierend auf Tag-Ähnlichkeit und Gesamt-Klicks abzurufen.
     *
     * @param postId Der ID des Beitrags, für den ähnliche Beiträge und Ausreißer gesucht werden sollen.
     * @param similarityPercentage Der Mindestprozentsatz der Übereinstimmung von Tags, um als ähnlich betrachtet zu werden.
     * @return Ein JSON-String, der ähnliche Beiträge und Ausreißer basierend auf Tag-Ähnlichkeit und Gesamt-Klicks repräsentiert.
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getSimilarPostsAndOutliersByClicks")
    public String getSimilarPostsAndOutliersByClicks(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {
        // 1. Das Ergebnis der getSimilarPostByTagAndClicks Methode abrufen
        JSONArray originalPostsArray = new JSONArray(getSimilarPostByTagAndClicks(postId, similarityPercentage));

        // 2. Eine Liste der Gesamt-Klickwerte aus dem JSONArray extrahieren
        List<Float> clicksList = new ArrayList<>();
        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            clicksList.add((float) obj.getDouble("totalClicks"));
        }

        // 3. Die Ausreißer basierend auf den Gesamt-Klickwerten mit Hilfe der getOutliersFloat Methode ermitteln
        List<Float> outliersValues = MathHelper.getOutliersFloat(clicksList);

        JSONArray postsArray = new JSONArray();
        JSONArray outliersArray = new JSONArray();

        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            float clicks = (float) obj.getDouble("totalClicks");

            if (outliersValues.contains(clicks)) {
                outliersArray.put(obj);
            } else {
                postsArray.put(obj);
            }
        }

        // 4. Ein neues JSONArray erstellen, das das bereinigte Ergebnis und die Ausreißer enthält
        JSONArray combinedResult = new JSONArray();
        combinedResult.put(postsArray);
        combinedResult.put(outliersArray);

        return combinedResult.toString();
    }



    /**
     * Endpoint, um ähnliche Beiträge und Ausreißer basierend auf Clicks innerhalb eines gegebenen Datumsbereichs abzurufen.
     *
     * @param postId Der ID des Beitrags, für den ähnliche Beiträge gesucht werden sollen.
     * @param similarityPercentage Der Mindestprozentsatz der Übereinstimmung von Tags, um als ähnlich betrachtet zu werden.
     * @param startDate Das Anfangsdatum des Bereichs im Format "dd.MM".
     * @param endDate Das Enddatum des Bereichs im Format "dd.MM".
     * @return Ein JSON-String, der ähnliche Beiträge und Ausreißer repräsentiert.
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getSimilarPostsAndOutliersByClicksInRange")
    public String getSimilarPostsAndOutliersByClicksInRange(@RequestParam long postId,
                                             @RequestParam float similarityPercentage,
                                             @RequestParam String startDate,
                                             @RequestParam String endDate) throws JSONException {

        // 1. Das Ergebnis der getSimilarPostByClicksInRange Methode abrufen
        JSONArray originalPostsArray = new JSONArray(getSimilarPostByClicksInRange(postId, similarityPercentage, startDate, endDate));

        // 2. Eine Liste der Klicks aus dem JSONArray extrahieren
        List<Long> clicksList = new ArrayList<>();
        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            clicksList.add(obj.getLong("clicks"));
        }

        // 3. Die Ausreißer basierend auf den Klicks mit Hilfe einer geeigneten Methode ermitteln.
        List<Long> outliersValues = MathHelper.getOutliersLong(clicksList);

        JSONArray postsArray = new JSONArray();
        JSONArray outliersArray = new JSONArray();

        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            long clicks = obj.getLong("clicks");

            if (outliersValues.contains(clicks)) {
                outliersArray.put(obj);
            } else {
                postsArray.put(obj);
            }
        }

        // 4. Ein neues JSONArray erstellen, das das bereinigte Ergebnis und die Ausreißer enthält
        JSONObject combinedResult = new JSONObject();
        combinedResult.put("similarPosts", postsArray);
        combinedResult.put("outliers", outliersArray);

        return combinedResult.toString();
    }

}

