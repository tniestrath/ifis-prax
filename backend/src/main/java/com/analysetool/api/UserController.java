package com.analysetool.api;

import com.analysetool.modells.SocialsImpressions;
import com.analysetool.modells.UserStats;
import com.analysetool.services.SocialsImpressionsService;
import com.analysetool.services.UserService;
import com.analysetool.util.DashConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping(value = { "/users", "/0wB4P2mly-xaRmeeDOj0_g/users"}, method = RequestMethod.GET, produces = "application/json")
public class UserController {

    @Autowired
    private SocialsImpressionsService soziImp;
    @Autowired
    private UserService userService;

    private final DashConfig config;

    public UserController(DashConfig config) {
        this.config = config;
    }

    /**
     * Fetch some user-data by id.
     * @param id the users' id.
     * @return a JSON-String containing some userdata.
     * @throws JSONException .
     */
    @GetMapping("/getById")
    public String getUserById(int id) throws JSONException {
        return userService.getUserById(id);
    }

    /**
     * Fetch a users data by their login-name.
     * @param u the login-name to fetch for.
     * @return a JSON-String containing some userdata.
     * @throws JSONException .
     */
    @GetMapping("/getByLogin")
    public String getUserByLogin(@RequestParam String u) throws JSONException {
        return userService.getUserByLogin(u);
    }

    /**
     * Fetch stats for all users that match the given criteria.
     * @param page the page of results.
     * @param size the amount of results to fetch .
     * @param search searches in name and id for matches.
     * @param filterAbo only displays users of the given membership (leave empty for no filter).
     * @param filterTyp only displays users of the given category (leave empty for no filter).
     * @param sorter what to sort users by.
     * @return a JSON-Array of JSON-Objects containing userdata.
     * @throws JSONException .
     */
    @GetMapping("/getAll")
    public String getAll(Integer page, Integer size, String search, String filterAbo, String filterTyp, String sorter) throws JSONException {
        return userService.getAll(page, size, search, filterAbo, filterTyp, sorter);
    }

    /**
     * Fetch stats for all users that match the given criteria.
     * @param page the page of results.
     * @param size the amount of results to fetch .
     * @param search searches in name and id for matches.
     * @param filterAbo only displays users of the given membership (leave empty for no filter).
     * @param filterTyp only displays users of the given category (leave empty for no filter).
     * @param sorter what to sort users by.
     * @return a JSON-Array of JSON-Objects containing userdata.
     * @throws JSONException .
     */
    @GetMapping("/getAllDir")
    public String getAllDir(Integer page, Integer size, String search, String filterAbo, String filterTyp, String sorter, String dir) throws JSONException {
        return userService.getAllDirectionTest(page, size, search, filterAbo, filterTyp, sorter, dir);
    }

    /**
     * Fetch all users with tags associated with their profile.
     * @param page the page of results.
     * @param size the amount of results to fetch .
     * @param search searches in name and id for matches.
     * @param filterAbo only displays users of the given membership (leave empty for no filter).
     * @param filterTyp only displays users of the given category (leave empty for no filter).
     * @param sorter what to sort users by.
     * @return a JSON-Array of JSON-Objects containing userdata.
     * @throws JSONException .
     */
    @GetMapping("/getAllWithTagsTest")
    public String getAllWithTagsTest(Integer page, Integer size, String search, String filterAbo, String filterTyp, String tag, String sorter) throws JSONException {
        return userService.getAllWithTagsTest(page, size, search, filterAbo, filterTyp, tag, sorter);
    }

