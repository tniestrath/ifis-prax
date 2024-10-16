package com.analysetool.api;

import com.analysetool.services.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/subscribe", "/0wB4P2mly-xaRmeeDOj0_g/subscribe"}, method = RequestMethod.GET, produces = "application/json")
public class SubscriptionsController {

    @Autowired
    private SubscriptionService subService;

    @GetMapping("/custom")
    public boolean subCustom(String type, String thema, String author, String word, HttpServletRequest request) {return subService.subCustom(type, thema, author, word, request);}

    @GetMapping("/unsubscribe")
    public boolean unsubscribe(String type, String thema, String author, String word, HttpServletRequest request) {return subService.unsubscribe(type, thema, author, word, request);}

    @GetMapping("/sendMailTest")
    public boolean sendMailTest() {
        return subService.sendMailsNotifications();
    }

    @GetMapping("/unsubscribeAll")
    public String unsubscribeAll(long userId) {return subService.unsubscribeAll(userId);}

}
