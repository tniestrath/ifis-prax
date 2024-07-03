package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.PostClicksByHourDLCService;
import com.analysetool.services.SocialsImpressionsService;
import com.analysetool.services.UserService;
import com.analysetool.services.UserViewsByHourDLCService;
import com.analysetool.util.Constants;
import com.analysetool.util.DashConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping(value = { "/users", "/0wB4P2mly-xaRmeeDOj0_g/users"}, method = RequestMethod.GET, produces = "application/json")
public class UserController {

    @Autowired
    private WPUserRepository userRepository;
    @Autowired
    private UserStatsRepository userStatsRepository;
    @Autowired
    private PostController postController;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostStatsRepository statRepository;
    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;
    @Autowired
    private WPTermRepository termRepo;
    @Autowired
    private WpTermRelationshipsRepository termRelRepo;
    @Autowired
    private WpTermTaxonomyRepository termTaxRepo;
    @Autowired
    private universalStatsRepository uniRepo;
    @Autowired
    private WPMembershipRepository wpMemberRepo;
    @Autowired
    private UserViewsByHourDLCRepository userViewsRepo;
    @Autowired
    private UserViewsByHourDLCService userViewsHourService;
    @Autowired
    private PostClicksByHourDLCService postHourlyService;
    @Autowired
    private UserRedirectsHourlyRepository userRedirectsRepo;
    @Autowired
    private EventsController eventsController;
    @Autowired
    private EventsRepository eventsRepo;
    @Autowired
    private SocialsImpressionsService soziImp;
    @Autowired
    private RankingTotalContentRepository rankingTotalContentRepo;
    @Autowired
    private RankingTotalProfileRepository rankingTotalProfileRepo;
    @Autowired
    private RankingGroupProfileRepository rankingGroupProfileRepo;
    @Autowired
    private RankingGroupContentRepository rankingGroupContentRepo;
    @Autowired
    private MembershipBufferRepository memberRepo;
    @Autowired
    private NewsletterRepository newsletterRepo;
    @Autowired
    private UserService userService;

    private final DashConfig config;

    public UserController(DashConfig config) {
        this.config = config;
    }

    @GetMapping("/getById")
    public String getUserById(int id) throws JSONException {
        JSONObject obj = new JSONObject();
        WPUser user = userRepository.findById((long) id).orElseThrow();

        obj.put("id", user.getId());
        obj.put("displayName", user.getDisplayName());
        obj.put("accountType", getType(Math.toIntExact(user.getId())));
        obj.put("accessLevel", getAccessLevel(user.getId()));

        return obj.toString();
    }

    public String getAccessLevel(long userId) {
        if(getType((int) userId).equals("admin")) {
            return "admin";
        } else if(isModerator(userId)) {
            return "mod";
        } else if(getType((int) userId).equals("premium")) {
            return "user";
        } else {
            return "none";
        }
    }

    @GetMapping("/getByLogin")
    public String getUserByLogin(@RequestParam String u) throws JSONException {
        JSONObject obj = new JSONObject();
        var user = userRepository.findByLogin(u);
        if (user.isPresent()){
            obj.put("id", user.get().getId());
            obj.put("displayName",user.get().getDisplayName());
            obj.put("accountType", getType(Math.toIntExact(user.get().getId())));
            obj.put("accessLevel", getAccessLevel(user.get().getId()));
        }
        return obj.toString();
    }