    /**
     * Generate a stat-update mail for the specified user.
     * @param userId the users' id.
     * @return whether the generation was successful.
     */
    @GetMapping("/generateMailSingle")
    public boolean generateMailSingle(int userId) {
        try {
            userService.generateMailSingle(userId);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch all stats for a single user.
     * @param id the users' id.
     * @return a JSON-Object containing detailed userdata.
     * @throws JSONException .
     */
    @GetMapping("/getAllSingleUser")
    public String getAllSingleUser(long id) throws JSONException {
        return userService.getAllSingleUser(id);
    }

    /**
     * Fetch stats of a user specific for newsletter usage.
     * @param id the users' id.
     * @return a JSON-Object containing userdata.
     */
    @GetMapping("/getAllSingleUserForNewsletter")
    public String getAllSingleUserNewsletter(long id) {
        return userService.getAllSingleUserNewsletter(id);
    }

    /**
     * Fetch a users profile picture.
     * @param id the users' id.
     * @return the users profile-picture as bytes.
     */
    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) {
        return userService.getProfilePic(id);
    }

    /**
     * Fetch data for the user-clicks chart.
     * @param id the users' id.
     * @param start the start of the time frame to show.
     * @param end the end of the time frame to show.
     * @return a JSON-String containing user-clicks-chart data.
     * @throws JSONException .
     */
    @GetMapping("/getUserClicksChartData")
    public String getUserClicksChartData(long id, String start, String end) throws JSONException {
        return userService.getUserClicksChartData(id, start, end);
    }

    /**
     * Fetches the total amount of events the user has registered.
     * @param id the users' id.
     * @return the amount of events as a String.
     * @throws JSONException .
     */
    @GetMapping("/getAmountOfEvents")
    public String getCountEvents(long id) throws JSONException {
        return userService.getCountEvents(id);
    }

    /**
     * Fetch posts by their type that were made by the user.
     * @param id the users' id.
     * @return a JSON-String containing post by their types with the users as author.
     * @throws JSONException .
     */
    @GetMapping("/getPostCountByType")
    public String getPostCountByType(long id) throws JSONException {
        return userService.getPostCountByType(id);
    }

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all events created within the last day, by the given User.
     */
    @GetMapping("/getAmountOfEventsCreatedYesterday")
    public List<String> getAmountOfEventsCreatedYesterday(long id) {
        return userService.getAmountOfEventsCreatedYesterday(id);
    }

    /**
     * Fetch Events made by the user.
     * @param page the page of results.
     * @param size the amount of results.
     * @param filter what Events shall be fetched.
     * @param search a search in events, only displaying matching events.
     * @param id the authors userid.
     * @return a JSON-String containing Events with their stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping("/getEventsWithStatsAndId")
    public String getEventsWithStats(Integer page, Integer size,  String filter, String search, long id) throws JSONException, ParseException {
        return userService.getEventsWithStats(page, size, filter, search, id);
    }

    /**
     * Fetch a users stats.
     * @param userId the user to fetch stats for.
     * @return a JSON-String of userdata.
     */
    @GetMapping("/{userId}")
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userService.getUserStats(userId);
    }

    /**
     * Fetch a users stats as string.
     * @param id the user to fetch stats for.
     * @return a JSON-String of userdata.
     */
    @GetMapping("/getUserStats")
    public String getUserStat(@RequestParam Long id) throws JSONException {
        return userService.getUserStat(id);
    }

    /**
     * Fetch a users views broken down.
     * @param id the users' id.
     * @return a JSON-String of userdata.
     * @throws JSONException .
     */
    @GetMapping("/getViewsBrokenDown")
    public String getViewsBrokenDown(@RequestParam Long id) throws JSONException {
        return userService.getViewsBrokenDown(id);
    }

    /**
     * Fetch a users profile views averages by membership and post-posession.
     * @return a JSON-String.
     * @throws JSONException .
     */
    @GetMapping("/getUserProfileViewsAveragesByTypeAndPosts")
    public String getUserProfileViewsAveragesByTypeAndPosts() throws JSONException {
        return userService.getUserProfileViewsAveragesByTypeAndPosts();
    }

    /**
     * Fetch a users profile views averages by membership.
     * @return a JSON-String.
     * @throws JSONException .
     */
    @GetMapping("/getUserProfileAndPostViewsAveragesByType")
    public String getUserProfileAndPostViewsAveragesByType() throws JSONException {
        return userService.getUserProfileAndPostViewsAveragesByType();
    }

    /**
     * Fetch a users profile views averages by membership and post-possesions, skewed towards higher memberships.
     * @return a JSON-String.
     * @throws JSONException .
     */
    @GetMapping("/getUserProfileAndPostViewsAveragesByTypeSkewed")
    public String getUserProfileAndPostViewsAveragesByTypeSkewed() throws JSONException {
        return userService.getUserProfileAndPostViewsAveragesByTypeSkewed();
    }

    /**
     * Method finds all dates user had views in, and adds the date and the views on that day into one list each.
     * @param userId the id of the user you are fetching for.
     * @return a JSON-String of a JSON-Object containing JSON-Array-Strings under the labels "dates" and "views".
     * @throws JSONException .
     */
    @GetMapping("/getProfileViewsByTime")
    public String getProfileViewsByTime(Long userId) throws JSONException {
        return userService.getProfileViewsByTime(userId);
    }

