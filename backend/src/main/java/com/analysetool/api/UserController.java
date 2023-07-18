package com.analysetool.api;
import com.analysetool.Application;
import com.analysetool.modells.*;
import com.analysetool.repositories.*;

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
import java.text.ParseException;
import java.util.*;

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
        String path = Paths.get(Objects.requireNonNull(Application.class.getClassLoader().getResource("user_img")).toURI()).toString() +"/"+id+"_profile_photo.jpg";
        File cutePic = new File(path);
        if (!cutePic.exists()) {
            cutePic = new File(Paths.get(Objects.requireNonNull(Application.class.getClassLoader().getResource("user_img/404_img.jpg")).toURI()).toUri());
        }
        byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    //STATS



    @GetMapping("/{userId}")
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }

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

        int tagIdPresse = termRepo.findBySlug("pressemitteilung").getId().intValue();
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
    @GetMapping("/getAllViewsByLocation")
    public String getAllViewsByLocation() {
        List<Post> posts= postRepository.findPublishedPosts();
        HashMap map = new HashMap<>();
        int count = 0;
        for(Post post : posts) {
            if(statRepository.getViewsByLocation(post.getId().intValue()) != null) {
                if (count == 0) {
                    map = statRepository.getViewsByLocation(post.getId().intValue());
                    count++;
                } else {
                    mergeMaps(map, statRepository.getViewsByLocation(post.getId().intValue()));
                }
            }

        }
        System.out.println(new JSONObject(map).toString());
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
                    mergeMaps(map, statRepository.getViewsByLocation(post.getId().intValue()));
                }
            }

        }
        System.out.println(new JSONObject(map).toString());
        return new JSONObject(map).toString();

    }
    private static void mergeMaps(Map<String, Map<String, Map<String, Long>>> map1, Map<String, Map<String, Map<String, Long>>> map2) {
        for (Map.Entry<String, Map<String, Map<String, Long>>> outerEntry : map2.entrySet()) {
            String outerKey = outerEntry.getKey();
            Map<String, Map<String, Long>> innerMap2 = outerEntry.getValue();
            if (map1.containsKey(outerKey)) {
                Map<String, Map<String, Long>> innerMap1 = map1.get(outerKey);
                mergeInnerMaps(innerMap1, innerMap2);
            } else {
                map1.put(outerKey, innerMap2);
            }
        }
    }

    private static void mergeInnerMaps(Map<String, Map<String, Long>> innerMap1, Map<String, Map<String, Long>> innerMap2) {
        for (Map.Entry<String, Map<String, Long>> innerEntry : innerMap2.entrySet()) {
            String innerKey = innerEntry.getKey();
            Map<String, Long> innermostMap2 = innerEntry.getValue();
            if (innerMap1.containsKey(innerKey)) {
                Map<String, Long> innermostMap1 = innerMap1.get(innerKey);
                mergeInnermostMaps(innermostMap1, innermostMap2);
            } else {
                innerMap1.put(innerKey, innermostMap2);
            }
        }
    }

    private static void mergeInnermostMaps(Map<String, Long> innermostMap1, Map<String, Long> innermostMap2) {
        for (Map.Entry<String, Long> entry : innermostMap2.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            innermostMap1.merge(key, value, Long::sum);
        }
    }
}
