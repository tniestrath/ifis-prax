package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.ContentDownloadsHourlyService;
import com.analysetool.services.PostClicksByHourDLCService;
import com.analysetool.services.PostService;
import com.analysetool.services.SocialsImpressionsService;
import com.analysetool.util.Constants;
import com.analysetool.util.MathHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/posts", "/0wB4P2mly-xaRmeeDOj0_g/posts"}, method = RequestMethod.GET, produces = "application/json")
public class PostController {

    @Autowired
    PostService postService;
    @Autowired
    PostClicksByHourDLCService postClicksService;


    /**
     * Fetches all Posts that have been published.
     * @return an unordered List of Posts.
     */
    @GetMapping("/publishedPosts")
    public List<Post> getPublishedPosts(){return postService.getPublishedPosts();}

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
    @GetMapping("/getPostsByAuthor")
    public String postsByAuthorPageable(long authorId, int page, int size, String filter, String search) throws JSONException, ParseException {return postService.postsByAuthorPageable(authorId, page, size, filter, search);}

    /**
     * Fetches the latest post of the specified author.
     * @param id the user_id to fetch for.
     * @return a JSON-String containing Data from PostStatsByIdForFrontend.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getNewestPostWithStatsByAuthor")
    public String getNewestPostWithStatsByAuthor(@RequestParam Long id) throws JSONException, ParseException {return postService.getNewestPostWithStatsByAuthor(id);}

    /**
     * Calculates the times a post has been interacted with, counting only specified days.
     * @param postId the post_id to calc for.
     * @param daysback the amount of days in the past (until now) that should be used to calculate.
     * @return a JSON-String.
     */
    @GetMapping("/postClicksDistributedByHours")
    public String getPostClicksOfLast24HourByPostIdAndDaysBackDistributedByHour(Long postId, Integer daysback){return postClicksService.getPostClicksOfLast24HourByPostIdAndDaysBackDistributedByHour(postId,daysback).toString();}

    /**
     * Fetches number of views on posts made by the specified user.
     * @param id the id of the user to fetch for.
     * @return a positive long.
     */
    @GetMapping("/getViewsOfUser")
    public long getPostViewsOfUserById(@RequestParam Long id){return postService.getPostViewsOfUserById(id);}

    /**
     * Fetches the number of posts made by the specified user.
     * @param id the id of the user to fetch for.
     * @return a positive long.
     */
    @GetMapping("/getPostCountOfUser")
    public long getPostCountOfUserById(@RequestParam Long id){return postService.getPostCountOfUserById(id);}

    /**
     * Endpoint for retrieval of a single posts full-statistics, identified by its id.
     * @param id the id of the post you want to fetch stats for.
     * @return a JSON String containing keys and values for each of a post's statistics, identifiers and adjacent information such as its type. <br>
     * Keys are: id, title, date, tags, type, performance, relevance, clicks, lettercount, searchSuccesses (optional),
     * SearchSuccessRate (optional), referrings, lettercount, articleReferringRate, downloads, authors.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getPostStatsByIdWithAuthor")
    public String PostStatsByIdForFrontend(@RequestParam long id) throws JSONException, ParseException {return postService.PostStatsByIdForFrontend(id);}

    /**
     * Further enriched data from PostStatsByIdForFrontend, not to be used in bulk.
     * @param id the post_id of the post to fetch data for.
     * @return a JSON-String (JSONObject). <br>
     * Adds keys "content" and "img" to PostStatsByIdForFrontend.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getPostStatsWithContent")
    public String getPostStatsWithContent(long id) throws JSONException, ParseException {return postService.getPostStatsWithContent(id);}

    /**
     * Fetches the latest published post.
     * @return JSON-String (see getPostStatsWithContent)
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getNewestPost")
    public String getNewestPost() throws JSONException, ParseException {return postService.getNewestPost();}

    /**
     * Fetches the number of clicks the best post of the given tag has.
     * @param tagId the tag to fetch for.
     * @return numeric value.
     */
    @GetMapping("/getBestPostByTagClicks")
    public int getBestPostClicks(long tagId) {return postService.getBestPostClicks(tagId);}