    /**
     * This accounts for users with and without posts, but does count post-views towards their averages. Hence, users with posts will seem better here.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithPostClicks")
    public String getUserAveragesWithPostClicks() throws JSONException {
        return userService.getUserAveragesWithPostClicks();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type. With a debug-label.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithPostsWithoutPostClicks")
    public String getUserAveragesWithPostsWithoutPostClicksDebug() throws JSONException {
        return userService.getUserAveragesWithPostsWithoutPostClicksDebug();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their post's views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type. With a debug-label.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithPostsOnlyPostClicks")
    public String getUserAveragesWithPostsOnlyPostClicksDebug() throws JSONException {
        return userService.getUserAveragesWithPostsOnlyPostClicksDebug();
    }


    /**
     * This accounts for ONLY users that do not have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithoutPosts")
    public String getUserAveragesWithoutPosts() throws JSONException {
        return userService.getUserAveragesWithoutPosts();
    }

    /**
     * This accounts for all users, whether they have posts or not and ONLY counts profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesByType")
    public String getUserAveragesByType() throws JSONException {
        return userService.getUserAveragesByType();
    }

    /**
     * Fetch all clicks on posts of this user.
     * @param uid the authors id to fetch for.
     * @return total amount of clicks on this user's posts.
     */
    @GetMapping("/getClickTotalOnPostsOfUser")
    public int getClickTotalOnPostsOfUser (int uid){
        return userService.getClickTotalOnPostsOfUser(uid);
    }

    /**
     * Fetches the account types of all users.
     * @return a JSON-String containing the account types of all users as counts.
     */
    @GetMapping("/getAccountTypeAll")
    public String getAccountTypeAll(){
        return userService.getAccountTypeAll();
    }


    /**
     *
     * @return ein JSON-String, der die Anzahl der Accounts pro Account-Typ enthält.
     */
    @GetMapping("/getAccountTypeAllYesterday")
    public String getAccTypes() {
        return userService.getAccTypes();
    }

    /**
     * Fetches a representation of all user-plan changes within the last week.
     * @return a JSON-Object containing Lists of Strings and counts of items for those lists.
     * @throws JSONException .
     */
    @GetMapping("/getNewUsersAll")
    public String getNewUsersAll() throws JSONException {
        return userService.getNewUsersAll();
    }

    /**
     * Fetch the full log of changes in memberships of this user.
     * @param page the page of results.
     * @param size the amount of results.
     * @param userId the user to fetch for.
     * @return a  String representation of all membership changes.
     * @throws JSONException .
     */
    @GetMapping("/getFullLog")
    public String getFullLog(int page, int size, String userId) throws JSONException {
        return userService.getFullLog(page, size, userId);
    }

    /**
     *
     * @param id user id to fetch an account type for.
     * @return "basis" "plus" "premium" "sponsor" "basis-plus" "admin" "none"
     */
    @GetMapping("/getTypeById")
    public String getType(int id) {
        return userService.getType(id);
    }

    /**
     * Checks whether a user has a post.
     * @param id the users' id.
     * @return whether the user has authored a post.
     */
    @GetMapping("/hasPost")
    public boolean hasPost(@RequestParam int id) {
        return userService.hasPost(id);
    }

    /**
     * Checks whether the user has posts in each specific post-type.
     * @param id the users' id.
     * @return a JSON-String containing whether each post-type has a post by this author.
     * @throws JSONException .
     */
    @GetMapping("/hasPostByType")
    public String hasPostByType(int id) throws JSONException {
        return userService.hasPostByType(id);
    }

    /**
     *
     * @param userId  id des users.
     * @return a collection of maximum and actual values for a user's completion status of their profile.
     */
    @GetMapping("/getPotentialById")
    public String getPotentialByID(int userId) throws JSONException {
        return userService.getPotentialByID(userId);
    }

    /**
     * Fetch how much of the anbieterprofilvervollständigung has been fulfilled on average globally.
     * @return percentage.
     */
    @GetMapping("/getPotentialPercentGlobal")
    public double getPotentialPercentGlobal(){
        return userService.getPotentialPercentGlobal();
    }

    /**
     *
     * @param userId the user you want to fetch data for.
     * @return a double representing the amount of clicks a user had for each day of tracking (arithmetic average) or zero if user has not been tracked.
     */
    @GetMapping("/getUserClicksPerDay")
    public double getUserClicksPerDay(long userId) {
        return userService.getUserClicksPerDay(userId);
    }

    /**
     * Checks whether the tendency of profile views is growing or falling for this user.
     * @param userId the users' id.
     * @return whether the tendency is growth.
     */
    @GetMapping("/tendencyUp")
    public Boolean tendencyUp(long userId) {
        return userService.tendencyUp(userId);
    }

