package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.DashConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping("/users")
public class UserController {

    private final int maxPostsPlus = 2;
    private final int maxPostsPremium = 3 + 2 + 3 + 3;

    private final int maxPostsSponsor= 3 + 3 + 6 + 6;



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

    private final DashConfig config;

    public UserController(DashConfig config) {
        this.config = config;
    }

    @GetMapping("/getById")
    public String getUserById(@RequestParam String id) throws JSONException {
        JSONObject obj = new JSONObject();
        var user = userRepository.findById(Long.valueOf(id));
        if (user.isPresent()){
            obj.put("id", user.get().getId());
            obj.put("displayName",user.get().getDisplayName());
            if (wpUserMetaRepository.existsByUserId(user.get().getId())){
                String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId(user.get().getId());
                if (wpUserMeta.contains("customer")) obj.put("accountType", "?customer?");
                if (wpUserMeta.contains("administrator")) obj.put("accountType", "admin");
                if (wpUserMeta.contains("anbieter")) obj.put("accountType", "basic");
                if (wpUserMeta.contains("plus-anbieter")) obj.put("accountType", "plus");
                if (wpUserMeta.contains("premium-anbieter")) obj.put("accountType", "premium");
            }
        }
        return obj.toString();
    }

    @GetMapping("/getByLogin")
    public String getUserByLogin(@RequestParam String u) throws JSONException {
        JSONObject obj = new JSONObject();
        var user = userRepository.findByLogin(u);
        if (user.isPresent()){
            obj.put("id", user.get().getId());
            obj.put("displayName",user.get().getDisplayName());
            if (wpUserMetaRepository.existsByUserId(user.get().getId())){
                String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId(user.get().getId());
                if (wpUserMeta.contains("customer")) obj.put("accountType", "?customer?");
                if (wpUserMeta.contains("administrator")) obj.put("accountType", "admin");
                if (wpUserMeta.contains("anbieter")) obj.put("accountType", "basic");
                if (wpUserMeta.contains("plus-anbieter")) obj.put("accountType", "plus");
                if (wpUserMeta.contains("premium-anbieter")) obj.put("accountType", "premium");
            }
        }
        return obj.toString();
    }