    /**
     *
     * @param page which page of results in the given size you want to fetch.
     * @param size the number of results you want per page.
     * @param search the search-term you want results for, give empty string for none.
     * @param filterAbo "basis" "basis-plus" "plus" "premium" "sponsor" "none" "admin"
     * @param sorter "profileView" "contentView" "viewsByTime", any other String searches by user id.
     * @return a JSON String containing information about all users in the specified page, and the number of users loaded.
     * @throws JSONException .
     */
    @GetMapping("/getAll")
    public String getAll(Integer page, Integer size, String search, String filterAbo, String filterTyp, String sorter) throws JSONException {
        List<WPUser> list;


        if(sorter != null) {
            //Both filters unused, sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsAll(search, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsAll(search, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeAll(search, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingAll(search, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingAbo(search, filterAbo, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Type Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingCompany(search, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else {
                //Abo, Company type and sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            }
        } else {
            //Neither filters nor sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                list = userRepository.getAllByNicenameContainingAll(search, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used.
                list = userRepository.getAllByNicenameContainingAbo(search, filterAbo, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Filter used.
                list = userRepository.getAllByNicenameContainingCompany(search, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                //Both filters used, no sorter used.
                list = userRepository.getAllByNicenameContainingAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
            }
        }

        JSONArray response = new JSONArray();

        for(WPUser user : list) {
            JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
            response.put(obj);
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
    }


    @GetMapping("/getAllWithTagsTest")
    public String getAllWithTagsTest(Integer page, Integer size, String search, String filterAbo, String filterTyp, String tag, String sorter) throws JSONException {
        List<WPUser> list;


        if(sorter != null) {
            //Both filters unused, sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingAllWithTags(search, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingAboWithTags(search, filterAbo, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Type Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else {
                //Abo, Company type and sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepository.getAllNameLikeAndContentViewsAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepository.getAllNameLikeAndProfileViewsByTimeAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepository.getAllByNicenameContainingAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            }
        } else {
            //Neither filters nor sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                list = userRepository.getAllByNicenameContainingAllWithTags(search, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used.
                list = userRepository.getAllByNicenameContainingAboWithTags(search, filterAbo, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Filter used.
                list = userRepository.getAllByNicenameContainingCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                //Both filters used, no sorter used.
                list = userRepository.getAllByNicenameContainingAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            }
        }

        JSONArray response = new JSONArray();

        for(WPUser user : list) {
            JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
            response.put(obj);
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
    }

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

    @GetMapping("/getAllSingleUser")
    public String getAllSingleUser(long id) throws JSONException {
        return userService.getAllSingleUser(id);
    }


    @GetMapping("/getAllSingleUserForNewsletter")
    public String getAllSingleUserNewsletter(long id) {
        JSONObject obj = new JSONObject();
        try {
            id = newsletterRepo.getWpUserIdById(id);
            WPUser user = userRepository.findById(id).isPresent() ? userRepository.findById(id).get() : null;
            if (user != null) {
                obj.put("id", user.getId());
                obj.put("email", user.getEmail());
                obj.put("displayName", user.getDisplayName());
                obj.put("niceName", user.getNicename());
                obj.put("creationDate", user.getRegistered().toLocalDate().toString());
            }
        } catch (Exception e) {
            return "Error, user kaputt";
        }
        return obj.toString();
    }

    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) {

        try {
            String path = String.valueOf(Paths.get(config.getProfilephotos() + "/" + id + "/profile_photo.png"));
            String path2 = String.valueOf(Paths.get(config.getProfilephotos() + "/" + id + "/profile_photo.jpg"));

            File cutePic = new File(path);
            if (!cutePic.exists())
            {
                cutePic = new File(path2);
            }
            byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/getUserClicksChartData")
    public String getUserClicksChartData(long id, String start, String end) throws JSONException {
        Date startDate = Date.valueOf(start);
        Date endDate  = Date.valueOf(end);

        //If the beginning is after the end (good manhwa), swap them.
        if(startDate.after(endDate)) {
            Date puffer = startDate;
            startDate = endDate;
            endDate = puffer;
        }

        JSONArray json = new JSONArray();
        //For all dates selected, add data
        for(LocalDate date : startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;
            //Check if we have stats for the day
            if (uniRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            if(uniId != 0 && uniRepo.findById(uniId).isPresent()) {
                //Since we have data, add date and profileViews
                JSONObject day = new JSONObject();
                JSONArray dailyPosts = new JSONArray();
                JSONObject biggestPost = new JSONObject();

                day.put("date", uniRepo.findById(uniId).get().getDatum());
                if(userViewsRepo.existsByUserId(id)) {
                    day.put("profileViews", userViewsRepo.getSumByUniIdAndUserId(uniId, id) != null ? userViewsRepo.getSumByUniIdAndUserId(uniId, id) : 0);
                } else {
                    day.put("profileViews", 0);
                }

                Post biggestPostbuffer = null;
                for(Post post : postRepository.getPostsByAuthorAndDate(id, date)) {
                    //Add data for all posts
                    JSONObject postToday = new JSONObject();
                    if(biggestPostbuffer == null) {
                        biggestPostbuffer = post;
                    } else {
                        if((statRepository.getSumClicks(post.getId())) != null) {
                            if(statRepository.getSumClicks(post.getId()) > statRepository.getSumClicks(biggestPostbuffer.getId())) {
                                biggestPostbuffer = post;
                            }
                        }
                    }
                    postToday.put("id", post.getId());
                    postToday.put("title", post.getTitle());
                    postToday.put("type", postController.getType(Math.toIntExact(post.getId())));
                    postToday.put("clicks", statRepository.getSumClicks(post.getId()) != null ? statRepository.getSumClicks(post.getId()) : 0);
                    dailyPosts.put(postToday);
                }
                day.put("posts", dailyPosts);
                if(biggestPostbuffer != null) {
                    biggestPost.put("id", biggestPostbuffer.getId());
                    biggestPost.put("title", biggestPostbuffer.getTitle());
                    biggestPost.put("type", postController.getType(Math.toIntExact(biggestPostbuffer.getId())));
                    biggestPost.put("clicks", statRepository.getSumClicks(biggestPostbuffer.getId()) != null ? statRepository.getSumClicks(biggestPostbuffer.getId()) : 0);
                } else {
                    biggestPost.put("id", 0);
                    biggestPost.put("title", 0);
                    biggestPost.put("type", 0);
                    biggestPost.put("clicks", 0);
                }
                day.put("biggestPost", biggestPost);

                json.put(day);
            }

        }
        return  json.toString();
    }

    /**
     *
     * @return a JSON-String containing a list of Events (newEvents) starting with u| for upcoming, c| for current and their type,
     * the count of events in the past for this user (countOldEvents)
     * and a count of all events by this user that are active (countTotal).
     */
    @GetMapping("/getAmountOfEvents")
    public String getCountEvents(long id) throws JSONException {

        JSONObject json = new JSONObject();

        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.getAllByOwnerID(id);
        int countOld = 0;

        for (Events e : allEvents) {
            if(eventsController.isActive(e)) {
                if (eventsController.isCurrent(e)) {
                    events.add("c|" + eventsController.getEventType(e));
                } else if (eventsController.isUpcoming(e)) {
                    events.add("u|" + eventsController.getEventType(e));
                } else {
                    countOld++;
                }
            }

        }
        json.put("newEvents", new JSONArray(events));
        json.put("countOldEvents", countOld);
        json.put("countTotal", countOld + events.size());

        return json.toString();
    }

    @GetMapping("/getPostCountByType")
    public String getPostCountByType(long id) throws JSONException {
        List<Post> posts = postRepository.findByAuthorPageable(id, "", PageRequest.of(0, postController.getCountTotalPosts()));

        int countArtikel = 0;
        int countBlogs = 0;
        int countNews = 0;
        int countWhitepaper = 0;
        int countPodcasts = 0;

        for(Post post : posts) {
            switch(postController.getType(post.getId())) {
                case "artikel" -> {
                    countArtikel++;
                }
                case "blog" -> {
                    countBlogs++;
                }
                case "news" -> {
                    countNews++;
                }
                case "podcast" -> {
                    countPodcasts++;
                }
                case "whitepaper" -> {
                    countWhitepaper++;
                }
            }
        }

        JSONObject json = new JSONObject();
        json.put("Whitepaper", countWhitepaper);
        json.put("Blogs", countBlogs);
        json.put("News", countNews);
        json.put("Podcasts", countPodcasts);
        json.put("Artikel", countArtikel);

        return json.toString();
    }

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all events created within the last day, by the given User.
     */
    @GetMapping("/getAmountOfEventsCreatedYesterday")
    public List<String> getAmountOfEventsCreatedYesterday(long id) {
        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.getAllByOwnerID(id);
        LocalDate today = LocalDate.now();

        for (Events e : allEvents) {
            LocalDate createdDate = e.getEventDateCreated().toLocalDate();

            if (createdDate.isBefore(today) && eventsController.isActive(e)) {
                if(eventsController.isCurrent(e)) {
                    events.add("c|" + eventsController.getEventType(e));
                } else if(eventsController.isUpcoming(e)) {
                    events.add("u|" + eventsController.getEventType(e));
                }

            }
        }
        return events;
    }

    @GetMapping("/getEventsWithStatsAndId")
    public String getEventsWithStats(Integer page, Integer size,  String filter, String search, long id) throws JSONException, ParseException {
        List<Post> list;

        if(filter.isBlank()) {
            list = postRepository.getAllEventsWithSearchAndAuthor(search, id, PageRequest.of(page, size));
        } else {
            list = postRepository.getAllEventsWithTypeAndSearchAndAuthor(eventsController.getTermIdFromFrontendType(filter), search, id, PageRequest.of(page, size));
        }

        List<JSONObject> stats = new ArrayList<>();

        for(Post post : list) {
           stats.add(new JSONObject(postController.PostStatsByIdForFrontend(post.getId())));
        }

        return new JSONArray(stats).toString();
    }

    @GetMapping("/{userId}")
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }

    @GetMapping("/getUserStats")
    public String getUserStat(@RequestParam Long id) throws JSONException {
        JSONObject obj = new JSONObject();
        UserStats user = userStatsRepository.findByUserId(id);
        obj.put("Profilaufrufe",user.getProfileView());
        return obj.toString();
    }

    @GetMapping("/getViewsBrokenDown")
    public String getViewsBrokenDown(@RequestParam Long id) throws JSONException {
        long viewsBlog = 0;
        long viewsArtikel = 0;
        long viewsProfile = 0;
        long viewsNews = 0;
        long viewsWP = 0;
        long viewsPodcast = 0;
        long viewsVideo = 0;
        try {
            viewsProfile = userStatsRepository.findByUserId(id).getProfileView();
        } catch (NullPointerException ignored) {
        }

        List<Post> posts = postRepository.findByAuthor(id.intValue());

        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                int stat = statRepository.getSumClicks(post.getId()) == null ? 0 : statRepository.getSumClicks(post.getId());
                switch(postController.getType(post.getId())) {
                    case "blog" -> viewsBlog += stat;
                    case "artikel" -> viewsArtikel += stat;
                    case "news" -> viewsNews += stat;
                    case "whitepaper" -> viewsWP += stat;
                    case "podcast" -> viewsPodcast += stat;
                    case "videos" -> viewsVideo += stat;
                }
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("viewsBlog", viewsBlog);
        obj.put("viewsArtikel", viewsArtikel);
        obj.put("viewsNews", viewsNews);
        obj.put("viewsWhitepaper", viewsWP);
        obj.put("viewsPodcast", viewsPodcast);
        obj.put("viewsVideo", viewsVideo);
        obj.put("viewsProfile", viewsProfile);
        return obj.toString();

    }

    @GetMapping("/getUserProfileViewsAveragesByTypeAndPosts")
    public String getUserProfileViewsAveragesByTypeAndPosts() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject(getUserAveragesWithoutPosts()));
        array.put(new JSONObject(getUserAveragesByType()));
        array.put(new JSONObject(getUserAveragesWithPostsWithoutPostClicks()));
        return array.toString();
    }

    @GetMapping("/getUserProfileAndPostViewsAveragesByType")
    public String getUserProfileAndPostViewsAveragesByType() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject(getUserAveragesWithPostsWithoutPostClicks()));
        array.put(new JSONObject(getUserAveragesWithPostsOnlyPostClicks()));
        return array.toString();
    }

