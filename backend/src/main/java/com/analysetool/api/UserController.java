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
import org.springframework.data.domain.PageRequest;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Deprecated
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
            response.put(obj);
        }
        return response.toString();
    }

    @GetMapping("/getAll")
    public String getAll(Integer page, Integer size, String search, String filter) throws JSONException {
        List<WPUser> list = userRepository.getAllByNicenameContaining(search, PageRequest.of(page, size));
        JSONArray response = new JSONArray();

        for(WPUser user : list) {
            if(getType(Math.toIntExact(user.getId())).equals(filter) || filter == null) {
                JSONObject obj = new JSONObject();
                obj.put("id", user.getId());
                obj.put("email", user.getEmail());
                obj.put("displayName", user.getNicename());
                if (userStatsRepository.existsByUserId(user.getId())) {
                    UserStats statsUser = userStatsRepository.findByUserId(user.getId());
                    obj.put("profileViews", statsUser.getProfileView());
                    obj.put("postViews", postController.getViewsOfUserById(user.getId()));
                    obj.put("postCount", postController.getPostCountOfUserById(user.getId()));

                } else {
                    obj.put("profileViews", 0);
                    obj.put("postViews", 0);
                    obj.put("postCount", 0);
                    obj.put("performance", 0);
                }
                obj.put("accountType", getType(Math.toIntExact(user.getId())));
                response.put(obj);
            }
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
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

    /**
     *
     * @param id user id to fetch account type for.
     * @return "basis" "plus" "premium" "sponsor" "basis-plus" "admin"
     */
    @GetMapping("/getTypeById")
    public String getType(int id) {
        if(wpMemberRepo.getUserMembership(id) != null) {
            switch (wpMemberRepo.getUserMembership(id)) {
                case (1) -> {
                    return "basis";
                }
                case (3) -> {
                    return "plus";
                }
                case (5) -> {
                    return "premium";
                }
                case (6) -> {
                    return "sponsor";
                }
                case (7) -> {
                    return "basis-plus";
                }
            }
        }

        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "none";
            if (wpUserMeta.contains("administrator")) return "admin";
            if (wpUserMeta.contains("plus-anbieter")) return "plus";
            if (wpUserMeta.contains("premium-anbieter")) return "premium";
            if (wpUserMeta.contains("anbieter")) return "basis";
        }


        return "none";
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
     * @param userId  id des users.
     * @return
     */
    @GetMapping("/getPotentialById")
    public String getPotentialByID(int userId) throws JSONException {

        String type = this.getType(userId);
        //Check whether these profile parts have been filled out.
        boolean hasProfilePic = wpUserMetaRepository.getProfilePath(((long) userId)).isPresent() && !wpUserMetaRepository.getProfilePath((long)((long) userId)).equals("https://it-sicherheit.de/wp-content/uploads/2023/06/it-sicherheit-logo_icon_190x190.png");
        boolean hasCover = wpUserMetaRepository.getCoverPath((long) userId).isPresent();
        boolean hasDescription = wpUserMetaRepository.getDescription((long) userId).isPresent();
        boolean hasSlogan = !type.equals("basis") && wpUserMetaRepository.getSlogan((long) userId).isPresent();

        //Check how many internal contacts have been filled.
        int countAnsprechpartnerIntern = 0;
        int maxAnsprechpartnerIntern = 3;
        if(wpUserMetaRepository.getPersonIntern((long) userId).isPresent() && !wpUserMetaRepository.getPersonIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;
        if(wpUserMetaRepository.getMailIntern((long) userId).isPresent() && !wpUserMetaRepository.getMailIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;
        if(wpUserMetaRepository.getTelIntern((long) userId).isPresent() && !wpUserMetaRepository.getTelIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;

        //Check how many external contacts have been filled.
        int countKontaktExtern = 0;
        int maxKontaktExtern = 7;
        if(wpUserMetaRepository.getNameExtern((long) userId).isPresent()  && !wpUserMetaRepository.getNameExtern((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getSecondaryMail((long) userId).isPresent() && !wpUserMetaRepository.getSecondaryMail((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getTelExtern((long) userId).isPresent() && !wpUserMetaRepository.getTelExtern((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdresseStreet((long) userId).isPresent() && !wpUserMetaRepository.getAdresseStreet((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdressePLZ((long) userId).isPresent() && !wpUserMetaRepository.getAdressePLZ((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdresseOrt((long) userId).isPresent() && !wpUserMetaRepository.getAdresseOrt((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getURLExtern((long) userId).isPresent() && !wpUserMetaRepository.getURLExtern((long) userId).get().isEmpty()) countKontaktExtern++;

        //Check how many tags are allowed, and how many are set.
        int allowedTags = 0;
        int allowedLosungen = 0;
        switch (type) {
            case "basis" -> {
                allowedTags = 1;
                maxKontaktExtern = 6;
            }
            case "basis-plus" -> {
                allowedTags = 3;
            }
            case "plus" -> {
                allowedTags = 8;
                allowedLosungen = 5;
            }
            case "premium" -> {
                allowedTags = 12;
                allowedLosungen = 12;
            }
            case "admin" -> {
                allowedTags = 100;
                allowedLosungen = 100;
            }
        }


        int countTags = 0;
        Matcher matcher = null;
        try {
            if(wpUserMetaRepository.getTags((long) userId).isPresent()) {
                matcher = Pattern.compile(";i:(\\d+);").matcher(wpUserMetaRepository.getTags((long) userId).get());
            }
        } catch (Exception ignored) {}
        while(matcher != null && matcher.find()) {
            countTags++;
        }

        //Check how many solutions are allowed, and how many are set.
        int solutions = 0;
        for(int i = 0; i < allowedLosungen; i++) {
            switch(i) {
                case(0) -> {
                    if(wpUserMetaRepository.getSolutionHead1((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead1((long) userId).get().isEmpty()) solutions ++;
                }
                case(1) -> {
                    if(wpUserMetaRepository.getSolutionHead2((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead2((long) userId).get().isEmpty()) solutions ++;
                }
                case(2) -> {
                    if(wpUserMetaRepository.getSolutionHead3((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead3((long) userId).get().isEmpty()) solutions ++;
                }
                case(3) -> {
                    if(wpUserMetaRepository.getSolutionHead4((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead4((long) userId).get().isEmpty()) solutions ++;
                }
                case(4) -> {
                    if(wpUserMetaRepository.getSolutionHead5((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead5((long) userId).get().isEmpty()) solutions ++;
                }
                case(5) -> {
                    if(wpUserMetaRepository.getSolutionHead6((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead6((long) userId).get().isEmpty()) solutions ++;
                }
                case(6) -> {
                    if(wpUserMetaRepository.getSolutionHead7((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead7((long) userId).get().isEmpty()) solutions ++;
                }
                case(7) -> {
                    if(wpUserMetaRepository.getSolutionHead8((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead8((long) userId).get().isEmpty()) solutions ++;
                }
                case(8) -> {
                    if(wpUserMetaRepository.getSolutionHead9((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead9((long) userId).get().isEmpty()) solutions ++;
                }
                case(9) -> {
                    if(wpUserMetaRepository.getSolutionHead10((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead10((long) userId).get().isEmpty()) solutions ++;
                }
                case(10) -> {
                    if(wpUserMetaRepository.getSolutionHead11((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead11((long) userId).get().isEmpty()) solutions ++;
                }
                case(11) -> {
                    if(wpUserMetaRepository.getSolutionHead12((long) userId).isPresent() && wpUserMetaRepository.getSolutionHead12((long) userId).get().isEmpty()) solutions ++;
                }
            }
        }

        //Check how many company datafields have been filled.
        int companyDetails = 0;
        int companyDetailsMax = 4;
        if(wpUserMetaRepository.getCompanyCategory((long) userId).isPresent() && !wpUserMetaRepository.getCompanyCategory((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getManager((long) userId).isPresent() && !wpUserMetaRepository.getManager((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getCompanyEmployees((long) userId).isPresent() && !wpUserMetaRepository.getCompanyEmployees((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getService((long) userId).isPresent() && !wpUserMetaRepository.getService((long) userId).get().isEmpty()) companyDetails++;


        JSONObject json = new JSONObject();
        json.put("profilePicture", hasProfilePic ? 1 : 0);
        json.put("titlePicture", hasCover ? 1 : 0);
        json.put("bio", hasDescription ? 1 : 0);
        json.put("slogan", hasSlogan ? 1 : 0);
        json.put("tagsCount", countTags);
        json.put("tagsMax", allowedTags);
        json.put("bio", hasDescription ? 1 : 0);
        json.put("contactPublic", countKontaktExtern);
        json.put("contactPublicMax", maxKontaktExtern);
        json.put("contactIntern", countAnsprechpartnerIntern);
        json.put("contactInternMax", maxAnsprechpartnerIntern);
        json.put("companyDetails", companyDetails);
        json.put("companyDetailsMax", companyDetailsMax);
        json.put("solutions", solutions);
        json.put("solutionsMax", allowedLosungen);

        return json.toString();
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
