package com.analysetool.services;

import com.analysetool.modells.Post;
import com.analysetool.modells.PostNotifications;
import com.analysetool.modells.Subscriptions;
import com.analysetool.modells.UserSubscriptions;
import com.analysetool.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    WpTermTaxonomyRepository termTaxRepo;
    @Autowired
    SubscriptionsRepository subRepo;
    @Autowired
    UserSubscriptionsRepository userSubRepo;
    @Autowired
    LoginService loginService;
    @Autowired
    AuthorRelationshipRepository authorRelRepo;
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepo;
    @Autowired
    AuthorRelationshipRepository authorsRepo;
    @Autowired
    WPTermRepository termRepo;
    @Autowired
    PostNotificationsRepository postNotificationsRepo;


    /**
     * Adds a Subscription to the logged in user, that notifies him on any post releases that fit the given criteria.
     * Not adding a parameter will omit it, and only use the remaining criteria.
     * @param type (Artikel | Blog | News | Video  Podcast) | null
     * @param thema Any tags slug.
     * @param author any authors slug.
     * @param word any String.
     * @param request not manually filled, used to validate userid.
     * @return true if successful, otherwise false.
     */
    public boolean subCustom(String type, String thema, String author, String word, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }


        if(type != null) {
            if (!(type.equals("Artikel") || type.equals("Blog") || type.equals("News") || type.equals("Whitepaper") || type.equals("Video") || type.equals("Podcast"))) {
                if (type.equals("none") || type.isBlank()) type = null;
                else return false;
            }
        }

        Integer tagId = null;
        if(thema != null && !thema.isBlank())  tagId = Math.toIntExact(termTaxRepo.getPostTagBySlug(thema).getTermId());

        Long authorId = 0L;
        if(authorRelRepo.findByAuthorSlugFirst(author).isPresent()) {
            authorId = authorRelRepo.findByAuthorSlugFirst(author).get().getAuthorTerm();
        }
        if(authorId == 0) authorId = null;

        if(word.isBlank() || word.equals("none")) word = null;

        if(subRepo.findByAll(type, tagId, authorId, word).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAll(type, tagId, authorId, word).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByAll(type, tagId, authorId, word).get().getId());
                userSub.setUserId(userid);
                userSubRepo.save(userSub);
            }
        } else {
            //Make subscription, add user
            Subscriptions sub = new Subscriptions();
            sub.setAuthor(authorId);
            sub.setTag(tagId);
            sub.setType(type);
            sub.setWord(word);
            subRepo.save(sub);

            UserSubscriptions userSub = new UserSubscriptions();
            userSub.setSubId(sub.getId());
            userSub.setUserId(userid);
            userSubRepo.save(userSub);
        }
        return true;
    }

    public boolean unsubscribe(String type, String thema, String author, String word, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }


        if(type != null) {
            if (!(type.equals("Artikel") || type.equals("Blog") || type.equals("News") || type.equals("Whitepaper") || type.equals("Video") || type.equals("Podcast"))) {
                if (type.equals("none") || type.isBlank()) type = null;
                else return false;
            }
        }

        Integer tagId = null;
        if(thema != null && !thema.isBlank())  tagId = Math.toIntExact(termTaxRepo.getPostTagBySlug(thema).getTermId());

        Long authorId = 0L;
        if(authorRelRepo.findByAuthorSlugFirst(author).isPresent()) {
            authorId = authorRelRepo.findByAuthorSlugFirst(author).get().getAuthorTerm();
        }
        if(authorId == 0) authorId = null;

        if(word.isBlank() || word.equals("none")) word = null;

        if(subRepo.findByAll(type, tagId, authorId, word).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAll(type, tagId, authorId, word).get().getId()).isPresent()) {
                //User is subscribed, so unsubscribe him.

                userSubRepo.delete(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAll(type, tagId, authorId, word).get().getId()).get());

                if(userSubRepo.findBySubId(subRepo.findByAll(type, tagId, authorId, word).get().getId()).isEmpty()) {
                    //If no one is subscribed to this, delete it.
                    subRepo.delete(subRepo.findByAll(type, tagId, authorId, word).get());
                }
                return true;
            }
        }
        return true;
    }

    @Scheduled(cron = "0 0 8-18 * * *")
    public void sendNotifications() {
        List<UserSubscriptions> userSubs = userSubRepo.findAll();
        for(Post post : postRepo.findAllUnnotified()) {
            //Find all relevant information about the new post
            String type = postService.getType(post.getId());
            List<Long> tags = postService.getTagsById(post.getId());
            List<Long> authors = authorsRepo.findAuthorsTermIdsByPostId(post.getId());
            String title = post.getTitle();
            List<String> terms = termRepo.getNamesFromList(tags);

            for(UserSubscriptions userSub : userSubs) {
                PostNotifications noti = new PostNotifications();
                if(subRepo.findById(userSub.getSubId()).isPresent()) {
                    Subscriptions sub = subRepo.findById(userSub.getSubId()).get();
                    //Check whether the criteria of any user subscription fit.
                    if((sub.getType() == null || sub.getType().equals(type))
                            && (sub.getTag() == null || tags.contains((long) sub.getTag()))
                            && (sub.getAuthor() == null || authors.contains(sub.getAuthor()))
                            && (sub.getWord() == null || (title.toLowerCase().contains(sub.getWord().toLowerCase()) || terms.contains(sub.getWord())))
                    ) {
                        //Since the criteria fit, notify user
                        noti.setNotificationSent(true);
                    } else {
                        noti.setNotificationSent(false);
                    }
                    noti.setPostId(post.getId());
                    noti.setTime(Timestamp.valueOf(LocalDateTime.now()));
                    postNotificationsRepo.save(noti);
                }
            }

        }

    }


}
