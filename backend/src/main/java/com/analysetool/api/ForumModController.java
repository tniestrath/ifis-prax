package com.analysetool.api;


import com.analysetool.services.ForumService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/forum", "/0wB4P2mly-xaRmeeDOj0_g/forum"}, method = RequestMethod.GET, produces = "application/json")
public class ForumModController {

    @Autowired
    private ForumService forumService;

    /**
     * Fetches all Unmoderated Forum posts.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
    @GetMapping("/getAllUnmoderated")
    public String getAllUnmoderated() throws JSONException {return forumService.getAllUnmoderated();}


    /**
     * Fetches all Moderated Forum posts.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
    @GetMapping("/getAllModerated")
    public String getAllModerated() throws JSONException {return forumService.getAllModerated();}


    /**
     * Fetches a page of unmoderated forum posts.
     * @param userId the user that is accessing the forum (acts as a filter).
     * @param filterForum the forum to search in (if applicable to user).
     * @param filterCat the category to search in (if applicable to forum and user).
     * @param filterTopic the topic to search in (if applicable to category, forum and user).
     * @param search searchs in titles emails etc.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
    @GetMapping("/getUnmoderatedWithFilter")
    public String getUnmoderatedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {return forumService.getUnmoderatedWithFilter(userId, filterForum, filterCat, filterTopic, search);}


    /**
     * Fetches a page of Moderated forum posts.
     * @param userId the user that is accessing the forum (acts as a filter).
     * @param filterForum the forum to search in (if applicable to user).
     * @param filterCat the category to search in (if applicable to forum and user).
     * @param filterTopic the topic to search in (if applicable to category, forum and user).
     * @param search searchs in titles emails etc.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
    @GetMapping("/getModeratedWithFilter")
    public String getModeratedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {return forumService.getModeratedWithFilter(userId, filterForum, filterCat, filterTopic, search);}

    /**
     * Fetches a posts values by id.
     * @param id the id of the post.
     * @return JSON-String containing values of it.
     * @throws JSONException .
     */
    @GetMapping("/getPostById")
    public String getPostById(long id) throws JSONException {return forumService.getPostById(id);}

    /**
     * Deletes the specified forum post by the authority of the given user.
     * @param id the id of the forum post to delete.
     * @param userId the userId exercising their authority.
     * @return whether the deletion worked as intended.
     */
    @PostMapping("/deleteById")
    public boolean deleteById(int id, int userId) {return forumService.deleteById(id, userId);}

    /**
     * Fetches all forum posts that are currently in the trash-can.
     * @return a JSON-String representation of all trashed posts.
     * @throws JSONException .
     */
    @GetMapping("/getAllTrashed")
    public String getAllTrashed() throws JSONException {return forumService.getAllTrashed();}

    /**
     * Restores a post from trashcan.
     * @param postId the postId to restore.
     * @return whether the restoring worked as intended.
     */
    @GetMapping("/restore")
    public boolean restoreById(int postId) {return forumService.restoreById(postId);}

    /**
     * Sets the moderation status of the selected post, using the given users' authority.
     * @param id the id of the post.
     * @param status the status to set (0 | 1)
     * @param userId the id of the user.
     * @return whether the method worked as intended.
     */
    @PostMapping("/setStatusById")
    public boolean setStatus(int id, int status, int userId) {return forumService.setStatus(id, status, userId);}

    /**
     * Fetches a JSON-String representation of all bad words from table.
     * @return a JSON-String.
     */
    @GetMapping("/getAllBadWords")
    public String getAllBadWords() {return forumService.getAllBadWords();}

    /**
     * Adds a bad word to table.
     * @param word the word to add.
     * @return whether the word was correctly added.
     */
    @PostMapping("/addBadWord")
    public boolean addBadWord(String word) {return forumService.addBadWord(word);}

    /**
     * Removes a bad word from table.
     * @param word the word to remove.
     * @return whether the word was found and removed.
     */
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

