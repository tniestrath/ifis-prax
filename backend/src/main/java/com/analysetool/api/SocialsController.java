package com.analysetool.api;

import com.analysetool.services.SocialsService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/socials", "/0wB4P2mly-xaRmeeDOj0_g/socials"}, method = RequestMethod.GET, produces = "application/json")
public class SocialsController {

    @Autowired
    SocialsService socialsService;

    /**
     * Fetch the sum of all incoming social media redirects.
     * @return JSON-String of social media platform to redirect-count.
     * @throws JSONException .
     */
    @GetMapping("/getSumAllTime")
    public String getSumAllTime() throws JSONException {return socialsService.getSumsOfIncoming();}
}