    /**
     * Fetches the id of the best post for the given tag.
     * @param tagId the tag to fetch for.
     * @return the post_id of the "best" post in that tag.
     */
    @GetMapping("/getBestPostByTag")
    public Long getBestPostByTag(long tagId) {return postService.getBestPostByTag(tagId);}

    /**
     * Fetches the average clicks of posts in the given tag.
     * @param tagId the tag to fetch for.
     * @return the average clicks on a post of this kind.
     */
    @GetMapping("/getAverageClicksByTag")
    public double getAverageClicksByTag(long tagId) {return postService.getAverageClicksByTag(tagId);}

    /**
     * Fetches all Posts with the given tag.
     * @param tagId the tag to fetch for.
     * @return a List of Posts.
     */
    @GetMapping("/getPostsByTag")
    public List<Post> getPostsByTag(long tagId) {return postService.getPostsByTag(tagId);}


    /**
     * Fetches the highest value in the performance column of stats.
     * @return positive float.
     */
    @GetMapping("/maxPerformance")
    public float getMaxPerformance(){return postService.getMaxPerformance();}

    /**
     * Fetches the highest value in the relevance column of stats.
     * @return positive float.
     */
    @GetMapping("/maxRelevance")
    public float getMaxRelevance(){return postService.getMaxRelevance();}

    /**
     * Fetches performance for a specific Post.
     * @param id the id of the post to fetch for.
     * @return positive float.
     */
    @GetMapping("/getPerformanceByArtId")
    public float getPerformanceByArtId(@RequestParam int id){return postService.getPerformanceByArtId(id);}

    /**
     * Fetches the name, id and type for a post by the given author - that has the highest value in type
     * @param id the id of the user to fetch for.
     * @param type the type to use as value ("relevance" | "performance").
     * @return a JSON-String containing keys id, the given value of (type) and title.
     * @throws JSONException
     */
    @GetMapping("/bestPost")
    public String getBestPost(@RequestParam Long id, @RequestParam String type) throws JSONException {return postService.getBestPost(id, type);}

    @GetMapping("/getViewsOfPostDistributedByHours")
    public Map<String,Long>getViewsDistributedByHour(@RequestParam Long PostId){return postService.getViewsDistributedByHour(PostId);}

    /**
     * Fetches stats for all posts present in the String representation of the given list.
     * @param list a list of postIds, split with a '-'.
     * @return see PostStatsByIdForFrontend.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getPostStatsForList")
    public String getStatsForPostsArray(String list) throws JSONException, ParseException {return postService.getStatsForPostsArray(list);}

    /**
     * Endpoint for retrieval for the number of total posts on the website.
     * @return count of all user posts.
     */
    @GetMapping("/getCountTotalPosts")
    public int getCountTotalPosts() {return postService.getCountTotalPosts();}

    /**
     *  Endpoint for retrieval for the number of posts on the website of a certain type.
     * @param type ("news" | "artikel" | "blog" | "whitepaper")
     * @return count of all posts with the type given.
     */
    @GetMapping("/getCountPostByType")
    public int getCountPostByType(String type) {return postService.getCountPostByType(type);}


    @GetMapping("/getPostViewsByTime")
    public String getPostViewsByTime(long id) throws JSONException {return postService.getPostViewsByTime(id);}

    /**
     *
     * @param sorter sorter "relevance" | "performance" | "clicks" - chooses what statistic you want to sort by.
     * @param type "news" | "artikel" | "blog" | "podcast" | "whitepaper" | "ratgeber"
     * @return a JSON String of the Top Posts (as many as Limit) with a post-type being type and sorted by sorter.
     */
    @GetMapping("/getTopWithType")
    public String getTopWithType(@RequestParam String sorter, String type, int limit) throws JSONException, ParseException {return postService.getTopWithType(sorter, type, limit);}