    /**
     * Method finds all dates user had views in, and adds the date and the views on that day into one list each.
     * @param userId the id of the user you are fetching for.
     * @return a JSON-String of a JSON-Object containing JSON-Array-Strings under the labels "dates" and "views".
     * @throws JSONException .
     */
    @GetMapping("/getProfileViewsByTime")
    public String getProfileViewsByTime(Long userId) throws JSONException {
        JSONArray dates = new JSONArray();
        JSONArray views = new JSONArray();

        if(userViewsRepo.existsByUserId(userId)) {
            List<Integer> userViewDays = userViewsRepo.getUniIdsForUser(userId);
            for(Integer uniId : userViewDays) {
                if(uniRepo.findById(uniId).isPresent()) {
                    dates.put(uniRepo.findById(uniId).get().getDatum().toString().substring(0, 9));
                    views.put(userViewsRepo.getSumByUniIdAndUserId(uniId, userId));
                }
            }

        } else {
            return "No Views found for user.";
        }

        return new JSONObject().put("dates", dates.toString()).put("views", views.toString()).toString();
    }

    /**
     * This accounts for users with and without posts, but does count post-views towards their averages. Hence, users with posts will seem better here.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithPostClicks")
    public String getUserAveragesWithPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepository.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            addCountAndProfileViewsByType(counts, clicks, u, stats, hasPost(Math.toIntExact(u.getId())));
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type. With a debug-label.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithPostsWithoutPostClicks")
    public String getUserAveragesWithPostsWithoutPostClicksDebug() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepository.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages + " ProfileViews";
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    private String getUserAveragesWithPostsWithoutPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepository.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }


    /**
     * This accounts for ONLY users that have posts, counting ONLY their post's views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type. With a debug-label.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithPostsOnlyPostClicks")
    public String getUserAveragesWithPostsOnlyPostClicksDebug() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepository.findAll()) {
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, false, true);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages + " -PostClicks";
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their post's views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    private String getUserAveragesWithPostsOnlyPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepository.findAll()) {
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, false, true);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for ONLY users that do not have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesWithoutPosts")
    public String getUserAveragesWithoutPosts() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepository.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(!hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for all users, whether they have posts or not and ONLY counts profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    @GetMapping("/getUserAveragesByType")
    public String getUserAveragesByType() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepository.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            addCountAndProfileViewsByType(counts, clicks, u, stats);
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);

        return averages.toString();
    }

    private void buildAveragesFromCountsAndClicks(JSONObject counts, JSONObject clicks, JSONObject averages) throws JSONException {
        if(counts.getInt("basis") != 0) {
            averages.put("basis", clicks.getInt("basis") / counts.getInt("basis"));
        } else {
            averages.put("basis", 0);
        }
        if(counts.getInt("basis-plus") != 0) {
            averages.put("basis-plus", clicks.getInt("basis-plus") / counts.getInt("basis-plus"));
        } else {
            averages.put("basis-plus", 0);
        }
        if(counts.getInt("plus") != 0) {
            averages.put("plus", clicks.getInt("plus") / counts.getInt("plus"));
        } else {
            averages.put("plus", 0);
        }
        if(counts.getInt("premium") != 0) {
            averages.put("premium", clicks.getInt("premium") / counts.getInt("premium"));
        } else {
            averages.put("premium", 0);
        }
    }

    private void addCountAndProfileViewsByType(JSONObject counts, JSONObject clicks, WPUser u, boolean profileViews) throws JSONException {
        switch(getType(Math.toIntExact((u.getId())))) {
            case "basis" -> {
                if(profileViews) {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("basis", counts.getInt("basis") + 1);
            }
            case "basis-plus" -> {
                if(profileViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
            }
            case "plus" -> {
                if(profileViews) {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("plus", counts.getInt("plus") + 1);
            }
            case "premium" -> {
                if(profileViews) {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("premium", counts.getInt("premium") + 1);
            }
            case "sponsor" -> {
                if(profileViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("sponsor", counts.getInt("sponsor") + 1);
            }
        }
    }

    private void addCountAndProfileViewsByType(JSONObject counts, JSONObject clicks, WPUser u, boolean profileViews, boolean postViews) throws JSONException {
        switch(getType(Math.toIntExact((u.getId())))) {
            case "basis" -> {
                if(profileViews) {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("basis", clicks.getInt("basis") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("basis", counts.getInt("basis") + 1);
            }
            case "basis-plus" -> {
                if(profileViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
            }
            case "plus" -> {
                if(profileViews) {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("plus", clicks.getInt("plus") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("plus", counts.getInt("plus") + 1);
            }
            case "premium" -> {
                if(profileViews) {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("premium", clicks.getInt("premium") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("premium", counts.getInt("premium") + 1);
            }
            case "sponsor" -> {
                if(profileViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("sponsor", counts.getInt("sponsor") + 1);
            }
        }
    }

    @GetMapping("/getClickTotalOnPostsOfUser")
    public int getClickTotalOnPostsOfUser (int uid){
        List<Post> posts= postRepository.findByAuthor(uid);
        int clicks = 0;
        for(Post post : posts) {
            if(post != null) {
                clicks += statRepository.getSumClicks(post.getId()) != null ? statRepository.getSumClicks(post.getId()) : 0;
            }
        }

        return clicks;
    }

    @GetMapping("/getAccountTypeAll")
    public String getAccountTypeAll(){
        HashMap<String, Integer> counts = new HashMap<>();

        for(WPUser user : userRepository.findAll()) {
            switch(this.getType(Math.toIntExact(user.getId()))) {
                case "admin" -> {
                    counts.put("Administrator", counts.get("Administrator") == null ? 1 : counts.get("Administrator") + 1);
                }
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
        return new JSONObject(counts).toString();
    }


    /**
     *
     * @return ein JSON-String, der die Anzahl der Accounts pro Account-Typ enthält.
     */
    @GetMapping("/getAccountTypeAllYesterday")
    public String getAccTypes() {
        HashMap<String, Long> map = new HashMap<>();
        UniversalStats uni = uniRepo.findAll().get(uniRepo.findAll().size() -2);

        map.put("Anbieter", uni.getAnbieter_abolos_anzahl());
        map.put("Basic", uni.getAnbieterBasicAnzahl());
        map.put("Basic-Plus", uni.getAnbieterBasicPlusAnzahl());
        map.put("Plus", uni.getAnbieterPlusAnzahl());
        map.put("Premium", uni.getAnbieterPremiumAnzahl());
        map.put("Sponsor", uni.getAnbieterPremiumSponsorenAnzahl());


        return new JSONObject(map).toString();

    }

