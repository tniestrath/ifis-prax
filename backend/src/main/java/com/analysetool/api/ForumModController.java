package com.analysetool.api;


import com.analysetool.services.ForumService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/forum", "/0wB4P2mly-xaRmeeDOj0_g/forum"}, method = RequestMethod.GET, produces = "application/json")
public class ForumModController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/getAllUnmoderated")
    public String getAllUnmoderated() throws JSONException {return forumService.getAllUnmoderated();}

    @GetMapping("/getAllModerated")
    public String getAllModerated() throws JSONException {return forumService.getAllModerated();}


    @GetMapping("/getUnmoderatedWithFilter")
    public String getUnmoderatedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {return forumService.getUnmoderatedWithFilter(userId, filterForum, filterCat, filterTopic, search);}


    @GetMapping("/getModeratedWithFilter")
    public String getModeratedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {return forumService.getModeratedWithFilter(userId, filterForum, filterCat, filterTopic, search);}

    @GetMapping("/getPostById")
    public String getPostById(long id) throws JSONException {return forumService.getPostById(id);}


    @PostMapping("/deleteById")
    public boolean deleteById(int id, int userId) {return forumService.deleteById(id, userId);}


    @GetMapping("/getAllTrashed")
    public String getAllTrashed() throws JSONException {return forumService.getAllTrashed();}

    @GetMapping("/restore")
    public boolean restoreById(int postId) {return forumService.restoreById(postId);}

    @PostMapping("/setStatusById")
    public boolean setStatus(int id, int status, int userId) {return forumService.setStatus(id, status, userId);}

    @GetMapping("/getAllBadWords")
    public String getAllBadWords() {return forumService.getAllBadWords();}

    @PostMapping("/addBadWord")
    public boolean addBadWord(String word) {return forumService.addBadWord(word);}

    @PostMapping("/removeBadWord")
    public boolean removeBadWord(String word) {return forumService.removeBadWord(word);}

    /**
     * Get ranked forum discussions based on clicks.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked forum discussions
     */
    @GetMapping("/rankedDiscussions")
    public String getRankedDiscussions(@RequestParam int page, @RequestParam int size)  {
        try{
        return forumService.getRankedDiscussion(page, size);}catch(Exception e){
            e.printStackTrace();
            return "buggy bug bug";
        }
    }

    /**
     * Get ranked forum topics based on clicks.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked forum topics
     * @throws JSONException if a JSON error occurs
     */
    @GetMapping("/rankedTopics")
    public String getRankedTopics(@RequestParam int page, @RequestParam int size) {
        try{
        return forumService.getRankedTopic(page, size);}catch(Exception e){
            e.printStackTrace();
            return "sir bugs-alot";
        }
    }

    /**
     * Get ranked search terms based on frequency.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked search terms
     * @throws JSONException if a JSON error occurs
     */
    @GetMapping("/rankedSearchTerms")
    public String getRankedSearchTerms(@RequestParam int page, @RequestParam int size)  {
        try{
        return forumService.getRankedSearchTerms(page, size);}catch(Exception e){
            e.printStackTrace();
            return "a bug a day keeps happiness away";
        }
    }

    @GetMapping("/rankedSearchTermsTop15")
    public String getRankedSearchTermsTop15() throws JSONException {
        try{
            return forumService.getRankedSearchTop15();}catch(Exception e){
            e.printStackTrace();
            return "a bug a day keeps happiness away";
        }
    }

    @PostMapping("/updatePost")
    public boolean updatePost(@RequestBody String hson, boolean accepted, int userId) throws JSONException {return forumService.updatePost(hson, accepted, userId);}

    @GetMapping("/getLinkToPost")
    public String getLinkToPost(long postId) {return forumService.getLinkToPost(postId);}

    @GetMapping("/getLinkToTopic")
    public String getLinkToTopic(long postId) {return getLinkToTopic(postId);}

    @GetMapping("/getLinkToForum")
    public String getLinkToForum(long postId) {return forumService.getLinkToForum(postId);}

    @GetMapping("/getLinks")
    public String getLinksAll(long id) {return forumService.getLinksAll(id);}

    @GetMapping("/getCounts")
    public String getCounts() throws JSONException {return forumService.getCounts();}

    @GetMapping("/isLocked")
    public boolean lock(int postId, int userId) {return forumService.lock(postId, userId);}

    @GetMapping("/unlock")
    public boolean unlock(int postId, int userId) {return forumService.unlock(postId, userId);}

    private boolean isLockedForUser(int postId, int userId) {return forumService.isLockedForUser(postId, userId);}

    @GetMapping("/unlockAll")
    public boolean unlockAllForUser(int userId) {return forumService.unlockAllForUser(userId);}

    @GetMapping("/getModCounts")
    public String getModCounts(Integer userId) throws JSONException {return forumService.getModCounts(userId);}

}
