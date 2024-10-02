package com.analysetool.api;

import com.analysetool.modells.Subscriptions;
import com.analysetool.modells.UserSubscriptions;
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

}