    /**
     * Fetches a representation of all user-plan changes within the last week.
     * @return a JSON-Object containing Lists of Strings and counts of items for those lists.
     * @throws JSONException .
     */
    @GetMapping("/getNewUsersAll")
    public String getNewUsersAll() throws JSONException {
        JSONObject obj = new JSONObject();

        Comparator<String> customComparator = Comparator.comparing(s -> s.charAt(0));

        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        List<String> ohne = new ArrayList<>(), basis = new ArrayList<>(), basis_plus = new ArrayList<>(), plus = new ArrayList<>(), premium = new ArrayList<>();

        for(WPUser user : userRepository.findAll()) {

            String secondLastMembership = memberRepo.getPageableSingle(user.getId(), PageRequest.of(1, 1)).size() > 0 ? memberRepo.getPageableSingle(user.getId(), PageRequest.of(1, 1)).get(0).getMembership() : "none";

            if(memberRepo.getLastByUserId(user.getId()) == null) continue;
            if(memberRepo.getLastByUserId(user.getId()).getTimestamp().toLocalDateTime().isAfter(lastWeek) && !getType(Math.toIntExact(user.getId())).equals("admin")) {
                char preSign = '+';
                if (memberRepo.getPageableSingle(user.getId(), PageRequest.of(1,1)).size() > 0 && !memberRepo.getLastByUserId(user.getId()).getMembership().equals("deleted")) {
                    preSign = '&';
                } else if (memberRepo.getLastByUserId(user.getId()).getMembership().equals("deleted")) {
                    preSign = '-';
                }

                List<String> newMembership, oldMembership;

                switch (memberRepo.getLastByUserId(user.getId()).getMembership()) {
                    case "none" -> {
                        newMembership = ohne;
                    }
                    case "basis" -> {
                        newMembership = basis;
                    }
                    case "basis-plus" -> {
                        newMembership = basis_plus;
                    }
                    case "plus" -> {
                        newMembership = plus;
                    }
                    case "premium" -> {
                        newMembership = premium;
                    }
                    default -> newMembership = null;
                }
                if (memberRepo.getPageableSingle(user.getId(), PageRequest.of(1, 1)).size() > 0) {
                    switch (secondLastMembership) {
                        case "none" -> {
                            oldMembership = ohne;
                        }
                        case "basis" -> {
                            oldMembership = basis;
                        }
                        case "basis-plus" -> {
                            oldMembership = basis_plus;
                        }
                        case "plus" -> {
                            oldMembership = plus;
                        }
                        case "premium" -> {
                            oldMembership = premium;
                        }
                        default -> oldMembership = null;
                    }
                } else {
                    oldMembership = null;
                }

                addToUserList(preSign, newMembership, oldMembership, user);
            }
        }

        ohne.sort(customComparator);
        basis.sort(customComparator);
        basis_plus.sort(customComparator);
        plus.sort(customComparator);
        premium.sort(customComparator);

        obj.put("ohne", new JSONArray(ohne));
        obj.put("basis", new JSONArray(basis));
        obj.put("basisPlus", new JSONArray(basis_plus));
        obj.put("plus", new JSONArray(plus));
        obj.put("premium", new JSONArray(premium));

        obj.put("ohneCount", getUserChangeCountFromList(ohne));
        obj.put("basisCount", getUserChangeCountFromList(basis));
        obj.put("basisPlusCount", getUserChangeCountFromList(basis_plus));
        obj.put("plusCount", getUserChangeCountFromList(plus));
        obj.put("premiumCount", getUserChangeCountFromList(premium));

        return obj.toString();

    }

