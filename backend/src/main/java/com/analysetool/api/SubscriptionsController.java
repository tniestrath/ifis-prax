package com.analysetool.api;

import com.analysetool.modells.Subscriptions;
import com.analysetool.modells.UserSubscriptions;
import com.analysetool.repositories.AuthorRelationshipRepository;
import com.analysetool.repositories.SubscriptionsRepository;
import com.analysetool.repositories.UserSubscriptionsRepository;
import com.analysetool.repositories.WpTermTaxonomyRepository;
import com.analysetool.services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/subscribe", "/0wB4P2mly-xaRmeeDOj0_g/subscribe"}, method = RequestMethod.GET, produces = "application/json")
public class SubscriptionsController {

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

    @GetMapping("/thema")
    public boolean subscribeThema(String thema, HttpServletRequest request) {
        long tagId = termTaxRepo.getPostTagBySlug(thema).getTermId();

        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }

        if(subRepo.findByTag(tagId).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByTag(tagId).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByTag(tagId).get().getId());
                userSub.setUserId(userid);
                userSubRepo.save(userSub);
            }
        } else {
            //Make subscription, add user
            Subscriptions sub = new Subscriptions();
            sub.setAuthor(null);
            sub.setTag((int) tagId);
            sub.setType(null);
            sub.setWord(null);
            subRepo.save(sub);

            UserSubscriptions userSub = new UserSubscriptions();
            userSub.setSubId(sub.getId());
            userSub.setUserId(userid);
            userSubRepo.save(userSub);
        }
        return true;
    }

    @GetMapping("/type")
    public boolean subscribeType(String type, HttpServletRequest request) {

        if(!(type.equals("Artikel") || type.equals("Blog") || type.equals("News") || type.equals("Whitepaper") || type.equals("Video") || type.equals("Podcast"))) {
            return false;
        }

        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }

        if(subRepo.findByType(type).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByType(type).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByType(type).get().getId());
                userSub.setUserId(userid);
                userSubRepo.save(userSub);
            }
        } else {
            //Make subscription, add user
            Subscriptions sub = new Subscriptions();
            sub.setAuthor(null);
            sub.setTag(null);
            sub.setType(type);
            sub.setWord(null);
            subRepo.save(sub);

            UserSubscriptions userSub = new UserSubscriptions();
            userSub.setSubId(sub.getId());
            userSub.setUserId(userid);
            userSubRepo.save(userSub);
        }
        return true;
    }

    @GetMapping("/word")
    public boolean subscribeWord(String word, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;

        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }

        if(subRepo.findByWord(word).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByWord(word).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByWord(word).get().getId());
                userSub.setUserId(userid);
                userSubRepo.save(userSub);
            }
        } else {
            //Make subscription, add user
            Subscriptions sub = new Subscriptions();
            sub.setAuthor(null);
            sub.setTag(null);
            sub.setType(null);
            sub.setWord(word);
            subRepo.save(sub);

            UserSubscriptions userSub = new UserSubscriptions();
            userSub.setSubId(sub.getId());
            userSub.setUserId(userid);
            userSubRepo.save(userSub);
        }
        return true;
    }

    @GetMapping("/author")
    public boolean subscribeAuthor(String author, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        long authorId = 0;
        if(authorRelRepo.findByAuthorSlugFirst(author).isPresent()) {
            authorId = authorRelRepo.findByAuthorSlugFirst(author).get().getAuthorTerm();
        }
        if(authorId == 0) return false;

        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }

        if(subRepo.findByAuthor(authorId).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAuthor(authorId).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByAuthor(authorId).get().getId());
                userSub.setUserId(userid);
                userSubRepo.save(userSub);
            }
        } else {
            //Make subscription, add user
            Subscriptions sub = new Subscriptions();
            sub.setAuthor(authorId);
            sub.setTag(null);
            sub.setType(null);
            sub.setWord(null);
            subRepo.save(sub);

            UserSubscriptions userSub = new UserSubscriptions();
            userSub.setSubId(sub.getId());
            userSub.setUserId(userid);
            userSubRepo.save(userSub);
        }
        return true;
    }

    @GetMapping("/custom")
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
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAuthor(authorId).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByAuthor(authorId).get().getId());
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

}