    /**
     * Endpoint for retrieval of the top 5 posts sorted by a specific sorter.
     * @param sorter "relevance" or "performance".
     * @return a JSON String containing the stats, identifiers, etc. of the top5 posts compared by given metric.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getTop5")
    public String getTop5(String sorter) throws JSONException, ParseException {return postService.getTop5(sorter);}

    /**
     * debug call to manually set the lettercount of a post.
     * @param lettercount the lettercount you want to set.
     * @param id the postId of the post you want to set lettercount for.
     */
    @GetMapping("/testLetterCount")
    public void updateLetterCount(int lettercount, long id) {postService.updateLetterCount(lettercount, id);}

    /**
     * Endpoint for retrieval of a post's creation date.
     * @param id the id of the post you want the creation date for.
     * @return the Creation-Date of the post as a String.
     */
    @GetMapping("/getDate")
    public String getDate(long id) {return postService.getDate(id);}

    /**
     * Endpoint for retrieval of all Dates (value) of Posts-creation by postId (name) in a JSON String.
     * @return JSON String of: int PostId, String Date for all Posts.
     */
    @GetMapping("/getAllDates")
    public String getAllDates() {return postService.getAllDates();}

    /**
     * Endpoint for retrieval of Relevance for a specific Post identified by their ID.
     * @param id the id of the Post you want to get relevance stat for.
     * @return float of the posts relevance stat.
     */
    @GetMapping("/getRelevanceById")
    public float getRelevanceById(long id) {return postService.getRelevanceById(id);}

    /**
     * Endpoint for retrieval of posts with their relevance.
     * @return a JSON String containing: int postId (name) and float relevance (value).
     */
    @GetMapping("/getAllRelevance")
    public String getAllRelevance() {return postService.getAllRelevance();}

