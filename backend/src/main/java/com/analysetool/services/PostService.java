package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
import com.analysetool.util.MathHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    WpTermRelationshipsRepository termRelRepo;
    @Autowired
    WpTermTaxonomyRepository taxTermRepo;
    @Autowired
    PostRepository postRepo;
    @Autowired
    PostStatsRepository postStatRepo;
    @Autowired
    PostTypeRepository postTypeRepo;
    private Calendar kalender = Calendar.getInstance();
    private int aktuellesJahr = kalender.get(Calendar.YEAR);
    @Autowired
    private PostStatsRepository statRepository;
    @Autowired
    private WPTermRepository termRepo;
    @Autowired
    private EventsService eventsService;
    @Autowired
    private EventsRepository eventsRepo;
    @Autowired
    private PostClicksByHourDLCRepository postClicksByHourRepo;
    @Autowired
    private universalStatsRepository uniRepo;
    @Autowired
    private ContentDownloadsHourlyRepository contentDownloadsRepo;
    @Autowired
    private SocialsImpressionsService soziImp;
    @Autowired
    private PostMetaRepository postMetaRepo;
    @Autowired
    private PostStatsRepository statsRepo;


    /**
     * Fetches all Posts that have been published.
     * @return an unordered List of Posts.
     */
    public List<Post> getPublishedPosts(){return postRepo.findPublishedPosts();}

    /**
     *
     * @param authorId the user_id of the author you want posts from.
     * @param page the page of results you want to receive.
     * @param size the number of results you want to receive at most.
     * @param filter the EXACT slug of a term the post is supposed to have.
     * @param search a String you want to search the db for, searches content AND title of posts.
     * @return a JSONArray of JSONObjects that contain enriched PostStats (See PostStatsByIdForFrontend).
     * @throws JSONException .
     * @throws ParseException .
     */
    public String postsByAuthorPageable(long authorId, int page, int size, String filter, String search) throws JSONException, ParseException {
        List<JSONObject> stats = new ArrayList<>();
        List<Post> list;
        if(filter.isBlank()) {
            list = postRepo.findByAuthorPageable(authorId, search, PageRequest.of(page, size));
        } else {
            list = postRepo.findByAuthorPageable(authorId, search, remapTypeToWebsiteStandard(filter), PageRequest.of(page, size));
        }

        for(Post post : list) {
            stats.add(new JSONObject(PostStatsByIdForFrontend(post.getId())));
        }
        return new JSONArray(stats).toString();
    }

    /**
     * Fetches the latest post of the specified author.
     * @param id the user_id to fetch for.
     * @return a JSON-String containing Data from PostStatsByIdForFrontend.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getNewestPostWithStatsByAuthor(@RequestParam Long id) throws JSONException, ParseException {
        List<Post> posts = postRepo.findByAuthor(id.intValue());
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
        return PostStatsByIdForFrontend(newestId);
    }

    /**
     * Fetches number of views on posts made by the specified user.
     * @param id the id of the user to fetch for.
     * @return a positive long.
     */
    public long getPostViewsOfUserById(@RequestParam Long id){
        List<Post> posts = postRepo.findByAuthor(id.intValue());
        long views = 0 ;

        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : taxTermRepo.findByTermTaxonomyId(l)) {
                        if (Constants.getInstance().getListOfPostTypesInteger().contains(termTax.getTermId().intValue())) {
                            views = views + statRepository.getSumClicks(post.getId());
                        }
                    }
                }
            }
        }
        return views ;
    }

    /**
     * Fetches the number of posts made by the specified user.
     * @param id the id of the user to fetch for.
     * @return a positive long.
     */
    public long getPostCountOfUserById(@RequestParam Long id){
        List<Post> posts = postRepo.findByAuthor(id.intValue());
        long postCount = 0 ;

        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : taxTermRepo.findByTermTaxonomyId(l)) {
                        if (Constants.getInstance().getListOfPostTypesInteger().contains(termTax.getTermId().intValue())) {
                            postCount++ ;
                        }
                    }
                }
            }
        }
        return postCount ;
    }

    /**
     * Endpoint for retrieval of a single posts full-statistics, identified by its id.
     * @param id the id of the post you want to fetch stats for.
     * @return a JSON String containing keys and values for each of a post's statistics, identifiers and adjacent information such as its type. <br>
     * Keys are: id, title, date, tags, type, performance, relevance, clicks, lettercount, searchSuccesses (optional),
     * SearchSuccessRate (optional), referrings, lettercount, articleReferringRate, downloads, authors.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String PostStatsByIdForFrontend(@RequestParam long id) throws JSONException, ParseException {
        if(postRepo.findById(id).isEmpty()) {return null;}
        Post post = postRepo.findById(id).get();
        List<String> tags = new ArrayList<>();
        List<Long> tagIds = new ArrayList<>();
        String type;

        List<Long> tagIDs = null;
        if(termRelRepo.existsByObjectId(post.getId())){
            tagIDs = termRelRepo.getTaxIdByObject(post.getId());
        }
        List<WPTerm> terms = new ArrayList<>();
        if (tagIDs != null) {
            for (long l : tagIDs) {
                if (termRepo.existsById(l)) {
                    if (termRepo.findById(l).isPresent()) {
                        terms.add(termRepo.findById(l).get());
                    }
                }
            }
        }
        for (WPTerm t: terms) {
            if (taxTermRepo.existsById(t.getId())){
                if (taxTermRepo.findById(t.getId()).isPresent()){
                    WpTermTaxonomy tt = taxTermRepo.findById(t.getId()).get();
                    if (Objects.equals(tt.getTaxonomy(), "post_tag")) {
                        //noinspection OptionalGetWithoutIsPresent
                        tags.add(termRepo.findById(tt.getTermId()).get().getName());
                        tagIds.add(tt.getTermId());
                    }
                }
            }
        }

        type = getType(id);

        JSONObject obj = new JSONObject();
        String formattedDate;
        DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        if (type.startsWith("Event") && eventsRepo.findByPostID(id).isPresent()) {
            date = onlyDate.parse(eventsRepo.findByPostID(id).get().getEventStartDate().toString());
        } else {
            date = onlyDate.parse(post.getDate().toString());
        }
        formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

        obj.put("id", post.getId());
        obj.put("title", post.getTitle());
        obj.put("date", formattedDate);


        JSONArray array = new JSONArray();
        for(int i = 0; i < tags.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("name", tags.get(i));
            json.put("id", tagIds.get(i));
            array.put(json);
        }

        obj.put("tags", array);
        obj.put("type", type);
        if(statRepository.existsByArtId(id)) {
            float maxPerformance = statsRepo.getMaxPerformance();
            float maxRelevance = statsRepo.getMaxRelevance();
            obj.put("performance", (statRepository.getPerformanceByArtID((int) id) / maxPerformance));
            obj.put("relevance", (statRepository.getRelevanceById(id) / maxRelevance));
            obj.put("clicks", statRepository.getSumClicks(id).toString());
            obj.put("lettercount", statRepository.getLetterCount(id));
        } else {
            obj.put("performance", 0);
            obj.put("relevance", 0);
            obj.put("clicks", "0");
            obj.put("lettercount", 0);
        }
        obj.put("searchSuccesses", 0);
        obj.put("searchSuccessRate", 0);
        obj.put("articleReferringRate", 0);
        obj.put("referrings", 0);

        if(type.equalsIgnoreCase("whitepaper")) {
            if(contentDownloadsRepo.existsByPostId(id)) {
                obj.put("downloads", contentDownloadsRepo.getAllDownloadsOfPostIdSummed(id));
            } else {
                obj.put("downloads", 0);
            }
        }

        obj.put("authors", new JSONArray(postMetaRepo.getAuthorsList(id)));

        return obj.toString();
    }

    /**
     * Further enriched data from PostStatsByIdForFrontend, not to be used in bulk.
     * @param id the post_id of the post to fetch data for.
     * @return a JSON-String (JSONObject). <br>
     * Adds keys "content" and "img" to PostStatsByIdForFrontend.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getPostStatsWithContent(long id) throws JSONException, ParseException {
        JSONObject json = new JSONObject(PostStatsByIdForFrontend(id));
        //noinspection OptionalGetWithoutIsPresent
        json.put("content", postRepo.findById(id).get().getContent());
        json.put("img", Constants.getInstance().getThumbnailLocationStart() + postMetaRepo.getThumbnail(id));
        return json.toString();
    }

    /**
     * Fetches the latest published post.
     * @return JSON-String (see getPostStatsWithContent)
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getNewestPost() throws JSONException, ParseException {
        return getPostStatsWithContent(postRepo.getNewestPost().getId());
    }

    /**
     * Fetches the number of clicks the best post of the given tag has.
     * @param tagId the tag to fetch for.
     * @return numeric value.
     */
    @GetMapping("/getBestPostByTagClicks")
    public int getBestPostClicks(long tagId) {
        return statsRepo.getSumClicks((getBestPostByTag(tagId)));
    }

    /**
     * Fetches the id of the best post for the given tag.
     * @param tagId the tag to fetch for.
     * @return the post_id of the "best" post in that tag.
     */
    public Long getBestPostByTag(long tagId) {
        Long bestPost = null;
        for(Post post : getPostsByTag(tagId)) {
            if(bestPost == null) bestPost = post.getId();
            if(statsRepo.getSumClicks(post.getId()) > statsRepo.getSumClicks(bestPost)) bestPost = post.getId();
        }
        return bestPost;
    }

    /**
     * Fetches the average clicks of posts in the given tag.
     * @param tagId the tag to fetch for.
     * @return the average clicks on a post of this kind.
     */
    public double getAverageClicksByTag(long tagId) {
        int value = 0;
        for(Post post : getPostsByTag(tagId)) {
            value += statsRepo.getSumClicks(post.getId());
        }
        return (double) value / getPostsByTag(tagId).size();
    }

    /**
     * Fetches all Posts with the given tag.
     * @param tagId the tag to fetch for.
     * @return a List of Posts.
     */
    public List<Post> getPostsByTag(long tagId) {
        return postRepo.findAllUserPosts().stream().filter(post -> getTagsById(post.getId()).contains(tagId)).toList();
    }

    /**
     * Utility Function to get all Tag-Ids for a specific post.
     * @param id the id of the post you want to get tags for.
     * @return a List of Ids, corresponding to Tags (Terms in the database)
     */
    public List<Long> getTagsById(long id) {
        if(postRepo.findById(id).isEmpty()) {return null;}
        Post post = postRepo.findById(id).get();
        List<Long> tags = new ArrayList<>();

        List<Long> tagIDs = null;
        if(termRelRepo.existsByObjectId(post.getId())){
            tagIDs = termRelRepo.getTaxIdByObject(post.getId());
        }
        List<WPTerm> terms = new ArrayList<>();
        if (tagIDs != null) {
            for (long l : tagIDs) {
                if (termRepo.existsById(l)) {
                    if (termRepo.findById(l).isPresent()) {
                        terms.add(termRepo.findById(l).get());
                    }
                }
            }
        }
        for (WPTerm t: terms) {
            if (taxTermRepo.existsById(t.getId())){
                if (taxTermRepo.findById(t.getId()).isPresent()){
                    WpTermTaxonomy tt = taxTermRepo.findById(t.getId()).get();
                    if (Objects.equals(tt.getTaxonomy(), "post_tag")) {
                        tags.add(tt.getTermId());
                    }
                }
            }
        }

        return tags;
    }

    /**
     * Fetches the highest value in the performance column of stats.
     * @return positive float.
     */
    public float getMaxPerformance(){
        return statRepository.getMaxPerformance();
    }

    /**
     * Fetches the highest value in the relevance column of stats.
     * @return positive float.
     */
    public float getMaxRelevance(){
        return statRepository.getMaxRelevance();
    }

    /**
     * Fetches performance for a specific Post.
     * @param id the id of the post to fetch for.
     * @return positive float.
     */
    public float getPerformanceByArtId(@RequestParam int id){
        return statRepository.getPerformanceByArtID(id);
    }

    /**
     * Fetches the name, id and type for a post by the given author - that has the highest value in type
     * @param id the id of the user to fetch for.
     * @param type the type to use as value ("relevance" | "performance").
     * @return a JSON-String containing keys id, the given value of (type) and title.
     * @throws JSONException
     */
    public String getBestPost(@RequestParam Long id, @RequestParam String type) throws JSONException {
        List<Post> Posts = postRepo.findByAuthor(id.intValue());
        if (Posts.size() == 0) {
            return null;
        }
        PostStats postStats;
        float max = 0;
        long PostId = 0;

        for (Post post : Posts) {
            if (statRepository.existsByArtId(post.getId())) {
                postStats = statRepository.getStatByArtIDLatestYear(post.getId());
                if (type.equals("relevance")) {
                    if (postStats.getRelevance() > max) {
                        float maxRelevance = statsRepo.getMaxRelevance();
                        max = (postStats.getRelevance() /maxRelevance);
                        PostId = postStats.getArtId();
                    }
                }
                if (type.equals("performance")) {
                    if (postStats.getPerformance() > max) {
                        float maxPerformance =   statsRepo.getMaxPerformance();
                        max = (postStats.getPerformance() /maxPerformance);
                        PostId = postStats.getArtId();
                    }
                }
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("ID", PostId);
        obj.put(type, max);
        //noinspection OptionalGetWithoutIsPresent
        obj.put("title", postRepo.findById(PostId).get().getTitle());
        return obj.toString();
    }

    /**
     * Endpoint for retrieval for the number of total posts on the website.
     * @return count of all user posts.
     */
    public int getCountTotalPosts() {
        return postRepo.findAllUserPosts().size();
    }

    /**
     *  Endpoint for retrieval for the number of posts on the website of a certain type.
     * @param type ("news" | "artikel" | "blog" | "whitepaper")
     * @return count of all posts with the type given.
     */
    public int getCountPostByType(String type) {
        int count = 0;

        for(Post post : postRepo.findAllUserPosts()) {
            if(getType(post.getId()).equalsIgnoreCase(type)) count++;
        }
        return count;
    }


    /**
     * Fetches a post-views by their daytime, given as hours.
     * @param PostId the post to fetch for.
     * @return a map from hour to the number of clicks.
     */
    public Map<String,Long> getViewsDistributedByHour(@RequestParam Long PostId){
        PostStats postStats= statsRepo.findByArtIdAndYear(PostId,aktuellesJahr);
        Map<String,Long>viewsPerHour=postStats.getViewsPerHour();
        return viewsPerHour;
    }

    /**
     *
     * @param sorter sorter "relevance" | "performance" | "clicks" - chooses what statistic you want to sort by.
     * @param type "news" | "artikel" | "blog" | "podcast" | "whitepaper" | "ratgeber" | "video"
     * @return a JSON String of the Top Posts (as many as Limit) with a post-type being type and sorted by sorter.
     */
    public String getTopWithType(@RequestParam String sorter, String type, int limit) throws JSONException, ParseException {
        List<PostStats> top = null;
        String errorString = "";

        String jsonString = null;
        JSONArray array = new JSONArray();

        switch(type) {
            case "news", "artikel", "whitepaper", "blog" -> {
                top = statsRepo.findAllByArtIdIn(postTypeRepo.getPostsByTypeLong(type));
            }
            case "podcast", "ratgeber" -> {
                if(type.equalsIgnoreCase("podcast")) {
                    top = statsRepo.findAllByArtIdIn(postTypeRepo.getPostsByTypeLong("podcast"));
                } else {
                    top = statsRepo.findAllByArtIdIn(postTypeRepo.getPostsByTypeLong("cyber-risk-check"));
                }
            }
        }

        if(top != null) {
            switch (sorter) {
                case "relevance" -> {
                    top.sort((o1, o2) -> Float.compare(o2.getRelevance(), o1.getRelevance()));
                }
                case "performance" -> {
                    top.sort((o1, o2) -> Float.compare(o2.getPerformance(), o1.getPerformance()));
                }
                case "clicks" -> {
                    top.sort((o1, o2) -> (int) (o2.getClicks() - o1.getClicks()));
                }
            }

            top = top.stream().limit(limit).toList();

            for (PostStats stats : top) {
                JSONObject obj = new JSONObject(PostStatsByIdForFrontend(stats.getArtId()));
                array.put(obj);
            }

            jsonString = array.toString();
        }
        return jsonString != null? jsonString : errorString;
    }

    /**
     *
     * @param id the id of the post you want the type of.
     * @return the type of Post "news" | "artikel" | "blog" | "podcast" | "whitepaper" | "ratgeber" | "video"
     */
    public String getType(@RequestParam long id) {
        if(postRepo.findById(id).isEmpty()) {return "errorPostNotFound";}


        if(postRepo.findById(id).isPresent() && postRepo.findById(id).get().getType().equals("post")) {

            if (postTypeRepo.getType((int) id) != null) {
                if (!postTypeRepo.getType((int) id).contains("cyber-risk")) {
                    return postTypeRepo.getType((int) id);
                } else {
                    return "ratgeber";
                }
            }


            if (postTypeRepo.getType((int) id) != null) {
                if (!postTypeRepo.getType((int) id).contains("podcast")) {
                    return postTypeRepo.getType((int) id);
                } else {
                    return "podcast";
                }
            }

            if (postTypeRepo.getType((int) id) != null) {
                if (!postTypeRepo.getType((int) id).contains("blog")) {
                    return postTypeRepo.getType((int) id);
                } else {
                    return "blog";
                }
            }

            Post post = postRepo.findById(id).get();
            String type = "default";
            List<Long> tagIDs = null;
            if (termRelRepo.existsByObjectId(post.getId())) {
                tagIDs = termRelRepo.getTaxIdByObject(post.getId());
            }
            List<WPTerm> terms = new ArrayList<>();
            if (tagIDs != null) {
                for (long l : tagIDs) {
                    if (termRepo.existsById(l)) {
                        if (termRepo.findById(l).isPresent()) {
                            terms.add(termRepo.findById(l).get());
                        }
                    }
                }
            }
            terms.sort((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()));

            for (WPTerm t : terms) {
                if (taxTermRepo.existsById(t.getId())) {
                    if (taxTermRepo.findById(t.getId()).isPresent()) {
                        WpTermTaxonomy tt = taxTermRepo.findById(t.getId()).get();
                        if (tt.getTaxonomy().equalsIgnoreCase("category") && tt.getParent() == 0) {
                            if (termRepo.findById(tt.getTermId()).isPresent() && tt.getTermId() != 1 && tt.getTermId() != 552) {
                                type = termRepo.findById(tt.getTermId()).get().getSlug();
                            }
                        }
                    }
                }
            }

            if(type.equals("blogeintrag")) {
                return "blog";
            }

            if(type.contains("cyber-risk-check")) {
                return "ratgeber";
            }


            return type;

        } else if(postRepo.findById(id).isPresent() && postRepo.findById(id).get().getType().equals("event")){
            String type = "Event: ";
            //noinspection OptionalGetWithoutIsPresent
            switch(eventsService.getEventType(eventsRepo.findByPostID(id).get())) {
                case "o", "r" ->  type += "Sonstige";
                case "k" -> type += "Kongress";
                case "m" -> type += "Messe";
                case "s" -> type += "Schulung/Seminar";
                case "w" -> type += "Workshop";
            }
            return type;
        } else if(postRepo.findById(id).isPresent() && postRepo.findById(id).get().getType().equalsIgnoreCase("video")) {
            return "video";
        } else if(postRepo.findById(id).isPresent() && postRepo.findById(id).get().getType().equalsIgnoreCase("podcast")) {
            return "podcast";
        }


        return "error";
    }

    /**
     * Fetches stats for all posts present in the String representation of the given list.
     * @param list a list of postIds, split with a '-'.
     * @return see PostStatsByIdForFrontend.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getStatsForPostsArray(String list) throws JSONException, ParseException {
        String[] postIds = list.split("-");
        JSONArray json = new JSONArray();
        for(String id : postIds) {
            json.put(new JSONObject(PostStatsByIdForFrontend(Integer.parseInt(id))));
        }
        return json.toString();
    }

    /**
     * Fetches all tag-ids this post has.
     * @param postId the post to fetch for.
     * @return a List of tag-ids.
     */
    private List<Long> getTagsForPost(long postId) {
        List<Long> termTaxonomyIds = termRelRepo.getTaxIdByObject(postId);
        return taxTermRepo.getTermIdByTaxId(termTaxonomyIds);
    }

    /**
     * Fetches similar posts.
     * @param postId the post to fetch from.
     * @param similarityPercentage how similar the other posts have to be.
     * @return ??
     */
    public Map<Long, Float> getSimilarPosts(long postId, float similarityPercentage) {
        // Retrieve tags for the given post
        List<Long> tagIdsForPostGiven = getTagsForPost(postId);

        Map<Long, Float> postAndSimilarityMap = new HashMap<>();
        List<wp_term_relationships> allPostsRelationships = termRelRepo.findAll();

        for (wp_term_relationships otherPostRel : allPostsRelationships) {
            Long otherPostId = otherPostRel.getObjectId();
            if (otherPostId.equals(postId)) continue;  // Ignore the given post

            List<Long> tagIdsForOtherPost = getTagsForPost(otherPostId);
            float currentSimilarityPercentage = calculateTagSimilarity(tagIdsForPostGiven, tagIdsForOtherPost);

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }
        return postAndSimilarityMap;
    }

    /**
     * Calculate the similarity two posts have to each other in tags.
     * @param tagsOfPostOne the tags of a post.
     * @param tagsOfPostTwo the tags of a post.
     * @return the percentage of their similarity.
     */
    private float calculateTagSimilarity(List<Long> tagsOfPostOne, List<Long> tagsOfPostTwo) {
        int commonTagsCount = (int) tagsOfPostOne.stream().filter(tagsOfPostTwo::contains).count();
        return (commonTagsCount * 1.0f / tagsOfPostOne.size()) * 100;
    }

    /**
     * Calculates and returns the average click counts for each post category,
     * sorted by average clicks in descending order.
     *
     * @return a JSON string representing the average click counts for each category
     */
    public String getAverageClicksOfCategoriesRanked() throws JSONException {
        JSONObject result = new JSONObject();
        for(String type : Constants.getInstance().getListOfPostTypesNoEvents()) {
            switch(type) {
                case "blog" -> result.put("Blogs", postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) == null ? 0 : postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) / postTypeRepo.getPostsByTypeLong(type).size());
                case "podcast" -> result.put("Podcasts", postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) == null ? 0 : postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) / postTypeRepo.getPostsByTypeLong(type).size());
                default -> result.put(type, postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) == null ? 0 : postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) / postTypeRepo.getPostsByTypeLong(type).size());
            }
        }

        return result.toString();
    }

    /**
     * Endpoint for retrieval of the top 5 posts sorted by a specific sorter.
     * @param sorter "relevance" or "performance".
     * @return a JSON String containing the stats, identifiers, etc. of the top5 posts compared by given metric.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getTop5(String sorter) throws JSONException, ParseException {
        List<PostStats> top = null;
        String errorString = "";
        if(sorter.equalsIgnoreCase("relevance")) {
            top = statsRepo.getTop5Relevance();
        }
        if(sorter.equalsIgnoreCase("performance")) {
            top = statsRepo.getTop5Performance();
        }
        String jsonString;
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
    public void updateLetterCount(int lettercount, long id) {
        statsRepo.updateLetterCount(lettercount, id, LocalDateTime.now().getYear());
    }

    /**
     * Endpoint for retrieval of a post's creation date.
     * @param id the id of the post you want the creation date for.
     * @return the Creation-Date of the post as a String.
     */
    public String getDate(long id) {
        return postRepo.getDateById(id).toString();
    }

    /**
     * Endpoint for retrieval of all Dates (value) of Posts-creation by postId (name) in a JSON String.
     * @return JSON String of: int PostId, String Date for all Posts.
     */
    public String getAllDates() {
        Map<Integer, String> answer = new HashMap<>();
        for (Post post : postRepo.findAll()) {
            answer.put(post.getId().intValue(), postRepo.getDateById(post.getId()).toString());
        }
        return new JSONObject(answer).toString();
    }

    /**
     * Endpoint for retrieval of Relevance for a specific Post identified by their ID.
     * @param id the id of the Post you want to get relevance stat for.
     * @return float of the posts relevance stat.
     */
    public float getRelevanceById(long id) {
        return statRepository.getRelevanceById(id);
    }

    /**
     * Endpoint for retrieval of posts with their relevance.
     * @return a JSON String containing: int postId (name) and float relevance (value).
     */
    public String getAllRelevance() {
        Map<Integer, Float> answer = new HashMap<>();
        for(Post post : postRepo.findAllUserPosts()){
            if(statRepository.existsByArtId(post.getId())) {
                answer.put(post.getId().intValue(), statRepository.getRelevanceById(post.getId()));
            }
        }

        return new JSONObject(answer).toString();
    }

    /**
     * Fetches size amount of Post-Stats from only those that MATCH the filter and include the search in their title.
     * @param sortBy what criteria the result should be listed in.
     * @param filter the type of post to look for.
     * @param search what titles to look for.
     * @return a JSON-String containing several PostStatsByIdForFrontend entries.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String pageTitleFinder(Integer page, Integer size, String sortBy, String filter, String search) throws JSONException, ParseException {
        List<Post> list;
        if(!filter.isBlank()) {
            list = postRepo.pageByTitleWithTypeQueryWithFilter(search, "publish", "post", filter, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy)));
        } else {
            list = postRepo.pageByTitleWithTypeQuery(search, "publish", "post", PageRequest.of(page, size, Sort.by(Sort.Direction.DESC , sortBy)));
        }
        List<JSONObject> stats = new ArrayList<>();
        for(Post post : list) {
            long id = post.getId();
            stats.add(new JSONObject(PostStatsByIdForFrontend(id)));
        }
        return new JSONArray(stats).toString();
    }

    /**
     * Fetches a page of events with their respective stat-JSON-strings.
     * @param page the page number.
     * @param size the size of the page.
     * @param filter the filter to apply (Event-Type).
     * @param search searches in titles.
     * @return stat-JSON-strings.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getEventsWithStats(Integer page, Integer size,  String filter, String search) throws JSONException, ParseException {
        List<Post> list;
        if(filter.isBlank()) {
            list = postRepo.getAllEventsWithSearch(search, PageRequest.of(page, size));
        } else {
            list = postRepo.getAllEventsWithTypeAndSearch(eventsService.getTermIdFromFrontendType(filter), search, PageRequest.of(page, size));
        }
        List<JSONObject> stats = new ArrayList<>();

        for(Post p : list) {
            long id = p.getId();
            stats.add(new JSONObject(PostStatsByIdForFrontend(id)));
        }

        return new JSONArray(stats).toString();
    }

    /**
     * Endpoint for retrieval of ALL Posts that are not Original Content (User Posts (Blog, Article, Whitepaper), News)
     * @return a JSON String containing all stats, identifiers, type and more for all posts.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getAll() throws JSONException, ParseException {

        List<JSONObject> stats = new ArrayList<>();


        for(Integer postId : postTypeRepo.getPostsByType("blog")) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(postId));
            stats.add(json);
        }
        for(Integer postId : postTypeRepo.getPostsByType("news")) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(postId));
            stats.add(json);
        }
        for(Integer postId : postTypeRepo.getPostsByType("artikel")) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(postId));
            stats.add(json);
        }
        for(Integer postId : postTypeRepo.getPostsByType("whitepaper")) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(postId));
            stats.add(json);
        }

        stats.sort((o1, o2) -> {
            try {
                return o2.getInt("id") - o1.getInt("id");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return new JSONArray(stats).toString();
    }

    /**
     * Fetches Stats for all posts of the given type. Values include all getType() can return.
     * @param type the type of post to list.
     * @return a JSON-String containing a List of Post-Stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getAllByTypeWithStats(String type) throws JSONException, ParseException {
        List<JSONObject> stats = new ArrayList<>();

        for(Integer postId : postTypeRepo.getPostsByType(type)) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(postId));
            stats.add(json);
        }
        return new JSONArray(stats).toString();
    }

    /**
     * Fetches Stats for all posts of the given type. Values include all getType() can return.
     * @param type the type of post to list.
     * @param page the page of results.
     * @param size the number of results per page.
     * @return a JSON-String containing a List of Post-Stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getAllByTypeWithStats(String type, int page, int size) throws JSONException, ParseException {
        List<JSONObject> stats = new ArrayList<>();

        for(Integer postId : postTypeRepo.getPostsByTypePageable(type, PageRequest.of(page, size))) {
            JSONObject json = new JSONObject(PostStatsByIdForFrontend(postId));
            stats.add(json);
        }
        return new JSONArray(stats).toString();
    }

    /**
     * Fetches stats-rows from table for posts of a specific term.
     * @param termId the term to fetch for.
     * @return a List of PostStats
     */
    public List<PostStats> getPostStatsByTermId(Long termId){
        List<Long> postIds = termRelRepo.findByTermTaxonomyId(termId)
                .stream()
                .map(wp_term_relationships::getObjectId)
                .collect(Collectors.toList());

        return statsRepo.findAllByArtIdIn(postIds);
    }

    /**
     * Retrieves a list of {@code PostStats} objects that are considered outliers based on their views (clicks)
     * for a given term ID. Outliers are determined by using the {@code MathHelper.getOutliersLong} method.
     *
     * @param termId The term ID used to filter the post's statistics. This is typically an identifier
     *               for a specific category or tag in a blog or article system.
     * @return A JSON string representing a list of {@code PostStats} objects that are outliers.
     *         In case of an exception during JSON processing, a simple error message is returned.
     *         If no outliers are found, an empty JSON array is returned.
     * @implNote This method relies on {@code getPostStatsByTermId} to fetch the relevant post statistics
     *           and {@code MathHelper.getOutliersLong} to determine outliers based on views.
     *           It uses Jackson's {@code ObjectMapper} to convert the list of {@code PostStats} to JSON.
     * @apiNote The term ID must be a valid identifier existing in the database. The method does not
     *          handle cases where the term ID does not exist or is null.
     *            This is a placeholder for more specific exception handling based on the application's requirements.
     */
    public String getOutliersByViewsAndTags(@RequestParam Long termId)  {
        List<PostStats> postStats = getPostStatsByTermId(termId);

        List<Long> views = postStats.stream()
                .map(PostStats::getClicks)
                .collect(Collectors.toList());
        List<Long> outliers = MathHelper.getOutliersLong(views);

        List<PostStats> filteredPostStats = postStats.stream()
                .filter(postStat -> outliers.contains(postStat.getClicks()))
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(filteredPostStats);}
        catch (Exception e){return "Computer sagt nein";}
    }

    /**
     * Retrieves outliers based on views or relevance for posts associated with a given term ID.
     * This method filters posts statistics by views or relevance and identifies outliers in those metrics.
     * It then fetches the corresponding post names based on the outliers and returns them along with the outlier values.
     *
     * @param termId The term ID used to find related post statistics. It refers to the ID of the term (e.g., a tag) in a blog or content system.
     * @param type   The type of metric to consider for finding outliers. It can be either "views" or "relevance".
     *               If "views" is specified, the method looks for outliers in post's views (clicks).
     *               If "relevance" is specified, the method looks for outliers in the relevance score of the posts.
     * @return       A JSON string representing a list of maps, each map containing the post's name (title) and its corresponding outlier value (either views or relevance).
     *               Returns a simple error message if any exception occurs during JSON processing.
     * @implNote This method uses {@link MathHelper#getOutliersLong(List)} or {@link MathHelper#getOutliersFloat(List)} (based on the 'type' parameter)
     *           to determine outliers and then fetches the corresponding post names using the {@code PostRepository}.
     *           It uses Jackson's {@code ObjectMapper} to convert the list of results to a JSON string.
     */
    public String getOutliersByViewsOrRelevanceAndTags(@RequestParam Long termId, @RequestParam String type) {
        List<PostStats> postStats = getPostStatsByTermId(termId);
        List<Map<String, Object>> result = new ArrayList<>();

        if ("views".equals(type)) {
            List<Long> views = postStats.stream()
                    .map(PostStats::getClicks)
                    .collect(Collectors.toList());
            List<Long> outliers = MathHelper.getOutliersLong(views);

            result = postStats.stream()
                    .filter(postStat -> outliers.contains(postStat.getClicks()))
                    .map(postStat -> {
                        Map<String, Object> map = new HashMap<>();
                        //noinspection OptionalGetWithoutIsPresent
                        map.put("postName", postRepo.findById(postStat.getArtId()).get().getTitle());
                        map.put("views", postStat.getClicks());
                        return map;
                    })
                    .collect(Collectors.toList());
        } else if ("relevance".equals(type)) {
            List<Float> relevances = postStats.stream()
                    .map(PostStats::getRelevance)
                    .collect(Collectors.toList());
            List<Float> outliers = MathHelper.getOutliersFloat(relevances);

            result = postStats.stream()
                    .filter(postStat -> outliers.contains(postStat.getRelevance()))
                    .map(postStat -> {
                        Map<String, Object> map = new HashMap<>();
                        //noinspection OptionalGetWithoutIsPresent
                        map.put("postName", postRepo.findById(postStat.getArtId()).get().getTitle());
                        map.put("relevanz", postStat.getRelevance());
                        return map;
                    })
                    .collect(Collectors.toList());
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(result);
        } catch (Exception e) {
            return "computadora dice que no";
        }
    }

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
            int commonTagsCount = (int) tagIdsForPostGiven.stream().filter(tagIdsForOtherPost::contains).count();
            float currentSimilarityPercentage = (commonTagsCount * 1.0f / tagIdsForPostGiven.size()) * 100;

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }

        int currentYear = LocalDate.now().getYear();

        for (Map.Entry<Long, Float> entry : postAndSimilarityMap.entrySet()) {
            JSONObject obj = new JSONObject();
            Long otherPostId = entry.getKey();
            PostStats postStat = statsRepo.findByArtIdAndYear(otherPostId, currentYear);

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
            int commonTagsCount = (int) tagIdsForPostGiven.stream().filter(tagIdsForOtherPost::contains).count();
            float currentSimilarityPercentage = (commonTagsCount * 1.0f / tagIdsForPostGiven.size()) * 100;

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }

        // 3. Ähnliche Beiträge basierend auf ihren Gesamt-Klicks hinzufügen
        for (Map.Entry<Long, Float> entry : postAndSimilarityMap.entrySet()) {
            JSONObject obj = new JSONObject();
            Long otherPostId = entry.getKey();
            PostStats postStat = statsRepo.findByArtIdAndYear(otherPostId, LocalDate.now().getYear());

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
    public String getSimilarPostByClicksInRange(@RequestParam long postId, @RequestParam float similarityPercentage, @RequestParam String startDate, @RequestParam String endDate) throws JSONException {

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
            int commonTagsCount = (int) tagIdsForPostGiven.stream().filter(tagIdsForOtherPost::contains).count();
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
            PostStats postStat = statsRepo.findByArtIdAndYear(otherPostId, LocalDate.now().getYear());
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
    public String getSimilarPostsAndOutliers(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {
        // 1. Das Ergebnis der getSimilarPostByTagSimilarity Methode abrufen
        JSONArray originalPostsArray = new JSONArray(getSimilarPostByTagSimilarity(postId, similarityPercentage));

        // 2. Eine Liste der Relevanzwerte aus dem JSONArray extrahieren
        List<Float> relevanceList = new ArrayList<>();
        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            relevanceList.add((float) obj.getDouble("relevance"));
        }

        // 3. Die Ausreißer basierend auf den Relevanzwerten mithilfe der getOutliersFloat Methode ermitteln
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
    public String getSimilarPostsAndOutliersByClicks(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {
        // 1. Das Ergebnis der getSimilarPostByTagAndClicks Methode abrufen
        JSONArray originalPostsArray = new JSONArray(getSimilarPostByTagAndClicks(postId, similarityPercentage));

        // 2. Eine Liste der Gesamt-Klickwerte aus dem JSONArray extrahieren
        List<Float> clicksList = new ArrayList<>();
        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            clicksList.add((float) obj.getDouble("totalClicks"));
        }

        // 3. Die Ausreißer basierend auf den Gesamt-Klickwerten mithilfe der getOutliersFloat Methode ermitteln
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
    public String getSimilarPostsAndOutliersByClicksInRange(@RequestParam long postId,@RequestParam float similarityPercentage, @RequestParam String startDate, @RequestParam String endDate) throws JSONException {

        // 1. Das Ergebnis der getSimilarPostByClicksInRange Methode abrufen
        JSONArray originalPostsArray = new JSONArray(getSimilarPostByClicksInRange(postId, similarityPercentage, startDate, endDate));

        // 2. Eine Liste der Klicks aus dem JSONArray extrahieren
        List<Long> clicksList = new ArrayList<>();
        for (int i = 0; i < originalPostsArray.length(); i++) {
            JSONObject obj = originalPostsArray.getJSONObject(i);
            clicksList.add(obj.getLong("clicks"));
        }

        // 3. Die Ausreißer basierend auf den Klicks mithilfe einer geeigneten Methode ermitteln.
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

    /**
     * Deletes a specific posts typing in PostTypes table.
     * @param id the id of the post-types row to delete.
     */
    public void deletePostTypesById(int id) {
        if(postTypeRepo.findById((long) id).isPresent()) {
            postTypeRepo.delete(postTypeRepo.findById((long) id).get());
        }
    }


    /**
     * Remaps one of our type-names to the corresponding website definition.
     * @param uncoolType our type.
     * @return website type.
     */
    private String remapTypeToWebsiteStandard(String uncoolType) {
        if(uncoolType.equals("blog")) {
            return "blogeintrag";
        }
        return uncoolType;
    }

    /**
     * Retrieves the total accumulated impressions for a specified post across all time.
     *
     * @param postId the ID of the post for which to retrieve impressions
     * @return a JSON string representing the total accumulated impressions for the post
     */
    public String getAccumulatedPostImpressionsAllTime(@RequestParam Long postId){
        return soziImp.getImpressionsAccumulatedAllTimeByPostId(postId);
    }

    /**
     * Retrieves the impression details of the post that has the highest number of impressions of all time.
     *
     * @return a JSON string representing the post with the best impression record
     */
    public String getBestPostImpressionAllTime(){
        List<SocialsImpressions>imps = soziImp.filterOutUserImpressions(soziImp.findAll());
        return soziImp.impToJSON(soziImp.getMostImpressionsFromList(imps));
    }

    /**
     * Retrieves the impression details of the post that has the highest number of impressions for the current day.
     *
     * @return a JSON string representing the post with the best impression record today
     */
    public String getBestPostImpressionToday(){
        List<SocialsImpressions>imps = soziImp.filterOutUserImpressions(soziImp.findAllToday());
        return soziImp.impToJSON(soziImp.getMostImpressionsFromList(imps));
    }

    /**
     * Fetches the given posts-views by time, lined with dates and clicks.
     * @param id the post to fetch for.
     * @return a JSON-Object with date-labels and click values.
     * @throws JSONException .
     */
    public String getPostViewsByTime(long id) throws JSONException {
        JSONArray dates = new JSONArray();
        JSONArray views = new JSONArray();

        LocalDate now = LocalDate.now();
        java.sql.Date oldest = java.sql.Date.valueOf(postRepo.getPostDateById(id).toLocalDate());

        for(LocalDate date : oldest.toLocalDate().datesUntil(now.plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniRepo.findByDatum(java.sql.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniRepo.findByDatum(java.sql.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }
            if(uniId != 0) {
                //noinspection OptionalGetWithoutIsPresent
                dates.put(uniRepo.findById(Math.toIntExact(uniId)).get().getDatum().toString().substring(0, 10));
                if(postClicksByHourRepo.getSumForDayForPost(uniId, id).isPresent()) {
                    views.put(postClicksByHourRepo.getSumForDayForPost(uniId, id).get());
                } else {
                    views.put(0);
                }
            }

        }

        return new JSONObject().put("dates", dates).put("views", views).toString();
    }

    public String postPage(Integer page, Integer size, String filter, String search, String sorter, String dir) throws JSONException, ParseException {

        List<JSONObject> stats = new ArrayList<>();
        for(Post post : fetchPostPageByCriteria(page, size, filter, search, sorter, dir)) {
            long id = post.getId();
            stats.add(new JSONObject(PostStatsByIdForFrontend(id)));
        }
        return new JSONArray(stats).toString();
    }

    private List<Post> fetchPostPageByCriteria(Integer page, Integer size, String filter, String search, String sorter, String dir) {
        List<Post> list;

        if(search == null) {
            search = "";
        }
        if(dir == null) dir = "";

        PageRequest request = PageRequest.of(page, size);

        if(dir.equals("ASC")) {
            if (sorter.isBlank()) {
                if (!filter.isBlank()) {
                    list = postRepo.pageByTitleWithTypeQueryWithFilterIdASC(search, "publish", "post", filter, request);
                } else {
                    list = postRepo.pageByTitleWithTypeQueryByIdASC(search, "publish", "post", request);
                }
            } else {
                if (!filter.isBlank()) {
                    if (sorter.equals("clicks")) {
                        list = postRepo.postPageByClicksASC(search, "publish", "post", filter, request);
                    } else {
                        list = postRepo.postPageByCreationByIdASC(search, "publish", "post", filter, request);
                    }
                } else {
                    if (sorter.equals("clicks")) {
                        list = postRepo.postPageByClicksASC(search, "publish", "post", request);
                    } else {
                        list = postRepo.postPageByCreationByIdASC(search, "publish", "post", request);
                    }
                }
            }
        } else {
            if (sorter.isBlank()) {
                if (!filter.isBlank()) {
                    list = postRepo.pageByTitleWithTypeQueryWithFilterIdDESC(search, "publish", "post", filter, request);
                } else {
                    list = postRepo.pageByTitleWithTypeQueryByIdDESC(search, "publish", "post", request);
                }
            } else {
                if (!filter.isBlank()) {
                    if (sorter.equals("clicks")) {
                        list = postRepo.postPageByClicksDESC(search, "publish", "post", filter, request);
                    } else {
                        list = postRepo.postPageByCreationByIdDESC(search, "publish", "post", filter, request);
                    }
                } else {
                    if (sorter.equals("clicks")) {
                        list = postRepo.postPageByClicksDESC(search, "publish", "post", request);
                    } else {
                        list = postRepo.postPageByCreationByIdDESC(search, "publish", "post", request);
                    }
                }
            }
        }
        return list;
    }

    public List<String> getSuggestions(String search, String filter) {
        if(filter == null || filter.isBlank()) {
            return postRepo.titlesOfPosts(postRepo.getSuggestions(search));
        } else {
            return postRepo.titlesOfPosts(postRepo.getSuggestions(search, filter));
        }
    }

}