    /**
     * Fetches the Top 15 searched terms.
     * @return a JSON-String containing the values of the fetched terms.
     * @throws JSONException .
     */
    @GetMapping("/rankedSearchTermsTop15")
    public String getRankedSearchTermsTop15() throws JSONException {
        try{
            return forumService.getRankedSearchTop15();}catch(Exception e){
            e.printStackTrace();
            return "a bug a day keeps happiness away";
        }
    }

    /**
     * Saves edits to a posts contents.
     * @param hson the entire, edited posts as a JSON-String.
     * @param accepted whether the post is now shown.
     * @param userId the userId exercising their authority.
     * @return whether the post was updated.
     * @throws JSONException .
     */
    @PostMapping("/updatePost")
    public boolean updatePost(@RequestBody String hson, boolean accepted, int userId) throws JSONException {return forumService.updatePost(hson, accepted, userId);}

    /**
     * Fetches the link to the forum-post on the website, for the specified post.
     * @param postId the post to fetch for.
     * @return the URL to the specified post.
     */
    @GetMapping("/getLinkToPost")
    public String getLinkToPost(long postId) {return forumService.getLinkToPost(postId);}

    /**
     * Fetches the link to the forum-topic on the website, for the specified post.
     * @param postId the post to fetch for.
     * @return the URL to the specified posts' topic.
     */
    @GetMapping("/getLinkToTopic")
    public String getLinkToTopic(long postId) {return forumService.getLinkToTopic(postId);}

    /**
     * Fetches the link to the forum on the website, for the specified post.
     * @param postId the post to fetch for.
     * @return the URL to the specified posts' forum.
     */
    @GetMapping("/getLinkToForum")
    public String getLinkToForum(long postId) {return forumService.getLinkToForum(postId);}

    /**
     * Fetches links to a posts forum, topic and post on the forum itself.
     * @param id the id of the post.
     * @return a JSON-Array-String containing links to forum, topic and post in order.
     */
    @GetMapping("/getLinks")
    public String getLinksAll(long id) {return forumService.getLinksAll(id);}

    /**
     * Fetches overarching, general statistics of the forum.
     * @return a JSON-Object-String containing the amount of posts in different kinds of areas.
     * @throws JSONException .
     */
    @GetMapping("/getCounts")
    public String getCounts() throws JSONException {return forumService.getCounts();}

    /**
     * Locks a post from edit for all except the specified user.
     * @param postId the post to lock.
     * @param userId the user to remain access to the post.
     * @return whether the post was correctly locked.
     */
    @GetMapping("/isLocked")
    public boolean lock(int postId, int userId) {return forumService.lock(postId, userId);}

    /**
     * Unlocks a post, making it open to edit for everyone.
     * @param postId the post to unlock.
     * @param userId the user to unlock the post with.
     * @return whether the post was correctly unlocked.
     */
    @GetMapping("/unlock")
    public boolean unlock(int postId, int userId) {return forumService.unlock(postId, userId);}

    /**
     * Unlocks all posts currently locked by the user.
     * @param userId the user to unlock all for.
     * @return whether the posts were correctly unlocked.
     */
    @GetMapping("/unlockAll")
    public boolean unlockAllForUser(int userId) {return forumService.unlockAllForUser(userId);}

    /**
     * Fetches the amount of unmoderated posts in all forums and all their subcategories.
     * @param userId the user to fetch for (also acts as a filter, users only see their forums).
     * @return a JSON-String.
     * @throws JSONException .
     */
    @GetMapping("/getModCounts")
    public String getModCounts(Integer userId) throws JSONException {return forumService.getModCounts(userId);}

    @GetMapping("/addModeratorToForum")
    public boolean addModToForum(int newModId, int forumId, HttpServletRequest request) {
        return forumService.addModToForum(newModId, forumId, request);
    }

}