    private void addToUserList(char preSign, List<String> newMembership, List<String> oldMembership, WPUser user) {

        switch(preSign) {
            case '&' -> {
                if(newMembership != null) newMembership.add("+" + user.getDisplayName() + "<&>" + getType(Math.toIntExact(user.getId())));
                if(oldMembership != null) oldMembership.add("-" + user.getDisplayName() + "<&>" + getType(Math.toIntExact(user.getId())));
            }
            case '+' -> {
                if(newMembership != null) newMembership.add("+" + user.getDisplayName() + "<&>" + getType(Math.toIntExact(user.getId())));
            }
            case '-' -> {
                if(oldMembership != null) oldMembership.add("-" + user.getDisplayName() + "<&>" + "DELETED");
            }
        }

    }

    private int getUserChangeCountFromList(List<String> list) {
        int total = 0;
        for(String user : list) {
            total = user.charAt(0) == '+' ? total + 1 : total - 1;
        }
        return total;
    }

    @GetMapping("/getFullLog")
    public String getFullLog(int page, int size, String userId) throws JSONException {

        JSONArray array = new JSONArray();

        Page<MembershipsBuffer> buffers;

        if(userId == null || !userId.isBlank()) {
            buffers = memberRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id")));
        } else {
            buffers = memberRepo.findAllForUser(Integer.parseInt(userId), PageRequest.of(page, size));
        }

        for (MembershipsBuffer buffer : buffers) {
            JSONObject obj = new JSONObject();
            obj.put("newPlan", buffer.getMembership());
            obj.put("oldPlan", memberRepo.getPreviousMembership(buffer.getUserId(), buffer.getId()) == null ? "none" : memberRepo.getPreviousMembership(buffer.getUserId(), buffer.getId()));
            obj.put("time", buffer.getTimestamp().toString());
            obj.put("user", userRepository.findById(buffer.getUserId()).isPresent() ? userRepository.findById(buffer.getUserId()).get().getDisplayName() : buffer.getUserId());
            array.put(obj);
        }


        return array.toString();
    }