    /**
     * Fetch this user's rankings in content and profile by group and total.
     * @param id the users' id.
     * @return a JSON-String of this user's rankings in content and profile views by group and total.
     * @throws JSONException .
     */
    @GetMapping("/getRankings")
    public String getRankings(long id) throws JSONException {
        return userService.getRankings(id);
    }

    /**
     * Gibt die verteilten Ansichten (Views) eines Benutzers über die letzten 24 Stunden als JSON-String zurück.
     * Die Methode berechnet die Ansichten basierend auf den Daten der letzten zwei Tage (basierend auf uniId)
     * für den angegebenen Benutzer (userId). Für jede Stunde der letzten 24 Stunden werden die Ansichten ermittelt.
     * Falls für eine bestimmte Stunde keine Daten vorhanden sind, wird der Wert 0 angenommen.
     *
     * @param userId   Die ID des Benutzers, für den die Ansichten abgerufen werden sollen.
     * @param daysback Gibt an, wie viele Tage zurückliegend die Daten berücksichtigt werden sollen.
     *                 Ein Wert von 0 bedeutet, dass die Daten für heute und gestern berücksichtigt werden.
     * @return Ein JSON-String, der eine Map darstellt, wobei jeder Schlüssel eine Stunde (0-23) und jeder Wert
     *         die Anzahl der Ansichten (Views) für diese Stunde ist. Das Format ist {"Stunde": Ansichten, ...}.
     * @throws JsonProcessingException Wenn beim Verarbeiten der Daten zu einem JSON-String ein Fehler auftritt.
     */
    @GetMapping("/getUserViewsDistributedByHours")
    public String getUserViewsDistributedByHours(@RequestParam Long userId,@RequestParam int daysback) throws JsonProcessingException {
        return userService.getUserViewsDistributedByHours(userId, daysback);
    }

    /**
     * Ermittelt die Anzahl der Anbieter für alle Tags.
     *
     * @return Eine Map von Tags zu ihrer jeweiligen Benutzeranzahl.
     */
    @GetMapping("/userCountForAllTags")
    public String getUserCountForAllTagsString() throws JSONException {
        return userService.getUserCountForAllTags().toString();
    }

    /**
     * Berechnet den prozentualen Anteil der Anbieter für alle Tags.
     *
     * @return Eine Map von Tags zu ihrem jeweiligen prozentualen Anteil an der Gesamtzahl der Benutzer.
     */
    @GetMapping("/userCountForAllTagsInPercentage")
    public String getUserCountForAllTagsInPercentageString() throws JSONException {
        return userService.getUserCountForAllTagsInPercentage().toString();
    }

    /**
     * Berechnet den prozentualen Anteil der Anbieter für die Tags eines spezifischen Benutzers.
     *
     * @param userId Die ID des Benutzers, dessen Tag-Prozentsätze abgerufen werden sollen.
     * @return Eine Map von Tags zu ihrem jeweiligen prozentualen Anteil an der Gesamtzahl der Benutzer.
     */
    @GetMapping("/getPercentageForTagsByUserId")
    public String getPercentageForTagsByUserIdString(Long userId) throws JSONException {
        return userService.getPercentageForTagsByUserId(userId).toString();
    }

    /**
     * Ruft eine Zuordnung von Tags zu konkurrierenden Benutzern basierend auf den Tags eines gegebenen Benutzers ab.
     * Diese Methode findet konkurrierende Benutzer für jeden Tag. Konkurrierende Benutzer werden anhand ihrer Anzeigenamen identifiziert.
     *
     * @param userId Die ID des Benutzers, dessen Tags verwendet werden, um Konkurrenz zu finden.
     * @return Eine Map, bei der Schlüssel Tags und Werte Zeichenketten sind, die Listen von Anzeigenamen konkurrierender Benutzer darstellen.
     */
    @GetMapping("/getCompetitionByTagsForUser")
    public Map<String,String> getCompetitionByTags(Long userId){
        return userService.getCompetitionByTags(userId);
    }

    /**
     * Endpunkt, um eine String-Darstellung der Konkurrenz für Tags zu erhalten, die einem bestimmten Benutzer zugeordnet sind.
     * Diese Methode ruft getCompetitionByTags auf, um die Zuordnung zu erhalten, und konvertiert sie dann in eine Zeichenkette.
     *
     * @param userId Die ID des Benutzers, für den die Konkurrenz nach Themenfeldern angefordert werden.
     * @return Eine Zeichenketten-Darstellung der Map, die von getCompetitionByTags zurückgegeben wird.
     */
    @GetMapping("/getCompetitionForTagsByUserId")
    public String getCompetitionForTagsByUserIdString(Long userId) {
        return userService.getCompetitionByTags(userId).toString();
    }