    @GetMapping("/getAllNew")
    public String getAllNew() throws JSONException {
        List<WPUser> list = userRepository.findAll();

        JSONArray response = new JSONArray();
        for (WPUser i : list) {
            JSONObject obj = new JSONObject();
            obj.put("id",i.getId());
            obj.put("email",i.getEmail());
            obj.put("displayName",i.getNicename());
            if(userStatsRepository.existsByUserId(i.getId())){
                UserStats statsUser = userStatsRepository.findByUserId(i.getId());
                obj.put("profileViews", statsUser.getProfileView());
                obj.put("postViews", postController.getViewsOfUserById(i.getId()));
                obj.put("postCount", postController.getPostCountOfUserById(i.getId()));

            } else {
                obj.put("profileViews", 0);
                obj.put("postViews",0);
                obj.put("postCount",0);
                obj.put ("performance",0);
            }
            if (wpUserMetaRepository.existsByUserId(i.getId())){
                String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId(i.getId());
                obj.put("accountType", "undefined");
                if (wpUserMeta.contains("administrator")) obj.put("accountType", "admin");
                if (wpUserMeta.contains("anbieter")) obj.put("accountType", "ohne abo");
                if (wpUserMeta.contains("basis-anbieter")) obj.put("accountType", "basis");
                if (wpUserMeta.contains("basis-anbieter-plus")) obj.put("accountType", "basis-plus");
                if (wpUserMeta.contains("plus-anbieter")) obj.put("accountType", "plus");
                if (wpUserMeta.contains("premium-anbieter")) obj.put("accountType", "premium");
                if (wpUserMeta.contains("premium-anbieter-sponsoren")) obj.put("accountType", "sponsor");
            }else {
                obj.put( "accountType" ,"undefined");
            }
            obj.put("usedPotential", getPotentialByID(Math.toIntExact(i.getId()), (String) obj.get("accountType")));
            response.put(obj);
        }
        return response.toString();
    }

    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) throws IOException, URISyntaxException {

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

    //STATS


    //ToDo Clean
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
        try {
            viewsProfile = userStatsRepository.findByUserId(id).getProfileView();
        } catch (NullPointerException ignored) {
        }
        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();

        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();
        long viewsPresse = 0;
        List<Post> posts = postRepository.findByAuthor(id.intValue());

        List<Long> postTags = new ArrayList<>();
        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                PostStats Stat = statRepository.getStatByArtID(post.getId());
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : termTaxRepo.findByTermTaxonomyId(l)) {
                        if (termTax.getTermId() == tagIdBlog) {
                            viewsBlog = viewsBlog + Stat.getClicks();
                        }
                        if (termTax.getTermId() == tagIdArtikel) {
                            viewsArtikel = viewsArtikel + Stat.getClicks();
                        }
                        if (termTax.getTermId() == tagIdPresse) {
                            viewsPresse = viewsPresse + Stat.getClicks();
                        }}


                }
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("viewsBlog", viewsBlog);
        obj.put("viewsArtikel", viewsArtikel);
        obj.put("viewsPresse", viewsPresse);
        obj.put("viewsProfile", viewsProfile);
        return obj.toString();

    }

    @GetMapping("/getUserAveragesByType")
    public String getUserAveragesByType() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);


        for(WPUser user : userRepository.findAll()) {
            String userMeta = wpUserMetaRepository.getWPUserMetaValueByUserId(user.getId());
            if(userMeta.contains("basis-anbieter")) {
                counts.put("basis", counts.getInt("basis") + 1);
                try {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(user.getId()).getProfileView());
                } catch (Exception ignored) {}
            } else if(userMeta.contains("basis-anbieter-plus")) {
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
                try {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(user.getId()).getProfileView());
                } catch (Exception ignored) {}
            } else if(userMeta.contains("plus-anbieter")) {
                counts.put("plus", counts.getInt("plus") + 1);
                try {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(user.getId()).getProfileView());
                } catch (Exception ignored) {}
            } else if(userMeta.contains("premium-anbieter")) {
                counts.put("premium", counts.getInt("premium") + 1);
                try {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(user.getId()).getProfileView());
                } catch (Exception ignored) {}
            }
        }
        if(counts.getInt("basis") != 0) {
            averages.put("basis", clicks.getInt("basis") / counts.getInt("basis"));
        }
        if(counts.getInt("basis-plus") != 0) {
            averages.put("basis-plus", clicks.getInt("basis-plus") / counts.getInt("basis-plus"));
        }
        if(counts.getInt("plus") != 0) {
            averages.put("plus", clicks.getInt("plus") / counts.getInt("plus"));
        }
        if(counts.getInt("premium") != 0) {
            averages.put("premium", clicks.getInt("premium") / counts.getInt("premium"));
        }

        return averages.toString();

    }

    @GetMapping("/getClickTotalOnPostsOfUser")
    public int getClickTotalOnPostsOfUser (int uid){
        List<Post> posts= postRepository.findByAuthor(uid);
        int clicks = 0;
        for(Post post : posts) {
            if(post != null) {
                clicks += statRepository.getClicksByArtId(post.getId());
            }
        }

        return clicks;
    }

    @GetMapping("/getAccountTypeAll")
    public String getAccountTypeAll(){
        HashMap<String, Integer> counts = new HashMap<>();

        wpUserMetaRepository.getWpCapabilities().forEach(s -> {
                if (s.contains("um_anbieter")) // hoffe "leere" Accounts sind "um_anbieter" und nicht "anbieter" oder was auch immer
                    counts.put("Anbieter", counts.get("Anbieter") == null ? 1 : counts.get("Anbieter") + 1);
                if (s.contains("administrator"))
                    counts.put("Administrator", counts.get("Administrator") == null ? 1 : counts.get("Administrator") + 1);
                if ((s.contains("um_basis-anbieter") ) && !s.contains("plus"))
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

    @GetMapping("/getNewUsersAll")
    public String getNewUsersAll() throws JSONException {
        JSONObject obj = new JSONObject();

        Comparator<String> customComparator = Comparator.comparing(s -> s.charAt(0));
        List<String> ohne = getNewUserSchmarotzer();
        List<String> basis = getNewUsersByTypeToday("basis");
        List<String> basis_plus = getNewUsersByTypeToday("basis-plus");
        List<String> plus = getNewUsersByTypeToday("plus");
        List<String> premium = getNewUsersByTypeToday("premium");
        List<String> sponsor = getNewUsersByTypeToday("sponsor");
        ohne.sort(customComparator);
        basis.sort(customComparator);
        basis_plus.sort(customComparator);
        plus.sort(customComparator);
        premium.sort(customComparator);
        sponsor.sort(customComparator);

        obj.put("ohne", new JSONArray(ohne));
        obj.put("basis", new JSONArray(basis));
        obj.put("basis-plus", new JSONArray(basis_plus));
        obj.put("plus", new JSONArray(plus));
        obj.put("premium", new JSONArray(premium));
        obj.put("sponsor", new JSONArray(sponsor));
        return obj.toString();

    }

    public List<String> getNewUserSchmarotzer() {
        List<Long> listCheck = wpMemberRepo.getAllActiveMembersIds();
        List<String> listResponse = new ArrayList<>();
        for(WPUser user : userRepository.findAll()) {
            if(user.getRegistered().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT))) {
                if (!listCheck.contains(user.getId())) {
                    listResponse.add("+" + user.getDisplayName());
                }
            }
        }
        return listResponse;
    }

    public List<String> getNewUsersByType(String type) {
        List<String> list = new ArrayList<>();
        for(WPMemberships member : wpMemberRepo.getAllActiveMembers()) {
            listAddByType(type, list, member);
        }
        return list;
    }

    private void listAddByType(String type, List<String> list, WPMemberships member) {
        if(type.equals("basis-plus") &&  member.getMembership_id() == 7) {
            listAdd(list, member);
        } else if(type.equals("sponsor") && member.getMembership_id() == 6) {
            listAdd(list, member);
        } else if(type.equals("premium") && member.getMembership_id() == 5) {
            listAdd(list, member);
        } else if(type.equals("plus") && member.getMembership_id() == 3) {
            listAdd(list, member);
        } else if(type.equals("basis") && member.getMembership_id() == 1) {
            listAdd(list, member);
        }
    }

    private void listAdd(List<String> list, WPMemberships member) {
        if(userRepository.findById(member.getUser_id()).isPresent()) {
            switch (member.getStatus()) {
                case "active" -> list.add("+" + userRepository.findById(member.getUser_id()).get().getDisplayName());
                case "cancelled" -> list.add("-" + userRepository.findById(member.getUser_id()).get().getDisplayName());
                case "changed" -> list.add("&" + userRepository.findById(member.getUser_id()).get().getDisplayName());
            }
        }
    }

    public List<String> getNewUsersByTypeToday(String type) {
        List<String> list = new ArrayList<>();
        for(WPMemberships member : wpMemberRepo.getAllActiveMembers()) {
            if(member.getModified().after(uniRepo.getLatestUniStat().getDatum())) {
                listAddByType(type, list, member);
            }
        }
        return list;
    }



    @GetMapping("/getPostDistribution")
    public String getPostDistribution() throws JSONException {
        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();
        List<Post> posts = postRepository.findAllUserPosts();

        int artCount = 0;
        int blogCount = 0;
        int newsCount = 0;

        for(Post post : posts) {
            for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                for (WpTermTaxonomy termTax : termTaxRepo.findByTermTaxonomyId(l)) {
                    if (termTax.getTermId() == tagIdBlog) blogCount++;
                    if (termTax.getTermId() == tagIdArtikel) artCount++;
                    if (termTax.getTermId() == tagIdPresse) newsCount++;
                }
            }
        }



        JSONObject obj = new JSONObject();
        obj.put("blogCount", blogCount);
        obj.put("artCount", artCount);
        obj.put("newsCount", newsCount);
        return obj.toString();

    }

    @GetMapping("/hasPost")
    public boolean hasPost(@RequestParam int id) {
        return !postRepository.findByAuthor(id).isEmpty();
    }

    /**
     *
     * @param id  id des users.
     * @param accType  der account typ des users ("admin" | "plus" | "premium" | "sponsor")
     * @return
     */
    @GetMapping("/getPotentialByIDandType")
    public double getPotentialByID(int id, String accType) {

        //ToDo check when user potential posts are reset - and adjust following logic.
        switch (accType) {
            case "admin" -> {
                return 1;
            }
            case "plus" -> {
                return (double) (postRepository.findByAuthor(id).size()) / maxPostsPlus;
            }
            case "premium" -> {
                return (double) (postRepository.findByAuthor(id).size()) / maxPostsPremium;
            }
            case "sponsor" -> {
                return (double) (postRepository.findByAuthor(id).size()) / maxPostsSponsor;
            } default -> {
                return 0;
            }
        }
    }

    @GetMapping("/getUserViewsDistributedByHours")
    public String getUserViewsDistributedByHours(@RequestParam int userId,@RequestParam int daysback) throws JsonProcessingException {
        int latestUniId = uniRepo.getLatestUniStat().getId() - daysback;
        int previousUniId = latestUniId - 1;

        List<UserViewsByHourDLC> combinedViews = new ArrayList<>();
        combinedViews.addAll(userViewsRepo.findByUserIdAndUniId(userId, previousUniId)); // Daten von gestern
        combinedViews.addAll(userViewsRepo.findByUserIdAndUniId(userId, latestUniId));   // Daten von heute

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

}