    public boolean isModerator(long userId) {
        if(wpUserMetaRepository.existsByUserId(userId)) {
            return wpUserMetaRepository.getWPUserMetaValueByUserId(userId).contains("editor");
        }
        return false;
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
     *
     * @param id user id to fetch an account type for.
     * @return "um_basis" "um_plus" "um_premium" "um_basis-plus" "admin" "none"
     */
    public String getTypeDirty(int id) {
        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "customer";
            if (wpUserMeta.contains("administrator") ) return "administrator";
            if (wpUserMeta.contains(Constants.getInstance().getPlusAnbieter())) return Constants.getInstance().getPlusAnbieter();
            if (wpUserMeta.contains(Constants.getInstance().getBasisPlusAnbieter())) return Constants.getInstance().getBasisPlusAnbieter();
            if(wpUserMeta.contains("sponsor")) return "um_premium-anbieter-sponsoren";
            if (wpUserMeta.contains(Constants.getInstance().getPremiumAnbieter())) return Constants.getInstance().getPremiumAnbieter();
            if(wpUserMeta.contains(Constants.getInstance().getBasisAnbieter())) return Constants.getInstance().getBasisAnbieter();
            if (wpUserMeta.contains("anbieter")) return "none";
        }


        return "none";
    }

    public String getTypeProfileTags(int id) {
        return userService.getTypeProfileTags(id);
    }

    @GetMapping("/hasPost")
    public boolean hasPost(@RequestParam int id) {
        return !postRepository.findByAuthor(id).isEmpty();
    }

