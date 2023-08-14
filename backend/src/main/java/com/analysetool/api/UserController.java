package com.analysetool.api;
import com.analysetool.Application;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;

import com.analysetool.util.DashConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.text.ParseException;
import java.util.*;

import static com.analysetool.util.MapHelper.*;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping("/users")
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
            if(userStatsRepository.existsByUserId(i.getId())){
                UserStats statsUser = userStatsRepository.findByUserId(i.getId());
                obj.put("id",i.getId());
                obj.put("email",i.getEmail());
                obj.put("displayName",i.getDisplayName());
                obj.put("profileViews ", statsUser.getProfileView());
                obj.put("postViews", postController.getViewsOfUserById(i.getId()));
                obj.put("postCount", postController.getPostCountOfUserById(i.getId()));
                obj.put ("performance",statsUser.getAveragePerformance());

            }
            if (wpUserMetaRepository.existsByUserId(i.getId())){
                String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId(i.getId());
                if (wpUserMeta.contains("customer")) obj.put("accountType", "?customer?");
                if (wpUserMeta.contains("administrator")) obj.put("accountType", "admin");
                if (wpUserMeta.contains("anbieter")) obj.put("accountType", "basic");
                if (wpUserMeta.contains("plus-anbieter")) obj.put("accountType", "plus");
                if (wpUserMeta.contains("premium-anbieter")) obj.put("accountType", "premium");
            }
            else {obj.put("id",i.getId());
                obj.put("email",i.getEmail());
                obj.put("displayName",i.getDisplayName());
                obj.put( "accountType" ,"undefined");
                obj.put("profileViews ", 0);
                obj.put("postViews",0);
                obj.put("postCount",0);
                obj.put ("performance",0);

            }
            response.put(obj);
        }
        return response.toString();
    }

    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) throws IOException, URISyntaxException {
        String path = String.valueOf(Paths.get(config.getProfilephotos() +"/"+id+"/profile_photo.jpg"));
        File cutePic = new File(path);
        if (!cutePic.exists()) {
            cutePic = new File(Paths.get(Objects.requireNonNull(Application.class.getClassLoader().getResource("user_img/404_img.jpg")).toURI()).toUri());
        }
        byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    //STATS


    //ToDo Clean
    @GetMapping("/{userId}")
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }

    //ToDo Clean
    @GetMapping("/getUserStats")
    public String getUserStat(@RequestParam Long id) throws JSONException {
        JSONObject obj = new JSONObject();
        UserStats user = userStatsRepository.findByUserId(id);
        obj.put("Interaktionsrate",user.getInteractionRate());
        obj.put("Average Performance",user.getAveragePerformance());
        obj.put("Average Relevance",user.getAverageRelevance());
        obj.put("Postfrequenz",user.getPostFrequence());
        obj.put("Profilaufrufe",user.getProfileView());
        return obj.toString();
    }

    @GetMapping("/getViewsBrokenDown")
    public String getViewsBrokenDown(@RequestParam Long id) throws JSONException {
        long viewsBlog = 0;
        long viewsArtikel = 0;
        long viewsProfile = userStatsRepository.findByUserId(id).getProfileView();
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

    //ToDo Move method somewhere
    @GetMapping("/getAllViewsByLocation")
    public String getAllViewsByLocation() {
        List<HashMap> posts = statRepository.getAllViewsByLocation();
        HashMap map = new HashMap<>();
        for(HashMap locMap : posts) {
            if(locMap != null) {
                mergeLocationMaps(map, locMap);
            }

        }
        return new JSONObject(map).toString();
    }

    @GetMapping("/getViewsByLocation")
    public String getViewsByLocation(@RequestParam int id) throws JSONException, ParseException, JsonProcessingException {
        List<Post> posts= postRepository.findByAuthor(id);
        HashMap map = new HashMap<>();
        int count = 0;
        for(Post post : posts) {
            if(statRepository.getViewsByLocation(post.getId().intValue()) != null) {
                if (count == 0) {
                    map = statRepository.getViewsByLocation(post.getId().intValue());
                    count++;
                } else {
                    mergeLocationMaps(map, statRepository.getViewsByLocation(post.getId().intValue()));
                }
            }

        }
        return new JSONObject(map).toString();
    }

    @GetMapping("/getViewsPerHour")
    public String getViewsPerHour(@RequestParam int id) throws JSONException, ParseException, JsonProcessingException {
        List<Post> posts= postRepository.findByAuthor(id);
        HashMap map = new HashMap<>();
        int count = 0;
        for(Post post : posts) {
            if(statRepository.getViewsByLocation(post.getId().intValue()) != null) {
                if (count == 0) {
                    map = statRepository.getViewsPerHour(post.getId().intValue());
                    count++;
                } else {
                    mergeTimeMaps(map, statRepository.getViewsPerHour(post.getId().intValue()));
                }
            }

        }
        return map.values().toString();
    }

    //ToDo Move method somewhere
    @GetMapping("/getAllViewsPerHour")
    public String getAllViewsPerHour() {
        List<HashMap> posts = statRepository.getAllViewsPerHour();

        HashMap map = new HashMap<>();
        for(HashMap locMap : posts) {
            if(locMap != null) {
                mergeTimeMaps(map, locMap);
            }

        }
        Long[] orderedViews = new Long[24];
        for(int i=0; i<24; i++) {
            orderedViews[i] = (Long) map.get("" + i);
        }
        return Arrays.toString(orderedViews);
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
}