    /**
     * Fetches size amount of Post-Stats from only those that MATCH the filter and include the search in their title.
     * @param sortBy what criteria the result should be listed in.
     * @param filter the type of post to look for.
     * @param search what titles to look for.
     * @return a JSON-String containing several PostStatsByIdForFrontend entries.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/pageByTitle")
    public String pageTitleFinder(Integer page, Integer size, String sortBy, String filter, String search) throws JSONException, ParseException {return postService.pageTitleFinder(page, size, sortBy, filter, search);}

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
    @GetMapping("/getEventsWithStats")
    public String getEventsWithStats(Integer page, Integer size,  String filter, String search) throws JSONException, ParseException {return postService.getEventsWithStats(page, size, filter, search);}


    /**
     * Endpoint for retrieval of ALL Posts that are not Original Content (User Posts (Blog, Article, Whitepaper), News)
     * @return a JSON String containing all stats, identifiers, type and more for all posts.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getAllPostsWithStats")
    public String getAll() throws JSONException, ParseException {return postService.getAll();}

    /**
     * Fetches Stats for all posts of the given type. Values include all getType() can return.
     * @param type the type of post to list.
     * @return a JSON-String containing a List of Post-Stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getAllTypeWithStats")
    public String getAllByTypeWithStats(String type) throws JSONException, ParseException {return postService.getAllByTypeWithStats(type);}

    /**
     * Fetches Stats for all posts of the given type. Values include all getType() can return.
     * @param type the type of post to list.
     * @param page the page of results.
     * @param size the number of results per page.
     * @return a JSON-String containing a List of Post-Stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getAllTypeWithStatsPageable")
    public String getAllByTypeWithStats(String type, int page, int size) throws JSONException, ParseException {return postService.getAllByTypeWithStats(type, page, size);}

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
    @GetMapping("/getOutliersByViewsAndTags")
    public String getOutliersByViewsAndTags(@RequestParam Long termId)  {return postService.getOutliersByViewsAndTags(termId);}

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
    @GetMapping("/getOutliersByViewsOrRelevanceAndTags")
    public String getOutliersByViewsOrRelevanceAndTags(@RequestParam Long termId, @RequestParam String type) {return postService.getOutliersByViewsOrRelevanceAndTags(termId, type);}


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
    public String getSimilarPostByTagSimilarity(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {return postService.getSimilarPostByTagSimilarity(postId, similarityPercentage);}

    /**
     * Endpoint, um ähnliche Beiträge basierend auf Tag-Ähnlichkeit und Gesamt-Klicks abzurufen.
     *
     * @param postId Der ID des Beitrags, für den ähnliche Beiträge gesucht werden sollen.
     * @param similarityPercentage Der Mindestprozentsatz der Übereinstimmung von Tags, um als ähnlich betrachtet zu werden.
     * @return Ein JSON-String, der ähnliche Beiträge basierend auf Tag-Ähnlichkeit und Gesamt-Klicks repräsentiert.
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getSimilarPostByTagAndClicks")
    public String getSimilarPostByTagAndClicks(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {return postService.getSimilarPostByTagAndClicks(postId, similarityPercentage);}


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
    public String getSimilarPostByClicksInRange(@RequestParam long postId, @RequestParam float similarityPercentage, @RequestParam String startDate, @RequestParam String endDate) throws JSONException {return postService.getSimilarPostByClicksInRange(postId, similarityPercentage, startDate, endDate);}


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
    public String getSimilarPostsAndOutliers(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {return postService.getSimilarPostsAndOutliers(postId, similarityPercentage);}

    /**
     * Endpoint, um ähnliche Beiträge und Ausreißer basierend auf Tag-Ähnlichkeit und Gesamt-Klicks abzurufen.
     *
     * @param postId Der ID des Beitrags, für den ähnliche Beiträge und Ausreißer gesucht werden sollen.
     * @param similarityPercentage Der Mindestprozentsatz der Übereinstimmung von Tags, um als ähnlich betrachtet zu werden.
     * @return Ein JSON-String, der ähnliche Beiträge und Ausreißer basierend auf Tag-Ähnlichkeit und Gesamt-Klicks repräsentiert.
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getSimilarPostsAndOutliersByClicks")
    public String getSimilarPostsAndOutliersByClicks(@RequestParam long postId, @RequestParam float similarityPercentage) throws JSONException {return postService.getSimilarPostsAndOutliersByClicks(postId, similarityPercentage);}


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
    public String getSimilarPostsAndOutliersByClicksInRange(@RequestParam long postId,@RequestParam float similarityPercentage, @RequestParam String startDate, @RequestParam String endDate) throws JSONException {return postService.getSimilarPostsAndOutliersByClicksInRange(postId, similarityPercentage, startDate, endDate);}

    @PostMapping("/deletePostTypesById")
    public void deletePostTypesById(int id) {postService.deletePostTypesById(id);}

    /**
     * Retrieves the total accumulated impressions for a specified post across all time.
     *
     * @param postId the ID of the post for which to retrieve impressions
     * @return a JSON string representing the total accumulated impressions for the post
     */
    @GetMapping("/getAccumulatedPostImpressions")
    public String getAccumulatedPostImpressionsAllTime(@RequestParam Long postId){return postService.getAccumulatedPostImpressionsAllTime(postId);}

    /**
     * Retrieves the impression details of the post that has the highest number of impressions of all time.
     *
     * @return a JSON string representing the post with the best impression record
     */
    @GetMapping("/getBestPostImpression")
    public String getBestPostImpressionAllTime(){return postService.getBestPostImpressionAllTime();}

    /**
     * Retrieves the impression details of the post that has the highest number of impressions for the current day.
     *
     * @return a JSON string representing the post with the best impression record today
     */
    @GetMapping("/getBestPostImpressionToday")
    public String getBestPostImpressionToday(){return postService.getBestPostImpressionToday();}

    @GetMapping("/rankPostTypesAllTime")
    public String rankPostTypesAllTime(){
        try{
        return postService.getAverageClicksOfCategoriesRanked();
        } catch(Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}