    @GetMapping("/hasPostByType")
    public String hasPostByType(int id) throws JSONException {
        JSONObject jsonTypes = new JSONObject();
        boolean news = false, artikel = false, blog = false, podcast = false, whitepaper= false;
        for(Post p : postRepository.findByAuthor(id)) {
            switch(postController.getType(p.getId())) {
                case "news" -> news = true;
                case "artikel" -> artikel = true;
                case "blog" -> blog = true;
                case "podcast" -> podcast = true;
                case "whitepaper" -> whitepaper = true;
            }
        }
        jsonTypes.put("news", news);
        jsonTypes.put("artikel", artikel);
        jsonTypes.put("blog", blog);
        jsonTypes.put("podcast", podcast);
        jsonTypes.put("whitepaper", whitepaper);

        return jsonTypes.toString();
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

    public double getPotentialPercent(int userId) throws JSONException {
        return userService.getPotentialPercent(userId);
    }

    @GetMapping("/getPotentialPercentGlobal")
    public double getPotentialPercentGlobal(){
        List<WPUser> users = userRepository.findAll();
        int countUsers = users.size();
        double potentialPercentCollector = 0;
        for(WPUser user : users) {
            try {
                potentialPercentCollector+= getPotentialPercent(Math.toIntExact(user.getId()));
            } catch (JSONException ignored) {
            }
        }
        return potentialPercentCollector / countUsers;

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

    @GetMapping("/tendencyUp")
    public Boolean tendencyUp(long userId) {
        return userService.tendencyUp(userId);
    }


    @GetMapping("/getRankings")
    public String getRankings(long id) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("rankingContent", userService.getRankingTotalContentViews(id));
        obj.put("rankingContentByGroup", userService.getRankingInTypeContentViews(id));
        obj.put("rankingProfile", userService.getRankingTotalProfileViews(id));
        obj.put("rankingProfileByGroup", userService.getRankingInTypeProfileViews(id));
        return obj.toString();
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
        int latestUniId = uniRepo.getLatestUniStat().getId() - daysback;
        int previousUniId = latestUniId - 1;
        System.out.println("latestUniId: "+latestUniId+'\n'+"previousUniId: " + previousUniId);
        List<UserViewsByHourDLC> combinedViews = new ArrayList<>();
        combinedViews.addAll(userViewsRepo.findByUserIdAndUniId(userId, previousUniId)); // Daten von gestern
        combinedViews.addAll(userViewsRepo.findByUserIdAndUniId(userId, latestUniId));   // Daten von heute
        System.out.println("combined views: "+combinedViews);
        Map<Integer, Long> hourlyViews = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();

        for (int i = 23; i >= 0; i--) {
            int hour = (currentHour - i + 24) % 24;
            Long viewCount = combinedViews.stream()
                    .filter(view -> view.getHour() == hour)
                    .map(UserViewsByHourDLC::getViews)
                    .findFirst()
                    .orElse(0L);
            hourlyViews.put(hour, viewCount);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(hourlyViews);
    }

    private Map<Long, Double> processAndConvertToDouble(String performanceData) {
        Map<Long, Double> performanceScores = new HashMap<>();
        // Entfernen der Anfangs- und Endklammern und Aufteilen der Einträge
        String[] entries = performanceData.substring(1, performanceData.length() - 1).split(", ");

        for (String entry : entries) {
            String[] parts = entry.split("=");
            if (parts.length == 2) {
                try {
                    Long id = Long.parseLong(parts[0].trim());
                    Double score = Double.parseDouble(parts[1].trim());
                    performanceScores.put(id, score);
                } catch (NumberFormatException ignored) {

                }
            }
        }

        return performanceScores;
    }

    /**
     *
     * @param cryptedTag a full value of a profiles tags, including MULTIPLE tags.
     * @return a List of Strings, containing all Tags in this String.
     */
    public List<String> decryptTags(String cryptedTag) {
        return userService.decryptTags(cryptedTag);
    }

    /**
     *
     * @param cryptedTags profile_tags String for several users.
     * @return a List of a List of Strings, containing all Tags in this String.
     */
    public List<List<String>> decryptTagsStringInList(List<String> cryptedTags) {
        return userService.decryptTagsStringInList(cryptedTags);
    }


    /**
     * Ermittelt die Anzahl der Anbieter für alle Tags.
     *
     * @return Eine Map von Tags zu ihrer jeweiligen Benutzeranzahl.
     */

    @GetMapping("/userCountForAllTags")
    public String getUserCountForAllTagsString() throws JSONException {
        return getUserCountForAllTags().toString();
    }



    public JSONArray getUserCountForAllTagsInPercentage() throws JSONException {
        // Gesamtzahl der Benutzer mit mindestens einem Tag ermitteln
        int totalUsersWithTag = getTotalCountOfUsersWithTags();

        // Tags und ihre Anzahl holen
        JSONObject companiesPerTag = getUserCountForAllTags();

        //Array als Container erstellen
        List<JSONObject> array = new ArrayList<>();

        // Map für prozentualen Anteil erstellen
        List<String> tagLabel = new ArrayList<>();
        List<Double> tagPercentages = new ArrayList<>();


        var iterator = companiesPerTag.keys();
        // Prozentualen Anteil für jeden Tag berechnen
        while(iterator.hasNext()) {
            String key = iterator.next().toString();
            int count = companiesPerTag.getInt(key);
            double percentage = (double) count / totalUsersWithTag * 100;
            array.add(new JSONObject().put(key, percentage));
        }

        array.sort((o1, o2) -> {
            try {
                double v = o2.getDouble(o2.keys().next().toString()) - o1.getDouble(o1.keys().next().toString());
                return (int) v;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return new JSONArray(array);
    }

    /**
     * Berechnet den prozentualen Anteil der Anbieter für alle Tags.
     *
     * @return Eine Map von Tags zu ihrem jeweiligen prozentualen Anteil an der Gesamtzahl der Benutzer.
     */

    @GetMapping("/userCountForAllTagsInPercentage")
    public String getUserCountForAllTagsInPercentageString() throws JSONException {
        return getUserCountForAllTagsInPercentage().toString();
    }

    public double getUserCountAsPercentageForSingleTag(String tag) {
        return userService.getUserCountAsPercentageForSingleTag(tag);
    }

    public Map<String, Double> getPercentageForMultipleTags(List<String> tags) {
        Map<String, Double> tagPercentages = new HashMap<>();

        for (String tag : tags) {
            double percentage = getUserCountAsPercentageForSingleTag(tag);
            tagPercentages.put(tag, percentage);
        }

        return tagPercentages;
    }

    public JSONObject getPercentageForTagsByUserId(Long userId) throws JSONException {
        return userService.getPercentageForTagsByUserId(userId);
    }


    /**
     * Berechnet den prozentualen Anteil der Anbieter für die Tags eines spezifischen Benutzers.
     *
     * @param userId Die ID des Benutzers, dessen Tag-Prozentsätze abgerufen werden sollen.
     * @return Eine Map von Tags zu ihrem jeweiligen prozentualen Anteil an der Gesamtzahl der Benutzer.
     */

    @GetMapping("/getPercentageForTagsByUserId")
    public String getPercentageForTagsByUserIdString(Long userId) throws JSONException {
        return getPercentageForTagsByUserId(userId).toString();
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
        return getCompetitionByTags(userId).toString();
    }


    @GetMapping("/getAllUserTagsData")
    public String getAllUserTagsDataFusion() throws JSONException {
        JSONObject json = getUserCountForAllTags();
        var jsonKeys = json.keys();
        JSONArray array = new JSONArray();
        while(jsonKeys.hasNext()) {
            String tag = jsonKeys.next().toString();
            array.put(new JSONObject().put("count", json.getInt(tag)).put("name", tag));
        }
        array.put(new JSONObject().put("count", getAllUserTagRowsInList(getAllUserIdsWithTags()).size()).put("name", "countTotal"));
        return array.toString();
    }

    @GetMapping("/getSingleUserTagsData")
    public String getSingleUserTagsData(long id, String sorter) throws JSONException {
        return userService.getSingleUserTagsData(id, sorter);
    }

    @GetMapping("/getRankingInTag")
    public String getRankingsInTagsForUserBySorter(long id, String sorter) throws JSONException {
        return userService.getRankingsInTagsForUserBySorter(id, sorter);
    }


    //Aggregate Queries for profile tags

    public List<String> getAllUserTagRowsInList(List<Long> list) {
        List<String> listOfTags = new ArrayList<>();
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListBasis(list));
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListBasisPlus(list));
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListPlus(list));
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListPremium(list));
        return listOfTags;
    }

    public List<Long> getUserIdsByTag(String tag) {
        return userService.getUserIdsByTag(tag);
    }

    public Integer getTotalCountOfUsersWithTags() {
        return userService.getTotalCountOfUsersWithTags();
    }

    @GetMapping("/countUsersByTag")
    public Integer countUsersByTag(String tag) {
        return userService.countUsersByTag(tag);
    }

    public Optional<String> getTags(long userId, String type) {
        return userService.getTags(userId, type);
    }

    public List<Long> getAllUserIdsWithTags() {
        List<Long> list = new ArrayList<>();
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsBasis());
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsBasisPlus());
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsPlus());
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsPremium());
        return list;
    }

    public JSONObject getUserCountForAllTags() throws JSONException {
        List<String> allTags = getAllUserTagRowsInList(getAllUserIdsWithTags());
        List<List<String>> decryptedAndCleanedTags= decryptTagsStringInList(allTags);
        JSONObject json = new JSONObject();

        for(List<String> tags : decryptedAndCleanedTags) {
            for (String tag : tags) {
                try {
                    json.put(tag, json.getInt(tag) + 1);
                } catch (Exception e) {
                    json.put(tag, 0);
                }
            }
        }

        return json;
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

    @GetMapping("/updateUserRankingBuffer")
    public boolean updateUserRankingBuffer() {
        try {
            updateRanksTotal();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            updateRankGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void updateRankGroups() {

        //Reset all
        if(!rankingGroupContentRepo.findAll().isEmpty()) {
            rankingGroupContentRepo.deleteAll();
        }
        if(!rankingGroupProfileRepo.findAll().isEmpty()) {
            rankingGroupProfileRepo.deleteAll();
        }

        for(String type : Constants.getInstance().getListOfUserTypesDirty()) {
            int rank = 1;
            List<WPUser> users = userRepository.getByAboType(type);

            //Sort for profile-view rankings
            users.sort((o1, o2) -> Math.toIntExact((userStatsRepository.existsByUserId(o2.getId()) ? userStatsRepository.findByUserId(o2.getId()).getProfileView() : 0) - (userStatsRepository.existsByUserId(o1.getId()) ? userStatsRepository.findByUserId(o1.getId()).getProfileView() : 0)));
            //Make entries for profile-view rankings.
            for(WPUser user : users) {
                RankingGroupProfile profileRank = new RankingGroupProfile();
                profileRank.setRank(rank);
                rank++;
                profileRank.setType(type);
                profileRank.setUserId(Math.toIntExact(user.getId()));
                rankingGroupProfileRepo.save(profileRank);
            }
            rank = 1;

            //Sort for content-view rankings
            users.sort((o1, o2) -> Math.toIntExact(postController.getPostViewsOfUserById(o2.getId()) - postController.getPostViewsOfUserById(o1.getId())));
            //Make entries for content-view rankings.
            for(WPUser user : users) {
                RankingGroupContent contentRank = new RankingGroupContent();
                contentRank.setRank(rank);
                rank++;
                contentRank.setType(type);
                contentRank.setUserId(Math.toIntExact(user.getId()));
                rankingGroupContentRepo.save(contentRank);
            }

        }
    }

    public void updateRanksTotal() {
        if(!rankingTotalContentRepo.findAll().isEmpty()) {
            rankingTotalContentRepo.deleteAll();
        }
        if(!rankingTotalProfileRepo.findAll().isEmpty()) {
            rankingTotalProfileRepo.deleteAll();
        }
        int rank = 1;

        List<WPUser> users = userRepository.getAllWithAbo();
        //Sort for profile-view rankings
        users.sort((o1, o2) -> Math.toIntExact((userStatsRepository.existsByUserId(o2.getId()) ? userStatsRepository.findByUserId(o2.getId()).getProfileView() : 0) - (userStatsRepository.existsByUserId(o1.getId()) ? userStatsRepository.findByUserId(o1.getId()).getProfileView() : 0)));
        //Make entries for profile-view rankings.
        for(WPUser user : users) {
            RankingTotalProfile profileRank = new RankingTotalProfile();
            profileRank.setRank(rank);
            rank++;
            profileRank.setUserId(Math.toIntExact(user.getId()));
            rankingTotalProfileRepo.save(profileRank);
        }
        rank = 1;

        //Sort for content-view rankings
        users.sort((o1, o2) -> Math.toIntExact(postController.getPostViewsOfUserById(o2.getId()) - postController.getPostViewsOfUserById(o1.getId())));
        //Make entries for content-view rankings.
        for(WPUser user : users) {
            RankingTotalContent contentRank = new RankingTotalContent();
            contentRank.setRank(rank);
            rank++;
            contentRank.setUserId(Math.toIntExact(user.getId()));
            rankingTotalContentRepo.save(contentRank);
        }
    }

}