    /**
     * Fetch all tags data for all users.
     * @return a JSON-String containing all tag-data of user-profiles.
     * @throws JSONException .
     */
    @GetMapping("/getAllUserTagsData")
    public String getAllUserTagsDataFusion() throws JSONException {
        return userService.getAllUserTagsDataFusion();
    }

    /**
     * Fetches tags data for a single user.
     * @param id the users' id to fetch for.
     * @param sorter what to sort the data with.
     * @return a JSON-String containing tags-data.
     * @throws JSONException .
     */
    @GetMapping("/getSingleUserTagsData")
    public String getSingleUserTagsData(long id, String sorter) throws JSONException {
        return userService.getSingleUserTagsData(id, sorter);
    }

    /**
     * Fetch the rankings of this user in their defined tags.
     * @param id the users' id.
     * @param sorter what to sort the rankings by.
     * @return a JSON-String containing rankings by term.
     * @throws JSONException .
     */
    @GetMapping("/getRankingInTag")
    public String getRankingsInTagsForUserBySorter(long id, String sorter) throws JSONException {
        return userService.getRankingsInTagsForUserBySorter(id, sorter);
    }

    /**
     * Count the users in the given tag.
     * @param tag the tag to search in.
     * @return amount of users with this tag.
     */
    @GetMapping("/countUsersByTag")
    public Integer countUsersByTag(String tag) {
        return userService.countUsersByTag(tag);
    }

    /**
     * Retrieves the total accumulated impressions for a specified user across all time.
     *
     * @param userId the ID of the user for whom to retrieve impressions
     * @return a JSON string representing the total accumulated impressions for the user
     */
    @GetMapping("/getAccumulatedUserImpressions")
    public String getAccumulatedUserImpressionsAllTime(@RequestParam Long userId){
        return soziImp.getImpressionsAccumulatedAllTimeByUserId(userId);
    }

    /**
     * Retrieves the impression details of the user who has the highest number of impressions of all time.
     *
     * @return a JSON string representing the user with the best impression record
     */
    @GetMapping("/getBestUserImpression")
    public String getBestUserImpressionAllTime(){
        List<SocialsImpressions>imps = soziImp.filterOutPostImpressions(soziImp.findAll());
        return soziImp.impToJSON(soziImp.getMostImpressionsFromList(imps));
    }

    /**
     * Retrieves the impression details of the user who has the highest number of impressions for the current day.
     *
     * @return a JSON string representing the user with the best impression record today
     */
    @GetMapping("/getBestUserImpressionToday")
    public String getBestUserImpressionToday(){
        List<SocialsImpressions>imps = soziImp.filterOutPostImpressions(soziImp.findAllToday());
        return soziImp.impToJSON(soziImp.getMostImpressionsFromList(imps));
    }

    /**
     * Updates the user-rankings buffer table.
     * @return whether the update was successful.
     */
    @GetMapping("/updateUserRankingBuffer")
    public boolean updateUserRankingBuffer() {
        return userService.updateUserRankingBuffer();
    }

    /**
     * Fetches a JSON for all Users in Email-List.
     * @return a JSON-String containing data on all mails to users.
     * @throws JSONException .
     */
    @GetMapping("/getJSONForEmailListAll")
    public String getJSONForEmailListAll() throws JSONException {
        return userService.getJSONForEmailListAll();
    }

    /**
     * Sends the stat-newsletters via the wordpress plugin.
     * @return whether the sending worked.
     */
    @GetMapping("/sendNewsletters")
    public boolean sendNewsletters() {
        return userService.sendNewsletters();
    }

    /**
     * Fetches the average redirects by profile membership.
     * @return JSON-String containing the average redirects by profile membership.
     * @throws JSONException .
     */
    @GetMapping("/getAverageRedirectsByPlan")
    public String getAverageRedirectsByPlan() throws JSONException {return userService.getAverageRedirectsByPlan();}

    /**
     * Fetches all anbieter names that contain the given search.
     * @param search .
     * @return a JSON-Array-String of Anbieter-names.
     */
    @GetMapping("/getPossibleAnbieter")
    public String getPossibleAnbieter(String search, String abo, String typ) {return userService.getUsernamesByStart(search, abo, typ);}
}
